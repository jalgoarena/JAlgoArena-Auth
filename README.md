# JAlgoArena Auth [![Build Status](https://travis-ci.org/jalgoarena/JAlgoArena-Auth.svg?branch=master)](https://travis-ci.org/jalgoarena/JAlgoArena-Auth) [![codecov](https://codecov.io/gh/spolnik/JAlgoArena-Auth/branch/master/graph/badge.svg)](https://codecov.io/gh/spolnik/JAlgoArena-Auth) [![GitHub release](https://img.shields.io/github/release/spolnik/jalgoarena-auth.svg)]()

JAlgoArena Auth is core service dedicated for authentication and authorization of the JAlgoArena users. It's keeping all data in Cockroach DB, and for authorization it's using JWT tokens which are verified on the requests. Initial creation of accounts happens through AJAX requests.

- [Introduction](#introduction)
- [API](#api)
- [Running Locally](#running-locally)
- [Notes](#notes)

## Introduction

- JAlgoArena Auth allows for creation of account, login in using username and password, authenticating using previously received token or just taking information about users of JAlgoArena.
- On the first run of the service - it creates admin account with _admin_ as username, and password put into logs
- Submissions service talks directly with Auth service to make sure users are authenticated and they have required roles

![Component Diagram](https://github.com/spolnik/JAlgoArena-Auth/raw/master/design/component_diagram.png)

## API

#### Sign up

  _Create a new user_

|URL|Method|
|---|------|
|_/signup_|`POST`|

* **Data Params**

  _User json data passed as request body_
  
  ```json
  {
    "username": "user1",
    "password": "password1",
    "firstname":"First Name",
    "surname":"Surname",
    "email": "user1@email.com",
    "region": "Krakow",
    "team": "TyniecTeam"
  }
  ```

* **Success Response:**

  _As the response you will get user data json filled with assigned id and role_

  * **Code:** 201 CREATED <br />
    **Content:** `{"id":1,"username":"user1","password":"","firstname":"First Name","surname":"Surname","email":"user1@email.com","region":"Krakow","team":"TyniecTeam","role":"USER"}`

* **Error Response:**

  _If you try using same user name or email which is already taken by one of existing users - then you will get error response_

  * **Code:** 409 CONFLICT <br />
    **Content:** `{ "error": "Registration Error", "message": "User name is already used" }`

  OR

  * **Code:** 409 CONFLICT <br />
    **Content:** `{ "error": "Registration Error", "message": "Email is already used" }`

* **Sample Call:**

  ```bash
  curl --header "Content-Type: application/json" \
       --data '{"username":"user1","password":"password1","firstname":"First Name","surname":"Surname","email":"user1@email.com","region":"Krakow","team":"TyniecTeam"}' \
       http://localhost:5003/signup
  ```

#### Get all users

Users api exposes two kind of APIs, public, and protected which can be accessed only using token.

> Token is generated and returned during successful login  

|URL|Method|
|---|------|
|_/users_|`GET`|

* **Success Response:**
  
  Array of users

  * **Code:** 200 <br />
    **Content:** `[{"id":1,"username":"user1","password":"","firstname":"First Name","surname":"Surname","email":"","region":"Krakow","team":"TyniecTeam","role":"USER"}]`
 
* **Sample Call:**

  `curl http://localhost:5003/users` 
 
#### Log in

  _Log in gives you access to contest platform - after receiving request response you get token which can be further used as your identity token_

|URL|Method|
|---|------|
|_/login_|`POST`|
  
* **Data Params**

  _As part of your request you have to pass login request json_
  
  ```json
  {
    "username": "user1",
    "password": "password1"
  }
  ```

* **Success Response:**
  
  _Once you successfully log in - you will get the token in the response which you may use for accessing protected endpoints_

  * **Code:** 200 <br />
    **Content:** `{"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMiIsInNjb3BlcyI6WyJST0xFX1VTRVIiXSwiaXNzIjoiamFsZ29hcmVuYS5jb20iLCJpYXQiOjE1MzI2MDk3OTcsImV4cCI6MTUzNTIwMTc5N30.-6GZNBIOwdpelIHzQ9zzamA-LVGHgxO97aL_5e1uDXBOXmXBr6uRAdgnZxNkOiHSp-Hx115hCkDlYIuDCBeMTw","user":{"id":1,"username":"user1","password":"","firstname":"First Name","surname":"Surname","email":"user1@email.com","region":"Krakow","team":"TyniecTeam","role":"USER"}}`
 
* **Error Response:**

  _In case of wrong credentials access will be forbidden._

  * **Code:** 403 FORBIDDEN <br />
    **Content:** `{"timestamp":"2018-07-26T12:59:24.523+0000","status":403,"error":"Forbidden","message":"Access Denied","path":"/login"}`

* **Sample Call:**

  ```bash
  curl --header "Content-Type: application/json" \
       --data '{"username":"user1","password":"password1"}' \
       http://localhost:5003/login
  ``` 

#### Check session

  _Checking session is using token given during log in process - which can be used for accessing secured platform REST api and to confirm identity_

|URL|Method|
|---|------|
|_/api/user_|`GET`|
  
* **Data Params**

  _As part of your request you have to set required headers_
  
  ```
  'Accept': 'application/json',
  'X-Authorization': 'Bearer <token>'
  ```

* **Success Response:**
  
  _Once you successfully check session - you will get the user data in the response which is used as your identity_

  * **Code:** 200 <br />
    **Content:** `{"id":1,"username":"user1","firstname":"First Name","surname":"Surname","password":"","email":"user1@email.com","region":"Krakow","team":"TyniecTeam","role":"USER"}`
 
* **Error Response:**

  _In case of wrong credentials access will be forbidden._

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:** `"timestamp":"2018-07-26T18:24:07.061+0000","status":401,"error":"Unauthorized","message":"Unauthorized","path":"/api/user"}`

* **Sample Call:**

  ```bash
  curl --header "Content-Type: application/json" \
       --header "X-Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsInNjb3BlcyI6WyJST0xFX1VTRVIiXSwiaXNzIjoiamFsZ29hcmVuYS5jb20iLCJpYXQiOjE1MzI2MjkwNTksImV4cCI6MTUzNTIyMTA1OX0.klPU-g_7hDWw-A5Fr6i0y4pCVPRuOLnHsRV1Y7GKMmxYELNFAeLpsAf1y1JmW-KV8wz0pUztvTgcH2f-BJ6zKA" \
       http://localhost:5003/api/user
  ``` 

## Running locally

There are two ways to run it - from sources or from binaries.

### Running from binaries
- go to [releases page](https://github.com/spolnik/JAlgoArena-Auth/releases) and download last app package (JAlgoArena-Auth-[version_number].zip)
- after unpacking it, go to folder and run `./run.sh` (to make it runnable, invoke command `chmod +x run.sh`)
- you can modify port in run.sh script, depending on your infrastructure settings. The script itself can be found in here: [run.sh](run.sh)

### Running from sources
- run `git clone https://github.com/spolnik/JAlgoArena-Auth` to clone locally the sources
- now, you can build project with command `./gradlew clean bootRepackage` which will create runnable jar package with app sources. Next, run `java -Dserver.port=9999 -jar build\libs\jalgoarena-auth-*.jar` which will start application
- there is second way to run app with gradle. Instead of running above, you can just run `./gradlew clean bootRun`

## Notes
- [Travis Builds](https://travis-ci.org/spolnik)

![Component Diagram](https://github.com/spolnik/JAlgoArena/raw/master/design/JAlgoArena_Logo.png)
