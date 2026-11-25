---
name: architecture
---

# Architecture

* There are three libraries with the following structure and meaning:
    * `plugin` - The base plugin JAR for ASN.1; contains ONLY IDE specific extensions apart from the language itself (e.g. settings, build / run configurations, tool windows); depends on `language`
    * `language` - The language plugin JAR for ASN.1; contains ONLY the language itself (lexer, parser, PSI, syntax highlighting and language related editor features); MUST NEVER depend on `plugin`
    * `buildSrc` - Additional things for Gradle functionality

## Structure

* The root package is ALWAYS `org.pcsoft.intellij.plugin.asn1`
    * For `plugin` ALWAYS the root
    * For `language` root + `language`
* Below that there are specialised packages for:
    * `language` - Language units such as parsers, editor features, ...
    * `services` - Injectable services (IntelliJ Platform)
    * `types` - General types
    * `settings` - Settings features, settings pages, ...
    * `build` - Everything around building ASN.1, e.g. run config, building, ...
* The `*Icons` class collecting all icons of a module MUST ALWAYS reside on the package root level of that
  module, NOT in a specialised package
* For any deviation the user MUST be asked
