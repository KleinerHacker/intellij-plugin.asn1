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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassParameter;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectClassDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;

import java.util.ArrayList;
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
        final List<Asn1ClassDefinition> classDefinitionList = Asn1ReferenceUtils.findClassDefinitions(objectClassDefinition.getProject(), objectClassDefinition);

        final List<PsiElement> targetList = new ArrayList<>();
        for (final Asn1ClassDefinition classDefinition : classDefinitionList) {
            if (classDefinition.getObjectClassParameterDefinition() == null)
                continue;

            targetList.addAll(
                    classDefinition.getObjectClassParameterDefinition().getObjectClassParameterList().stream()
                            .filter(item -> !StringUtils.isEmpty(item.getObjectClassDefinitionRef().getName()) && item.getObjectClassDefinitionRef().getName().equals(objectClassDefinition.getName()))
                            .collect(Collectors.toList())
            );
        }

        result.add(
                NavigationGutterIconBuilder.create(AllIcons.Gutter.OverridenMethod)
                        .setTargets(targetList)
                        .setTooltipText("Navigate to implementations")
                        .createLineMarkerInfo(objectClassDefinition)
        );
    }

    private void handleClassDefinitionExtension(@NotNull Asn1ClassDefinition classDefinition, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (classDefinition.getObjectClassParameterDefinition() == null)
            return;

        for (final Asn1ObjectClassParameter extension : classDefinition.getObjectClassParameterDefinition().getObjectClassParameterList()) {
            final Asn1ObjectClassDefinitionReference reference = new Asn1ObjectClassDefinitionReference(extension.getObjectClassDefinitionRef());
            final ResolveResult[] multiResolve = reference.multiResolve(false);

            for (final ResolveResult resolveResult : multiResolve) {
                result.add(
                        NavigationGutterIconBuilder.create(AllIcons.Gutter.OverridingMethod)
                                .setTarget(resolveResult.getElement())
                                .setTooltipText("Navigate to " + extension.getObjectClassDefinitionRef().getName())
                                .createLineMarkerInfo(classDefinition)
                );
            }
        }
    }
}
