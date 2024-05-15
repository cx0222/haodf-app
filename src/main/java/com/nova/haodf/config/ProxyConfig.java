package com.nova.haodf.config;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {
    @Value("${options.proxy.host}")
    private String host;
    @Value("${options.proxy.port}")
    private int port;
    @Value("${options.proxy.timeout-second}")
    private int timeoutSeconds;
    @Value("${options.proxy.username}")
    private String username;
    @Value("${options.proxy.password}")
    private String password;
    @Value("${options.proxy.user-agent}")
    private String userAgent;

    public HttpHost getProxy() {
        return new HttpHost("http", host, port);
    }

    public BasicCredentialsProvider getCredentialsProvider() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password.toCharArray());
        credentialsProvider.setCredentials(new AuthScope(getProxy()), credentials);
        return credentialsProvider;
    }

    public RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutSeconds))
                .setProxy(getProxy())
                .build();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public String toString() {
        return "ProxyConfiguration {" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", timeoutSeconds=" + timeoutSeconds +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
