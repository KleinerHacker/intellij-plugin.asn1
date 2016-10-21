package org.pcsoft.plugin.intellij.asn1.language.reference.go_to;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 06.10.2016.
 */
public class Asn1GoToSymbolFieldContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        final List<String> list = new ArrayList<>();

        final List<Asn1SymbolDefinitionField> symbolDefinitionFieldList = Asn1ReferenceUtils.findSymbolDefinitionFields(project, null);
        list.addAll(
                symbolDefinitionFieldList.stream()
                        .map(Asn1SymbolDefinitionField::getName)
                        .collect(Collectors.toList())
        );

        return list.toArray(new String[list.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String full, String entered, Project project, boolean b) {
        final List<NavigationItem> list = new ArrayList<>();

        final List<Asn1SymbolDefinitionField> symbolDefinitionFieldList = Asn1ReferenceUtils.findSymbolDefinitionFields(project, null);
        list.addAll(
                symbolDefinitionFieldList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        return list.toArray(new NavigationItem[list.size()]);
    }
}
