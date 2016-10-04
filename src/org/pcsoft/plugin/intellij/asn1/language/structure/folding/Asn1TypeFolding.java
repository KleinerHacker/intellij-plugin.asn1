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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
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

        return list.toArray(new FoldingDescriptor[list.size()]);
    }

    private List<FoldingDescriptor> handleClassDefinition(@NotNull PsiElement psiElement) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1ClassDefinition> classDefinitions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1ClassDefinition.class);
        list.addAll(
                classDefinitions.stream()
                        .filter(classDefinition -> classDefinition.getClassContent() != null)
                        .filter(classDefinition -> classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .map(classDefinition ->
                                new FoldingDescriptor(classDefinition, new TextRange(
                                        classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        classDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        ).collect(Collectors.toList())
        );

        return list;
    }

    private List<FoldingDescriptor> handleObjectClassDefinition(@NotNull PsiElement psiElement) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1ObjectClassDefinition> objectClassDefinitions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1ObjectClassDefinition.class);
        list.addAll(
                objectClassDefinitions.stream()
                        .filter(objectClassDefinition -> objectClassDefinition.getObjectClassContent() != null)
                        .filter(objectClassDefinition -> objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN) != null &&
                                objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE) != null)
                        .map(objectClassDefinition ->
                                new FoldingDescriptor(objectClassDefinition, new TextRange(
                                        objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN).getStartOffset() + 1,
                                        objectClassDefinition.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE).getStartOffset()
                                ))
                        )
                        .collect(Collectors.toList())
        );

        return list;
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
