package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;

import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1ModuleDefinitionReference extends Asn1Reference<Asn1ModuleDefinition> {
    public Asn1ModuleDefinitionReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1ModuleDefinition> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findModuleDefinitions(project, false, key);
    }

    @Override
    protected List<Asn1ModuleDefinition> findAll(Project project) {
        return Asn1ReferenceUtils.findModuleDefinitions(project);
    }

    @Override
    protected boolean isValidItem(Asn1ModuleDefinition value) {
        return value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1ModuleDefinition value) {
        return LookupElementBuilder.create(value)
                .withIcon(Asn1Icons.FILE);
    }
}
