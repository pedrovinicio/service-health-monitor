package com.example.healthmonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.healthmonitor.db.DBConstants;
import com.example.healthmonitor.db.ServiceModel;
import com.google.gson.Gson;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class HealthMonitorHttpServerVerticle extends AbstractVerticle {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private WebClient client;
    private String DBQueue = DBConstants.DB_QUEUE;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        client = WebClient.create(vertx);

        // Routing
        Router router = Router.router(vertx);

        // Option route to support DELETE, POST and PUT
        router.options("/")
            .handler(this.createRoutingContext(Arrays.asList(HttpMethod.OPTIONS, HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PUT)));

        // Get services route
        router.get("/")
            .handler(this.createRoutingContext(Arrays.asList(HttpMethod.GET)))
            .handler(this::listServicesHandler);

        router.post().handler(BodyHandler.create());
        router.post("/")
            .handler(this.createRoutingContext(Arrays.asList(HttpMethod.OPTIONS, HttpMethod.POST)))
            .handler(this::addServiceHandler);

        router.delete().handler(BodyHandler.create());
        router.delete("/")
            .handler(this.createRoutingContext(Arrays.asList(HttpMethod.OPTIONS, HttpMethod.DELETE)))
            .handler(this::deleteServiceHandler);

        router.put().handler(BodyHandler.create());
        router.put("/")
            .handler(this.createRoutingContext(Arrays.asList(HttpMethod.OPTIONS, HttpMethod.PUT)))
            .handler(this::updateServiceHandler);

        // Update service records every minutes
        Runnable updateServicesRunnable = this::updateServices;
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(updateServicesRunnable, 0, 1, TimeUnit.MINUTES);

        server.requestHandler(router)
            .listen(8888, response -> {
                if (response.succeeded()) {
                    LOGGER.info("Service start at 8888");
                    startPromise.complete();
                } else {
                    LOGGER.error("Service start failed", response.cause());
                    startPromise.fail(response.cause());
                }
            });
    }

    private void updateServices() {
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "list-services");
        vertx.eventBus().request(DBQueue, null, options, response -> {
            if (response.succeeded()) {
                JsonObject body = (JsonObject) response.result().body();
                JsonArray servicesAsJson = body.getJsonArray("services");
                ArrayList<ServiceModel> listServices = new ArrayList<ServiceModel>();
                servicesAsJson.forEach(serviceJson -> {
                    listServices.add(new Gson().fromJson(String.valueOf(serviceJson), ServiceModel.class));
                });
                listServices.forEach(service -> {
                    client.getAbs(service.Url)
                    .timeout(5000)
                    .send()
                    .onSuccess(res -> {
                        int statusCode = res.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            this.updateServiceStatus(service, true);
                        } else {
                            this.updateServiceStatus(service, false);
                        }
                    })
                    .onFailure(error -> {
                        LOGGER.info("Request to " + service.Url + " failed");
                        this.updateServiceStatus(service, false);
                    });
                });
            } else {
                LOGGER.error("Could not fetch services");
            }
        });
    }

    private void listServicesHandler(RoutingContext context) {
        System.out.println("listServicesHandler");
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "list-services");
        vertx.eventBus().request(DBQueue, null, options, response -> {
            if (response.succeeded()) {
                JsonObject body = (JsonObject) response.result().body();
                context.response().putHeader("content-type", "application/json");
                context.response().setStatusCode(200);
                context.response().end(body.encodePrettily());
            } else {
                context.fail(response.cause());
            }
        });
    }

    private void addServiceHandler(RoutingContext context) {
        JsonObject body = context.getBodyAsJson();
        String name = body.getString("name");
        String url = body.getString("url");
        
        client.getAbs(url)
            .timeout(5000)
            .send()
            .onSuccess(response -> {
                int statusCode = response.statusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    this.addServiceToDB(context, name, url, true);
                } else {
                    this.addServiceToDB(context, name, url, false);
                }
            })
            .onFailure(error -> {
                LOGGER.info("Request to " + url + " failed");
                this.addServiceToDB(context, name, url, false);
            });
    }

    private void addServiceToDB(RoutingContext context, String name, String url, Boolean valid) {
        JsonObject params = new JsonObject()
            .put("name", name)
            .put("url", url)
            .put("valid", valid);
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "add-service");

        this.sendRequest(context, params, options);
    }

  
    private void deleteServiceHandler(RoutingContext context) {
        JsonObject body = context.getBodyAsJson();
        JsonObject params = new JsonObject()
            .put("id", body.getString("id"));
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "delete-service");

        this.sendRequest(context, params, options);
    }

    private void updateServiceHandler(RoutingContext context) {
        JsonObject body = context.getBodyAsJson();
        JsonObject params = new JsonObject()
            .put("id", body.getString("id"))
            .put("name", body.getString("name"))
            .put("url", body.getString("url"));
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "update-service");

        this.sendRequest(context, params, options);
    }

    private void updateServiceStatus(ServiceModel service, Boolean valid) {
        JsonObject params = new JsonObject()
            .put("id", service.Id)
            .put("valid", valid);
        DeliveryOptions updateOptions = new DeliveryOptions().addHeader("action", "update-service-status");
        vertx.eventBus().request(DBQueue, params, updateOptions, response -> {
            if (response.failed()) {
                LOGGER.error("Updating service status failed for service " + service.Name);
            }
        });
    }

    private void sendRequest(RoutingContext context, JsonObject params, DeliveryOptions options) {
        vertx.eventBus().request(DBQueue, params, options, response -> {
            if (response.succeeded()) {
                LOGGER.info("Request to database success: " + context.request().toString());
                context.response().setStatusCode(204);
                context.response().end();
            } else {
                LOGGER.error("Request to database failed", response.cause());
                context.fail(response.cause());
            }
        });
    }

    private CorsHandler createRoutingContext(List<HttpMethod> httpMethods) {
        CorsHandler handler = CorsHandler.create("*");
        for (HttpMethod httpMethod : httpMethods) {
            handler.allowedMethod(httpMethod);
        }
        handler.allowedHeader("Access-Control-Request-Method")
            .allowedHeader("Access-Control-Allow-Credentials")
            .allowedHeader("Access-Control-Allow-Origin")
            .allowedHeader("Access-Control-Allow-Headers")
            .allowedHeader("Content-Type");
        return handler;
    }
}
