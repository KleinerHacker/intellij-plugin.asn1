# Parsing

## Error tolerance

* Every part of the language support (lexer, parser, PSI access, annotators, completion, highlighting,
  brace matching) MUST ALWAYS expect incomplete or invalid text, at ANY time while the user is typing
    * NEVER throw an exception because of incomplete or invalid input
    * NEVER make an assumption about the existence of a child element or token (no `!!`, no unchecked casts)
* The lexer MUST be total
    * Every character is assigned to a token, a catch-all rule yields a bad-character token
    * An unterminated construct (e.g. a block comment) runs to the end of the input
* The parser MUST ALWAYS produce a complete PSI tree for ANY text, including an empty file
    * Missing parts are reported as errors, the already typed part stays in the tree
    * Use `pin` and `recoverWhile` so one broken construct does not swallow the rest of the file
* NEVER catch `ProcessCanceledException`

## Tests

* EVERY new grammar rule MUST be covered by tests with incomplete input
    * Every prefix of a complete example MUST parse without exception, and the tree MUST cover the whole text
    * Additionally: empty file, comment only, opening brace only, text ending inside a comment
