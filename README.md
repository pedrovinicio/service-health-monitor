# Service health monitoring app

This application allows you to keep track on your services' health. 

You are able to add as many URL as you want in the frontend application. The frontend will keep polling the backend every 30s to check for status changes.

The backend will be firing requests to each service URL every minute and update the service status accordingly.

## About the app

-The frontend code is located on "healthmonitor-front" and it was built using [ReactJS](https://reactjs.org/).
-You can see more details about it [HERE](https://github.com/pedrovinicio/service-health-monitor/tree/main/healthmonitor-front)

-The backend code is located on "healthmonitor" and it was built using [Vert.x](https://vertx.io/docs/).
-You can see more details about it [HERE](https://github.com/pedrovinicio/service-health-monitor/tree/main/healthmonitor)

