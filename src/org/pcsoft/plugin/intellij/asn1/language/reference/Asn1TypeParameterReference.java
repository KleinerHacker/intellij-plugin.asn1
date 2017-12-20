package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForType;
import org.pcsoft.plugin.intellij.asn1.util.LookupElementUtils;

import java.util.List;

/**
 * Created by Christoph on 29.09.2016.
 */
public class Asn1TypeParameterReference extends Asn1Reference<Asn1ParameterForType> {
    /**
     * @param element
     */
    public Asn1TypeParameterReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1ParameterForType> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findTypeParameters(project, myElement, true, key);
    }

    @Override
    protected List<Asn1ParameterForType> findAll(Project project) {
        return Asn1ReferenceUtils.findTypeParameters(project, myElement);
    }

    @Override
    protected boolean isValidItem(Asn1ParameterForType value) {
        return value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1ParameterForType value) {
        return LookupElementUtils.updateStyle(LookupElementBuilder.create(value), Asn1HighlighterScheme.PARAMETER)
                .withIcon(value.getPresentation().getIcon(false));
    }
}
