package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolConstantElement;

import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1SymbolConstantReference extends Asn1Reference<Asn1SymbolConstantElement> {
    /**
     * @param element
     */
    public Asn1SymbolConstantReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1SymbolConstantElement> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findSymbolConstants(project, myElement, true, key);
    }

    @Override
    protected List<Asn1SymbolConstantElement> findAll(Project project) {
        return Asn1ReferenceUtils.findSymbolConstants(project, myElement);
    }

    @Override
    protected boolean isValidItem(Asn1SymbolConstantElement value) {
        return value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1SymbolConstantElement value) {
        return LookupElementBuilder.create(value)
                .withIcon(Asn1Icons.FILE);
    }
}
