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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TagDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.type.Asn1ListType;
import org.pcsoft.plugin.intellij.asn1.type.Asn1PrimitiveType;

import java.awt.Font;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Christoph on 29.09.2016.
 */
public class Asn1NativeTypeCompletionContributor extends CompletionContributor {

    public Asn1NativeTypeCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.BRACES_CORNER_CLOSE)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf("::=").withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(
                                Stream.of(Asn1PrimitiveType.values())
                                        .map(item -> LookupElementBuilder.create(item.getName())
                                                .withTypeText(Arrays.toString(item.getValueTypes()))
                                                .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                                                .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        )
                                        .collect(Collectors.toList())
                        );
                    }
                }
        );
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.BRACES_CORNER_CLOSE).withParent(Asn1TagDefinition.class)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf("::=").withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(
                                Stream.of(Asn1ListType.values())
                                        .map(item -> LookupElementBuilder.create(item.getName())
                                                .withBoldness(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                                                .withItemTextForeground(Asn1HighlighterScheme.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        )
                                        .collect(Collectors.toList())
                        );
                    }
                }
        );
    }
}
