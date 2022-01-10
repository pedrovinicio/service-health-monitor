package com.example.healthmonitor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class TestHealthMonitorHttpServerVerticle {
    @Test
    public void testAddAndListServices(Vertx vertx, VertxTestContext testContext) {
        final JsonObject newService = new JsonObject().put("name", "Google").put("url", "https://www.google.com");
        WebClient client = WebClient.create(vertx);
        client
            .post(8888, "localhost", "/")
            .sendJson(
                newService
            )
            .onComplete(response -> {
                client.get(8888, "localhost", "/").send().onComplete(resp -> {
                    // Get the previously inserted service name and url
                    System.out.println("Services" + resp.result());
                    testContext.completeNow();
                });
            });
    }
}
