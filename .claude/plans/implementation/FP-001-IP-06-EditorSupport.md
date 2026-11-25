# Plan: FP-001 IP-06 Editor-Unterstützung für Body

* Feature Plan: `.claude/plans/features/FP-001-Assignments.md`
* Abhängigkeiten: IP-02, IP-03, IP-05

## Aufgabe 1: Syntaxhervorhebung

* `AsnSyntaxHighlighter`: neue Keywords, Typ-Keywords, Strings, Zahlen und Trennzeichen zuordnen
* Neue `TextAttributesKey` für Typnamen, Wertnamen, Strings, Zahlen
* `AsnColorSettingsPage` um neue Attribute mit I18N-Namen erweitern
* Semantische Hervorhebung von Definitionen und Referenzen im `AsnAnnotator`
* Annotator liest nur vorhandene Kindelemente; keine Exception bei unvollständigem Baum

## Aufgabe 2: Strukturansicht

* `AsnStructureViewFactory` mit `StructureViewTreeElement` für Modul und Zuweisungen
* Anzeige: Name, Art (Typ oder Wert) und Icon
* Neue Icons nur in der Klasse `AsnIcons` auf Paketwurzel-Ebene ergänzen
* Registrierung als `lang.psiStructureViewFactory` in `asn-1.language.xml`
* Leere Module und Zuweisungen ohne Namen werden ohne Fehler angezeigt

## Aufgabe 3: Faltung

* `AsnFoldingBuilder` für geschweifte Klammern von Strukturtypen und Werten
* Block-Kommentare ebenfalls faltbar
* Platzhaltertext aus I18N oder kurzer fester Text `{...}`
* Registrierung als `lang.foldingBuilder`
* Nur gültig geschlossene Bereiche falten; unvollständige Klammern ignorieren

## Aufgabe 4: Prüfungen im Annotator

* Doppelte Definitionen gleicher Art im Modul melden
* Nicht auflösbare Typ- und Wertreferenzen als Warnung melden
* Unvollständige Zuweisung (fehlender Typ oder Wert) als Fehler melden
* Alle Meldungstexte aus `PluginBundle.properties`
* Prüfungen laufen nur bei vorhandenem Index (`DumbService`-sicher)

## Aufgabe 5: Tests

* `AsnSyntaxHighlighterTest`: neue Token-Typen und Attribute
* `AsnStructureViewTest`: Struktur für Modul mit mehreren Zuweisungen und kaputtem Text
* `AsnFoldingBuilderTest`: Strukturtypen, Werte, Kommentare, unvollständige Klammern
* `AsnAnnotatorTest`: Duplikate, unbekannte Referenz, unvollständige Zuweisung
* Zusätzlich: leere Datei, nur Kommentar, nur `{`, Text endet im Kommentar
* Jede Testmethode mit ausführlichem KDoc im Stil „Use case / Expected“

## Aufgabe 6: Dokumentation und Abschluss

* KDoc für alle neuen öffentlichen Member
* `README.md`: Editor Features aktualisieren
* `docs/docs/`: Seite zu Editor-Funktionen anlegen oder erweitern, `mkdocs.yml` ergänzen
* `plugin.xml`: Feature-Liste ergänzen
* `CHANGELOG.md`: Einträge unter `[UNRELEASED]`
* Status in `FP-001-Assignments-Status.md` aktualisieren; bei Abschluss Overview-Datei entfernen
* Neue Dateien mit `git add` aufnehmen, kein Commit
* Gradle-Target `build` über einen Agenten ausführen; `verifyPlugin` nie
