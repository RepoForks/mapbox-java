package com.mapbox.services.android.navigation.v5;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.mapbox.services.Experimental;
import com.mapbox.services.android.R;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.api.navigation.v5.RouteProgress;

import timber.log.Timber;

import static com.mapbox.services.android.telemetry.location.LocationEnginePriority.BALANCED_POWER_ACCURACY;
import static com.mapbox.services.android.telemetry.location.LocationEnginePriority.HIGH_ACCURACY;

/**
 * This is an experimental API. Experimental APIs are quickly evolving and
 * might change or be removed in minor versions.
 */
@Experimental
public class NavigationService extends Service implements LocationEngineListener {
  private static final int ONGOING_NOTIFICATION_ID = 1;

  private int startId;

  private final IBinder localBinder = new LocalBinder();

  private LocationEngine locationEngine;
  private boolean running;
  private NotificationCompat.Builder notifyBuilder;
  private NotificationManager notificationManager;
  private LocationUpdatedThread locationUpdatedThread;

  private RouteProgress routeProgress;

  @Override
  public void onCreate() {
    super.onCreate();
    Timber.d("Navigation service created.");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.d("Navigation service started.");
    this.startId = startId;
    running = false;
    startNavigation();
    return Service.START_NOT_STICKY;
  }

  public void setupNotification(Activity activity) {
    Timber.d("Setting up notification.");

    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // Sets up the top bar notification
    notifyBuilder = new NotificationCompat.Builder(this)
      .setContentTitle("Mapbox Navigation")
      .setContentText("Distance: " + routeProgress.getDistanceRemainingOnStep())
      .setSmallIcon(R.drawable.ic_navigation_black)
      .setContentIntent(PendingIntent.getActivity(this, 0,
        new Intent(this, activity.getClass()), 0));

    startForeground(ONGOING_NOTIFICATION_ID, notifyBuilder.build());
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Timber.d("Navigation service is now bound.");
    return localBinder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Timber.d("Navigation service is now unbound.");
    return super.onUnbind(intent);
  }

  @Override
  public void onRebind(Intent intent) {
    Timber.d("Navigation service is now rebound.");
    super.onRebind(intent);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Timber.d("Navigation service destroyed.");
  }

  public class LocalBinder extends Binder {
    public NavigationService getService() {
      Timber.d("Local binder called.");
      return NavigationService.this;
    }
  }

  /*
   * Public API
   */

  public void setSnapToRoute(boolean snapToRoute) {
    locationUpdatedThread.setSnapToRoute(snapToRoute);
  }

  private void startNavigation() {
    Timber.d("Navigation session started.");
    routeProgress = new RouteProgress();
    running = true;

    Handler responseHandler = new Handler();
    locationUpdatedThread = new LocationUpdatedThread(responseHandler);
    locationUpdatedThread.start();
    locationUpdatedThread.getLooper();
    Timber.d("Background thread started");
  }

  public void startRoute(DirectionsRoute route) {
    Timber.d("Start Route called.");
    routeProgress.setRoute(route);

    if (locationEngine != null) {
      // Begin listening into location at its highest accuracy and add navigation location listener
      locationEngine.setPriority(HIGH_ACCURACY);
      locationEngine.requestLocationUpdates();
      locationEngine.addLocationEngineListener(this);
    } else {
      Timber.d("locationEngine null in NavigationService");
    }
  }

  public void endNavigation() {
    Timber.d("Navigation session ended.");
    running = false;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      locationUpdatedThread.quitSafely();
    } else {
      locationUpdatedThread.quit();
    }

    // Lower accuracy to minimize battery usage while not in navigation mode.
    locationEngine.setPriority(BALANCED_POWER_ACCURACY);
    locationEngine.removeLocationEngineListener(this);
    locationEngine.deactivate();

    // Removes the foreground notification
    stopForeground(true);

    // Stops the service
    stopSelf(startId);
  }

  public void setAlertLevelChangeListener(AlertLevelChangeListener alertLevelChangeListener) {
    locationUpdatedThread.setAlertLevelChangeListener(alertLevelChangeListener);
  }

  public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
    locationUpdatedThread.setProgressChangeListener(progressChangeListener);
  }

  public void setLocationEngine(LocationEngine locationEngine) {
    this.locationEngine = locationEngine;
  }

  @Override
  public void onConnected() {
    Timber.d("NavigationService now connected to location listener");
    locationEngine.requestLocationUpdates();
    Location lastLocation = locationEngine.getLastLocation();
    if (locationUpdatedThread != null && lastLocation != null) {
      locationUpdatedThread.updateLocation(routeProgress, lastLocation);
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    Timber.d("LocationChange occurred");
    if (locationUpdatedThread != null && location != null) {
      locationUpdatedThread.updateLocation(routeProgress, location);
    }
  }

  public boolean isRunning() {
    Timber.d("Navigation session is running: %b", running);
    return running;
  }
}
