package org.pcsoft.plugin.intellij.asn1.language.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1DefinitiveObjectIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

import java.awt.Font;
import java.util.Arrays;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1ModuleKeywordCompletionContributor extends CompletionContributor {
    public Asn1ModuleKeywordCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).withParent(Asn1DefinitiveObjectIdentifier.class)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1GenElementFactory.NAME_CAP).withParent(Asn1ModuleIdentifier.class)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1GenElementFactory.NAME_UPPER).withParent(Asn1ModuleIdentifier.class)).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(Arrays.asList(
                                LookupElementBuilder.create("DEFINITIONS")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                        ));
                    }
                }
        );
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf("DEFINITIONS").withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(Arrays.asList(
                                LookupElementBuilder.create("IMPLICIT TAGS")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD),
                                LookupElementBuilder.create("EXPLICIT TAGS")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD),
                                LookupElementBuilder.create("AUTOMATIC TAGS")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                        ));
                    }
                }
        );
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.OPERATOR).withParent(Asn1ModuleDefinition.class)).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(Arrays.asList(
                                LookupElementBuilder.create("BEGIN")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                        ));
                    }
                }
        );

        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement(Asn1ModuleDefinition.class).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(Arrays.asList(
                                LookupElementBuilder.create("END")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                        ));
                    }
                }
        );
    }
}
