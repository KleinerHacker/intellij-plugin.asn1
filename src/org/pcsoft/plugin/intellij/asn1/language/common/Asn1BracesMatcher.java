package org.pcsoft.plugin.intellij.asn1.language.common;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;

/**
 * Created by pfeifchr on 27.09.2016.
 */
public class Asn1BracesMatcher implements PairedBraceMatcher {
    @Override
    public BracePair[] getPairs() {
        return new BracePair[]{
                new BracePair(Asn1CustomElementFactory.BRACES_CORNER_OPEN, Asn1CustomElementFactory.BRACES_CORNER_CLOSE, false),
                new BracePair(Asn1CustomElementFactory.BRACES_CURLY_OPEN, Asn1CustomElementFactory.BRACES_CURLY_CLOSE, true),
                new BracePair(Asn1CustomElementFactory.BRACES_ROUND_OPEN, Asn1CustomElementFactory.BRACES_ROUND_CLOSE, false),
        };
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType, @Nullable IElementType iElementType1) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile psiFile, int i) {
        return 0;
    }
}
