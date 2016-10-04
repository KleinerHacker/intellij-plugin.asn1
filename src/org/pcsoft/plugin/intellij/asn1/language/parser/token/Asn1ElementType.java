package org.pcsoft.plugin.intellij.asn1.language.parser.token;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;

/**
 * Created by pfeifchr on 27.09.2016.
 */
public class Asn1ElementType extends IElementType {
    public Asn1ElementType(@NotNull @NonNls String debugName) {
        super(debugName, Asn1Language.INSTANCE);
    }
}
