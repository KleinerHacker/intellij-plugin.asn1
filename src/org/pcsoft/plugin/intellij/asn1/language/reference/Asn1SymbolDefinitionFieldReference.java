package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinitionField;
import org.pcsoft.plugin.intellij.asn1.type.Asn1FieldType;

import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1SymbolDefinitionFieldReference extends Asn1Reference<Asn1SymbolDefinitionField> {
    private final Asn1FieldType[] fieldTypes;

    /**
     * @param element
     * @param fieldTypes Leave empty to find all
     */
    public Asn1SymbolDefinitionFieldReference(PsiNamedElement element, Asn1FieldType... fieldTypes) {
        super(element);
        this.fieldTypes = fieldTypes;
    }

    @Override
    protected List<Asn1SymbolDefinitionField> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findSymbolDefinitionFields(project, myElement, true, key, fieldTypes);
    }

    @Override
    protected List<Asn1SymbolDefinitionField> findAll(Project project) {
        return Asn1ReferenceUtils.findSymbolDefinitionFields(project, myElement, fieldTypes);
    }

    @Override
    protected boolean isValidItem(Asn1SymbolDefinitionField value) {
        return value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1SymbolDefinitionField value) {
        return LookupElementBuilder.create(value)
                .withIcon(Asn1Icons.FILE);
    }
}
