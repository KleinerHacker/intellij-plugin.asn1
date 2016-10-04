package org.pcsoft.plugin.intellij.asn1.language.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.Asn1FileType;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;

import javax.swing.Icon;

/**
 *
 */
public class Asn1File extends PsiFileBase {
    public Asn1File(@NotNull FileViewProvider fileViewProvider) {
        super(fileViewProvider, Asn1Language.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Asn1FileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "ASN1 File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
