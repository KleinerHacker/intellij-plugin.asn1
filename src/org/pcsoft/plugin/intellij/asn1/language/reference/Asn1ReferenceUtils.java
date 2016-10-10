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
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1EnumeratedDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1EnumeratedDefinitionElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElementType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionConstructor;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassFieldType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeParameter;
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
                        final String refImportElementName = importElementType.getImportElementTypeRef().getName();
                        //Only if search key fit to given class definition name
                        if (!checkString(key, refImportElementName, checkFull))
                            continue;

                        final PsiElement classDefinition = importElementType.getImportElementTypeRef().getClassDefinitionReference().resolve();
                        if (classDefinition == null || !(classDefinition instanceof Asn1ClassDefinition))
                            continue;

                        list.add((Asn1ClassDefinition) classDefinition);
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
                if (classDefinition.getClassDefinitionContent() == null)
                    continue;

                list.addAll(
                        classDefinition.getClassDefinitionContent().getClassDefinitionFieldList().stream()
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
        if (objectValueDefinition != null && objectValueDefinition.getValueList() != null && objectValueDefinition.getElementDefinitionType() != null) {
            PsiElement foundElement = null;
            for (final PsiReference reference : objectValueDefinition.getElementDefinitionType().getElementDefinitionRef().getReferences()) {
                foundElement = reference.resolve();
                if (foundElement != null)
                    break;
            }
            if (foundElement != null && foundElement instanceof Asn1ClassDefinition && ((Asn1ClassDefinition) foundElement).getClassDefinitionContent() != null) {
                list.addAll(
                        ((Asn1ClassDefinition) foundElement).getClassDefinitionContent().getClassDefinitionFieldList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return objectValueDefinition != null;
    }

    private static boolean handleClassDefinitionFieldInClassDefinition(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ClassDefinitionField> list) {
        final Asn1ClassDefinition classDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ClassDefinition.class);
        if (classDefinition != null && classDefinition.getClassDefinitionContent() != null) {
            list.addAll(
                    classDefinition.getClassDefinitionContent().getClassDefinitionFieldList().stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return classDefinition != null;
    }
    //endregion

    //region Object Value Definition
    @NotNull
    public static List<Asn1ObjectValueDefinition> findObjectValueDefinitions(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findObjectValueDefinitions(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ObjectValueDefinition> findObjectValueDefinitions(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ObjectValueDefinition> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ObjectValueDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ObjectValueDefinition.class);
                list.addAll(
                        definitionList.stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            });
        } else {
            if (!handleObjectValueDefinitionInImportElement(currentElement, checkFull, key, list)) {
                handleObjectValueDefinitionInModule(currentElement, checkFull, key, list);
            }
        }

        return list;
    }

    private static boolean handleObjectValueDefinitionInImportElement(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ObjectValueDefinition> list) {
        final Asn1ImportElement importElement = PsiTreeUtil.getParentOfType(currentElement, Asn1ImportElement.class);
        if (importElement != null && importElement.getImportElementModule().getReference().resolve() != null) {
            //Get imported module...
            final Asn1ModuleDefinition importModuleDefinition = (Asn1ModuleDefinition) importElement.getImportElementModule().getReference().resolve();
            //...and collect all fit classes, based on class import name above
            list.addAll(
                    importModuleDefinition.getModuleContent().getObjectValueDefinitionList().stream()
                            .filter(classDefinition -> checkString(key, classDefinition.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return importElement != null;
    }

    private static boolean handleObjectValueDefinitionInModule(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ObjectValueDefinition> list) {
        final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ModuleDefinition.class);
        if (moduleDefinition != null) {
            //Search for module internal class definitions
            list.addAll(
                    moduleDefinition.getModuleContent().getObjectValueDefinitionList().stream()
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
                        final String refImportElementName = importElementType.getImportElementTypeRef().getName();
                        //Only if search key fit to given class definition name
                        if (!checkString(key, refImportElementName, checkFull))
                            continue;

                        final PsiElement objectValueDefinition = importElementType.getImportElementTypeRef().getObjectValueDefinitionReference().resolve();
                        if (objectValueDefinition == null || !(objectValueDefinition instanceof Asn1ObjectValueDefinition))
                            continue;

                        list.add((Asn1ObjectValueDefinition) objectValueDefinition);
                    }
                }
            }
        }

        return moduleDefinition != null;
    }
    //endregion

    //region Object Set Definition
    @NotNull
    public static List<Asn1ObjectSetDefinition> findObjectSetDefinitions(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findObjectSetDefinitions(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ObjectSetDefinition> findObjectSetDefinitions(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ObjectSetDefinition> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ObjectSetDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ObjectSetDefinition.class);
                list.addAll(
                        definitionList.stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            });
        } else {
            if (!handleObjectSetDefinitionInImportElement(currentElement, checkFull, key, list)) {
                handleObjectSetDefinitionInModule(currentElement, checkFull, key, list);
            }
        }

        return list;
    }

    private static boolean handleObjectSetDefinitionInImportElement(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ObjectSetDefinition> list) {
        final Asn1ImportElement importElement = PsiTreeUtil.getParentOfType(currentElement, Asn1ImportElement.class);
        if (importElement != null && importElement.getImportElementModule().getReference().resolve() != null) {
            //Get imported module...
            final Asn1ModuleDefinition importModuleDefinition = (Asn1ModuleDefinition) importElement.getImportElementModule().getReference().resolve();
            //...and collect all fit classes, based on class import name above
            list.addAll(
                    importModuleDefinition.getModuleContent().getObjectSetDefinitionList().stream()
                            .filter(classDefinition -> checkString(key, classDefinition.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return importElement != null;
    }

    private static boolean handleObjectSetDefinitionInModule(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ObjectSetDefinition> list) {
        final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ModuleDefinition.class);
        if (moduleDefinition != null) {
            //Search for module internal class definitions
            list.addAll(
                    moduleDefinition.getModuleContent().getObjectSetDefinitionList().stream()
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
                        final String refImportElementName = importElementType.getImportElementTypeRef().getName();
                        //Only if search key fit to given class definition name
                        if (!checkString(key, refImportElementName, checkFull))
                            continue;

                        final PsiElement objectSetDefinition = importElementType.getImportElementTypeRef().getObjectSetDefinitionReference().resolve();
                        if (objectSetDefinition == null || !(objectSetDefinition instanceof Asn1ObjectSetDefinition))
                            continue;

                        list.add((Asn1ObjectSetDefinition) objectSetDefinition);
                    }
                }
            }
        }

        return moduleDefinition != null;
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
                        final String refImportElementName = importElementType.getImportElementTypeRef().getName();
                        //Only if search key fit to given class definition name
                        if (!checkString(key, refImportElementName, checkFull))
                            continue;

                        final PsiElement objectClassDefinition = importElementType.getImportElementTypeRef().getObjectClassDefinitionReference().resolve();
                        if (objectClassDefinition == null || !(objectClassDefinition instanceof Asn1ObjectClassDefinition))
                            continue;

                        list.add((Asn1ObjectClassDefinition) objectClassDefinition);
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
                if (objectClassDefinition.getObjectClassDefinitionContent() == null)
                    continue;

                list.addAll(
                        objectClassDefinition.getObjectClassDefinitionContent().getObjectClassDefinitionFieldList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        } else {
            if (!handleObjectClassDefinitionFieldInConstructor(currentElement, checkFull, key, list)) {
                handleObjectClassDefinitionFieldInObjectClassFieldType(currentElement, checkFull, key, list);
            }
        }

        return list;
    }

    private static boolean handleObjectClassDefinitionFieldInConstructor(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key,
                                                                         @NotNull List<Asn1ObjectClassDefinitionField> list) {
        final Asn1ObjectClassDefinitionConstructor objectClassDefinitionConstructor = PsiTreeUtil.getParentOfType(currentElement, Asn1ObjectClassDefinitionConstructor.class);
        if (objectClassDefinitionConstructor != null) {
            final Asn1ObjectClassDefinition objectClassDefinition = PsiTreeUtil.getParentOfType(objectClassDefinitionConstructor, Asn1ObjectClassDefinition.class);
            if (objectClassDefinition != null && objectClassDefinition.getObjectClassDefinitionContent() != null) {
                list.addAll(
                        objectClassDefinition.getObjectClassDefinitionContent().getObjectClassDefinitionFieldList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return objectClassDefinitionConstructor != null;
    }

    private static boolean handleObjectClassDefinitionFieldInObjectClassFieldType(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key,
                                                                                  @NotNull List<Asn1ObjectClassDefinitionField> list) {
        final Asn1ObjectClassFieldType objectClassFieldType = PsiTreeUtil.getParentOfType(currentElement, Asn1ObjectClassFieldType.class);
        if (objectClassFieldType != null) {
            final PsiElement foundElement = objectClassFieldType.getObjectClassDefinitionRef().getReference().resolve();
            if (foundElement != null && foundElement instanceof Asn1ObjectClassDefinition && ((Asn1ObjectClassDefinition) foundElement).getObjectClassDefinitionContent() != null) {
                list.addAll(
                        ((Asn1ObjectClassDefinition) foundElement).getObjectClassDefinitionContent().getObjectClassDefinitionFieldList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return objectClassFieldType != null;
    }
    //endregion

    //region Enumerated Definition
    @NotNull
    public static List<Asn1EnumeratedDefinition> findEnumeratedDefinitions(@NotNull final Project project, @Nullable PsiElement currentElement) {
        return findEnumeratedDefinitions(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1EnumeratedDefinition> findEnumeratedDefinitions(@NotNull final Project project, @Nullable PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1EnumeratedDefinition> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1EnumeratedDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1EnumeratedDefinition.class);
                list.addAll(
                        definitionList.stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            });
        } else {
            if (!handleEnumeratedDefinitionInImportElement(currentElement, checkFull, key, list)) {
                handleEnumeratedDefinitionInModule(currentElement, checkFull, key, list);
            }
        }

        return list;
    }

    private static boolean handleEnumeratedDefinitionInImportElement(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1EnumeratedDefinition> list) {
        final Asn1ImportElement importElement = PsiTreeUtil.getParentOfType(currentElement, Asn1ImportElement.class);
        if (importElement != null && importElement.getImportElementModule().getReference().resolve() != null) {
            //Get imported module...
            final Asn1ModuleDefinition importModuleDefinition = (Asn1ModuleDefinition) importElement.getImportElementModule().getReference().resolve();
            //...and collect all fit classes, based on class import name above
            list.addAll(
                    importModuleDefinition.getModuleContent().getEnumeratedDefinitionList().stream()
                            .filter(classDefinition -> checkString(key, classDefinition.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return importElement != null;
    }

    private static boolean handleEnumeratedDefinitionInModule(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1EnumeratedDefinition> list) {
        final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ModuleDefinition.class);
        if (moduleDefinition != null) {
            //Search for module internal class definitions
            list.addAll(
                    moduleDefinition.getModuleContent().getEnumeratedDefinitionList().stream()
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
                        final String refImportElementName = importElementType.getImportElementTypeRef().getName();
                        //Only if search key fit to given class definition name
                        if (!checkString(key, refImportElementName, checkFull))
                            continue;

                        final PsiElement enumeratedDefinition = importElementType.getImportElementTypeRef().getEnumeratedDefinitionReference().resolve();
                        if (enumeratedDefinition == null || !(enumeratedDefinition instanceof Asn1EnumeratedDefinition))
                            continue;

                        list.add((Asn1EnumeratedDefinition) enumeratedDefinition);
                    }
                }
            }
        }

        return moduleDefinition != null;
    }
    //endregion

    //region Enumerated Definition Element
    @NotNull
    public static List<Asn1EnumeratedDefinitionElement> findEnumeratedDefinitionElements(@NotNull final Project project, @Nullable PsiElement currentElement) {
        return findEnumeratedDefinitionElements(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1EnumeratedDefinitionElement> findEnumeratedDefinitionElements(@NotNull final Project project, @Nullable PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1EnumeratedDefinitionElement> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1EnumeratedDefinition> definitionList = PsiTreeUtil.findChildrenOfType(psiFile, Asn1EnumeratedDefinition.class);
                for (final Asn1EnumeratedDefinition enumeratedDefinition : definitionList) {
                    if (enumeratedDefinition.getEnumeratedDefinitionContent() == null)
                        continue;

                    list.addAll(
                            enumeratedDefinition.getEnumeratedDefinitionContent().getEnumeratedDefinitionElementList().stream()
                                    .filter(item -> checkString(key, item.getName(), checkFull))
                                    .collect(Collectors.toList())
                    );
                }
            });
        } else {
            if (!handleEnumeratedDefinitionElementInObjectClassDefinitionField(currentElement, checkFull, key, list)) {
                if (!handleEnumeratedDefinitionElementInClassDefinitionField(currentElement, checkFull, key, list)) {
                    if (!handleEnumeratedDefinitionElementInValueListLine(currentElement, checkFull, key, list)) {
                        handleEnumeratedDefinitionElementInObjectValueDefinition(currentElement, checkFull, key, list);
                    }
                }
            }
        }

        return list;
    }

    private static boolean handleEnumeratedDefinitionElementInObjectClassDefinitionField(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1EnumeratedDefinitionElement> list) {
        final Asn1ObjectClassDefinitionField objectClassDefinitionField = PsiTreeUtil.getParentOfType(currentElement, Asn1ObjectClassDefinitionField.class);
        if (objectClassDefinitionField != null && objectClassDefinitionField.getElementDefinitionType() != null) {
            final Asn1EnumeratedDefinition enumeratedDefinition = (Asn1EnumeratedDefinition) objectClassDefinitionField.getElementDefinitionType().getElementDefinitionRef().getEnumeratedReference().resolve();
            if (enumeratedDefinition != null && enumeratedDefinition.getEnumeratedDefinitionContent() != null) {
                list.addAll(
                        enumeratedDefinition.getEnumeratedDefinitionContent().getEnumeratedDefinitionElementList().stream()
                                .filter(enumeratedDefinitionElement -> checkString(key, enumeratedDefinitionElement.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return objectClassDefinitionField != null;
    }

    private static boolean handleEnumeratedDefinitionElementInClassDefinitionField(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1EnumeratedDefinitionElement> list) {
        final Asn1ClassDefinitionField objectClassDefinitionField = PsiTreeUtil.getParentOfType(currentElement, Asn1ClassDefinitionField.class);
        if (objectClassDefinitionField != null && objectClassDefinitionField.getElementDefinitionType() != null) {
            final Asn1EnumeratedDefinition enumeratedDefinition = (Asn1EnumeratedDefinition) objectClassDefinitionField.getElementDefinitionType().getElementDefinitionRef().getEnumeratedReference().resolve();
            if (enumeratedDefinition != null && enumeratedDefinition.getEnumeratedDefinitionContent() != null) {
                list.addAll(
                        enumeratedDefinition.getEnumeratedDefinitionContent().getEnumeratedDefinitionElementList().stream()
                                .filter(enumeratedDefinitionElement -> checkString(key, enumeratedDefinitionElement.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return objectClassDefinitionField != null;
    }

    private static boolean handleEnumeratedDefinitionElementInValueListLine(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1EnumeratedDefinitionElement> list) {
        final Asn1ValueListLine valueListLine = PsiTreeUtil.getParentOfType(currentElement, Asn1ValueListLine.class);
        if (valueListLine != null && valueListLine.getClassDefinitionFieldRef().getReference().resolve() != null) {
            final Asn1ClassDefinitionField classDefinitionField = (Asn1ClassDefinitionField) valueListLine.getClassDefinitionFieldRef().getReference().resolve();
            if (classDefinitionField != null && classDefinitionField.getElementDefinitionType() != null) {
                final Asn1EnumeratedDefinition enumeratedDefinition = (Asn1EnumeratedDefinition) classDefinitionField.getElementDefinitionType().getElementDefinitionRef().getEnumeratedReference().resolve();
                if (enumeratedDefinition != null && enumeratedDefinition.getEnumeratedDefinitionContent() != null) {
                    list.addAll(
                            enumeratedDefinition.getEnumeratedDefinitionContent().getEnumeratedDefinitionElementList().stream()
                                    .filter(enumeratedDefinitionElement -> checkString(key, enumeratedDefinitionElement.getName(), checkFull))
                                    .collect(Collectors.toList())
                    );
                }
            }
        }

        return valueListLine != null;
    }

    private static boolean handleEnumeratedDefinitionElementInObjectValueDefinition(@Nullable PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1EnumeratedDefinitionElement> list) {
        final Asn1ObjectValueDefinition objectValueDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ObjectValueDefinition.class);
        if (objectValueDefinition != null && objectValueDefinition.getElementDefinitionType() != null) {
            final Asn1EnumeratedDefinition enumeratedDefinition = (Asn1EnumeratedDefinition) objectValueDefinition.getElementDefinitionType().getElementDefinitionRef().getEnumeratedReference().resolve();
            if (enumeratedDefinition != null && enumeratedDefinition.getEnumeratedDefinitionContent() != null) {
                list.addAll(
                        enumeratedDefinition.getEnumeratedDefinitionContent().getEnumeratedDefinitionElementList().stream()
                                .filter(enumeratedDefinitionElement -> checkString(key, enumeratedDefinitionElement.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return objectValueDefinition != null;
    }
    //endregion

    //region Parameter (Object Set)
    @NotNull
    public static List<Asn1ObjectSetParameter> findObjectSetParameters(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findObjectSetParameters(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ObjectSetParameter> findObjectSetParameters(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ObjectSetParameter> list = new ArrayList<>();

        if (currentElement == null) {
            final List<Asn1ClassDefinition> classDefinitionList = findClassDefinitions(project, currentElement);
            for (Asn1ClassDefinition classDefinition : classDefinitionList) {
                if (classDefinition.getParameterDefinition() == null)
                    continue;

                list.addAll(
                        classDefinition.getParameterDefinition().getObjectSetParameterList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        } else {
            handleObjectSetParameterInClassDefinition(currentElement, checkFull, key, list);
        }

        return list;
    }

    private static boolean handleObjectSetParameterInClassDefinition(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ObjectSetParameter> list) {
        final Asn1ClassDefinition classDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ClassDefinition.class);
        if (classDefinition != null && classDefinition.getParameterDefinition() != null) {
            list.addAll(
                    classDefinition.getParameterDefinition().getObjectSetParameterList().stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return classDefinition != null;
    }
    //endregion

    //region Parameter (Type)
    @NotNull
    public static List<Asn1TypeParameter> findTypeParameters(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findTypeParameters(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1TypeParameter> findTypeParameters(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1TypeParameter> list = new ArrayList<>();

        if (currentElement == null) {
            final List<Asn1ClassDefinition> classDefinitionList = findClassDefinitions(project, currentElement);
            for (Asn1ClassDefinition classDefinition : classDefinitionList) {
                if (classDefinition.getParameterDefinition() == null)
                    continue;

                list.addAll(
                        classDefinition.getParameterDefinition().getTypeParameterList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        } else {
            handleTypeParameterInClassDefinition(currentElement, checkFull, key, list);
        }

        return list;
    }

    private static boolean handleTypeParameterInClassDefinition(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1TypeParameter> list) {
        final Asn1ClassDefinition classDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1ClassDefinition.class);
        if (classDefinition != null && classDefinition.getParameterDefinition() != null) {
            list.addAll(
                    classDefinition.getParameterDefinition().getTypeParameterList().stream()
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
                if (classDefinition.getClassDefinitionContent() == null)
                    continue;

                for (final Asn1ClassDefinitionField field : classDefinition.getClassDefinitionContent().getClassDefinitionFieldList()) {
                    if (field.getConstantDefinitionContent() == null)
                        continue;

                    list.addAll(
                            field.getConstantDefinitionContent().getConstantDefinitionValueList().stream()
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
            if (foundElement != null && foundElement instanceof Asn1ClassDefinitionField && ((Asn1ClassDefinitionField) foundElement).getConstantDefinitionContent() != null) {
                list.addAll(
                        ((Asn1ClassDefinitionField) foundElement).getConstantDefinitionContent().getConstantDefinitionValueList().stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            }
        }

        return valueListLine != null;
    }

    private static boolean handleConstantValueInClassDefinitionField(@NotNull PsiElement currentElement, boolean checkFull, @Nullable String key, @NotNull List<Asn1ConstantDefinitionValue> list) {
        final Asn1ClassDefinitionField classDefinitionField = PsiTreeUtil.getParentOfType(currentElement, Asn1ClassDefinitionField.class);
        if (classDefinitionField != null && classDefinitionField.getConstantDefinitionContent() != null) {
            list.addAll(
                    classDefinitionField.getConstantDefinitionContent().getConstantDefinitionValueList().stream()
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
