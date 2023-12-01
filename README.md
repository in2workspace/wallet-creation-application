# WALLET-CREATION-APPLICATION

## Introduction
The Wallet-Creation Application is a pivotal component in the issuance and exchange of verifiable credentials with issuers. It plays a key role in credential-based authentication processes for service access on various portals. The application also features QRContent functionality, which determines the appropriate flow based on the contents of a QR code.

## Main Features
* **Credential Issuance:** Manages the issuance of verifiable credentials in collaboration with credential issuers
* **Credential Exchange:** Facilitates the exchange of credentials, enabling users to access services on various portals through credential-based login.
* **QRContent Functionality:** Interprets the content of QR codes to determine and initiate the correct credential-related flow.

## Installation
### Prerequisites
- [Docker Desktop](https://www.docker.com/)
- [Git](https://git-scm.com/)

### Dependencies for Installation
To successfully install and operate the Wallet-Creation-Application, you will need the following dependencies:
* **Wallet-Data:** Essential for storing issued credentials linked to users. This component is used to manage and store credential data, ensuring that users can select and present their credentials effectively during the credential presentation process. For its installation, follow the guide provided here: [Wallet-Data Configuration Component.](https://github.com/in2workspace/wallet-data).
  
Ensure these dependencies are properly set up and configured before proceeding with the Wallet-Creation Application setup.

## Configuration
Now that you have the necessary dependencies, you can configure the wallet-creation-application using the following docker-compose. Ensure to adjust the environment variables to match your Wallet-Data configurations.
* Wallet-Crypto Configuration
```yaml
wallet-creation-application:
  container_name: wallet-creation-application
  image: in2kizuna/wallet-creation-application:v1.0.0
  environment:
    SERVER_PORT: "8087"
    OPENAPI_SERVER_URL: "http://wallet-creation-application:8087"
    WALLET-DATA_URL: "http://wallet-data:8086"
  command:
    - run
  ports:
    - "8087:8087"
  networks:
    local_network:
```
## Project Status
The project is currently at version **1.0.0** and is in a stable state.

## Contact
For any inquiries or collaboration, you can contact us at:
* **Email:** [info@in2.es](mailto:info@in2.es)
* **Name:** IN2, Ingeniería de la Información
* **Website:** [https://in2.es](https://in2.es)

## Creation Date and Update Dates
* **Creation Date:** October 26, 2023
* **Last Updated:** December 4, 2023