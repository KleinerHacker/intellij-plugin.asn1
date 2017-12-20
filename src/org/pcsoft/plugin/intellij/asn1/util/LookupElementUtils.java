package org.pcsoft.plugin.intellij.asn1.util;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;

import java.awt.Font;

/**
 * Created by Christoph on 28.10.2016.
 */
public final class LookupElementUtils {

    @NotNull
    public static LookupElementBuilder updateStyle(@NotNull LookupElementBuilder lookupElementBuilder, @NotNull final TextAttributesKey textAttributesKey) {
        final TextAttributes attributes = textAttributesKey.getDefaultAttributes();
        if (attributes.getForegroundColor() != null) {
            lookupElementBuilder = lookupElementBuilder.withItemTextForeground(attributes.getForegroundColor());
        }
        if (attributes.getEffectType() == EffectType.LINE_UNDERSCORE || attributes.getEffectType() == EffectType.BOLD_LINE_UNDERSCORE ||
                attributes.getEffectType() == EffectType.WAVE_UNDERSCORE || attributes.getEffectType() == EffectType.BOLD_DOTTED_LINE) {
            lookupElementBuilder = lookupElementBuilder.withItemTextUnderlined(true);
        }
        if (attributes.getFontType() == Font.BOLD) {
            lookupElementBuilder = lookupElementBuilder.withBoldness(true);
        }
        if (attributes.getEffectType() == EffectType.STRIKEOUT) {
            lookupElementBuilder = lookupElementBuilder.withStrikeoutness(true);
        }

        return lookupElementBuilder;
    }

    private LookupElementUtils() {
    }
}
