package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;

import java.util.List;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1FileReference extends Asn1Reference<Asn1File> {
    public Asn1FileReference(PsiNamedElement element) {
        super(element);
    }

    @Override
    protected List<Asn1File> findByKey(Project project, String key) {
        return Asn1ReferenceUtils.findFiles(project, key);
    }

    @Override
    protected List<Asn1File> findAll(Project project) {
        return Asn1ReferenceUtils.findFiles(project);
    }

    @Override
    protected boolean isValidItem(Asn1File value) {
        return value.getLanguage() == Asn1Language.INSTANCE;
    }

    @Override
    protected LookupElementBuilder buildItem(Asn1File value) {
        return LookupElementBuilder.create(value)
                .withIcon(Asn1Icons.FILE);
    }
}
