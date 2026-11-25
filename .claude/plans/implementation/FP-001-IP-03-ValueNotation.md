# Plan: FP-001 IP-03 Wertnotation

* Feature Plan: `.claude/plans/features/FP-001-Assignments.md`
* Abhängigkeiten: IP-02

## Aufgabe 1: Lexer

* Token `CSTRING` für `"…"` mit `""` als Escape; unterminiert läuft bis Dateiende
* Token `BSTRING` für `'0101'B` und `HSTRING` für `'FF'H`; unterminiert fehlertolerant
* Token `REAL_NUMBER` für Dezimalzahlen mit Punkt und Exponent
* Keywords `TRUE`, `FALSE`, `PLUS-INFINITY`, `MINUS-INFINITY`, `NOT-A-NUMBER`, `MIN`, `MAX`
* `-` vor Zahlen als `OTHER_SYMBOL`; Vorzeichen entscheidet der Parser
* Lexer bleibt total; `AsnLexerTest` um alle neuen Tokens erweitern

## Aufgabe 2: Einfache Werte

* `valueNotation ::= builtinValue | definedValue | objectIdentifierValue` in `AsnGrammar.bnf`
* `booleanValue`, `nullValue`, `integerValue` mit optionalem Minus, `realValue`
* `stringValue` für `CSTRING`, `BSTRING`, `HSTRING`
* `definedValue ::= IDENTIFIER` bleibt; Referenzierung folgt in IP-05
* `objectIdentifierValue` aus dem Modulkopf wiederverwenden
* Platzhalter-`valueNotation` aus IP-01 entfällt
* Jede Regel mit `pin` und `recoverWhile` bis Zuweisungsanfang

## Aufgabe 3: Strukturwerte

* `braceValue ::= '{' valueList? '}'` als typunabhängige Struktur, da der Typ beim Parsen unbekannt ist
* Elemente: `namedValue ::= IDENTIFIER valueNotation`, einfache Werte, verschachtelte `braceValue`
* `choiceValue ::= IDENTIFIER ':' valueNotation`
* `realValue` in Mantissen-Form `{ mantissa 1, base 10, exponent 0 }` als `braceValue`
* `objectIdentifierValue` und `braceValue` eindeutig abgrenzen: OID-Form nur direkt hinter `OBJECT IDENTIFIER`
* Komma-Trennung über `comma`; geschweifte Klammern bleiben paarweise im Wiederaufsetzen

## Aufgabe 4: Wertzuweisung vollständig

* `valueAssignment` nutzt echtes `typeNotation` aus IP-02 und echtes `valueNotation`
* `valueSetTypeAssignment ::= UPPER_REFERENCE typeNotation ASSIGN '{' elementSetSpecs? '}'` als Gerüst
* Element-Mengen-Inhalt bleibt Werteliste; Constraints folgen in IP-04
* `atAssignmentStart` aus `AsnParserUtil` auf neue Struktur prüfen und anpassen
* Annotator: führende Nullen und Zahlenprüfung auf `integerValue` anwenden

## Aufgabe 5: Tests

* `AsnValueNotationParserTest`: jeder einfache Wert gültig und kaputt
* `AsnStructuredValueParserTest`: verschachtelte Werte, `choiceValue`, leere Klammern, Real-Mantissenform
* `AsnLexerTest`: Strings mit `""`, unterminierte Strings und Hex-/Bit-Strings
* `AsnValueRobustnessTest`: jeder Präfix und Suffix eines Beispielmoduls deckt den ganzen Text ab
* Zusätzlich: leere Datei, nur Kommentar, nur `{`, Text endet im Kommentar, `x INTEGER ::=` ohne Wert
* Jede Testmethode mit ausführlichem KDoc im Stil „Use case / Expected“

## Aufgabe 6: Dokumentation und Abschluss

* KDoc für alle neuen öffentlichen Member
* `README.md`: Tabelle „Value notation“ ergänzen und Status setzen
* `docs/docs/types.md` um Werte erweitern oder neue Seite `values.md` anlegen
* `plugin.xml`: Feature-Liste ergänzen
* `CHANGELOG.md`: Eintrag unter `[UNRELEASED]`
* Status in `FP-001-Assignments-Status.md` aktualisieren
* Neue Dateien mit `git add` aufnehmen, kein Commit
* Gradle-Target `build` über einen Agenten ausführen; `verifyPlugin` nie
