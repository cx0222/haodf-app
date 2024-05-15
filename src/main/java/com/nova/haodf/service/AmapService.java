package com.nova.haodf.service;

import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.config.ProxyConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Service
public class AmapService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmapService.class);
    private static final String QUERY_LOCATION_URL_TEMPLATE = "https://restapi.amap.com/v3/geocode/geo?key=%s&address=%s";
    private final ProxyConfig proxyConfig;
    @Value("${options.other.amap-key}")
    public String amapKey;

    @Autowired
    public AmapService(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    @Cacheable(value = "getLocation")
    public String getGeocode(String location, Function<String, Boolean> assertionFunction) throws IOException {
        if (location == null) {
            return null;
        }
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        HttpGet httpGet = new HttpGet(QUERY_LOCATION_URL_TEMPLATE.formatted(amapKey, encodedLocation));
        String geocode = null;
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(proxyConfig.getCredentialsProvider())
                .build()) {
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    try {
                        String rawString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                        JSONObject geocodeObject = JSONObject.parseObject(rawString)
                                .getJSONArray("geocodes")
                                .getJSONObject(0);
                        String level = geocodeObject.getString("level");
                        assert assertionFunction == null || assertionFunction.apply(level);
                        geocode = geocodeObject.getString("location");
                    } catch (Exception exception) {
                        LOGGER.warn("Could not find geocode with expected level, location = {}", location, exception);
                    }
                }
            }
        }
        if (geocode != null) {
            LOGGER.info("Found the geocode, location = {}, geocode = {}", location, geocode);
        } else {
            LOGGER.warn("Failed to find geocode, location = {}", location);
        }
        return geocode;
    }
}
