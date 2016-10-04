package org.pcsoft.plugin.intellij.asn1.language.structure.tree;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1StructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {
    public Asn1StructureViewModel(@NotNull PsiFile psiFile) {
        super(psiFile, new Asn1StructureViewElement(psiFile));
    }

    @NotNull
    @Override
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement structureViewTreeElement) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement structureViewTreeElement) {
        return structureViewTreeElement instanceof Asn1File;
    }
}
