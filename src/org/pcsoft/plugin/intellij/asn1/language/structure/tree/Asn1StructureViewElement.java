package org.pcsoft.plugin.intellij.asn1.language.structure.tree;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ObjectClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ObjectDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 29.09.2016.
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
        return element instanceof NavigationItem ? ((NavigationItem) element).getPresentation() : null;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof Asn1File) {
            final List<TreeElement> list = new ArrayList<>();

            final Collection<Asn1ClassDefinition> classDefinitionList = PsiTreeUtil.findChildrenOfType(element, Asn1ClassDefinition.class);
            list.addAll(
                    classDefinitionList.stream()
                            .map(Asn1StructureViewElement::new)
                            .collect(Collectors.toList())
            );

            final Collection<Asn1ObjectClassDefinition> objectClassDefinitionList = PsiTreeUtil.findChildrenOfType(element, Asn1ObjectClassDefinition.class);
            list.addAll(
                    objectClassDefinitionList.stream()
                            .map(Asn1StructureViewElement::new)
                            .collect(Collectors.toList())
            );

            final Collection<Asn1ObjectDefinition> objectDefinitionList = PsiTreeUtil.findChildrenOfType(element, Asn1ObjectDefinition.class);
            list.addAll(
                    objectDefinitionList.stream()
                            .map(Asn1StructureViewElement::new)
                            .collect(Collectors.toList())
            );

            return list.toArray(new TreeElement[list.size()]);
        } else if (element instanceof Asn1ClassDefinition) {
            final Collection<Asn1ClassDefinitionField> classFieldDefinitionList = PsiTreeUtil.findChildrenOfType(element, Asn1ClassDefinitionField.class);
            return classFieldDefinitionList.stream()
                    .map(Asn1StructureViewElement::new)
                    .toArray(TreeElement[]::new);
        } else if (element instanceof Asn1ObjectClassDefinition) {
            final Collection<Asn1ObjectClassDefinitionField> objectClassFieldDefinitionList = PsiTreeUtil.findChildrenOfType(element, Asn1ObjectClassDefinitionField.class);
            return objectClassFieldDefinitionList.stream()
                    .map(Asn1StructureViewElement::new)
                    .toArray(TreeElement[]::new);
        } else {
            return EMPTY_ARRAY;
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
