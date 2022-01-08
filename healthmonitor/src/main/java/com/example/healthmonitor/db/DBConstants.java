package com.example.healthmonitor.db;

public interface DBConstants {
    //configuration
    public static final Integer DB_PORT = 3306;
    public static final String DB_HOST = "localhost";
    public static final String DB_NAME = "healthmonitor";
    public static final String DB_USER = "dev";
    public static final String DB_PASSWORD = "secret";
    public static final String DB_QUEUE = "db.queue";

    //sql statements
    public static final String CREATE_DATABASE = "CREATE DATABASE healthmonitor;";
    public static final String CREATE_SERVICE_TABLE = "CREATE TABLE IF NOT EXISTS Service (Id INTEGER auto_increment PRIMARY KEY, Name VARCHAR(255), Url VARCHAR(255), Valid BOOLEAN NOT NULL DEFAULT '0' , Created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, LastVerified TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP)";
    public static final String LIST_SERVICES = "SELECT * FROM Service";
    public static final String ADD_SERVICE = "INSERT INTO Service (Name, Url, Valid, Created, LastVerified) VALUES (#{name}, #{url}, #{valid}, current_timestamp(), current_timestamp());";
    public static final String UPDATE_SERVICE = "UPDATE Service SET Name=#{name}, Url=#{url} WHERE Id=#{id}";
    public static final String UPDATE_SERVICE_STATUS = "UPDATE Service SET Valid=#{valid}, LastVerified=current_timestamp() WHERE Id=#{id}";
    public static final String DELETE_SERVICE = "DELETE FROM Service WHERE Id=#{id}";
}
