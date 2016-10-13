package org.pcsoft.plugin.intellij.asn1.language.template;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;

/**
 * Created by Christoph on 13.10.2016.
 */
public class Asn1TemplateContext extends TemplateContextType {
    public Asn1TemplateContext() {
        super("ASN1", "ASN1");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile psiFile, int i) {
        return psiFile instanceof Asn1File;
    }
}
