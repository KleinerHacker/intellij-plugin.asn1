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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.*;

import javax.swing.*;
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
        if (element instanceof Asn1File || element instanceof Asn1ModuleDefinition) {
            final List<TreeElement> list = new ArrayList<>();

            final Collection<Asn1TypeDefinition> typeDefinitionList = PsiTreeUtil.findChildrenOfType(element, Asn1TypeDefinition.class);
            list.addAll(
                    typeDefinitionList.stream()
                            .map(Asn1StructureViewElement::new)
                            .collect(Collectors.toList())
            );

            final Collection<Asn1ObjectClassDefinition> objectClassDefinitionList = PsiTreeUtil.findChildrenOfType(element, Asn1ObjectClassDefinition.class);
            list.addAll(
                    objectClassDefinitionList.stream()
                            .map(Asn1StructureViewElement::new)
                            .collect(Collectors.toList())
            );

            final Collection<Asn1ValueDefinition> valueDefinitions = PsiTreeUtil.findChildrenOfType(element, Asn1ValueDefinition.class);
            list.addAll(
                    valueDefinitions.stream()
                            .map(Asn1StructureViewElement::new)
                            .collect(Collectors.toList())
            );

            final Collection<Asn1ObjectSetDefinition> objectSetDefinitions = PsiTreeUtil.findChildrenOfType(element, Asn1ObjectSetDefinition.class);
            list.addAll(
                    objectSetDefinitions.stream()
                            .map(Asn1StructureViewElement::new)
                            .collect(Collectors.toList())
            );

            final Collection<Asn1EnumeratedDefinition> enumeratedDefinitions = PsiTreeUtil.findChildrenOfType(element, Asn1EnumeratedDefinition.class);
            list.addAll(
                    enumeratedDefinitions.stream()
                            .map(Asn1StructureViewElement::new)
                            .collect(Collectors.toList())
            );

            return list.toArray(new TreeElement[list.size()]);
        } else if (element instanceof Asn1TypeDefinition) {
            final Collection<Asn1TypeDefinitionField> typeDefinitionFieldList = PsiTreeUtil.findChildrenOfType(element, Asn1TypeDefinitionField.class);
            return typeDefinitionFieldList.stream()
                    .map(Asn1StructureViewElement::new)
                    .toArray(TreeElement[]::new);
        } else if (element instanceof Asn1ObjectClassDefinition) {
            final Collection<Asn1ObjectClassDefinitionField> objectClassFieldDefinitionList = PsiTreeUtil.findChildrenOfType(element, Asn1ObjectClassDefinitionField.class);
            return objectClassFieldDefinitionList.stream()
                    .map(Asn1StructureViewElement::new)
                    .toArray(TreeElement[]::new);
        } else if (element instanceof Asn1EnumeratedDefinition) {
            final Collection<Asn1EnumeratedDefinitionElement> enumeratedDefinitionElements = PsiTreeUtil.findChildrenOfType(element, Asn1EnumeratedDefinitionElement.class);
            return enumeratedDefinitionElements.stream()
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
