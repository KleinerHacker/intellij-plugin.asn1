package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.util.LookupElementUtils;

import java.util.List;

/**
 * Created by Christoph on 29.09.2016.
 */
public class Asn1ModuleDefinitionReference extends Asn1Reference<Asn1ModuleDefinition> {
    public Asn1ModuleDefinitionReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1ModuleDefinition> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findModuleDefinitions(project, myElement, true, key);
    }

    @Override
    protected List<Asn1ModuleDefinition> findAll(Project project) {
        return Asn1ReferenceUtils.findModuleDefinitions(project, myElement);
    }

    @Override
    protected boolean isValidItem(Asn1ModuleDefinition value) {
        return value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1ModuleDefinition value) {
        return LookupElementUtils.updateStyle(LookupElementBuilder.create(value), Asn1HighlighterScheme.NAME)
                .withIcon(value.getPresentation().getIcon(false));
    }
}
