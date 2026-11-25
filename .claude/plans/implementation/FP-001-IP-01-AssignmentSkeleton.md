# Plan: FP-001 IP-01 Zuweisungsgerüst

* Feature Plan: `.claude/plans/features/FP-001-Assignments.md`
* Abhängigkeiten: keine

## Aufgabe 1: Parser-Hilfsklasse

* Neue Klasse `AsnParserUtil` im Paket `language.parser`, in `AsnGrammar.bnf` per `parserUtilClass` einbinden
* Externe Regel `comma`: `OTHER_SYMBOL` mit Text `,`
* Externe Regel `semicolon`: `OTHER_SYMBOL` mit Text `;`
* Externe Regel `atAssignmentStart`: Lookahead auf `UPPER_REFERENCE ASSIGN` oder `IDENTIFIER … ASSIGN`
* Lookahead bricht bei `BEGIN`, `END` oder `}` ab; keine Exceptions bei Dateiende
* Lookahead nutzt ausschließlich `PsiBuilder.lookAhead`, kein Verbrauch von Tokens

## Aufgabe 2: Grammatik

* `moduleBody ::= assignment*` in `AsnGrammar.bnf` ersetzt den Platzhalter `bodyToken*`
* `assignment ::= typeAssignment | valueAssignment | assignmentJunk`
* `typeAssignment ::= UPPER_REFERENCE ASSIGN typeNotation` mit `pin=2`
* `valueAssignment ::= IDENTIFIER typeNotation ASSIGN valueNotation` mit `pin=3`
* `typeNotation` und `valueNotation` zunächst Platzhalter: Tokens bis `atAssignmentStart` oder `END`
* Klammertiefe im Platzhalter beachten; `{` und `}` bleiben paarweise zusammen
* `assignmentJunk` schluckt verirrte Tokens bis `atAssignmentStart` oder `END` mit `recoverWhile`
* `END` beendet den Body weiterhin; `bodyToken` bleibt für Modulkopf-Wiederaufsetzen erhalten
* Parser neu generieren (`generateAsnParser`)

## Aufgabe 3: PSI und Annotator

* Generierte Interfaces `AsnTypeAssignment` und `AsnValueAssignment` prüfen; keine Handarbeit am generierten Code
* `AsnAnnotator`: Prüfung verirrter Tokens im Body an neue Struktur anpassen
* Annotator meldet `assignmentJunk` als Fehler mit I18N-Text in `PluginBundle.properties`
* Annotator darf bei fehlenden Kindelementen nie werfen

## Aufgabe 4: Tests

* `AsnAssignmentParserTest`: Typzuweisung, Wertzuweisung, mehrere Zuweisungen, Kommentare dazwischen
* `AsnAssignmentRobustnessTest`: jeder Präfix und Suffix eines vollständigen Moduls deckt den ganzen Text ab
* Zusätzlich: leere Datei, nur Kommentar, nur `{`, Text endet im Kommentar, `Name ::=` ohne Rest
* Fehlerfall: Zuweisung mit fehlendem `::=` zerstört die folgende Zuweisung nicht
* `AsnAnnotatorTest` um Junk im Body erweitern
* Bestehende Parser-Tests auf geänderten Body prüfen und anpassen
* Jede Testmethode mit ausführlichem KDoc im Stil „Use case / Expected“

## Aufgabe 5: Dokumentation und Abschluss

* KDoc für alle neuen öffentlichen Member, vor allem `AsnParserUtil`
* `README.md`: Zeile „Module body“ auf `Partial` setzen
* `docs/docs/module-definition.md`: Satz zum ungeprüften Body ersetzen
* `CHANGELOG.md`: Eintrag unter `[UNRELEASED]`
* `plugin.xml`: Feature-Liste prüfen und ergänzen
* Status in `FP-001-Assignments-Status.md` aktualisieren
* Neue Dateien mit `git add` aufnehmen, kein Commit
* Gradle-Target `build` über einen Agenten ausführen; `verifyPlugin` nie
