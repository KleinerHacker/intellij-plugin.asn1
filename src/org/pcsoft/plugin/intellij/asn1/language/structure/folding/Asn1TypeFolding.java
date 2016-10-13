package org.pcsoft.plugin.intellij.asn1.language.structure.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.*;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1TypeFolding extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        list.addAll(handleClassDefinition(psiElement));
        list.addAll(handleObjectClassDefinition(psiElement));
        list.addAll(handleObjectValueDefinition(psiElement));
        list.addAll(handleObjectSetDefinition(psiElement));
        list.addAll(handleEnumeratedDefinition(psiElement));

        return list.toArray(new FoldingDescriptor[list.size()]);
    }

    private List<FoldingDescriptor> handleClassDefinition(@NotNull PsiElement psiElement) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1TypeDefinition> typeDefinitions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1TypeDefinition.class);
        list.addAll(
                typeDefinitions.stream()
                        .filter(classDefinition -> classDefinition.getTypeDefinitionContent() != null)
                        .filter(classDefinition -> classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .filter(classDefinition ->
                                !new TextRange(
                                        classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ).isEmpty()
                        )
                        .map(classDefinition ->
                                new FoldingDescriptor(classDefinition, new TextRange(
                                        classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        ).collect(Collectors.toList())
        );

        typeDefinitions.stream()
                .filter(classDefinition -> classDefinition.getTypeDefinitionContent() != null)
                .map(Asn1TypeDefinition::getTypeDefinitionContent)
                .forEach(item -> handleAnonymousClassDefinition(item, list));

        return list;
    }

    private List<FoldingDescriptor> handleObjectClassDefinition(@NotNull PsiElement psiElement) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1ObjectClassDefinition> objectClassDefinitions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1ObjectClassDefinition.class);
        list.addAll(
                objectClassDefinitions.stream()
                        .filter(objectClassDefinition -> objectClassDefinition.getObjectClassDefinitionContent() != null)
                        .filter(objectClassDefinition -> objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .filter(objectClassDefinition ->
                                !new TextRange(
                                        objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ).isEmpty()
                        )
                        .map(objectClassDefinition ->
                                new FoldingDescriptor(objectClassDefinition, new TextRange(
                                        objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        )
                        .collect(Collectors.toList())
        );
        list.addAll(
                objectClassDefinitions.stream()
                        .filter(objectClassDefinition -> objectClassDefinition.getObjectClassDefinitionConstructor() != null)
                        .filter(objectClassDefinition ->
                                objectClassDefinition.getObjectClassDefinitionConstructor().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                        objectClassDefinition.getObjectClassDefinitionConstructor().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null
                        )
                        .filter(objectClassDefinition ->
                                !new TextRange(
                                        objectClassDefinition.getObjectClassDefinitionConstructor().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectClassDefinition.getObjectClassDefinitionConstructor().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ).isEmpty()
                        )
                        .map(objectClassDefinition ->
                                new FoldingDescriptor(objectClassDefinition.getObjectClassDefinitionConstructor(), new TextRange(
                                        objectClassDefinition.getObjectClassDefinitionConstructor().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectClassDefinition.getObjectClassDefinitionConstructor().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        )
                        .collect(Collectors.toList())
        );

        objectClassDefinitions.stream()
                .filter(objectClassDefinition -> objectClassDefinition.getObjectClassDefinitionContent() != null)
                .map(Asn1ObjectClassDefinition::getObjectClassDefinitionContent)
                .forEach(objectClassDefinitionContent ->
                        objectClassDefinitionContent.getObjectClassDefinitionFieldList().stream()
                                .filter(objectClassDefinitionField -> objectClassDefinitionField.getTypeDefinitionContent() != null)
                                .map(Asn1ObjectClassDefinitionField::getTypeDefinitionContent)
                                .forEach(classDefinitionContent -> handleAnonymousClassDefinition(classDefinitionContent, list))
                );

        return list;
    }

    private List<FoldingDescriptor> handleObjectValueDefinition(@NotNull PsiElement psiElement) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1ValueDefinition> valueDefinitions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1ValueDefinition.class);
        list.addAll(
                valueDefinitions.stream()
                        .filter(objectValueDefinition -> objectValueDefinition.getDefinitiveType() != null)
                        .filter(objectValueDefinition -> objectValueDefinition.getDefinitiveType().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                objectValueDefinition.getDefinitiveType().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .filter(objectValueDefinition ->
                                !new TextRange(
                                        objectValueDefinition.getDefinitiveType().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectValueDefinition.getDefinitiveType().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ).isEmpty()
                        )
                        .map(objectValueDefinition ->
                                new FoldingDescriptor(objectValueDefinition, new TextRange(
                                        objectValueDefinition.getDefinitiveType().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectValueDefinition.getDefinitiveType().getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        )
                        .collect(Collectors.toList())
        );

        list.addAll(
                valueDefinitions.stream()
                        .filter(objectValueDefinition -> objectValueDefinition.getObjectClassDefinitionConstructorCall() != null)
                        .map(Asn1ValueDefinition::getObjectClassDefinitionConstructorCall)
                        .filter(objectClassDefinitionConstructorCall -> objectClassDefinitionConstructorCall.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                objectClassDefinitionConstructorCall.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .filter(objectClassDefinitionConstructorCall ->
                                !new TextRange(
                                        objectClassDefinitionConstructorCall.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectClassDefinitionConstructorCall.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ).isEmpty()
                        )
                        .map(objectClassDefinitionConstructorCall ->
                                new FoldingDescriptor(objectClassDefinitionConstructorCall, new TextRange(
                                        objectClassDefinitionConstructorCall.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectClassDefinitionConstructorCall.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        )
                        .collect(Collectors.toList())
        );

        valueDefinitions.stream()
                .filter(objectValueDefinition -> objectValueDefinition.getObjectClassDefinitionConstructorCall() != null)
                .map(Asn1ValueDefinition::getObjectClassDefinitionConstructorCall)
                .filter(objectClassDefinitionConstructorCall -> !objectClassDefinitionConstructorCall.getObjectClassDefinitionConstructorCallContent().getTypeDefinitionContentList().isEmpty())
                .map(objectClassDefinitionConstructorCall -> objectClassDefinitionConstructorCall.getObjectClassDefinitionConstructorCallContent().getTypeDefinitionContentList())
                .forEach(classDefinitionContentList ->
                        classDefinitionContentList.forEach(classDefinitionContent -> handleAnonymousClassDefinition(classDefinitionContent, list))
                );

        return list;
    }

    private List<FoldingDescriptor> handleObjectSetDefinition(@NotNull PsiElement psiElement) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1ObjectSetDefinition> objectValueDefinitions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1ObjectSetDefinition.class);
        list.addAll(
                objectValueDefinitions.stream()
                        .filter(objectSetDefinition -> objectSetDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                objectSetDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .filter(objectSetDefinition ->
                                !new TextRange(
                                        objectSetDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectSetDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ).isEmpty()
                        )
                        .map(objectSetDefinition ->
                                new FoldingDescriptor(objectSetDefinition, new TextRange(
                                        objectSetDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectSetDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        )
                        .collect(Collectors.toList())
        );

        return list;
    }

    private List<FoldingDescriptor> handleEnumeratedDefinition(@NotNull PsiElement psiElement) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1EnumeratedDefinition> enumeratedDefinitions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1EnumeratedDefinition.class);
        list.addAll(
                enumeratedDefinitions.stream()
                        .filter(enumeratedDefinition -> enumeratedDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                enumeratedDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .filter(enumeratedDefinition ->
                                !new TextRange(
                                        enumeratedDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        enumeratedDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ).isEmpty()
                        )
                        .map(enumeratedDefinition ->
                                new FoldingDescriptor(enumeratedDefinition, new TextRange(
                                        enumeratedDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        enumeratedDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        )
                        .collect(Collectors.toList())
        );

        return list;
    }

    private void handleAnonymousClassDefinition(@NotNull Asn1TypeDefinitionContent typeDefinitionContent, @NotNull List<FoldingDescriptor> list) {
        list.addAll(
                typeDefinitionContent.getTypeDefinitionFieldList().stream()
                        .filter(item -> item.getTypeDefinitionContent() != null)
                        .filter(item -> item.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                item.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .filter(item ->
                                !new TextRange(
                                        item.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        item.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ).isEmpty()
                        )
                        .map(item ->
                                new FoldingDescriptor(item, new TextRange(
                                        item.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        item.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        ).collect(Collectors.toList())
        );
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return false;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return "...";
    }
}
