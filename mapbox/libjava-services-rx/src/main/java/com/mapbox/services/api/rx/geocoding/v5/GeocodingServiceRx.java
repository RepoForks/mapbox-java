package com.mapbox.services.api.rx.geocoding.v5;

import com.mapbox.services.api.geocoding.v5.models.GeocodingResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Interface that defines the geocoding service.
 *
 * @since 2.0.0
 */
public interface GeocodingServiceRx {

  /**
   * Observable-based interface
   *
   * @param userAgent    The user
   * @param mode         mapbox.places or mapbox.places-permanent for enterprise geocoding.
   * @param query        a location; a place name for forward geocoding or a coordinate pair
   *                     (longitude, latitude location) for reverse geocoding
   * @param accessToken  Mapbox access token.
   * @param country      ISO 3166 alpha 2 country codes, separated by commas.
   * @param proximity    Location around which to bias results.
   * @param types        Filter results by one or more type.
   * @param autocomplete True if you want auto complete.
   * @param bbox         Optionally pass in a bounding box to limit results in.
   * @param limit        Optionally pass in a limit the amount of returning results.
   * @param language     The locale in which results should be returned.
   * @return A retrofit Observable object
   * @since 1.0.0
   */
  @GET("/geocoding/v5/{mode}/{query}.json")
  Observable<GeocodingResponse> getObservable(
    @Header("User-Agent") String userAgent,
    @Path("mode") String mode,
    @Path("query") String query,
    @Query("access_token") String accessToken,
    @Query("country") String country,
    @Query("proximity") String proximity,
    @Query("types") String types,
    @Query("autocomplete") Boolean autocomplete,
    @Query("bbox") String bbox,
    @Query("limit") String limit,
    @Query("language") String language);

  /**
   * Observable-based interface
   *
   * @param userAgent    The user
   * @param mode         mapbox.places-permanent for batch geocoding.
   * @param query        a location; a place name for forward geocoding or a coordinate pair
   *                     (longitude, latitude location) for reverse geocoding
   * @param accessToken  Mapbox access token.
   * @param country      ISO 3166 alpha 2 country codes, separated by commas.
   * @param proximity    Location around which to bias results.
   * @param types        Filter results by one or more type.
   * @param autocomplete True if you want auto complete.
   * @param bbox         Optionally pass in a bounding box to limit results in.
   * @param limit        Optionally pass in a limit the amount of returning results.
   * @param language     The locale in which results should be returned.
   * @return A retrofit Observable object
   * @since 1.0.0
   */
  @GET("/geocoding/v5/{mode}/{query}.json")
  Observable<List<GeocodingResponse>> getBatchObservable(
    @Header("User-Agent") String userAgent,
    @Path("mode") String mode,
    @Path("query") String query,
    @Query("access_token") String accessToken,
    @Query("country") String country,
    @Query("proximity") String proximity,
    @Query("types") String types,
    @Query("autocomplete") Boolean autocomplete,
    @Query("bbox") String bbox,
    @Query("limit") String limit,
    @Query("language") String language);
}
