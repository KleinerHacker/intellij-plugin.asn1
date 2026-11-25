# Plan: FP-001 IP-05 Definitionen und Referenzen

* Feature Plan: `.claude/plans/features/FP-001-Assignments.md`
* Abhängigkeiten: IP-01; Referenzstellen aus IP-02 und IP-03 werden eingebunden, sobald vorhanden

## Aufgabe 1: Benennbare Definitionen

* In `AsnGrammar.bnf` `mixin` und `implements` für `typeAssignment` und `valueAssignment` setzen
* Mixin-Klassen im Paket `language.parser.psi` als `PsiNameIdentifierOwner` ausprägen
* `getNameIdentifier` liefert das erste Namens-Leaf null-sicher; `getName` liefert dessen Text
* `setName` ersetzt das Leaf über neue `AsnElementFactory` (Paket `language.parser.psi`)
* `AsnElementFactory` erzeugt Namens-Leafs aus Dummy-Text über `PsiFileFactory`
* `getTextOffset` zeigt auf den Namen
* Kein `!!`, keine Exception bei unvollständigem Baum

## Aufgabe 2: Definitionsindex

* `AsnDefinitionIndex` als `ScalarIndexExtension<String>` in Paket `language.feature.index`
* Schlüssel sind Namen von Typ- und Wertzuweisungen; Indexer arbeitet fehlertolerant
* Registrierung als `fileBasedIndex` in `asn-1.language.xml`
* Eingabefilter nur für `AsnFileType`; Index-Version festlegen
* Hilfsfunktion für Auflösung nach Namen im Projekt-Scope, `DumbService`-sicher

## Aufgabe 3: Referenzen

* `referencedType` und `definedValue` erhalten per `mixin` eine `getReference()`
* `AsnDefinitionReference : PsiReferenceBase` im Paket `language.feature.reference`
* `resolve()` sucht zuerst im umgebenden Modul per PSI, danach Index (Modulübergreifend nur lesend)
* Typreferenzen lösen nur auf Typzuweisungen, Wertreferenzen nur auf Wertzuweisungen
* Namen von `namedType` und Modulnamen sind keine Referenzen
* Ergebnis immer null-sicher; keine Exception bei kaputtem Baum

## Aufgabe 4: Completion

* Neuer Provider `DefinitionNameProvider` in `AsnCompletionContributor.kt`
* Typposition: alle Typnamen des Moduls; Wertposition: alle Wertnamen
* Eingebaute Typ-Keywords nur an Typpositionen anbieten
* Eigene Zuweisung aus den Vorschlägen ausschließen, wenn sie gerade getippt wird
* `getVariants()` der Referenz liefert dieselben Namen für Basic Completion

## Aufgabe 5: Find Usages und Rename

* `AsnFindUsagesProvider` mit `AsnWordsScanner` auf Basis des Lexers
* `canFindUsagesFor` nur für Definitionen; Typ-Beschreibung aus I18N-Schlüsseln
* Registrierung als `lang.findUsagesProvider` in `asn-1.language.xml`
* Rename über `PsiNamedElement.setName`; `NamesValidator` für gültige Namen je Zuweisungsart
* Registrierung als `lang.namesValidator`

## Aufgabe 6: Tests

* `AsnDefinitionReferenceTest`: Auflösung von Typen und Werten, unbekannter Name, doppelte Definitionen
* `AsnDefinitionIndexTest`: mehrere Dateien, kaputter Text, Duplikate
* `AsnDefinitionCompletionTest`: Typ- und Wertposition, eigene Zuweisung ausgeschlossen
* `AsnRenameTest`: Rename von Definition aktualisiert alle Referenzen
* `AsnFindUsagesTest`: Treffer in Typ- und Wertposition
* Fehlerfälle: leere Datei, nur Kommentar, `Name ::=` ohne Rest
* Jede Testmethode mit ausführlichem KDoc im Stil „Use case / Expected“

## Aufgabe 7: Dokumentation und Abschluss

* KDoc für alle neuen öffentlichen Member
* `README.md`: Editor Features „References, Rename, Find Usages“ auf `Implemented` setzen
* `docs/docs/`: Seite `navigation.md` anlegen, `mkdocs.yml` ergänzen
* `plugin.xml`: Feature-Liste ergänzen
* `CHANGELOG.md`: Eintrag unter `[UNRELEASED]`
* Status in `FP-001-Assignments-Status.md` aktualisieren
* Neue Dateien mit `git add` aufnehmen, kein Commit
* Gradle-Target `build` über einen Agenten ausführen; `verifyPlugin` nie
