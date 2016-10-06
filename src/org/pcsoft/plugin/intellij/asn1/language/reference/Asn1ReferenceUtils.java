package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.Asn1FileType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValue;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElementType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ValueListLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public final class Asn1ReferenceUtils {

    //region File
    @NotNull
    public static List<Asn1File> findFiles(@NotNull final Project project) {
        return findFiles(project, true, null);
    }

    @NotNull
    public static List<Asn1File> findFiles(@NotNull final Project project, final boolean checkFull, @Nullable final String key) {
        final List<Asn1File> list = new ArrayList<>();

        visitFiles(project, psiFile -> {
            final Collection<Asn1File> fileList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1File.class);
            list.addAll(
                    fileList.stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        });

        return list;
    }
    //endregion

    //region Module Definition
    @NotNull
    public static List<Asn1ModuleDefinition> findModuleDefinitions(@NotNull final Project project) {
        return findModuleDefinitions(project, true, null);
    }

    @NotNull
    public static List<Asn1ModuleDefinition> findModuleDefinitions(@NotNull final Project project, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ModuleDefinition> list = new ArrayList<>();

        visitFiles(project, psiFile -> {
            final Collection<Asn1ModuleDefinition> moduleDefinitions = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ModuleDefinition.class);
            list.addAll(
                    moduleDefinitions.stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        });

        return list;
    }
    //endregion

    //region Class Definition
    @NotNull
    public static List<Asn1ClassDefinition> findClassDefinitions(@NotNull final Project project, @Nullable PsiElement currentElement) {
        return findClassDefinitions(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ClassDefinition> findClassDefinitions(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ClassDefinition> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ClassDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ClassDefinition.class);
                list.addAll(
                        definitionList.stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            });
        } else {
            if (!handleClassDefinitionInImportElement(currentElement, checkFull, key, list)) {
                handleClassDefinitionInModule(currentElement, checkFull, key, list);
            }
        }

        return list;
    }

    private static boolean handleClassDefinitionInImportElement(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ClassDefinition> list) {
        final Asn1ImportElement importElement = PsiTreeUtil.getParentOfType(currentElement, Asn1ImportElement.class);
        if (importElement != null && importElement.getImportElementModule().getReference().resolve() != null) {
            //Get imported module...
            final Asn1ModuleDefinition importModuleDefinition = (Asn1ModuleDefinition) importElement.getImportElementModule().getReference().resolve();
            //...and collect all fit classes, based on class import name above
            list.addAll(
                    importModuleDefinition.getModuleContent().getClassDefinitionList().stream()
                            .filter(classDefinition -> checkString(key, classDefinition.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return importElement != null;
    }

    private static boolean handleClassDefinitionInModule(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ClassDefinition> list) {
        final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ModuleDefinition.class);
        if (moduleDefinition != null) {
            //Search for module internal class definitions
            list.addAll(
                    moduleDefinition.getModuleContent().getClassDefinitionList().stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );

            //Search for referenced class definitions
            if (moduleDefinition.getImportDefinition() != null && moduleDefinition.getImportDefinition().getImportContent() != null) {
                for (final Asn1ImportElement importElement : moduleDefinition.getImportDefinition().getImportContent().getImportElementList()) {
                    //Only for resolvable imports
                    if (importElement.getImportElementModule().getReference().resolve() == null)
                        continue;

                    for (Asn1ImportElementType importElementType : importElement.getImportElementTypeList()) {
                        final String refClassDefinitionName = importElementType.getAllClassDefinitionRef().getName();

                        //Only if search key fit to given class definition name
                        if (!checkString(key, refClassDefinitionName, checkFull))
                            continue;

                        //Get imported module...
                        final Asn1ModuleDefinition importModuleDefinition = (Asn1ModuleDefinition) importElement.getImportElementModule().getReference().resolve();
                        //...and collect all fit classes, based on class import name above
                        list.addAll(
                                importModuleDefinition.getModuleContent().getClassDefinitionList().stream()
                                        .filter(classDefinition -> !StringUtils.isEmpty(classDefinition.getName()) && classDefinition.getName().equals(refClassDefinitionName))
                                        .collect(Collectors.toList())
                        );
                    }
                }
            }
        }

        return moduleDefinition != null;
    }
    //endregion

    //region Class Definition Field
    @NotNull
    public static List<Asn1ClassDefinitionField> findClassDefinitionFields(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findClassDefinitionFields(project, currentElement, true, null);
    }

    public static List<Asn1ClassDefinitionField> findClassDefinitionFields(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ClassDefinitionField> list = new ArrayList<>();

        if (currentElement == null) {
            final List<Asn1ClassDefinition> classDefinitionList = findClassDefinitions(project, currentElement);
            for (final Asn1ClassDefinition classDefinition : classDefinitionList) {
                if (classDefinition.getClassContent() == null)
                    continue;

                list.addAll(
                        classDefinition.getClassContent().getClassDefinitionFieldList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        } else {
            if (!handleClassDefinitionFieldInClassDefinition(currentElement, checkFull, key, list)) {
                handleClassDefinitionFieldInObjectValueDefinition(currentElement, checkFull, key, list);
            }
        }

        return list;
    }

    private static boolean handleClassDefinitionFieldInObjectValueDefinition(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ClassDefinitionField> list) {
        final Asn1ObjectValueDefinition objectValueDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ObjectValueDefinition.class);
        if (objectValueDefinition != null && objectValueDefinition.getValueList() != null && objectValueDefinition.getAllClassDefinitionRef() != null) {
            PsiElement foundElement = null;
            for (final PsiReference reference : objectValueDefinition.getAllClassDefinitionRef().getReferences()) {
                foundElement = reference.resolve();
                if (foundElement != null)
                    break;
            }
            if (foundElement != null && foundElement instanceof Asn1ClassDefinition && ((Asn1ClassDefinition) foundElement).getClassContent() != null) {
                list.addAll(
                        ((Asn1ClassDefinition) foundElement).getClassContent().getClassDefinitionFieldList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return objectValueDefinition != null;
    }

    private static boolean handleClassDefinitionFieldInClassDefinition(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ClassDefinitionField> list) {
        final Asn1ClassDefinition classDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ClassDefinition.class);
        if (classDefinition != null && classDefinition.getClassContent() != null) {
            list.addAll(
                    classDefinition.getClassContent().getClassDefinitionFieldList().stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return classDefinition != null;
    }
    //endregion

    //region Object Value Definition
    @NotNull
    public static List<Asn1ObjectValueDefinition> findObjectValueDefinitions(@NotNull final Project project) {
        return findObjectValueDefinitions(project, true, null);
    }

    @NotNull
    public static List<Asn1ObjectValueDefinition> findObjectValueDefinitions(@NotNull final Project project, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ObjectValueDefinition> list = new ArrayList<>();

        visitFiles(project, psiFile -> {
            final Collection<Asn1ObjectValueDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ObjectValueDefinition.class);
            list.addAll(
                    definitionList.stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        });

        return list;
    }
    //endregion

    //region Object Class Definition
    @NotNull
    public static List<Asn1ObjectClassDefinition> findObjectClassDefinitions(@NotNull final Project project, @Nullable PsiElement currentElement) {
        return findObjectClassDefinitions(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ObjectClassDefinition> findObjectClassDefinitions(@NotNull final Project project, @Nullable PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ObjectClassDefinition> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ObjectClassDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ObjectClassDefinition.class);
                list.addAll(
                        definitionList.stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            });
        } else {
            if (!handleObjectClassDefinitionInImportElement(currentElement, checkFull, key, list)) {
                handleObjectClassDefinitionInModule(currentElement, checkFull, key, list);
            }
        }

        return list;
    }

    private static boolean handleObjectClassDefinitionInImportElement(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ObjectClassDefinition> list) {
        final Asn1ImportElement importElement = PsiTreeUtil.getParentOfType(currentElement, Asn1ImportElement.class);
        if (importElement != null && importElement.getImportElementModule().getReference().resolve() != null) {
            //Get imported module...
            final Asn1ModuleDefinition importModuleDefinition = (Asn1ModuleDefinition) importElement.getImportElementModule().getReference().resolve();
            //...and collect all fit classes, based on class import name above
            list.addAll(
                    importModuleDefinition.getModuleContent().getObjectClassDefinitionList().stream()
                            .filter(classDefinition -> checkString(key, classDefinition.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return importElement != null;
    }

    private static boolean handleObjectClassDefinitionInModule(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ObjectClassDefinition> list) {
        final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ModuleDefinition.class);
        if (moduleDefinition != null) {
            //Search for module internal class definitions
            list.addAll(
                    moduleDefinition.getModuleContent().getObjectClassDefinitionList().stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );

            //Search for referenced class definitions
            if (moduleDefinition.getImportDefinition() != null && moduleDefinition.getImportDefinition().getImportContent() != null) {
                for (final Asn1ImportElement importElement : moduleDefinition.getImportDefinition().getImportContent().getImportElementList()) {
                    //Only for resolvable imports
                    if (importElement.getImportElementModule().getReference().resolve() == null)
                        continue;

                    for (Asn1ImportElementType importElementType : importElement.getImportElementTypeList()) {
                        final String refObjectClassDefinitionName = importElementType.getAllClassDefinitionRef().getName();

                        //Only if search key fit to given class definition name
                        if (!checkString(key, refObjectClassDefinitionName, checkFull))
                            continue;

                        //Get imported module...
                        final Asn1ModuleDefinition importModuleDefinition = (Asn1ModuleDefinition) importElement.getImportElementModule().getReference().resolve();
                        //...and collect all fit classes, based on class import name above
                        list.addAll(
                                importModuleDefinition.getModuleContent().getObjectClassDefinitionList().stream()
                                        .filter(classDefinition -> !StringUtils.isEmpty(classDefinition.getName()) && classDefinition.getName().equals(refObjectClassDefinitionName))
                                        .collect(Collectors.toList())
                        );
                    }
                }
            }
        }

        return moduleDefinition != null;
    }
    //endregion

    //region Object Class Definition Field
    @NotNull
    public static List<Asn1ObjectClassDefinitionField> findObjectClassDefinitionFields(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findObjectClassDefinitionFields(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ObjectClassDefinitionField> findObjectClassDefinitionFields(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ObjectClassDefinitionField> list = new ArrayList<>();

        if (currentElement == null) {
            final List<Asn1ObjectClassDefinition> objectClassDefinitionList = findObjectClassDefinitions(project, currentElement);
            for (final Asn1ObjectClassDefinition objectClassDefinition : objectClassDefinitionList) {
                if (objectClassDefinition.getObjectClassContent() == null)
                    continue;

                list.addAll(
                        objectClassDefinition.getObjectClassContent().getObjectClassDefinitionFieldList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        } else {
            handleObjectClassDefinitionFieldInObjectClassDefinitionDefinitionType(currentElement, checkFull, key, list);
        }

        return list;
    }

    private static boolean handleObjectClassDefinitionFieldInObjectClassDefinitionDefinitionType(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ObjectClassDefinitionField> list) {
        final Asn1ObjectClassDefinitionType objectClassDefinitionType = PsiTreeUtil.getParentOfType(currentElement, Asn1ObjectClassDefinitionType.class);
        if (objectClassDefinitionType != null) {
            final PsiElement foundElement = objectClassDefinitionType.getObjectClassDefinitionRef().getReference().resolve();
            if (foundElement != null && foundElement instanceof Asn1ObjectClassDefinition && ((Asn1ObjectClassDefinition) foundElement).getObjectClassContent() != null) {
                list.addAll(
                        ((Asn1ObjectClassDefinition) foundElement).getObjectClassContent().getObjectClassDefinitionFieldList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return objectClassDefinitionType != null;
    }
    //endregion

    //region Parameter
    @NotNull
    public static List<Asn1ParameterName> findParameters(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findParameters(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ParameterName> findParameters(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ParameterName> list = new ArrayList<>();

        if (currentElement == null) {
            final List<Asn1ClassDefinition> classDefinitionList = findClassDefinitions(project, currentElement);
            for (Asn1ClassDefinition classDefinition : classDefinitionList) {
                if (classDefinition.getObjectClassParameterDefinition() == null)
                    continue;

                list.addAll(
                        classDefinition.getObjectClassParameterDefinition().getObjectClassParameterList().stream()
                                .map(Asn1ObjectClassParameter::getParameterName)
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        } else {
            handleParameterInClassDefinition(currentElement, checkFull, key, list);
        }

        return list;
    }

    private static boolean handleParameterInClassDefinition(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ParameterName> list) {
        final Asn1ClassDefinition classDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ClassDefinition.class);
        if (classDefinition != null && classDefinition.getObjectClassParameterDefinition() != null) {
            list.addAll(
                    classDefinition.getObjectClassParameterDefinition().getObjectClassParameterList().stream()
                            .map(Asn1ObjectClassParameter::getParameterName)
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return classDefinition != null;
    }
    //endregion

    //region Constant Values
    @NotNull
    public static List<Asn1ConstantDefinitionValue> findConstantValues(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findConstantValues(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ConstantDefinitionValue> findConstantValues(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ConstantDefinitionValue> list = new ArrayList<>();

        if (currentElement == null) {
            final List<Asn1ClassDefinition> classDefinitionList = findClassDefinitions(project, currentElement);
            for (final Asn1ClassDefinition classDefinition : classDefinitionList) {
                if (classDefinition.getClassContent() == null)
                    continue;

                for (final Asn1ClassDefinitionField field : classDefinition.getClassContent().getClassDefinitionFieldList()) {
                    if (field.getConstantDefinition() == null)
                        continue;

                    list.addAll(
                            field.getConstantDefinition().getConstantDefinitionValueList().stream()
                                    .filter(item -> checkString(key, item.getName(), checkFull))
                                    .collect(Collectors.toList())
                    );
                }
            }
        } else {
            if (!handleConstantValueInClassDefinitionField(currentElement, checkFull, key, list)) {
                handleConstantValueInValueList(currentElement, checkFull, key, list);
            }
        }

        return list;
    }

    private static boolean handleConstantValueInValueList(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ConstantDefinitionValue> list) {
        final Asn1ValueListLine valueListLine = PsiTreeUtil.getParentOfType(currentElement, Asn1ValueListLine.class);
        if (valueListLine != null) {
            final PsiElement foundElement = valueListLine.getClassDefinitionFieldRef().getReference().resolve();
            if (foundElement != null && foundElement instanceof Asn1ClassDefinitionField && ((Asn1ClassDefinitionField) foundElement).getConstantDefinition() != null) {
                list.addAll(
                        ((Asn1ClassDefinitionField) foundElement).getConstantDefinition().getConstantDefinitionValueList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return valueListLine != null;
    }

    private static boolean handleConstantValueInClassDefinitionField(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ConstantDefinitionValue> list) {
        final Asn1ClassDefinitionField classDefinitionField = PsiTreeUtil.getParentOfType(currentElement, Asn1ClassDefinitionField.class);
        if (classDefinitionField != null && classDefinitionField.getConstantDefinition() != null) {
            list.addAll(
                    classDefinitionField.getConstantDefinition().getConstantDefinitionValueList().stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return classDefinitionField != null;
    }
    //endregion

    //region Basic Methods
    private static void visitFiles(@NotNull final Project project, @NotNull final Consumer<PsiFile> fileConsumer) {

        final Collection<VirtualFile> virtualFiles =
                FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, Asn1FileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (final VirtualFile virtualFile : virtualFiles) {
            final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            fileConsumer.accept(psiFile);
        }
    }

    private static boolean checkString(@Nullable final String key, @Nullable final String name, final boolean checkFull) {
        if (StringUtils.isEmpty(key))
            return true;
        if (StringUtils.isEmpty(name))
            return false;

        if (checkFull) {
            return name.equals(key);
        } else {
            return name.startsWith(key);
        }
    }
    //endregion

    private Asn1ReferenceUtils() {
    }
}
