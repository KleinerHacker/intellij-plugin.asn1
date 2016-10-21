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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeReference;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueObjectIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueObjectIdentifierElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;
import org.pcsoft.plugin.intellij.asn1.type.Asn1SymbolElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by pfeifchr on 21.10.2016.
 */
public class Asn1OIDFolding extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1SymbolValueObjectIdentifier> objectIdentifiers = PsiTreeUtil.findChildrenOfType(psiElement, Asn1SymbolValueObjectIdentifier.class);
        for (final Asn1SymbolValueObjectIdentifier objectIdentifier : objectIdentifiers) {
            final ASTNode curlyOpen = objectIdentifier.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_OPEN);
            final ASTNode curlyClose = objectIdentifier.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CURLY_CLOSE);
            if (curlyOpen != null && curlyClose != null) {
                final TextRange textRange = new TextRange(curlyOpen.getStartOffset() + 1, curlyClose.getStartOffset());
                if (!textRange.isEmpty()) {
                    list.add(new FoldingDescriptor(objectIdentifier, textRange));
                }
            }
        }

        return list.toArray(new FoldingDescriptor[list.size()]);
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return true;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        final StringBuilder sb = new StringBuilder();

        final Asn1SymbolValueObjectIdentifier objectIdentifier = (Asn1SymbolValueObjectIdentifier) astNode.getPsi();
        for (int i = 0; i < objectIdentifier.getSymbolValueObjectIdentifierElementList().size(); i++) {
            final Asn1SymbolValueObjectIdentifierElement element = objectIdentifier.getSymbolValueObjectIdentifierElementList().get(i);

            if (i > 0) {
                sb.append(".");
            }

            if (element.getNumber() != null) {
                sb.append(element.getNumber());
            } else if (element.getSymbolTypeReference() != null) {
                extractValueFromReference(element.getSymbolTypeReference(), sb);
            } else {
                sb.append("?");
            }
        }

        return sb.toString().trim().isEmpty() ? "..." : sb.toString();
    }

    private void extractValueFromReference(@NotNull final Asn1SymbolTypeReference reference, @NotNull final StringBuilder sb) {
        final PsiElement resolvedElement = Asn1ReferenceUtils.resolveReference(reference);
        if (resolvedElement != null && resolvedElement instanceof Asn1SymbolDefinition) {
            final Asn1SymbolDefinition symbolDefinition = (Asn1SymbolDefinition) resolvedElement;
            if (symbolDefinition.getSymbolElement() == Asn1SymbolElement.ValueDefinition && symbolDefinition.getSymbolTypeForValue() != null) {
                if (symbolDefinition.getSymbolTypeForValue().getSymbolValue() != null && symbolDefinition.getSymbolTypeForValue().getSymbolValue().getSymbolValueInteger() != null) {
                    sb.append(symbolDefinition.getSymbolTypeForValue().getSymbolValue().getSymbolValueInteger().getInteger());
                } else if (symbolDefinition.getSymbolTypeForValue().getSymbolTypeReference() != null) {
                    extractValueFromReference(symbolDefinition.getSymbolTypeForValue().getSymbolTypeReference(), sb);
                } else {
                    sb.append("?");
                }
            } else {
                sb.append("?");
            }
        } else {
            sb.append("?");
        }
    }
}
