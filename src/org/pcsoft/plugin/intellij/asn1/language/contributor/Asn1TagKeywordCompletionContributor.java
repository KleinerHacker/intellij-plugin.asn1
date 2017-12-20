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
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

import java.awt.Font;
import java.util.Arrays;

/**
 * Created by Christoph on 06.10.2016.
 */
public class Asn1TagKeywordCompletionContributor extends CompletionContributor {
    public Asn1TagKeywordCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement(Asn1GenElementFactory.NAME).afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.BRACES_CORNER_OPEN)).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(Arrays.asList(
                                LookupElementBuilder.create("APPLICATION")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                        ));
                    }
                }
        );
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.BRACES_CORNER_CLOSE)).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(Arrays.asList(
                                LookupElementBuilder.create("IMPLICIT")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD),
                                LookupElementBuilder.create("EXPLICIT")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                        ));
                    }
                }
        );
    }
}
