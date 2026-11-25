---
name: testing
description: Test conventions: IntelliJ test system, developer vs integration tests, package mirroring, coverage. Use when writing or restructuring tests.
---

# Testing

* The IntelliJ plugin test system MUST be used
* Every use case MUST be tested
* Code coverage should reach at least 90%, ideally 100% where possible
* The package structure of the production code is to be mirrored
* Tests are to be split into two categories
    * **Developer tests** - Simple unit tests covering individual pieces of functionality
    * **Integration tests** - Tests covering complete features or aiming at performance
* EVERY test method is to be documented with a detailed KDoc describing the use case
