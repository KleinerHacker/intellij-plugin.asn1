package org.pcsoft.plugin.intellij.asn1.language.reference.go_to;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeParameter;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 29.09.2016.
 */
public class Asn1GoToParameterContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        final List<String> list = new ArrayList<>();

        final List<Asn1ObjectSetParameter> objectSetParameterList = Asn1ReferenceUtils.findObjectSetParameters(project, null);
        list.addAll(
                objectSetParameterList.stream()
                        .map(Asn1ObjectSetParameter::getName)
                        .collect(Collectors.toList())
        );

        final List<Asn1TypeParameter> typeParameterList = Asn1ReferenceUtils.findTypeParameters(project, null);
        list.addAll(
                typeParameterList.stream()
                        .map(Asn1TypeParameter::getName)
                        .collect(Collectors.toList())
        );

        return list.toArray(new String[list.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String full, String entered, Project project, boolean b) {
        final List<NavigationItem> list = new ArrayList<>();

        final List<Asn1ObjectSetParameter> objectSetParameterList = Asn1ReferenceUtils.findObjectSetParameters(project, null, true, full);
        list.addAll(
                objectSetParameterList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        final List<Asn1TypeParameter> typeParameterList = Asn1ReferenceUtils.findTypeParameters(project, null, true, full);
        list.addAll(
                typeParameterList.stream()
                        .map(item -> (NavigationItem) item)
                        .collect(Collectors.toList())
        );

        return list.toArray(new NavigationItem[list.size()]);
    }
}
