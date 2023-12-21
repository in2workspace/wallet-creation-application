# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v2.0.0] - 2023-12-12

### Added
- Add support for GitHub Actions for CI/CD.
- Checkstyle for code quality.
- Enabled centralized cross-origin resource sharing (CORS) to allow frontend applications to call the endpoints.
- Set the frontend URL dynamically through an external environment variable, enhancing configuration flexibility.
- SonarCloud for code quality.

## [v1.0.0] - 2023-12-4

### Added
- Manages the issuance of verifiable credentials in collaboration with credential issuers.
- Facilitates the exchange of credentials, enabling users to access services on various portals through credential-based login.
- Interprets the content of QR codes to determine and initiate the correct credential-related flow.
- Functions for DID management, rotation, and revocation.
- Functions for DID management, rotation, and revocation.
- Integration with Wallet-Data for persisting and retrieving credentials associated to a user.
- Creational Patterns, utilization of the Builder pattern
- Structural Patterns, implementation of the Facade pattern.
- Environment variable configuration for integrating with the Wallet-Data component.
- Docker-compose configuration for easy deployment and setup
- Project status, contact information, and creation/update dates in README.


[release]:
[1.0.0]: https://github.com/in2workspace/wallet-creation-application/releases/tag/v1.0.0
[2.0.0]: https://github.com/in2workspace/wallet-creation-application/releases/tag/v2.0.0