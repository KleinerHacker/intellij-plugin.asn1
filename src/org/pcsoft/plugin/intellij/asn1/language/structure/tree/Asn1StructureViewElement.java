package org.pcsoft.plugin.intellij.asn1.language.structure.tree;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinitionField;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 29.09.2016.
 */
public class Asn1StructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    @NotNull
    private PsiElement element;

    public Asn1StructureViewElement(@NotNull PsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        return element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        if (element instanceof Asn1File) {
            return new ItemPresentation() {
                @Nullable
                @Override
                public String getPresentableText() {
                    final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getChildOfType(element, Asn1ModuleDefinition.class);
                    if (moduleDefinition != null && !StringUtils.isEmpty(moduleDefinition.getName())) {
                        return moduleDefinition.getName();
                    }

                    return ((Asn1File) element).getPresentation().getPresentableText();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return ((Asn1File) element).getPresentation().getLocationString();
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    return ((Asn1File) element).getPresentation().getIcon(b);
                }
            };
        }

        return element instanceof NavigationItem ? ((NavigationItem) element).getPresentation() : null;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return getElementChildren().stream()
                .map(Asn1StructureViewElement::new)
                .toArray(Asn1StructureViewElement[]::new);
    }

    @NotNull
    public Collection<? extends PsiNamedElement> getElementChildren() {
        if (element instanceof Asn1File || element instanceof Asn1ModuleDefinition) {
            final List<PsiNamedElement> list = new ArrayList<>();

            list.addAll(PsiTreeUtil.findChildrenOfType(element, Asn1SymbolDefinition.class));

            return list;
        } else if (element instanceof Asn1SymbolDefinition && ((Asn1SymbolDefinition) element).getSymbolContent() != null) {
            return ((Asn1SymbolDefinition) element).getSymbolContent().getSymbolDefinitionFieldList();
        } else if (element instanceof Asn1SymbolDefinitionField && ((Asn1SymbolDefinitionField) element).getSymbolContent() != null) {
            return ((Asn1SymbolDefinitionField) element).getSymbolContent().getSymbolDefinitionFieldList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem && ((NavigationItem) element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem && ((NavigationItem) element).canNavigateToSource();
    }
}
