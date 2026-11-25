# Plan: FP-001 IP-02 Typnotation

* Feature Plan: `.claude/plans/features/FP-001-Assignments.md`
* Abhängigkeiten: IP-01

## Aufgabe 1: Lexer

* Keywords ergänzen: `BOOLEAN`, `INTEGER`, `ENUMERATED`, `REAL`, `BIT`, `STRING`, `OCTET`, `NULL`, `OBJECT`, `IDENTIFIER`-Keyword-Konflikt prüfen
* Weitere Keywords: `RELATIVE-OID`, `SEQUENCE`, `SET`, `CHOICE`, `OF`, `COMPONENTS`, `OPTIONAL`, `DEFAULT`
* Zeichenketten-Typen: `UTF8String`, `IA5String`, `PrintableString`, `NumericString`, `VisibleString`, `BMPString` und weitere
* Zeit-Typen: `UTCTime`, `GeneralizedTime`, `DATE`, `TIME`, `DATE-TIME`, `DURATION`, `TIME-OF-DAY`
* Tag-Keywords: `UNIVERSAL`, `APPLICATION`, `PRIVATE` sowie `EXPLICIT`, `IMPLICIT`, `AUTOMATIC` wiederverwenden
* Token `ELLIPSIS` für `...`; `::` bleibt ohne Sonderbehandlung
* Neue Keywords vor `{UPPER_REFERENCE}` einsortieren; Lexer bleibt total
* `AsnLexerTest` um alle neuen Tokens erweitern

## Aufgabe 2: Einfache und eingebaute Typen

* `typeNotation ::= builtinType | referencedType | taggedType` in `AsnGrammar.bnf`
* Eigene Regeln für `booleanType`, `nullType`, `realType`, `objectIdentifierType`, `relativeOidType`
* `integerType` mit optionaler Namensliste `{ name(1), … }`
* `enumeratedType` mit `enumerations` und optionalem `ELLIPSIS`
* `bitStringType` mit optionaler Namensliste; `octetStringType`
* `characterStringType` als Auswahl aller Zeichenketten-Keywords; `timeType` analog
* `referencedType ::= UPPER_REFERENCE`, später in IP-05 mit Referenz versehen
* Jede Regel mit `pin` hinter dem Schlüsselwort und `recoverWhile` bis Zuweisungsanfang

## Aufgabe 3: Strukturtypen

* `sequenceType` und `setType` mit `{ componentTypeLists }`
* `choiceType` mit `{ alternativeTypeLists }`
* `componentType ::= namedType (OPTIONAL | DEFAULT valueNotation)? | COMPONENTS OF typeNotation`
* `namedType ::= IDENTIFIER typeNotation`
* Erweiterungsmarker `ELLIPSIS` mit optionaler Exception-Spezifikation und Versionsklammern `[[ … ]]`
* `sequenceOfType` und `setOfType` mit optionalem benanntem Element
* `DEFAULT`-Wert bleibt bis IP-03 Platzhalter `valueNotation`
* Komma-Trennung über `comma` aus `AsnParserUtil`; Rekursion über `{}` fehlertolerant

## Aufgabe 4: Tagging

* `taggedType ::= tag (IMPLICIT | EXPLICIT)? typeNotation`
* `tag ::= '[' (UNIVERSAL | APPLICATION | PRIVATE)? classNumber ']'`
* `classNumber` als `NUMBER` oder `definedValue`
* Eckige Klammern als `OTHER_SYMBOL` über externe Regeln `lbracket` und `rbracket`
* Zuweisungsanfang-Lookahead berücksichtigt Klammertiefe `[ ]` nicht fälschlich
* Platzhalter-`typeNotation` aus IP-01 entfällt vollständig

## Aufgabe 5: Tests

* `AsnTypeNotationParserTest`: je eingebautem Typ ein gültiges und ein kaputtes Beispiel
* `AsnStructuredTypeParserTest`: verschachtelte `SEQUENCE`, `CHOICE`, `SET OF`, Erweiterungsmarker, `OPTIONAL`, `DEFAULT`
* `AsnTaggingParserTest`: Tags mit und ohne Klasse und Modus
* `AsnTypeRobustnessTest`: jeder Präfix und Suffix eines großen Beispielmoduls deckt den ganzen Text ab
* Zusätzlich: leere Datei, nur Kommentar, nur `{`, Text endet im Kommentar, `SEQUENCE {` ohne Ende
* Jede Testmethode mit ausführlichem KDoc im Stil „Use case / Expected“
* Bestehende Tests auf geändertes Keyword-Verhalten prüfen

## Aufgabe 6: Dokumentation und Abschluss

* KDoc für alle neuen öffentlichen Member
* `README.md`: Tabelle „Type notation“ ergänzen und Status setzen
* `docs/docs/`: neue Seite `types.md` anlegen, `mkdocs.yml` ergänzen
* `plugin.xml`: Feature-Liste ergänzen
* `CHANGELOG.md`: Eintrag unter `[UNRELEASED]`
* Status in `FP-001-Assignments-Status.md` aktualisieren
* Neue Dateien mit `git add` aufnehmen, kein Commit
* Gradle-Target `build` über einen Agenten ausführen; `verifyPlugin` nie
