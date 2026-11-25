# Plan: OID-Registry als YAML mit Auto-Completion

## Task 1: JSON Schema und YAML-Datei

* `language/src/main/resources/oid/oid-registry.schema.json` anlegen (JSON Schema Draft 2020-12)
* Schema beschreibt rekursiven Knoten: `name` (Kürzel), `number`, `description`, optional `children`
* Schema fordert Pflichtfelder und eindeutige `number` sowie eindeutigen `name` je Ebene
* `language/src/main/resources/oid/oid-registry.yaml` anlegen, Kopfzeile verweist per `yaml-language-server` auf das Schema
* Wurzelbögen `itu-t(0)`, `iso(1)`, `joint-iso-itu-t(2)` eintragen
* Darunter nur belegbare Bögen, etwa `iso.org(3).dod(6).internet(1)` und `joint-iso-itu-t.asn1(1)`
* Jeder Eintrag mit Quellenangabe der Norm (X.660 bzw. X.680) im Kommentar
* Beschreibungen in Englisch

## Task 2: Kotlin Data Classes

* Paket `org.pcsoft.intellij.plugin.asn1.language.types` im Modul `language`
* `AsnOidNode` mit `name`, `number`, `description`, `children` (Standard: leere Liste)
* `AsnOidRegistry` mit Liste der Wurzelknoten
* Hilfsfunktion `AsnOidNode.child(nameOrNumber)` liefert `null` statt Exception bei unbekanntem Bogen
* Hilfsfunktion `AsnOidRegistry.resolve(path)` löst Pfad aus Namen und Zahlen auf, `null` bei Fehlschlag
* KDoc an jedem öffentlichen Member

## Task 3: Laden der Registry

* Service `AsnOidRegistryService` im Paket `language.services` (Application Service)
* Lädt `oid-registry.yaml` per Jackson YAML (bereits im Versionskatalog) aus dem Classpath
* Ergebnis wird einmal gecacht
* Fehlende oder defekte Datei ergibt leere Registry und Log-Eintrag, keine Exception
* Service in `asn-1.language.xml` registrieren
* Abhängigkeit `jackson-yaml` und `jackson-kotlin` im Modul `language` prüfen und ergänzen

## Task 4: Auto-Completion

* Neuer `CompletionProvider` `ObjIdArcProvider` in `AsnCompletionContributor`
* Position gilt als OID-Position innerhalb von `DEFINITIVE_OBJ_ID_COMPONENT` oder `OBJ_ID_COMPONENTS`
* Bereits getippte Komponenten davor werden als Pfad gelesen: Name, Zahl oder Name mit Zahl
* Pfad wird gegen die Registry aufgelöst, angeboten werden die Kinder des gefundenen Knotens
* Ohne vorherige Komponente werden die Wurzelbögen angeboten
* Einfügetext `name(number)`, Typtext zeigt die Beschreibung, Tail-Text zeigt die Nummer
* Unbekannter Pfad oder unvollständiger Baum ergibt keine Vorschläge, keine Exception
* Keine `!!`, keine ungeprüften Casts, `ProcessCanceledException` nicht fangen

## Task 5: Tests

* Registry-Test: YAML wird geladen, Wurzelbögen und eine tiefere Ebene sind vorhanden
* Schema-Test: Datei erfüllt das Schema, Namen und Nummern je Ebene sind eindeutig
* Test `resolve`: Namen, Zahlen, Name mit Zahl, unbekannter Bogen, leerer Pfad
* Completion-Test: Wurzelbögen im Modulkopf und im OID-Wert
* Completion-Test: Kinder von `iso`, `iso(1) org`, `{ iso 3 6 }` und tieferen Ebenen
* Completion-Test: unbekannter Pfad, leerer Text, nur `{`, Text endet im Kommentar
* Fehlertoleranz: jeder Präfix eines vollständigen Moduls completet ohne Exception
* KDoc mit Anwendungsfall an jeder Testmethode

## Task 6: Regel zum Planen in `development.md`

* Abschnitt "Planning" in `.claude/rules/development.md` ergänzen, Text in Englisch
* Regel: Entweder Plan schreiben UND Plan-Modus betreten, oder KEINEN Plan schreiben und direkt umsetzen
* Regel: Eine Plan-Datei NIEMALS außerhalb des Plan-Modus schreiben
* Regel: Im Plan-Modus wird die Plan-Datei geschrieben und in `.claude/plans` abgelegt
* Bestehende Zeile "Do NOT switch to plan mode right away" bleibt unverändert
* Datei mit `git add` aufnehmen

## Task 7: Abschluss

* Plan-Datei `.claude/plans/OID-Registry-Completion.md` mit dieser Fassung überschreiben, da vorzeitig außerhalb des Plan-Modus entstanden
* Neue Dateien mit `git add` aufnehmen, kein Commit
* README (Implementation Status), `plugin.xml`, MkDocs `module-definition.md` und CHANGELOG unter `[UNRELEASED]` anpassen
* Neue UI-Texte in `PluginBundle.properties`
* Gradle-Target `build` über einen Agenten ausführen, `verifyPlugin` nie
