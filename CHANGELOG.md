<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# ASN.1 Changelog

## [Unreleased]

### Added

- **ASN.1 file type.** Files with the extensions `.asn` and `.asn1` are recognised as ASN.1 files and shown with
  a dedicated icon.
- **Module definition.** The module header (`Name { object identifier } DEFINITIONS ... ::= BEGIN ... END`) is
  parsed, including all forms of the object identifier and the encoding, tag and extension defaults. A wrong order
  or a repeated default and numbers with leading zeros are reported. The parser copes with incomplete text.
- **Syntax highlighting.** Keywords, `::=`, numbers, comments, braces, the module name and the arcs of the object
  identifier are highlighted with the colors of the standard color scheme; the colors can be changed in the color
  settings (*ASN.1*).
- **Brace matching.** `{` `}` and `(` `)` are matched.
- **Completion of the tag default.** Behind `DEFINITIONS` the tag defaults `EXPLICIT TAGS`, `IMPLICIT TAGS` and
  `AUTOMATIC TAGS` are offered.
- **Completion of module keywords.** `DEFINITIONS` behind the module name, `BEGIN` behind `::=` and `END` inside
  the module body are offered as long as the module does not contain them yet.
- **New file icon.** The icon of ASN.1 files has been redesigned.
- **New plugin logo.** The plugin icon, the readme and the documentation use the new ASN.1 logo; the documentation
  colour scheme (light and dark mode) follows the logo.
