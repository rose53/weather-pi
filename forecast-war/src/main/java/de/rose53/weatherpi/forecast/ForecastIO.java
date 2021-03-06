package de.rose53.weatherpi.forecast;

import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class ForecastIO {

    private final String apiKey;
    private final String latitude;
    private final String longitude;
    private final String units;
    private final String lang;

    public static class Builder {

        private final String apiKey;
        private final String latitude;
        private final String longitude;

        private String units;
        private String lang;

        public Builder(String apiKey, String latitude, String longitude) {
            this.apiKey = apiKey;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Builder units(String units) {
            this.units = units;
            return this;
        }

        public Builder lang(String lang) {
            this.lang = lang;
            return this;
        }

        public ForecastIO build() {
            return new ForecastIO(this);
        }
    }

    private ForecastIO(Builder builder) {
        this.apiKey = builder.apiKey;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.units = builder.units;
        this.lang = builder.lang;
    }

    public Forecastdata getForecastdata() throws IOException {

        Client clientBuilder = ClientBuilder.newClient();

        UriBuilder uriBuilder = UriBuilder.fromUri("https://api.darksky.net/");

        uriBuilder.segment("forecast",apiKey,latitude + "," + longitude);

        if (units != null) {
            uriBuilder.queryParam("units",units);
        }
        if (lang != null) {
            uriBuilder.queryParam("lang",lang);
        }
        String result = clientBuilder.target(uriBuilder)
                                     .request(MediaType.APPLICATION_JSON_TYPE)
                                     .get(String.class);

        return new Forecastdata(Json.createReader(new StringReader(result)).readObject());
    }
}
