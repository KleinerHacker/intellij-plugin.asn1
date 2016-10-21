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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolContentVersion;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1VersionFolding extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<Asn1SymbolContentVersion> symbolContentVersions = PsiTreeUtil.findChildrenOfType(psiElement, Asn1SymbolContentVersion.class);
        for (final Asn1SymbolContentVersion symbolContentVersion : symbolContentVersions) {
            final ASTNode cornerOpen = symbolContentVersion.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CORNER_OPEN);
            final ASTNode cornerClose = symbolContentVersion.getNode().findChildByType(Asn1CustomElementFactory.BRACES_CORNER_CLOSE);
            if (cornerOpen != null && cornerClose != null) {
                final TextRange textRange = new TextRange(cornerOpen.getStartOffset() + 2, cornerClose.getStartOffset());
                if (!textRange.isEmpty()) {
                    list.add(new FoldingDescriptor(symbolContentVersion, textRange));
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
        if (astNode.getPsi() instanceof Asn1SymbolContentVersion && ((Asn1SymbolContentVersion) astNode.getPsi()).getVersionNumber() != null) {
            return "Version " + ((Asn1SymbolContentVersion) astNode.getPsi()).getVersionNumber();
        }

        return "...";
    }
}
