package org.pcsoft.plugin.intellij.asn1.language.common;

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.Asn1Language;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectDefinition;

/**
 * Created by pfeifchr on 04.10.2016.
 */
public class Asn1StatementUpDownMover extends StatementUpDownMover {

    private PsiFile psiFile;

    @Override
    public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile psiFile, @NotNull MoveInfo moveInfo, boolean b) {
        if (psiFile.getLanguage() == Asn1Language.INSTANCE) {
            this.psiFile = psiFile;
            return true;
        }

        return false;
    }

    @Override
    public void beforeMove(@NotNull Editor editor, @NotNull MoveInfo info, boolean down) {
        if (psiFile == null)
            return;

        final Pair<PsiElement, PsiElement> elementRange1 = getElementRange(editor, psiFile, info.toMove);
        final Pair<PsiElement, PsiElement> elementRange2 = getElementRange(editor, psiFile, info.toMove2);

        if (elementRange1 == null || elementRange2 == null || elementRange2.second == null)
            return;

        if (elementRange1.second instanceof Asn1ClassDefinition || elementRange1.second instanceof Asn1ObjectClassDefinition || elementRange1.second instanceof Asn1ObjectDefinition) {
            if (elementRange2.second instanceof Asn1ClassDefinition || elementRange2.second instanceof Asn1ObjectClassDefinition || elementRange2.second instanceof Asn1ObjectDefinition) {
                if (down) {
                    info.toMove2 = new LineRange(elementRange2.second.getNextSibling());
                } else {
                    info.toMove2 = new LineRange(elementRange2.second.getPrevSibling());
                }
            }
        }
    }
}
