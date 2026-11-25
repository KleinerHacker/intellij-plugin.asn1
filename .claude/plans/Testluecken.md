# Plan: Testlücken schließen und OID separat testen

## Task 1: Eigene Testklasse `AsnObjectIdentifierValueTest`

* Neue Klasse in `language/src/test/kotlin/.../language/parser/`, parst die Regel `objectIdentifierValue` direkt ohne Modul
* Hilfsfunktion `parseValue` aus `AsnParserTest.testObjectIdentifierValue` übernehmen und dorthin entfernen
* Test je Komponentenform: nur Zahl, nur Name, Name mit Zahl, Name mit Defined Value, nur Defined Value
* Test gemischte und verschachtelte Werte, etwa `{ iso(1) 3 internet other(some-value) }`
* Test aller drei Wurzelbögen `itu-t`, `iso`, `joint-iso-itu-t` in Namens- und Name-mit-Zahl-Form
* Test Zahlenrandfälle: `0`, lange Zahl, führende Null (Parser ohne Fehler)
* Fehlerfälle: `{ }`, `{ iso(1`, `{ iso(`, `{ iso(1 3 }`, `{ 1 2`, fehlende öffnende Klammer
* Jeder Fehlerfall: Text vollständig im Baum, bereits getippter Teil bleibt erhalten

## Task 2: Fehlertoleranz der OID-Regel

* Test: jeder Präfix und jeder Suffix eines vollständigen Wertes parst ohne Exception und deckt den ganzen Text ab
* Zusätzlich: leerer Text, nur `{`, nur `}`, Text endet im Kommentar

## Task 3: Lücken im Modulkopf (`AsnParserTest`)

* Parametrisierter Test über alle Teilmengen und Reihenfolgen der drei Defaults (Encoding, Tag, Extension)
* Alle drei Tags mit Encoding-Default und mit Extension-Default
* Encoding-Default mit Extension-Default ohne Tags
* Definitive Identifier mit allen drei Wurzelbögen im Modulkopf
* Definitive Identifier: `0`, lange Zahl, Namen mit Bindestrich
* Fehlerfall: `iso(wert)` mit Namen statt Zahl im Definitive Identifier wird als Fehler gemeldet

## Task 4: Abschluss

* KDoc mit Anwendungsfall und Erwartung an jeder Testmethode
* Neue Datei mit `git add` aufnehmen, kein Commit
* README, `plugin.xml`, MkDocs und CHANGELOG prüfen; reine Teständerung, voraussichtlich keine Anpassung nötig
* Gradle-Target `build` über einen Agenten ausführen, `verifyPlugin` nie
