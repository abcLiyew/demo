package com.esdllm.bilibiliApi;

import kong.unirest.Unirest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiBase {
    public static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0";
    public static final String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7";
    public static kong.unirest.HttpResponse<String> getCloseableHttpResponse(String url) throws IOException {
        return Unirest.get(url)
                .header("User-Agent", ApiBase.userAgent)
                .accept(ApiBase.accept)
                .asString();

    }
    public static HttpResponse getHttpResponseNotRedirect(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        return client.execute(new HttpGet(url));
    }
}
