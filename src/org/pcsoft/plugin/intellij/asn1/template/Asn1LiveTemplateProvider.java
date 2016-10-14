package org.pcsoft.plugin.intellij.asn1.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 13.10.2016.
 */
public class Asn1LiveTemplateProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[] {"liveTemplates/tdef", "liveTemplates/tbdef", "liveTemplates/tag"};
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return new String[0];
    }
}
