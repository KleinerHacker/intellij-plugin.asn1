package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.apache.commons.lang.StringUtils;
import org.pcsoft.plugin.intellij.asn1.language.Asn1FileType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.gen.Asn1ObjectClassDefinitionField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public final class Asn1ReferenceUtils {

    public static List<Asn1File> findFiles(final Project project) {
        return findFiles(project, null);
    }

    public static List<Asn1File> findFiles(final Project project, final String key) {
        final List<Asn1File> list = new ArrayList<>();

        visitFiles(project, psiFile -> {
            final Collection<Asn1File> fileList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1File.class);
            if (key != null) {
                list.addAll(
                        fileList.stream()
                                .filter(item -> item != null && item.getName().startsWith(key))
                                .collect(Collectors.toList())
                );
            } else {
                list.addAll(fileList);
            }
        });

        return list;
    }

    public static List<Asn1ClassDefinition> findClassDefinitions(final Project project) {
        return findClassDefinitions(project, null);
    }

    public static List<Asn1ClassDefinition> findClassDefinitions(final Project project, final String key) {
        final List<Asn1ClassDefinition> list = new ArrayList<>();

        visitFiles(project, psiFile -> {
            final Collection<Asn1ClassDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ClassDefinition.class);
            if (key != null) {
                list.addAll(
                        definitionList.stream()
                                .filter(item -> item != null && item.getName() != null && item.getName().startsWith(key))
                                .collect(Collectors.toList())
                );
            } else {
                list.addAll(definitionList);
            }
        });

        return list;
    }

    public static List<Asn1ClassDefinitionField> findClassDefinitionFields(final Project project) {
        return findClassDefinitionFields(project, null);
    }

    public static List<Asn1ClassDefinitionField> findClassDefinitionFields(final Project project, final String key) {
        final List<Asn1ClassDefinitionField> list = new ArrayList<>();

        final List<Asn1ClassDefinition> classDefinitionList = findClassDefinitions(project);
        for (final Asn1ClassDefinition classDefinition : classDefinitionList) {
            if (classDefinition.getClassContent() == null)
                continue;

            if (StringUtils.isEmpty(key)) {
                list.addAll(classDefinition.getClassContent().getClassDefinitionFieldList());
            } else {
                list.addAll(
                        classDefinition.getClassContent().getClassDefinitionFieldList().stream()
                        .filter(item -> !StringUtils.isEmpty(item.getName()) && item.getName().startsWith(key))
                        .collect(Collectors.toList())
                );
            }
        }

        return list;
    }

    public static List<Asn1ObjectClassDefinition> findObjectClassDefinitions(final Project project) {
        return findObjectClassDefinitions(project, null);
    }

    public static List<Asn1ObjectClassDefinition> findObjectClassDefinitions(final Project project, final String key) {
        final List<Asn1ObjectClassDefinition> list = new ArrayList<>();

        visitFiles(project, psiFile -> {
            final Collection<Asn1ObjectClassDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ObjectClassDefinition.class);
            if (key != null) {
                list.addAll(
                        definitionList.stream()
                                .filter(item -> item != null && item.getName() != null && item.getName().startsWith(key))
                                .collect(Collectors.toList())
                );
            } else {
                list.addAll(definitionList);
            }
        });

        return list;
    }

    public static List<Asn1ObjectClassDefinitionField> findObjectClassDefinitionFields(final Project project) {
        return findObjectClassDefinitionFields(project, null);
    }

    public static List<Asn1ObjectClassDefinitionField> findObjectClassDefinitionFields(final Project project, final String key) {
        final List<Asn1ObjectClassDefinitionField> list = new ArrayList<>();

        final List<Asn1ObjectClassDefinition> objectClassDefinitionList = findObjectClassDefinitions(project);
        for (final Asn1ObjectClassDefinition objectClassDefinition : objectClassDefinitionList) {
            if (objectClassDefinition.getObjectClassContent() == null)
                continue;

            if (StringUtils.isEmpty(key)) {
                list.addAll(objectClassDefinition.getObjectClassContent().getObjectClassDefinitionFieldList());
            } else {
                list.addAll(
                        objectClassDefinition.getObjectClassContent().getObjectClassDefinitionFieldList().stream()
                                .filter(item -> !StringUtils.isEmpty(item.getName()) && item.getName().startsWith(key))
                                .collect(Collectors.toList())
                );
            }
        }

        return list;
    }

    private static void visitFiles(final Project project, final Consumer<PsiFile> fileConsumer) {

        final Collection<VirtualFile> virtualFiles =
                FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, Asn1FileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (final VirtualFile virtualFile : virtualFiles) {
            final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            fileConsumer.accept(psiFile);
        }
    }

    private Asn1ReferenceUtils() {
    }
}
