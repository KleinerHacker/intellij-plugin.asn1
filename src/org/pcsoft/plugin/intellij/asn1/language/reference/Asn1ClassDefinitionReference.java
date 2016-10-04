package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinition;

import java.util.List;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public class Asn1ClassDefinitionReference extends Asn1Reference<Asn1ClassDefinition> {
    public Asn1ClassDefinitionReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1ClassDefinition> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findClassDefinitions(project, key);
    }

    @Override
    protected List<Asn1ClassDefinition> findAll(Project project) {
        return Asn1ReferenceUtils.findClassDefinitions(project);
    }

    @Override
    protected boolean isValidItem(Asn1ClassDefinition value) {
        return value.getName() != null && value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1ClassDefinition value) {
        return LookupElementBuilder.create(value)
                .withIcon(value.getPresentation().getIcon(false))
                .withTypeText(value.getPresentation().getLocationString());
    }
}
