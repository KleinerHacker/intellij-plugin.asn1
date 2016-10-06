package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionField;

import java.util.List;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public class Asn1ClassDefinitionFieldReference extends Asn1Reference<Asn1ClassDefinitionField> {
    public Asn1ClassDefinitionFieldReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1ClassDefinitionField> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findClassDefinitionFields(project, myElement, true, key);
    }

    @Override
    protected List<Asn1ClassDefinitionField> findAll(Project project) {
        return Asn1ReferenceUtils.findClassDefinitionFields(project, myElement);
    }

    @Override
    protected boolean isValidItem(Asn1ClassDefinitionField value) {
        return value.getName() != null && value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1ClassDefinitionField value) {
        return LookupElementBuilder.create(value)
                .withIcon(value.getPresentation().getIcon(false))
                .withTypeText(value.getPresentation().getLocationString());
    }
}
