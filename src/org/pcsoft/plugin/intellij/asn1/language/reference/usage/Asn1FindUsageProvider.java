package org.pcsoft.plugin.intellij.asn1.language.reference.usage;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.parser.lexer.Asn1LexerAdapter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.*;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

/**
 * Created by Christoph on 06.10.2016.
 */
public class Asn1FindUsageProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new Asn1LexerAdapter(),
                TokenSet.create(Asn1GenElementFactory.NAME),
                Asn1CustomElementFactory.COMMENT_SET,
                TokenSet.EMPTY
        );
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement psiElement) {
        return getDescriptiveName(psiElement);
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement psiElement) {
        if (psiElement instanceof Asn1ModuleDefinition) {
            return "Module Definition";
        }

        return "";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement psiElement, boolean b) {
        if (psiElement instanceof PsiNamedElement) {
            final PsiNamedElement namedElement = (PsiNamedElement) psiElement;
            return namedElement.getName() == null ? "" : namedElement.getName();
        }

        return "";
    }
}
