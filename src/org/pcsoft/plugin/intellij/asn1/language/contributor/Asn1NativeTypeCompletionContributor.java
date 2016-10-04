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
import org.pcsoft.plugin.intellij.asn1.language.highlighting.syntax.Asn1SyntaxHighlighter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ObjectClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1TagDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1ElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;
import org.pcsoft.plugin.intellij.asn1.type.ListOfType;
import org.pcsoft.plugin.intellij.asn1.type.ListType;
import org.pcsoft.plugin.intellij.asn1.type.PrimitiveType;

import java.awt.Font;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1NativeTypeCompletionContributor extends CompletionContributor {

    public Asn1NativeTypeCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.BRACES_CORNER_CLOSE)).withParent(Asn1TagDefinition.class).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf("::=").withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1GenElementFactory.LIST_OF_TYPE)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1ElementFactory.NAME).withParent(Asn1ClassDefinitionField.class)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1ElementFactory.NAME).withParent(Asn1ObjectClassDefinitionField.class)).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(
                                Stream.of(PrimitiveType.values())
                                        .map(item -> LookupElementBuilder.create(item.getName())
                                                .withTypeText(Arrays.toString(item.getValueTypes()))
                                                .withBoldness(Asn1SyntaxHighlighter.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                                                .withItemTextForeground(Asn1SyntaxHighlighter.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        )
                                        .collect(Collectors.toList())
                        );
                    }
                }
        );
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.BRACES_CORNER_CLOSE).withParent(Asn1TagDefinition.class).withParent(Asn1ClassDefinition.class)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf("::=").withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1ElementFactory.LIST_OF_TYPE).withParent(Asn1ClassDefinition.class)).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(
                                Stream.of(ListType.values())
                                        .map(item -> LookupElementBuilder.create(item.getName())
                                                .withBoldness(Asn1SyntaxHighlighter.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                                                .withItemTextForeground(Asn1SyntaxHighlighter.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        )
                                        .collect(Collectors.toList())
                        );
                    }
                }
        );
        extend(
                CompletionType.BASIC,
                PlatformPatterns.or(
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1CustomElementFactory.BRACES_CORNER_CLOSE)).withParent(Asn1TagDefinition.class).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf("::=").withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1ElementFactory.NAME).withParent(Asn1ClassDefinitionField.class)).withLanguage(Asn1Language.INSTANCE),
                        PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(Asn1ElementFactory.NAME).withParent(Asn1ObjectClassDefinitionField.class)).withLanguage(Asn1Language.INSTANCE)
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        completionResultSet.addAllElements(
                                Stream.of(ListOfType.values())
                                        .map(item -> LookupElementBuilder.create(item.getName())
                                                .withBoldness(Asn1SyntaxHighlighter.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                                                .withItemTextForeground(Asn1SyntaxHighlighter.KEYWORD.getDefaultAttributes().getForegroundColor())
                                        )
                                        .collect(Collectors.toList())
                        );
                    }
                }
        );
    }
}