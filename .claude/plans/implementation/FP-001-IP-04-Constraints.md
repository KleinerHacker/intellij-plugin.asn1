# Plan: FP-001 IP-04 Constraints

* Feature Plan: `.claude/plans/features/FP-001-Assignments.md`
* Abhängigkeiten: IP-02

## Aufgabe 1: Lexer

* Token `RANGE` für `..`; `ELLIPSIS` aus IP-02 wiederverwenden; Reihenfolge `...` vor `..`
* Keywords `SIZE`, `INCLUDES`, `PATTERN`, `EXCEPT`, `WITH`, `COMPONENT`, `ALL`, `UNION`, `INTERSECTION`
* `FROM`, `MIN`, `MAX` aus IP-02/IP-03 wiederverwenden
* Zeichen `|`, `^`, `<`, `!`, `(`, `)` bleiben `OTHER_SYMBOL` bzw. Klammer-Token
* Lexer bleibt total; `AsnLexerTest` um neue Tokens erweitern

## Aufgabe 2: Constraint-Grundstruktur

* `constraint ::= '(' constraintSpec exceptionSpec? ')'` hinter `typeNotation` in `AsnGrammar.bnf`
* Mehrere Constraints hintereinander an einem Typ erlauben
* `constraintSpec ::= elementSetSpecs`; `elementSetSpecs` mit optionalem `ELLIPSIS` und Zusatzmenge
* `unions ::= intersections (('|' | UNION) intersections)*`
* `intersections ::= elements (('^' | INTERSECTION) elements)*`
* `elements ::= subtypeElements | '(' elementSetSpec ')'` mit `EXCEPT`- und `ALL EXCEPT`-Zweig
* `exceptionSpec ::= '!' (typeNotation ':')? valueNotation`
* `pin` und `recoverWhile` bis schließende Klammer oder Zuweisungsanfang

## Aufgabe 3: Subtype-Elemente

* `singleValue ::= valueNotation`
* `valueRange ::= endpoint RANGE endpoint` mit `MIN`, `MAX` und offenen Grenzen über `<`
* `sizeConstraint ::= SIZE constraint`
* `permittedAlphabet ::= FROM constraint`
* `containedSubtype ::= INCLUDES? typeNotation`
* `patternConstraint ::= PATTERN valueNotation`
* `typeConstraint` für Typ als Constraint-Element
* Enthaltene Werte nutzen `valueNotation` aus IP-03

## Aufgabe 4: Inner Subtyping

* `innerTypeConstraints ::= WITH COMPONENT constraint`
* `WITH COMPONENTS '{' componentConstraintList '}'` mit optionalem Partial-Marker `ELLIPSIS`
* `namedConstraint ::= IDENTIFIER constraint? presenceConstraint?`
* `presenceConstraint ::= PRESENT | ABSENT | OPTIONAL`
* Keywords `PRESENT` und `ABSENT` im Lexer ergänzen
* Verschachtelung mit Klammertiefe fehlertolerant behandeln

## Aufgabe 5: Wertemengen-Zuweisung

* `valueSetTypeAssignment` aus IP-03 auf `elementSetSpecs` aus diesem Plan umstellen
* Platzhalter-Werteliste entfällt
* Annotator: Erweiterungsmarker mehrfach im selben Constraint melden

## Aufgabe 6: Tests

* `AsnConstraintParserTest`: je Subtype-Element gültig und kaputt, Mengenoperationen, Exception-Spezifikation
* `AsnInnerSubtypeParserTest`: `WITH COMPONENT(S)` mit und ohne Partial-Marker
* `AsnConstraintRobustnessTest`: jeder Präfix und Suffix eines Beispielmoduls deckt den ganzen Text ab
* Zusätzlich: leere Datei, nur Kommentar, nur `{`, Text endet im Kommentar, `INTEGER (1..` ohne Ende
* `AsnLexerTest`: `...` und `..` korrekt getrennt
* Jede Testmethode mit ausführlichem KDoc im Stil „Use case / Expected“

## Aufgabe 7: Dokumentation und Abschluss

* KDoc für alle neuen öffentlichen Member
* `README.md`: Tabelle „Constraints“ ergänzen und Status setzen
* `docs/docs/`: Seite `constraints.md` anlegen, `mkdocs.yml` ergänzen
* `plugin.xml`: Feature-Liste ergänzen
* `CHANGELOG.md`: Eintrag unter `[UNRELEASED]`
* Status in `FP-001-Assignments-Status.md` aktualisieren
* Neue Dateien mit `git add` aufnehmen, kein Commit
* Gradle-Target `build` über einen Agenten ausführen; `verifyPlugin` nie
