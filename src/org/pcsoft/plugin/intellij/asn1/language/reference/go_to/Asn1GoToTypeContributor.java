package org.pcsoft.plugin.intellij.asn1.language.reference.go_to;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1GoToTypeContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        final List<String> list = new ArrayList<>();

        final List<Asn1ClassDefinition> classDefinitionList = Asn1ReferenceUtils.findClassDefinitions(project, null);
        list.addAll(
                classDefinitionList.stream()
                        .map(Asn1ClassDefinition::getName)
                        .collect(Collectors.toList())
        );

        final List<Asn1ObjectClassDefinition> objectClassDefinitions = Asn1ReferenceUtils.findObjectClassDefinitions(project, null);
        list.addAll(
                objectClassDefinitions.stream()
                        .map(Asn1ObjectClassDefinition::getName)
                        .collect(Collectors.toList())
        );

        return list.toArray(new String[list.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String full, String entered, Project project, boolean b) {
        final List<NavigationItem> list = new ArrayList<>();

        final List<Asn1ClassDefinition> classDefinitionList = Asn1ReferenceUtils.findClassDefinitions(project, null, true, full);
        list.addAll(
                classDefinitionList.stream()
                .map(item -> (NavigationItem) item)
                .collect(Collectors.toList())
        );

        final List<Asn1ObjectClassDefinition> objectClassDefinitions = Asn1ReferenceUtils.findObjectClassDefinitions(project, null, true, full);
        list.addAll(
                objectClassDefinitions.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        return list.toArray(new NavigationItem[list.size()]);
    }
}
