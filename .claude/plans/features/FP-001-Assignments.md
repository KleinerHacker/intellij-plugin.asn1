# Feature Plan: Zuweisungen (Assignments)

## 1. Ziel

* Der Modulbody wird als Liste echter Zuweisungen nach ITU-T X.680 geparst
* Typ- und Wertzuweisungen erscheinen als benannte PSI-Elemente und sind auflösbar
* Grundlage für spätere Features: IMPORTS/EXPORTS mit Symbolreferenzen, Rename, Find Usages

## 2. Ist-Zustand

* `moduleBody ::= bodyToken*` ist ein Platzhalter; alles bis `END` wird unstrukturiert aufgenommen
* Es gibt weder Typ- noch Wertnotation, keine benannten Elemente und keine Index-Infrastruktur
* Lexer kennt nur Modulkopf-Keywords, `UPPER_REFERENCE`, `IDENTIFIER`, `NUMBER`, `OTHER_SYMBOL`
* Completion, Annotator und Dokumentation arbeiten nur auf dem Modulkopf und Object Identifiern

## 3. Soll-Zustand

* `moduleBody ::= assignment*` mit Fehlertoleranz je Zuweisung
* Typen, Werte und Constraints nach X.680 sind im PSI abgebildet
* `typereference` und `valuereference` sind Definitionen mit Referenzen und Completion
* Syntaxhervorhebung, Faltung und Strukturansicht kennen Zuweisungen

## 4. Anforderungen

### Funktionale Anforderungen

* `TypeAssignment`: `Name ::= Type`
* `ValueAssignment`: `name Type ::= Value`
* `ValueSetTypeAssignment`: `Name Type ::= { ... }`
* Eingebaute Typen: BOOLEAN, INTEGER, ENUMERATED, REAL, BIT STRING, OCTET STRING, NULL, OBJECT IDENTIFIER, RELATIVE-OID, SEQUENCE, SET, CHOICE, SEQUENCE OF, SET OF, Zeichenketten-Typen, Zeit-Typen, ANY-artige Typen entfallen
* Tagging mit `[n]`, `IMPLICIT`, `EXPLICIT`; Erweiterungsmarker `...`
* Constraints: Größe, Wertebereich, Alphabet, `WITH COMPONENT(S)`
* Werte: Zahlen, Strings, `TRUE`/`FALSE`, `NULL`, Listen, Choice-Werte, Referenzen
* Fehlertolerant bei jedem Präfix gemäß `parsing.md`

### Technische Anforderungen

* Kotlin, Grammar-Kit, Pakete gemäß `architecture.md`; nur im Modul `language`
* Kein Zugriff über `!!`, keine Exceptions bei unvollständigem Text
* Jede neue Grammatikregel mit Präfix-Tests und Robustheitstests
* Keine veralteten oder internen Plattform-APIs

## 5. Architektur

* Lexer: neue Keywords für Typen und Notation, `{`/`}`/`,`/`;`/`[`/`]` bleiben `OTHER_SYMBOL` bzw. bestehende Tokens
* Grammatik: `assignment`, `typeNotation`, `valueNotation`, `constraint` als getrennte Regelgruppen in `AsnGrammar.bnf`
* PSI: `mixin` für benennbare Elemente (`AsnTypeAssignment`, `AsnValueAssignment`)
* Index: `AsnDefinitionIndex` als `FileBasedIndex` oder `StubIndex` für Definitionsnamen
* Features: Referenzen, Completion, Highlighting, Struktur, Annotator-Prüfungen

## 6. Übersicht der Teilpläne

| ID    | Teilplan                          | Ziel                                             | Abhängigkeiten |
| ----- | --------------------------------- | ------------------------------------------------ | -------------- |
| IP-01 | Zuweisungsgerüst                  | Zuweisungsliste, Namen, Typ-/Wertzuweisung leer  | -              |
| IP-02 | Typnotation                       | Alle eingebauten und referenzierten Typen        | IP-01          |
| IP-03 | Wertnotation                      | Werte aller Typen, Wertzuweisungen vollständig   | IP-02          |
| IP-04 | Constraints                       | Subtype-Constraints und Erweiterungsmarker       | IP-02          |
| IP-05 | Definitionen und Referenzen       | Benannte Elemente, Index, Auflösung, Completion  | IP-01          |
| IP-06 | Editor-Unterstützung für Body     | Highlighting, Struktur, Faltung, Annotator       | IP-02, IP-03, IP-05 |

## 7. Teilpläne

### IP-01: Zuweisungsgerüst

**Ziel**

* `moduleBody ::= assignment*` mit Fehlerbehandlung je Zuweisung

**Umfang**

* Enthalten: Lexer-Grundlagen, `typeAssignment`/`valueAssignment` mit Platzhalter für Typ und Wert, `recoverWhile`
* Nicht enthalten: konkrete Typen, Werte, Constraints, Referenzen

**Abhängigkeiten**

* `-` (unabhängig)

**Schnittstellen zu anderen Plänen**

* Liefert `assignment`, `typeNotation`- und `valueNotation`-Einstiegspunkte für IP-02, IP-03, IP-04
* Liefert benennbare Zuweisungs-Elemente für IP-05

### IP-02: Typnotation

**Ziel**

* Vollständige Typen nach X.680 Abschnitte 17 bis 41 als PSI

**Umfang**

* Enthalten: eingebaute Typen, Strukturtypen, Tagging, `DefinedType`, `NamedType`
* Nicht enthalten: Constraints, Werte, parametrisierte Typen (X.683)

**Abhängigkeiten**

* IP-01

**Schnittstellen zu anderen Plänen**

* Liefert `typeNotation` für IP-03 und IP-04
* Liefert `DefinedType` als Referenzstelle für IP-05

### IP-03: Wertnotation

**Ziel**

* Werte aller eingebauten Typen und vollständige `valueAssignment`

**Umfang**

* Enthalten: einfache Werte, Strukturwerte, `ChoiceValue`, `DefinedValue`, `ObjectIdentifierValue` einbinden
* Nicht enthalten: typabhängige Prüfung der Werte

**Abhängigkeiten**

* IP-02

**Schnittstellen zu anderen Plänen**

* Liefert `DefinedValue` als Referenzstelle für IP-05

### IP-04: Constraints

**Ziel**

* Subtype-Constraints und Erweiterungsmarker

**Umfang**

* Enthalten: `SIZE`, Wertebereiche, `FROM`, `WITH COMPONENT(S)`, `INCLUDES`, Mengenoperationen, `...`
* Nicht enthalten: Prüfung der Constraints gegen Werte, benutzerdefinierte Constraints (X.682)

**Abhängigkeiten**

* IP-02

**Schnittstellen zu anderen Plänen**

* Erweitert `typeNotation` aus IP-02; keine Rückwirkung auf IP-03

### IP-05: Definitionen und Referenzen

**Ziel**

* Typ- und Wertnamen als Definitionen mit Referenzen, Index und Completion

**Umfang**

* Enthalten: `PsiNameIdentifierOwner`, `PsiReference`, Definitionsindex, Namens-Completion, Find Usages, Rename
* Nicht enthalten: Modulübergreifende Auflösung über IMPORTS

**Abhängigkeiten**

* IP-01 (Referenzstellen aus IP-02 und IP-03 werden ergänzt)

**Schnittstellen zu anderen Plänen**

* Liefert Definitionsindex und Referenzmechanik für späteres IMPORTS/EXPORTS

### IP-06: Editor-Unterstützung für Body

**Ziel**

* Hervorhebung, Struktur, Faltung und Prüfungen für den Body

**Umfang**

* Enthalten: Keywords und Namen einfärben, Structure View, Code Folding, Annotator (Duplikate, unbekannte Namen)
* Nicht enthalten: Formatierung, Quick Fixes, Intentions

**Abhängigkeiten**

* IP-02, IP-03, IP-05

**Schnittstellen zu anderen Plänen**

* Nutzt PSI aus IP-02/IP-03 und Referenzen aus IP-05

## 8. Abhängigkeitsgraph

```text
IP-01
├── IP-02
│   ├── IP-03
│   │   └── IP-06
│   ├── IP-04
│   └── IP-06
└── IP-05
    └── IP-06
```

## 9. Risiken und offene Fragen

* Grammatik wird groß; Aufteilung in mehrere `.bnf`-Dateien oder `include` entscheiden
* Mehrdeutigkeit zwischen Wert- und Typzuweisung am Zeilenanfang; Lookahead nötig
* `FROM` und `IMPORTS` werden im Body zu Keywords; Reihenfolge zum IMPORTS-Feature klären
* Index: `FileBasedIndex` oder `StubIndex`; erfordert Stub-Elementtypen im Fall `StubIndex`
* Umfang X.681 bis X.683 (Information Object Classes, Parametrisierung) vorerst ausgeschlossen
* Sind ältere Konstrukte wie `ANY` und Makros (`MACRO`) gewünscht oder ausgeschlossen

## 10. Abschlusskriterien

* Beispielmodule mit allen Zuweisungsarten parsen ohne Fehlerelement
* Jeder Präfix und Suffix der Beispiele erzeugt einen vollständigen Baum
* Typ- und Wertnamen sind auflösbar, per Completion anbietbar und umbenennbar
* README, `plugin.xml`, Docs und CHANGELOG sind aktuell; `build` ist grün
