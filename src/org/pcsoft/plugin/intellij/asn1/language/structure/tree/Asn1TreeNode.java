package org.pcsoft.plugin.intellij.asn1.language.structure.tree;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 13.10.2016.
 */
public class Asn1TreeNode extends BasePsiNode<PsiNamedElement> {

    public Asn1TreeNode(Project project, PsiNamedElement value, ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    @Nullable
    @Override
    protected Collection<AbstractTreeNode> getChildrenImpl() {
        final List<AbstractTreeNode> children = new ArrayList<>();

        final Asn1StructureViewElement structureViewElement = new Asn1StructureViewElement(getValue());
        for (final TreeElement treeElement : structureViewElement.getChildren()) {
            children.add(new Asn1TreeNode(myProject, (PsiNamedElement) ((Asn1StructureViewElement)treeElement).getValue(), getSettings()));
        }

        return children;
    }

    @Override
    protected void updateImpl(PresentationData presentationData) {
        presentationData.setIcon(((NavigationItem)getValue()).getPresentation().getIcon(false));
        presentationData.setPresentableText(((NavigationItem)getValue()).getPresentation().getPresentableText());
        presentationData.setLocationString(((NavigationItem)getValue()).getPresentation().getLocationString());
    }
}
