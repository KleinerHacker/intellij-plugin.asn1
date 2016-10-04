package org.pcsoft.plugin.intellij.asn1.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;

import javax.swing.Icon;

/**
 *
 */
public class Asn1FileType extends LanguageFileType {
    public static final Asn1FileType INSTANCE = new Asn1FileType();

    private Asn1FileType() {
        super(Asn1Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "ASN1";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Abstract Syntax Notation One";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "asn";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Asn1Icons.FILE;
    }
}
