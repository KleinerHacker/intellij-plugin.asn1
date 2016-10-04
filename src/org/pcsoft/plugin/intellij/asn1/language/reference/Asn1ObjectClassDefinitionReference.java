package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ObjectClassDefinition;

import java.util.List;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public class Asn1ObjectClassDefinitionReference extends Asn1Reference<Asn1ObjectClassDefinition> {
    public Asn1ObjectClassDefinitionReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1ObjectClassDefinition> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findObjectClassDefinitions(project, key);
    }

    @Override
    protected List<Asn1ObjectClassDefinition> findAll(Project project) {
        return Asn1ReferenceUtils.findObjectClassDefinitions(project);
    }

    @Override
    protected boolean isValidItem(Asn1ObjectClassDefinition value) {
        return value.getName() != null && value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1ObjectClassDefinition value) {
        return LookupElementBuilder.create(value)
                .withIcon(value.getPresentation().getIcon(false))
                .withTypeText(value.getPresentation().getLocationString());
    }
}
