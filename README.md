# Deployment & Installation Guide

## Prerequisites:
- Docker installed  
- Postman installed  
- Must call authorization API to obtain JWT token, then include the token as JWT Bearer in request header for subsequent API calls  

## Docker Container Deployment
- Go to the root directory(directory that stores docker-compose.yml) of the project using Terminal  
- Execute bash command: `docker-compose up --build`  
- After containers deployment, the following ports will be exposed:  
  - `localhost:8081` (restaurant-api)  
  - `localhost:5432` (postgresql)  
- **Remark:** First time deployment/installation might acquires longer time  

## Postman API
- Look up for the `Restaurant-API.postman_collection.json` in the project root directory(directory that stores docker-compose.yml)  
- Import into Postman collection  
- Trigger each API call via the template prepared [Must ensure containers are running]  

## Swagger
- Visit `localhost:8081`  
- The API service comes with Swagger OpenAPI Documentation  
- Trigger each API call via the swagger UI  
