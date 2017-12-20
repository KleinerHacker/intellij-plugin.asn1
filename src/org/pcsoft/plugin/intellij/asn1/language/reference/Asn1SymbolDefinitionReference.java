package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.highlighting.Asn1HighlighterScheme;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.type.Asn1SymbolElement;
import org.pcsoft.plugin.intellij.asn1.util.LookupElementUtils;

import java.util.List;

/**
 * Created by Christoph on 29.09.2016.
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
        return LookupElementUtils.updateStyle(LookupElementBuilder.create(value), from(value))
                .withIcon(value.getPresentation().getIcon(false));
    }

    private TextAttributesKey from(final Asn1SymbolDefinition symbolDefinition) {
        if (symbolDefinition.getSymbolElement() == null)
            return Asn1HighlighterScheme.NAME;

        switch (symbolDefinition.getSymbolElement()) {
            case ChoiceDefinition:
                return Asn1HighlighterScheme.CHOICE;
            case EnumeratedDefinition:
                return Asn1HighlighterScheme.ENUM;
            case ObjectClassDefinition:
                return Asn1HighlighterScheme.CLASS;
            case TypeDefinition:
                return Asn1HighlighterScheme.TYPE;
            case ValueDefinition:
                return Asn1HighlighterScheme.VALUE;
            case SetDefinition:
            case ValueSetDefinition:
            case ObjectSetDefinition:
                return Asn1HighlighterScheme.SET;
            case FieldTypeDefinition:
            case CopyDefinition:
                return Asn1HighlighterScheme.NAME;
            default:
                throw new RuntimeException();
        }
    }
}
