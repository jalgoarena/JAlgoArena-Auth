# JAlgoArena Auth [![Build Status](https://travis-ci.org/spolnik/JAlgoArena-Auth.svg?branch=master)](https://travis-ci.org/spolnik/JAlgoArena-Auth)

JAlgoArena Auth is core service dedicated for authentication and authorization of the JAlgoArena users. It's keeping all data in Xodus DB, and for authorization it's using JWT tokens which are verified on the requests. Initial creation of accounts happens through AJAX requests.

- [Introduction](#introduction)
- [Components](#components)
- [Continuous Delivery](#continuous-delivery)
- [Infrastructure](#infrastructure)
- [Notes](#notes)

## Introduction

- JAlgoArena Auth allows for creation of account, login in using username and password, authenticating using previously received token or just taking information about users of JAlgoArena.
- Submissions service talks directly with Auth service to make sure users are authenticated and they have required roles

![Component Diagram](https://github.com/spolnik/JAlgoArena/raw/master/design/component_diagram.png)

## Components

- [JAlgoArena UI](https://github.com/spolnik/JAlgoArena-UI)
- [JAlgoArena Submissions (and Ranking)](https://github.com/spolnik/JAlgoArena-Submissions)
- [JAlgoArena Eureka Server](https://github.com/spolnik/JAlgoArena-Eureka)
- [JAlgoArena API Gateway](https://github.com/spolnik/JAlgoArena-API)

## Continuous Delivery

- initially, developer push his changes to GitHub
- in next stage, GitHub notifies Travis CI about changes
- Travis CI runs whole continuous integration flow, running compilation, tests and generating reports
- coverage report is sent to Codecov
- application is deployed into Heroku machine

## Infrastructure

- Heroku (PaaS)
- Xodus (embedded highly scalable database) - http://jetbrains.github.io/xodus/
- Spring Boot, Spring Cloud (Eureka Client)
- TravisCI - https://travis-ci.org/spolnik/JAlgoArena-Auth

## Notes
- [Running locally](https://github.com/spolnik/jalgoarena/wiki)
- [Travis Builds](https://travis-ci.org/spolnik)

![Component Diagram](https://github.com/spolnik/JAlgoArena/raw/master/design/JAlgoArena_Logo.png)
