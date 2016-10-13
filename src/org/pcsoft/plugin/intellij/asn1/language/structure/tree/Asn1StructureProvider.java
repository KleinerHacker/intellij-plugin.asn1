package org.pcsoft.plugin.intellij.asn1.language.structure.tree;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.Asn1FileType;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Christoph on 13.10.2016.
 */
public class Asn1StructureProvider implements TreeStructureProvider {
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode abstractTreeNode, @NotNull Collection<AbstractTreeNode> children, ViewSettings viewSettings) {
        ArrayList<AbstractTreeNode> nodes = new ArrayList<>();
        for (AbstractTreeNode child : children) {
            if (viewSettings.isShowMembers()) {
                if (child instanceof PsiFileNode) {
                    VirtualFile file = ((PsiFileNode) child).getVirtualFile();
                    if (file != null && !file.isDirectory() && file.getFileType() instanceof Asn1FileType) {
                        try {
                            final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getChildOfType(
                                    PsiFileFactory.getInstance(abstractTreeNode.getProject()).createFileFromText(
                                            file.getName(), Asn1Language.INSTANCE, new String(file.contentsToByteArray(), file.getCharset())
                                    ), Asn1ModuleDefinition.class);
                            if (moduleDefinition != null) {
                                nodes.add(new Asn1TreeNode(abstractTreeNode.getProject(), moduleDefinition, viewSettings));
                                continue;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            nodes.add(child);
        }
        return nodes;
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> collection, String s) {
        return null;
    }
}
