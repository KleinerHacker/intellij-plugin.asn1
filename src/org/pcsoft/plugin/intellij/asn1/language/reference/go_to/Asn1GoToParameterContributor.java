package org.pcsoft.plugin.intellij.asn1.language.reference.go_to;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForSet;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForType;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 06.10.2016.
 */
public class Asn1GoToParameterContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        final List<String> list = new ArrayList<>();

        final List<Asn1ParameterForType> parameterForTypeList = Asn1ReferenceUtils.findTypeParameters(project, null);
        list.addAll(
                parameterForTypeList.stream()
                        .map(Asn1ParameterForType::getName)
                        .collect(Collectors.toList())
        );

        final List<Asn1ParameterForSet> parameterForSetList = Asn1ReferenceUtils.findSetParameters(project, null);
        list.addAll(
                parameterForSetList.stream()
                        .map(Asn1ParameterForSet::getName)
                        .collect(Collectors.toList())
        );

        return list.toArray(new String[list.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String full, String entered, Project project, boolean b) {
        final List<NavigationItem> list = new ArrayList<>();

        final List<Asn1ParameterForType> parameterForTypeList = Asn1ReferenceUtils.findTypeParameters(project, null);
        list.addAll(
                parameterForTypeList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        final List<Asn1ParameterForSet> parameterForSetList = Asn1ReferenceUtils.findSetParameters(project, null);
        list.addAll(
                parameterForSetList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        return list.toArray(new NavigationItem[list.size()]);
    }
}
