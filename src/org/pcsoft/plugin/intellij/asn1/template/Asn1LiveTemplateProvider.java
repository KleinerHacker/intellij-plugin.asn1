package org.pcsoft.plugin.intellij.asn1.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 13.10.2016.
 */
public class Asn1LiveTemplateProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[]{
                "liveTemplates/chd", "liveTemplates/ocd", "liveTemplates/end", "liveTemplates/fid", "liveTemplates/firef", "liveTemplates/imp", "liveTemplates/mod",
                "liveTemplates/setd", "liveTemplates/tyd", "liveTemplates/vald", "liveTemplates/wsyn"
        };
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return new String[0];
    }
}
