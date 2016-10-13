package org.pcsoft.plugin.intellij.asn1.language.reference.go_to;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1GoToFieldContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        final List<String> list = new ArrayList<>();

        final List<Asn1TypeDefinitionField> typeDefinitionFieldList = Asn1ReferenceUtils.findTypeDefinitionFields(project, null);
        list.addAll(
                typeDefinitionFieldList.stream()
                        .map(Asn1TypeDefinitionField::getName)
                        .collect(Collectors.toList())
        );

        final List<Asn1ObjectClassDefinitionField> objectClassDefinitionFieldList = Asn1ReferenceUtils.findObjectClassDefinitionFields(project, null);
        list.addAll(
                objectClassDefinitionFieldList.stream()
                        .map(Asn1ObjectClassDefinitionField::getName)
                        .collect(Collectors.toList())
        );

        final List<Asn1ObjectValueDefinition> objectValueDefinitionList = Asn1ReferenceUtils.findObjectValueDefinitions(project, null);
        list.addAll(
                objectValueDefinitionList.stream()
                        .map(Asn1ObjectValueDefinition::getName)
                        .collect(Collectors.toList())
        );

        return list.toArray(new String[list.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String full, String entered, Project project, boolean b) {
        final List<NavigationItem> list = new ArrayList<>();

        final List<Asn1TypeDefinitionField> typeDefinitionFieldList = Asn1ReferenceUtils.findTypeDefinitionFields(project, null, true, full);
        list.addAll(
                typeDefinitionFieldList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        final List<Asn1ObjectClassDefinitionField> objectClassDefinitionFieldList = Asn1ReferenceUtils.findObjectClassDefinitionFields(project, null, true, full);
        list.addAll(
                objectClassDefinitionFieldList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        final List<Asn1ObjectValueDefinition> objectValueDefinitionList = Asn1ReferenceUtils.findObjectValueDefinitions(project, null, true, full);
        list.addAll(
                objectValueDefinitionList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        return list.toArray(new NavigationItem[list.size()]);
    }
}
