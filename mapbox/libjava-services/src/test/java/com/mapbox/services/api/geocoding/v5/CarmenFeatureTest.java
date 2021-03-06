package com.mapbox.services.api.geocoding.v5;

import com.google.gson.JsonObject;
import com.mapbox.services.api.BaseTest;
import com.mapbox.services.api.geocoding.v5.models.CarmenContext;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.commons.geojson.Geometry;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CarmenFeatureTest extends BaseTest {

  private static final double DELTA = 1E-10;
  private static final String GEOCODING_FIXTURE = "src/test/fixtures/geocoder_tofromjson.json";

  @Test
  public void checksBuilding() {
    String text = "Text field 1";
    String placeName = "Text field 2";
    double[] bbox = new double[] {1.2, 3.4, 5.6, 7.8};
    String address = "Text field 3";
    double[] center = new double[] {1.2, 3.4};
    double relevance = 1.2;
    JsonObject properties = new JsonObject();
    String property = "foo1";
    String value = "bar";
    String anotherProperty = "foo2";
    properties.addProperty(property, value);
    String id = "Text field 4";
    CarmenFeatureBuilder builder = new CarmenFeatureBuilder();
    builder.text(text)
      .placeName(placeName)
      .address(address)
      .id(id);
    CarmenFeature feature = builder.build();

    assertEquals(text, feature.getText());
    assertEquals(placeName, feature.getPlaceName());
    assertEquals(bbox[0], feature.getBbox()[0], DELTA);
    assertEquals(bbox[1], feature.getBbox()[1], DELTA);
    assertEquals(bbox[2], feature.getBbox()[2], DELTA);
    assertEquals(bbox[3], feature.getBbox()[3], DELTA);
    assertEquals(address, feature.getAddress());
    assertEquals(center[0], feature.getCenter()[0], DELTA);
    assertEquals(center[1], feature.getCenter()[1], DELTA);
    assertNull(feature.getContext());
    assertEquals(relevance, feature.getRelevance(), DELTA);
    assertNull(feature.getGeometry());
    assertEquals(properties, feature.getProperties());
    assertEquals(value, feature.getStringProperty(property));
    assertTrue(feature.hasNonNullValueForProperty(property));
    assertFalse(feature.hasNonNullValueForProperty(anotherProperty));
    assertEquals(id, feature.getId());
  }

  @Test
  public void checksFromJson() throws IOException {
    CarmenFeatureBuilder builder = new CarmenFeatureBuilder();
    builder.text("Text field 1")
      .placeName("Text field 2")
      .address("Text field 3")
      .id("Text field 4");
    CarmenFeature expected = builder.build();
    String body = new String(Files.readAllBytes(Paths.get(GEOCODING_FIXTURE)), Charset.forName("utf-8"));

    CarmenFeature actual = CarmenFeature.fromJson(body);

    assertEquals(expected.getText(), actual.getText());
  }

  @Test
  public void checksToJson() throws IOException {
    CarmenFeatureBuilder builder = new CarmenFeatureBuilder();
    builder.text("Text field 1")
      .placeName("Text field 2")
      .address("Text field 3")
      .id("Text field 4");
    CarmenFeature feature = builder.build();

    compareJson(loadJsonFixture("", "geocoder_tofromjson.json"), feature.toJson());
  }

  private class CarmenFeatureBuilder {
    private CarmenFeature feature;
    private String text;
    private String placeName;
    private double[] bbox = new double[] {1.2, 3.4, 5.6, 7.8};
    private String address;
    private double[] center = new double[] {1.2, 3.4};
    private List<CarmenContext> context = null;
    private double relevance = 1.2;
    private Geometry geometry = null;
    private JsonObject properties;
    private String id;

    public CarmenFeatureBuilder() {
      feature = new CarmenFeature();
      properties = new JsonObject();
      properties.addProperty("foo1", "bar");
    }

    public CarmenFeatureBuilder text(String text) {
      this.text = text;
      return this;
    }

    public CarmenFeatureBuilder placeName(String placeName) {
      this.placeName = placeName;
      return this;
    }

    public CarmenFeatureBuilder bbox(double[] bbox) {
      this.bbox = bbox;
      return this;
    }

    public CarmenFeatureBuilder address(String address) {
      this.address = address;
      return this;
    }

    public CarmenFeatureBuilder center(double[] center) {
      this.center = center;
      return this;
    }

    public CarmenFeatureBuilder context(List<CarmenContext> context) {
      this.context = context;
      return this;
    }

    public CarmenFeatureBuilder relevance(double relevance) {
      this.relevance = relevance;
      return this;
    }

    public CarmenFeatureBuilder geometry(Geometry geometry) {
      this.geometry = geometry;
      return this;
    }

    public CarmenFeatureBuilder properties(JsonObject properties) {
      this.properties = properties;
      return this;
    }

    public CarmenFeatureBuilder id(String id) {
      this.id = id;
      return this;
    }

    public CarmenFeature build() {
      feature.setText(text);
      feature.setPlaceName(placeName);
      feature.setBbox(bbox);
      feature.setAddress(address);
      feature.setCenter(center);
      feature.setContext(context);
      feature.setRelevance(relevance);
      feature.setGeometry(geometry);
      feature.setProperties(properties);
      feature.setId(id);
      return feature;
    }
  }
}
