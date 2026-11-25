# Plan: Moduldefinition (Zeile 1) und Object Identifier Value

## Paketstruktur (Vorbild: `inno-setup`, Modul `language/script`)

* Modul-Root `org.pcsoft.intellij.plugin.asn1` bleibt: `AsnIcons`, `PluginBundle`
* `...asn1.language`: bestehende `AsnLanguage` und `AsnFileType` bleiben unverändert
* `...asn1.language.parser`: `AsnLexerAdapter`, `AsnParserDefinition`, `AsnSyntaxHighlighter`, `AsnSyntaxHighlighterFactory`, `AsnAnnotator`
* `...asn1.language.parser.psi`: `AsnTokenType`, `AsnElementType`, generierte PSI-Interfaces und `AsnTypes`
* `...asn1.language.parser.psi.impl`: generierte PSI-Implementierungen und Mixins
* `...asn1.language.feature.editor`: `AsnBraceMatcher`, `AsnColorSettingsPage`
* `...asn1.language.feature.completion`: `AsnCompletionContributor`
* Grammatik unter `language/src/main/resources/parsing`: `AsnGrammar.bnf` und `AsnLexer.flex`
* Generierter Code unter `language/build/generated`, nicht im Git
* Testpakete spiegeln die Produktionspakete

## Grundregel Fehlertoleranz (gilt für ALLE Tasks)

* Lexer, Parser, Annotator, Completion und Highlighter rechnen jederzeit mit unvollständigem Text
* Kein Code darf bei unvollständigem oder falschem Text eine Exception werfen
* Jeder Text, auch leer oder nur halb getippt, ergibt einen vollständigen PSI-Baum
* Der Lexer bleibt total: jedes Zeichen wird einem Token zugeordnet, nie ein Abbruch
* Fehlende Teile werden als Fehlerelement markiert, nie als Absturz
* PSI-Zugriffe sind nullsicher: kein `!!`, keine Annahme über vorhandene Kinder
* `ProcessCanceledException` wird nie abgefangen

## Task 1: Build-Setup Grammar-Kit und JFlex

* Plugin `org.jetbrains.grammarkit` in `gradle/libs.versions.toml` eintragen, Version wie `inno-setup` (2023.3.0.4), Aktualität prüfen
* Plugin in `language/build.gradle.kts` anwenden
* `GenerateParserTask` für `AsnGrammar.bnf`: `pathToParser`, `pathToPsiRoot`, `purgeOldFiles` auf `true`
* `GenerateLexerTask` für `AsnLexer.flex`, `purgeOldFiles` auf `false`, sonst löscht er das PSI-Verzeichnis
* Beide Tasks in Gruppe `grammar-kit` registrieren
* `compileKotlin` und `compileJava` hängen von beiden Generatoren ab
* `build/generated` als Source-Verzeichnis des Source-Sets `main` eintragen
* Generierten Code vom Kover-Report und von der KDoc-Pflicht ausnehmen

## Task 2: Token und Lexer (`AsnLexer.flex`)

* `AsnTokenType` und `AsnElementType` als handgeschriebene Basisklassen im Paket `parser.psi`
* Schlüsselwörter: `DEFINITIONS`, `BEGIN`, `END`, `EXPLICIT`, `IMPLICIT`, `AUTOMATIC`, `TAGS`, `EXTENSIBILITY`, `IMPLIED`, `INSTRUCTIONS`
* Operator `::=` als `ASSIGN`, Symbole `{`, `}`, `(`, `)` als eigene Token
* `NUMBER`: Ziffernfolge, führende Null oder Überlänge erzeugt Fehlerhinweis im Annotator, nie Lexer-Fehler
* `UPPER_REFERENCE` und `IDENTIFIER`: Bindestrich am Ende oder `--` im Namen beendet das Namenstoken
* Kommentare `-- ...` (bis `--` oder Zeilenende) und `/* ... */`, nicht beendeter Block läuft bis Dateiende
* Teile von `::=` (`:` oder `::`) und alle unbekannten Zeichen als `BAD_CHARACTER`
* Auffangregel für jedes übrige Zeichen, Lexer endet nie mit Fehler
* Ein Block-Kommentar ist EIN Token, dadurch braucht der Lexer keinen Zustand und der Editor kann an jedem Token neu starten
* Whitespace und Zeilenumbrüche überall zwischen Token erlaubt

## Task 3: Grammatik Moduldefinition (`AsnGrammar.bnf`)

* Kopf: `parserClass`, `psiPackage`, `psiImplPackage`, `elementTypeHolderClass`, `tokenTypeClass`, `psiClassPrefix="Asn"`
* Regel `moduleDefinition ::= moduleIdentifier DEFINITIONS headerDefault* ASSIGN BEGIN moduleBody END`
* `pin` und `recoverWhile`, damit Teilmodule weiter geparst werden
* `moduleIdentifier ::= UPPER_REFERENCE definitiveIdentifier?`
* `definitiveIdentifier ::= '{' definitiveObjIdComponent+ '}'`, fehlende Komponente oder `}` wird als Fehler gemeldet
* `definitiveObjIdComponent`: Namensform, Zahlform oder Name mit Zahl in Klammern
* `encodingReferenceDefault ::= UPPER_REFERENCE INSTRUCTIONS`
* `tagDefault`: `EXPLICIT TAGS`, `IMPLICIT TAGS` oder `AUTOMATIC TAGS`, alleiniges `EXPLICIT` ist ein Fehler, kein Abbruch
* `extensionDefault ::= EXTENSIBILITY IMPLIED`, Reihenfolge und Wiederholung der Defaults prüft der Annotator
* `moduleBody` vorerst Platzhalter bis `END`, fehlendes `END` bis Dateiende ist ein Fehler, kein Abbruch

## Task 4: Grammatik Object Identifier Value (separat)

* Eigener Regelblock `objectIdentifierValue` in `AsnGrammar.bnf`, unabhängig von `moduleDefinition`
* `objectIdentifierValue ::= '{' objIdComponents+ '}'`, `pin` auf `{`, `recoverWhile` auf `}`
* `objIdComponents`: Namensform, Zahlform, Name mit Zahl oder `definedValue`
* `numberForm ::= NUMBER | definedValue`
* `definedValue` vorerst nur `IDENTIFIER`-Verweis, Erweiterung folgt mit der Werte-Grammatik
* `nameForm` wird von `definitiveIdentifier` mitverwendet
* Rolle je OID-Komponente nullsicher abfragbar: Wurzelbogen, Bogenname, Zahl
* Halbe Komponenten wie `iso(` oder `iso(1` bleiben im Baum erhalten
* IRI-Wert hinter der OID ist nicht enthalten, da nicht gegen X.680 geprüft

## Task 5: Syntax Highlighting

* Keine eigenen Farben: ausschließlich Standardschlüssel aus `DefaultLanguageHighlighterColors` (Java/Kotlin-Schema)
* Lexer-Ebene: Schlüsselwörter `KEYWORD`, `::=` `OPERATION_SIGN`, Zahlen `NUMBER`
* Lexer-Ebene: `-- ...` `LINE_COMMENT`, `/* ... */` `BLOCK_COMMENT`, `{` `}` `BRACES`, `(` `)` `PARENTHESES`
* `AsnAnnotator` auf PSI-Ebene: Modulname `CLASS_NAME`, Zahl in OID `NUMBER`
* OID-Wurzelbogen `itu-t`, `iso`, `joint-iso-itu-t` als `CONSTANT`, sonstiger OID-Name als `INSTANCE_FIELD`
* Annotator färbt nur vorhandene Elemente und überspringt Fehlerelemente ohne Exception
* Eigene `TextAttributesKey` je Rolle mit Fallback auf den jeweiligen Standardschlüssel
* `AsnColorSettingsPage` mit Demo-Text, Anzeigenamen über I18N im `PluginBundle`
* Registrierung in `asn-1.language.xml`

## Task 6: Brace Matcher und ParserDefinition

* `AsnBraceMatcher` für `{` mit `}` und `(` mit `)`
* Brace Matcher kommt mit fehlenden oder überzähligen Klammern ohne Fehler zurecht
* `AsnParserDefinition` mit Lexer, Parser, `AsnFile`, Kommentar- und Whitespace-Sets
* `AsnFile` als `PsiFileBase` mit `AsnFileType`
* Registrierung von Brace Matcher und `ParserDefinition` in `asn-1.language.xml`

## Task 7: Auto Completion für Tag-Default

* `AsnCompletionContributor` in `language.feature.completion`, Registrierung als `completion.contributor` für `AsnLanguage`
* Vorschläge: `EXPLICIT TAGS`, `IMPLICIT TAGS`, `AUTOMATIC TAGS` als vollständige Wendung
* Position: direkt nach `DEFINITIONS` oder nach `INSTRUCTIONS`, Kommentare und Whitespace werden übersprungen
* Kein Vorschlag, wenn die Moduldefinition bereits einen Tag-Default enthält
* Kein Vorschlag nach `::=`, in der OID oder im Modulnamen
* Position wird auch im unvollständigen Baum mit Dummy-Bezeichner des Completion-Systems sicher erkannt
* Hinweistext je Eintrag über I18N im `PluginBundle`, Schlüsselwort selbst bleibt unübersetzt
* Weitere Completion, etwa `EXTENSIBILITY IMPLIED`, ist nicht Teil dieses Plans

## Task 8: Datei-Icon ersetzen

* Bestehendes `asn1-file-icon.svg` in `language/src/main/resources/icons/` neu gestalten
* Farben, Strich, Füllung und Größe an bestehenden Icons und `docs/docs/assets/images/logo.png` ausrichten
* Umsetzung über den Agenten `Icons`
* `AsnIcons.FILE` und Pfad bleiben unverändert
* `pluginIcon.svg` bleibt unverändert, da das Logo gleich bleibt

## Task 9: Tests

* `AsnLexerTest`: alle Token, Kommentare, Bindestrich-Regeln, Zahlen, nicht beendete Kommentare, Einzelzeichen
* `AsnParserTest`: jede Variante aus Task 3 und Task 4, Prüfung über den PSI-Baum
* Fehlerfälle: leere OID, falsche Reihenfolge der Defaults, fehlendes `BEGIN` oder `END`, fehlende Klammern
* Abbruchtest: jeder Präfix einer vollständigen Moduldefinition parst ohne Exception und deckt den ganzen Text ab
* Abbruchtest: leere Datei, nur Kommentar, nur `{`, nur `DEFINITIONS`, mitten im Kommentar endender Text
* `AsnSyntaxHighlighterTest` und `AsnAnnotatorTest`: Farbe je Rolle, Wurzelbogen gegen sonstigen Namen, unvollständige Eingabe
* `AsnBraceMatcherTest` und `AsnCompletionContributorTest`: Paare, Verschachtelung, Vorschläge nach `DEFINITIONS` und `INSTRUCTIONS`, unvollständiger Text
* `AsnFileTypeTest` und `AsnIconsTest` an Parser-Registrierung und neues Icon anpassen
* Jede Testmethode mit ausführlichem KDoc, Ziel mindestens 90 % Coverage ohne generierten Code

## Task 10: Regel Fehlertoleranz

* Neue Regeldatei `.claude/rules/parsing.md` (Englisch) mit der Grundregel Fehlertoleranz, kein Memory-Eintrag
* Regeldatei fordert: keine Exceptions bei unvollständigem Text, totaler Lexer, vollständiger PSI-Baum, nullsichere PSI-Zugriffe
* Regeldatei fordert: Abbruchtests mit allen Präfixen für jede neue Grammatikregel
* Datei mit `git add` aufnehmen, kein Commit

## Task 11: Dokumentation und Abschluss

* KDoc für alle öffentlichen Member, außer generiertem Code
* `plugin.xml`: Liste der unterstützten Features aktualisieren, inklusive Completion
* `README.md`: Implementation Status gemäß Skill `readme` anpassen
* `docs`: MkDocs-Seite zu Syntax, Highlighting und Completion gemäß Skill `mkdocs` ergänzen
* `CHANGELOG.md`: sichtbare Änderungen unter `[UNRELEASED]` eintragen
* Neue Dateien mit `git add` aufnehmen, kein Commit, kein Push
* Gradle-Target `build` über einen Agenten ausführen, `verifyPlugin` nie
* Plan `.claude/plans/Moduldefinition.md` ohne Status-Datei mit `git add` aufnehmen, kein Aufräumen
