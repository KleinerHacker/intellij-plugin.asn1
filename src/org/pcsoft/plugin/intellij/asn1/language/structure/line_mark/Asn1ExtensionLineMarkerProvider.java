package org.pcsoft.plugin.intellij.asn1.language.structure.line_mark;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectClassDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 04.10.2016.
 */
public class Asn1ExtensionLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof Asn1ClassDefinition) {
            handleClassDefinitionExtension((Asn1ClassDefinition) element, result);
        } else if (element instanceof Asn1ObjectClassDefinition) {
            handleObjectClassDefinitionImplementation((Asn1ObjectClassDefinition) element, result);
        }
    }

    private void handleObjectClassDefinitionImplementation(@NotNull final Asn1ObjectClassDefinition objectClassDefinition, Collection<? super RelatedItemLineMarkerInfo> result) {
        final List<Asn1ClassDefinition> classDefinitionList = Asn1ReferenceUtils.findClassDefinitions(objectClassDefinition.getProject());
        result.add(
                NavigationGutterIconBuilder.create(AllIcons.Gutter.OverridenMethod)
                        .setTargets(
                                classDefinitionList.stream()
                                        .filter(classDefinition ->
                                                classDefinition.getClassDefinitionExtension() != null && !StringUtils.isEmpty(objectClassDefinition.getName()) &&
                                                        objectClassDefinition.getName().equals(classDefinition.getClassDefinitionExtension().getObjectClassDefinitionRef().getName()))
                                        .collect(Collectors.toList())
                        )
                        .setTooltipText("Navigate to implementation(s)")
                        .createLineMarkerInfo(objectClassDefinition)
        );
    }

    private void handleClassDefinitionExtension(@NotNull Asn1ClassDefinition classDefinition, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (classDefinition.getClassDefinitionExtension() == null)
            return;

        final Asn1ObjectClassDefinitionReference reference = new Asn1ObjectClassDefinitionReference(classDefinition.getClassDefinitionExtension().getObjectClassDefinitionRef());
        final ResolveResult[] multiResolve = reference.multiResolve(false);

        for (final ResolveResult resolveResult : multiResolve) {
            result.add(
                    NavigationGutterIconBuilder.create(AllIcons.Gutter.OverridingMethod)
                            .setTarget(resolveResult.getElement())
                            .setTooltipText("Navigate to " + classDefinition.getClassDefinitionExtension().getObjectClassDefinitionRef().getName())
                            .createLineMarkerInfo(classDefinition)
            );
        }
    }
}
