package org.pcsoft.plugin.intellij.asn1.language.reference.go_to;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1GoToSymbolContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        final List<String> list = new ArrayList<>();

        final List<Asn1SymbolDefinition> symbolDefinitionList = Asn1ReferenceUtils.findSymbolDefinitions(project, null);
        list.addAll(
                symbolDefinitionList.stream()
                        .map(Asn1SymbolDefinition::getName)
                        .collect(Collectors.toList())
        );

        return list.toArray(new String[list.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String full, String entered, Project project, boolean b) {
        final List<NavigationItem> list = new ArrayList<>();

        final List<Asn1SymbolDefinition> symbolDefinitionList = Asn1ReferenceUtils.findSymbolDefinitions(project, null);
        list.addAll(
                symbolDefinitionList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        return list.toArray(new NavigationItem[list.size()]);
    }
}
