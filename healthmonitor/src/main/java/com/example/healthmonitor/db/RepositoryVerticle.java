package com.example.healthmonitor.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;

public class RepositoryVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryVerticle.class);

    private MySQLPool pool;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
            .setPort(DBConstants.DB_PORT)
            .setHost(DBConstants.DB_HOST)
            .setDatabase(DBConstants.DB_NAME)
            .setUser(DBConstants.DB_USER)
            .setPassword(DBConstants.DB_PASSWORD);

        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        pool = MySQLPool.pool(vertx, connectOptions, poolOptions);
        pool.getConnection(sqlConnection -> {
            if (sqlConnection.succeeded()) {
                pool.query(DBConstants.CREATE_SERVICE_TABLE).execute(create -> {
                    if (create.succeeded()) {
                        LOGGER.info("Service initialized correctly");
                        vertx.eventBus().consumer(DBConstants.DB_QUEUE, this::messageHandler);
                        startPromise.complete();
                    } else {
                        LOGGER.error("Could not create service table", create.cause());
                        startPromise.fail(create.cause());
                    }
                });
            } else {
                LOGGER.error("Could not open a connection", sqlConnection.cause());
                startPromise.fail(sqlConnection.cause());
            }
        });
    }

    /**
     * @param message - message to be handled
     */
    public void messageHandler(Message<JsonObject> message) {
        String action = message.headers().get("action");

        switch (action) {
            case "list-services":
                listServices(message);
                break;
            case "add-service":
                addService(message);
                break;
            case "delete-service":
                deleteService(message);
                break;
            case "update-service":
                updateService(message);
                break;
            case "update-service-status":
                updateServiceStatus(message);
                break;
            default:
                LOGGER.error("Bad request: unknown action -> " + action);
                message.fail(ErrorCodes.REQUEST_ERROR, "Bad request: unknown action -> " + action);
        }
    }

    /**
     * @param message - message containing request to list services
     */
    private void listServices(Message<JsonObject> message) {
        pool.query(DBConstants.LIST_SERVICES).execute(response -> {
            if (response.succeeded()) {
                ArrayList<JsonObject> services = new ArrayList<JsonObject>();
                RowSet<Row> result = response.result();
                result.forEach(row -> services.add(row.toJson()));
                JsonObject json = new JsonObject().put("services", services);
                message.reply(json);
            } else {
                LOGGER.error("DB error", response.cause());
                message.fail(ErrorCodes.DB_ERROR, response.cause().getMessage());
            }
        });
    }

    /**
     * @param message - message containing request to add service
     */
    private void addService(Message<JsonObject> message) {
        JsonObject json = message.body();
        Map<String, Object> params = new HashMap<>();
        params.put("name", json.getString("name"));
        params.put("url", json.getString("url"));
        params.put("valid", json.getBoolean("valid"));
        LOGGER.info("Adding service with params: " + params);
        this.execute(DBConstants.ADD_SERVICE, params, message);
    }

    /**
     * @param message - message containing request to delete service
     */
    private void deleteService(Message<JsonObject> message) {
        JsonObject json = message.body();
        Map<String, Object> params = new HashMap<>();
        params.put("id", json.getString("id"));
        LOGGER.info("Deleting service with params: " + params);
        this.execute(DBConstants.DELETE_SERVICE, params, message);
    }

    /**
     * @param message - message containing request to update service name and url
     */
    private void updateService(Message<JsonObject> message) {
        JsonObject json = message.body();
        Map<String, Object> params = new HashMap<>();
        params.put("id", json.getString("id"));
        params.put("name", json.getString("name"));
        params.put("url", json.getString("url"));
        LOGGER.info("Updating service with params: " + params);
        this.execute(DBConstants.UPDATE_SERVICE, params, message);
    }

    /**
     * @param message - message containing request to update service status
     */
    private void updateServiceStatus(Message<JsonObject> message) {
        JsonObject json = message.body();
        Map<String, Object> params = new HashMap<>();
        params.put("id", json.getString("id"));
        params.put("valid", json.getBoolean("valid"));
        LOGGER.info("Updating service status with params: " + params);
        this.execute(DBConstants.UPDATE_SERVICE_STATUS, params, message);
    }

    private void execute(String sql, Map<String, Object> params, Message<JsonObject> message) {
        SqlTemplate.forQuery(pool, sql).execute(params)
            .onSuccess(response -> {
                LOGGER.info("Request executed correctly");
                message.reply(new JsonObject());
            })
            .onFailure(error -> {
                LOGGER.error("DB error", error.getCause());
                message.fail(ErrorCodes.DB_ERROR, error.getCause().getMessage());
            });
    }
}
