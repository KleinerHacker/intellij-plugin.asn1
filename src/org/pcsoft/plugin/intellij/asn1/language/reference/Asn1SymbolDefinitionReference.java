package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.type.Asn1SymbolElement;

import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1SymbolDefinitionReference extends Asn1Reference<Asn1SymbolDefinition> {
    private final Asn1SymbolElement[] symbolElements;

    /**
     * @param element
     * @param symbolElements Leave empty to find all
     */
    public Asn1SymbolDefinitionReference(PsiNamedElement element, Asn1SymbolElement... symbolElements) {
        super(element);
        this.symbolElements = symbolElements;
    }

    @Override
    protected List<Asn1SymbolDefinition> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findSymbolDefinitions(project, myElement, true, key, symbolElements);
    }

    @Override
    protected List<Asn1SymbolDefinition> findAll(Project project) {
        return Asn1ReferenceUtils.findSymbolDefinitions(project, myElement, symbolElements);
    }

    @Override
    protected boolean isValidItem(Asn1SymbolDefinition value) {
        return value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1SymbolDefinition value) {
        return LookupElementBuilder.create(value)
                .withIcon(Asn1Icons.FILE);
    }
}
