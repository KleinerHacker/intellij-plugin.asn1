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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValue;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1FindUsageProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new Asn1LexerAdapter(),
                TokenSet.create(Asn1GenElementFactory.NAME_CAP, Asn1GenElementFactory.NAME_LOWER, Asn1GenElementFactory.NAME_NO_CAP, Asn1GenElementFactory.NAME_UPPER),
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
        if (psiElement instanceof Asn1TypeDefinitionField) {
            return "Type Definition Field";
        } else if (psiElement instanceof Asn1TypeDefinition) {
            return "Type Definition";
        } else if (psiElement instanceof Asn1ObjectClassDefinitionField) {
            return "Object Class Definition Field";
        } else if (psiElement instanceof Asn1ObjectClassDefinition) {
            return "Object Class Definition";
        } else if (psiElement instanceof Asn1ObjectValueDefinition) {
            return "Object Value Definition";
        } else if (psiElement instanceof Asn1ObjectSetParameter || psiElement instanceof Asn1TypeParameter) {
            return "Parameter";
        } else if (psiElement instanceof Asn1ConstantDefinitionValue) {
            return "Constant";
        } else if (psiElement instanceof Asn1ModuleDefinition) {
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
