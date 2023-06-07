# Lowell's Rate Storage Microservice

This application exposes a REST API on port 8080 with endpoints that 
provide functionality for updating and retrieving rates for a given 
time range.  These rates are expected not to span days within their
original timezone.

It is a purely Kotlin, Spring Boot project that is documented with 
Swagger and is containerized alongside PostgreSQL for "prod", and 
uses an in memory H2 database for "dev".  It uses Spring Boot's
concept of profiles to trigger between the two.

It has a basic but thorough enough set of service and controller 
layer tests.

## Usage

Once the application has started via either of the two methods below,
send a GET request to http://localhost:8080/rates to see it in action
or check out the Swagger documentation link below.

### prod

To run the application with PostgreSQL (you will need to have Docker,
first run will be download intensive) run the following command from a 
terminal within this directory.

```
docker-compose up -d
```

### dev

To directly run the "dev" version, run the following command. 
Optionally, place a file with name ending with 'rate.json' in this 
directory containing desired rate defaults.  If no file is provided, 
default example data will be loaded.

```
./mvnw spring-boot:run
```

## Documentation

See http://localhost:8080/swagger-ui.html for REST API endpoint usage
documentation
