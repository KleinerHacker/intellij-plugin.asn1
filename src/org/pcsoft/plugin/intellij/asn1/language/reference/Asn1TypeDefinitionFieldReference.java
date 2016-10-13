package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinitionField;

import java.util.List;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public class Asn1TypeDefinitionFieldReference extends Asn1Reference<Asn1TypeDefinitionField> {
    public Asn1TypeDefinitionFieldReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1TypeDefinitionField> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findTypeDefinitionFields(project, myElement, true, key);
    }

    @Override
    protected List<Asn1TypeDefinitionField> findAll(Project project) {
        return Asn1ReferenceUtils.findTypeDefinitionFields(project, myElement);
    }

    @Override
    protected boolean isValidItem(Asn1TypeDefinitionField value) {
        return value.getName() != null && value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1TypeDefinitionField value) {
        return LookupElementBuilder.create(value)
                .withIcon(value.getPresentation().getIcon(false))
                .withTypeText(value.getPresentation().getLocationString());
    }
}
