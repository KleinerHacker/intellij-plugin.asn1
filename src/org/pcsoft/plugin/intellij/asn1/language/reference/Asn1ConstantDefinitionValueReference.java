package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValue;

import java.util.List;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public class Asn1ConstantDefinitionValueReference extends Asn1Reference<Asn1ConstantDefinitionValue> {
    public Asn1ConstantDefinitionValueReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1ConstantDefinitionValue> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findConstantValues(project, myElement, true, key);
    }

    @Override
    protected List<Asn1ConstantDefinitionValue> findAll(Project project) {
        return Asn1ReferenceUtils.findConstantValues(project, myElement);
    }

    @Override
    protected boolean isValidItem(Asn1ConstantDefinitionValue value) {
        return value.getName() != null && value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1ConstantDefinitionValue value) {
        final Asn1ClassDefinition classDefinition = PsiTreeUtil.getParentOfType(value, Asn1ClassDefinition.class);

        return LookupElementBuilder.create(value)
                .withIcon(value.getPresentation().getIcon(false))
                .withTypeText(classDefinition != null ? classDefinition.getName() : null);
    }
}
