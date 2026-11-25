# Module Definition

The first part of every ASN.1 module (ITU-T X.680, clause 13) is the module header:

```asn1
MyModule { iso(1) identified-organization(3) dod(6) internet(1) private(4) 1 }
DEFINITIONS XER INSTRUCTIONS AUTOMATIC TAGS EXTENSIBILITY IMPLIED ::= BEGIN
    -- module body
END
```

## Syntax

| Part                | Variants                                                                       |
|---------------------|--------------------------------------------------------------------------------|
| Module name         | Starts with an upper case letter; single hyphens allowed, none at the end      |
| Object identifier   | Optional, in braces; at least one component                                    |
| Encoding reference  | Optional, e.g. `XER INSTRUCTIONS`                                              |
| Tag default         | Optional: `EXPLICIT TAGS`, `IMPLICIT TAGS` or `AUTOMATIC TAGS`                 |
| Extension default   | Optional: `EXTENSIBILITY IMPLIED`                                              |

The order of the defaults is: encoding reference, tag default, extension default. A wrong order or a repeated
default is reported as error. Comments (`-- ...` and nested `/* ... */`) and line breaks are allowed between all
parts.

The module body is accepted without checks for now.

## Object Identifier

The object identifier of a module is a path in the worldwide tree of identifiers. Every component is one of:

| Form            | Example                  | Meaning                                                           |
|-----------------|--------------------------|-------------------------------------------------------------------|
| Number form     | `1`                      | The number of the arc. Only the number identifies the arc         |
| Name and number | `iso(1)`                 | Number with a readable name; the name is a label for humans       |
| Name form       | `iso`                    | Name only; allowed for arcs whose number is fixed by ITU-T X.660  |

The three root arcs are `itu-t(0)`, `iso(1)` and `joint-iso-itu-t(2)`. Below them the names are assigned by the
owner of the respective arc, for example `identified-organization(3)`, `dod(6)` and `internet(1)`.

The object identifier value is described as a separate rule, so it can be reused wherever an object identifier
value is needed.

## Highlighting

The colors are taken from the standard color scheme (the Java / Kotlin look) and can be changed in
*Settings → Editor → Color Scheme → ASN.1*.

| Element                                  | Color of                   |
|------------------------------------------|----------------------------|
| Keywords (`DEFINITIONS`, `BEGIN`, ...)   | Keyword                    |
| `::=`                                    | Operator                   |
| Numbers                                  | Number                     |
| Module name                              | Class name                 |
| Arc names of an object identifier        | Instance field             |
| Comments                                 | Line / block comment       |
| `{` `}` and `(` `)`                      | Braces                     |

Curly braces and parentheses are treated alike: they share one color and the matching partner is highlighted.

## Completion

Behind `DEFINITIONS` (and behind `INSTRUCTIONS`) the three tag defaults `EXPLICIT TAGS`, `IMPLICIT TAGS` and
`AUTOMATIC TAGS` are offered. Typing the start of one of them completes the whole phrase. Nothing is offered if the
module header already has a tag default.

The structural keywords are offered where they are missing: `DEFINITIONS` behind the module name or its object
identifier, `BEGIN` behind `::=` and `END` behind `BEGIN` or inside the module body. A keyword that the module
contains already is not offered again, and nothing is offered inside comments.

## Incomplete Text

The editor copes with incomplete text at any time while typing: what is missing is reported as error, the rest of
the file keeps its highlighting and structure.
