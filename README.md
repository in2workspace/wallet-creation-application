# Introduction 
IN2 Wallet API is the server side application for the IN2 Wallet project. It is a Spring Boot application that uses the Orion-LD Context Broker to store the data related to user unique identifiers (DIDs), user attributes, and user personal data such as PId, (Q)EAA, and EAA in form of Verifiable Credentials. 

## Architecture
The application is based on the following architecture:
### Cryptographic Keys Management System
### Wallet Data Storage Components
### Wallet Authentication Services
### Wallet Creation Application (WCA)

## Main Features
// TODO: Add the main features of the application
- User registration
- User login
- User logout
- User profile management
- User attributes management
- User personal data management
- User DID management
- User Verifiable Credentials management
- User Verifiable Presentations management
- User Verifiable Credentials and Verifiable Presentations validation
- User Verifiable Credentials and Verifiable Presentations revocation
- User Verifiable Credentials and Verifiable Presentations expiration
- User Verifiable Credentials and Verifiable Presentations expiration notification

# Getting Started
1. Clone the repository:
```git clone https://dev.azure.com/in2Dome/DOME/_git/in2-dome-wallet_api```
2.  


# Build and Test
We have 3 different ways to build and test the project depending on the selected Spring Boot profile.
- `test` profile: This profile is used for unit testing. It uses an in-memory database and does not require any external dependencies.
- `local` profile: This profile is used for local development. It uses an in-memory database and generates default data to test the application. You need to run a set of docker containers to run the application (Orion Context Broker and MongoDb).
- `local-docker` profile: This profile is used for local development. It uses a dockerized database and generates default data to test the application.
- `dev` profile: This profile is used for development. It uses a dockerized database and generates default data to test the application.

# Contribute

# License

# Documentation
