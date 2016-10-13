package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinition;

import java.util.List;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public class Asn1ObjectValueDefinitionReference extends Asn1Reference<Asn1ObjectValueDefinition> {
    public Asn1ObjectValueDefinitionReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1ObjectValueDefinition> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findObjectValueDefinitions(project, myElement, true, key);
    }

    @Override
    protected List<Asn1ObjectValueDefinition> findAll(Project project) {
        return Asn1ReferenceUtils.findObjectValueDefinitions(project, myElement);
    }

    @Override
    protected boolean isValidItem(Asn1ObjectValueDefinition value) {
        return value.getName() != null && value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1ObjectValueDefinition value) {
        final Asn1TypeDefinition typeDefinition = PsiTreeUtil.getParentOfType(value, Asn1TypeDefinition.class);

        return LookupElementBuilder.create(value)
                .withIcon(value.getPresentation().getIcon(false))
                .withTypeText(typeDefinition != null ? typeDefinition.getName() : null);
    }
}
