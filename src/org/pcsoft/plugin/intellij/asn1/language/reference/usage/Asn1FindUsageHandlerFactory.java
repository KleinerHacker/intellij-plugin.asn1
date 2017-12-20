package org.pcsoft.plugin.intellij.asn1.language.reference.usage;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.lang.HelpID;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 06.10.2016.
 */
public class Asn1FindUsageHandlerFactory extends FindUsagesHandlerFactory {
    @Override
    public boolean canFindUsages(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement psiElement, boolean b) {
        return new FindUsagesHandler(psiElement) {
            @Nullable
            @Override
            protected String getHelpId() {
                return HelpID.FIND_OTHER_USAGES;
            }
        };
    }
}
