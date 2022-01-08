package com.example.healthmonitor;

import com.example.healthmonitor.db.RepositoryVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.rxjava3.core.Vertx;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Vertx vertx = Vertx.vertx();
    vertx.rxDeployVerticle(RepositoryVerticle.class.getName())
      .flatMap(x -> vertx.rxDeployVerticle(HealthMonitorHttpServerVerticle.class.getName()))
      .subscribe(id -> startPromise.complete(), startPromise::fail);
  }
}
