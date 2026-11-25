# ASN.1 – JetBrains Plugin

**Language support for ASN.1 module definition files (`.asn`, `.asn1`) in all JetBrains IDEs.**

!!! note "AI transparency notice"

    The code and documentation of this project were largely created with AI assistance
    (transparency notice pursuant to the EU AI Act). The object identifier registry used for completion
    (`oid-registry.yaml`) was compiled with AI assistance and checked against public OID repositories; it is
    incomplete, so verify an arc against the official registry before relying on it.

---

## What is ASN.1?

[ASN.1](https://www.itu.int/rec/T-REC-X.680) (Abstract Syntax Notation One) is a standardised notation by the ITU-T
(X.680) to describe data structures independent of their encoding. It is widely used in telecommunication,
cryptography (e.g. X.509 certificates) and network protocols (e.g. SNMP, LDAP).

---

## What this Plugin does

The plugin brings language support for ASN.1 to any JetBrains IDE. The current state is the basic structure and the
module header:

- **File types** — `.asn` and `.asn1` files are recognised and shown with a dedicated icon
- **Module definition** — parsing of the module header including the object identifier, syntax highlighting,
  brace matching and completion of the tag default (`EXPLICIT TAGS`, `IMPLICIT TAGS`, `AUTOMATIC TAGS`), of the module keywords
  `DEFINITIONS`, `BEGIN` and `END` and of the known object identifier arcs

See [File Types](file-types.md) and [Module Definition](module-definition.md) for details. The full list of supported features is kept in the
[README](https://github.com/KleinerHacker/intellij-plugin.asn1#implementation-status).
