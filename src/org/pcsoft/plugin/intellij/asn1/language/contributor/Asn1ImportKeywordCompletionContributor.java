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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElementType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

import java.awt.Font;
import java.util.Arrays;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1ImportKeywordCompletionContributor extends CompletionContributor {
    public Asn1ImportKeywordCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().withParent(Asn1ModuleDefinition.class).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(Arrays.asList(
                                LookupElementBuilder.create("IMPORTS")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                        ));
                    }
                }
        );
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1GenElementFactory.NAME_CAP).withParent(Asn1ImportElementType.class)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1GenElementFactory.NAME_UPPER).withParent(Asn1ImportElementType.class)).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(Arrays.asList(
                                LookupElementBuilder.create("FROM")
                                        .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                        ));
                    }
                }
        );
    }
}
