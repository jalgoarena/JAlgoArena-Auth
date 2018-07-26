# JAlgoArena Auth [![Build Status](https://travis-ci.org/spolnik/JAlgoArena-Auth.svg?branch=master)](https://travis-ci.org/spolnik/JAlgoArena-Auth) [![codecov](https://codecov.io/gh/spolnik/JAlgoArena-Auth/branch/master/graph/badge.svg)](https://codecov.io/gh/spolnik/JAlgoArena-Auth) [![GitHub release](https://img.shields.io/github/release/spolnik/jalgoarena-auth.svg)]()

JAlgoArena Auth is core service dedicated for authentication and authorization of the JAlgoArena users. It's keeping all data in Xodus DB, and for authorization it's using JWT tokens which are verified on the requests. Initial creation of accounts happens through AJAX requests.

- [Introduction](#introduction)
- [Components](#components)
- [Continuous Delivery](#continuous-delivery)
- [Infrastructure](#infrastructure)
- [Running Locally](#running-locally)
- [Notes](#notes)

## Introduction

- JAlgoArena Auth allows for creation of account, login in using username and password, authenticating using previously received token or just taking information about users of JAlgoArena.
- On the first run of the service - it creates admin account with _admin_ as username, and password put into logs
- Submissions service talks directly with Auth service to make sure users are authenticated and they have required roles

![Component Diagram](https://github.com/spolnik/JAlgoArena-Auth/raw/master/design/component_diagram.png)

## API

####Sign up

  _Create a new user_

* **URL**

  _/signup_

* **Method:**

  `POST`

* **Data Params**

  _User json data passed as request body_
  
  ```json
  {
    "username": "user1",
    "password": "password1",
    "email": "user1@email.com",
    "region": "Krakow",
    "team": "Tyniec Team"
  }
  ```

* **Success Response:**

  _As the response you will get user data json filled with assigned id and role_

  * **Code:** 201 CREATED <br />
    **Content:** `{"id":1,"username":"user1","password":"","email":"user1@email.com","region":"Krakow","team":"TyniecTeam","role":"USER"}`

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
       --data '{"username":"user2","password":"password1","email":"user1@email.com","region":"Krakow","team":"TyniecTeam"}' \
       http://localhost:5003/signup
  ```

####Get all users

Users api exposes two kind of APIs, public, and protected which can be accessed only using token.

> Token is generated and returned during successful login  

* **URL**

  _/users_

* **Method:**
  
  `GET`

* **Success Response:**
  
  Array of users

  * **Code:** 200 <br />
    **Content:** `[]`
 
* **Sample Call:**

  `curl http://localhost:5003/users` 
 

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
