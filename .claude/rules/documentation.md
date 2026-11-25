---
name: documentation
---

# Documentation

## Code

* EVERY public member in EVERY source file (except automatically generated source files) is to be documented with KDoc
* EVERY test method is to be documented with a detailed KDoc describing the use case

## Readme

* The readme MUST be checked after every change and adjusted if required
    * For the required sections and the "Implementation Status" use the skill `readme`

# Plugin Description

* Update all supported features ALWAYS in plugin.xml
  * List MUST be up to date

## MkDocs

* The MkDocs documentation under `docs` MUST be checked after every change and adjusted if necessary
    * For structure and conventions use the skill `mkdocs`

## CHANGELOG.md

* A change file MUST be present
* It MUST be updated with the applied changes after a change
    * The changes MUST be visible to the user, otherwise they MUST NOT go into the changelog
* The prescribed format MUST be kept
    * New entries MUST go under `[UNRELEASED]`

## External Documentation

* The ASN.1 specification (ITU-T X.680) is available here: `https://www.itu.int/rec/T-REC-X.680`
