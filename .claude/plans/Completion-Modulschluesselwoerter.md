# Plan: Auto Completion für DEFINITIONS, BEGIN und END

## Task 1: Provider in `AsnCompletionContributor`

* Neuer Provider `ModuleKeywordProvider` im selben Contributor, neben `TagDefaultProvider`, Registrierung im `init`-Block
* Pro Schlüsselwort eine Positionsfunktion: `isDefinitionsPosition`, `isBeginPosition`, `isEndPosition`
* Wiederverwendung von `previousSignificantLeaf` (Kommentare und Whitespace werden übersprungen), dafür auf Dateiebene teilen
* `DEFINITIONS`: vorheriges Token ist Modulname oder schließende `}` der OID, Modul hat noch kein `DEFINITIONS`
* `BEGIN`: vorheriges Token ist `::=`, Modul hat noch kein `BEGIN`
* `END`: vorheriges Token ist `BEGIN` oder Position liegt im Modulrumpf, Modul hat noch kein `END`
* Kein Vorschlag in Kommentaren, am Dateianfang (Modulname) und in der OID
* Alle Positionen arbeiten am unvollständigen Baum mit Dummy-Bezeichner, ohne `!!` und ohne Annahme über Kinder
* Hinweistext je Schlüsselwort über I18N: `completion.keyword.definitions`, `.begin`, `.end` in `PluginBundle.properties`

## Task 2: Tests (`AsnCompletionContributorTest`)

* `DEFINITIONS` nach Modulname, nach OID, nach OID mit Kommentar und Zeilenumbruch
* `DEFINITIONS` nicht, wenn bereits vorhanden, am Dateianfang, in der OID, im Rumpf
* `BEGIN` nach `::=`, mit Kommentar dazwischen, nicht doppelt, nicht vor `::=`
* `END` nach `BEGIN`, im Rumpf nach einer Definition, nicht doppelt, nicht im Kopf
* Präfix-Eingabe vervollständigt das Schlüsselwort, etwa `DEF`, `BEG`, `EN`
* Unvollständiger Text: Datei endet nach Modulname, nach `::=`, nach `BEGIN`, leere Datei, offener Kommentar
* Bestehende Tag-Default-Tests bleiben unverändert grün
* Jede Testmethode mit ausführlichem KDoc

## Task 3: Dokumentation und Abschluss

* KDoc der Klasse `AsnCompletionContributor` um die neuen Vorschläge erweitern
* `plugin.xml`, `README.md` (Features, Implementation Status), `docs/docs/module-definition.md`: Completion ergänzen
* `CHANGELOG.md`: Eintrag unter `[Unreleased]`
* Plan als `.claude/plans/Completion-Modulschluesselwoerter.md` ablegen, mit `git add` aufnehmen, kein Commit
* Gradle-Target `build` über einen Agenten ausführen, `verifyPlugin` nie
