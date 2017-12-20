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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.Asn1FileType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.Asn1File;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ArgumentForField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ExportContent;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModifierElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForSet;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolConstantElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolConstructor;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeDefinedBy;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeReference;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeReferenceValue;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueTypeContentLine;
import org.pcsoft.plugin.intellij.asn1.type.Asn1FieldType;
import org.pcsoft.plugin.intellij.asn1.type.Asn1SymbolElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 28.09.2016.
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
    public static List<Asn1ModuleDefinition> findModuleDefinitions(@NotNull final Project project, @Nullable PsiElement currentElement) {
        return findModuleDefinitions(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ModuleDefinition> findModuleDefinitions(@NotNull final Project project, @Nullable PsiElement currentElement, final boolean checkFull, @Nullable final String key) {
        final List<Asn1ModuleDefinition> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ModuleDefinition> moduleDefinitions = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ModuleDefinition.class);
                list.addAll(
                        moduleDefinitions.stream()
                                .filter(item -> checkString(key, item.getName(), checkFull))
                                .collect(Collectors.toList())
                );
            });
        } else {
            if (!handleModuleDefinitionInImport(project, currentElement, checkFull, key, list)) {
                if (!handleModuleDefinitionInReference(currentElement, checkFull, key, list)) {
                    //Future Use
                }
            }
        }

        return list;
    }

    private static boolean handleModuleDefinitionInImport(@NotNull final Project project, @NotNull final PsiElement currentElement, final boolean checkFull,
                                                          @Nullable final String key, @NotNull final List<Asn1ModuleDefinition> list) {
        final Asn1ImportElement importElement = PsiTreeUtil.getParentOfType(currentElement, Asn1ImportElement.class);
        if (importElement != null) {
            list.addAll(
                    findModuleDefinitions(project, null, checkFull, key).stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }

        return importElement != null;
    }

    private static boolean handleModuleDefinitionInReference(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                             @Nullable final String key, @NotNull final List<Asn1ModuleDefinition> list) {
        if (currentElement instanceof Asn1SymbolTypeReferenceValue) {
            final Asn1SymbolTypeReferenceValue symbolTypeReferenceValue = (Asn1SymbolTypeReferenceValue) currentElement;
            final Asn1SymbolTypeReferenceValue prevSymbolTypeReferenceValue = PsiTreeUtil.getPrevSiblingOfType(symbolTypeReferenceValue, Asn1SymbolTypeReferenceValue.class);
            if (prevSymbolTypeReferenceValue == null) {
                final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(symbolTypeReferenceValue, Asn1ModuleDefinition.class);
                if (moduleDefinition != null) {
                    //Own Module?
                    if (checkString(key, moduleDefinition.getName(), checkFull)) {
                        list.add(moduleDefinition);
                    }

                    //Imported Module?
                    if (moduleDefinition.getImportDefinition() != null && moduleDefinition.getImportDefinition().getImportContent() != null) {
                        list.addAll(
                                moduleDefinition.getImportDefinition().getImportContent().getImportElementList().stream()
                                        .filter(item -> item.getModuleRef() != null)
                                        .map(item -> item.getModuleRef().getReference().resolve())
                                        .filter(item -> item != null && item instanceof Asn1ModuleDefinition)
                                        .map(item -> (Asn1ModuleDefinition) item)
                                        .filter(item -> checkString(key, item.getName(), checkFull))
                                        .collect(Collectors.toList())
                        );
                    }
                }
            }
        }

        return currentElement instanceof Asn1SymbolTypeReferenceValue;
    }
    //endregion

    //region Symbol Definition

    /**
     * @param project
     * @param currentElement
     * @param elements       Leave empty find all
     * @return
     */
    @NotNull
    public static List<Asn1SymbolDefinition> findSymbolDefinitions(@NotNull final Project project, @Nullable final PsiElement currentElement,
                                                                   @Nullable final Asn1SymbolElement... elements) {
        return findSymbolDefinitions(project, currentElement, true, null, elements);
    }

    /**
     * @param project
     * @param currentElement
     * @param checkFull
     * @param key
     * @param elements       Leave empty to find all
     * @return
     */
    @NotNull
    public static List<Asn1SymbolDefinition> findSymbolDefinitions(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull,
                                                                   @Nullable final String key, @Nullable final Asn1SymbolElement... elements) {
        final List<Asn1SymbolDefinition> list = new ArrayList<>();
        final Asn1SymbolElement[] usedElements = elements == null || elements.length <= 0 ? Asn1SymbolElement.values() : elements;

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ModuleDefinition> moduleDefinitions = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ModuleDefinition.class);
                for (final Asn1ModuleDefinition moduleDefinition : moduleDefinitions) {
                    handleSymbolDefinitionFromModule(moduleDefinition, checkFull, key, usedElements, list);
                }
            });
        } else {
            if (!handleSymbolDefinitionInImport(currentElement, checkFull, key, usedElements, list)) {
                if (!handleSymbolDefinitionInExport(currentElement, checkFull, key, usedElements, list)) {
                    if (!handleSymbolDefinitionInReference(currentElement, checkFull, key, usedElements, list)) {
                        //Future Use
                    }
                }
            }
        }

        return list;
    }

    private static boolean handleSymbolDefinitionInImport(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                          @Nullable final String key, @NotNull final Asn1SymbolElement[] symbolElements, @NotNull final List<Asn1SymbolDefinition> list) {
        final Asn1ImportElement importElement = PsiTreeUtil.getParentOfType(currentElement, Asn1ImportElement.class);
        if (importElement != null && importElement.getModuleRef() != null) {
            final Asn1ModuleDefinition moduleDefinition = (Asn1ModuleDefinition) importElement.getModuleRef().getReference().resolve();
            if (moduleDefinition != null) {
                handleSymbolDefinitionFromModule(moduleDefinition, checkFull, key, symbolElements, list);
            }
        }

        return importElement != null;
    }

    private static boolean handleSymbolDefinitionInExport(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                          @Nullable final String key, @NotNull final Asn1SymbolElement[] symbolElements, @NotNull final List<Asn1SymbolDefinition> list) {
        final Asn1ExportContent exportContent = PsiTreeUtil.getParentOfType(currentElement, Asn1ExportContent.class);
        if (exportContent != null) {
            //Search in own module only (imported cannot be exported)
            final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(exportContent, Asn1ModuleDefinition.class);
            if (moduleDefinition != null) {
                handleSymbolDefinitionFromModule(moduleDefinition, checkFull, key, symbolElements, list);
            }
        }

        return exportContent != null;
    }

    private static boolean handleSymbolDefinitionInReference(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                             @Nullable final String key, @NotNull final Asn1SymbolElement[] symbolElements, @NotNull final List<Asn1SymbolDefinition> list) {
        if (currentElement instanceof Asn1SymbolTypeReferenceValue) {
            final Asn1SymbolTypeReferenceValue symbolTypeReferenceValue = (Asn1SymbolTypeReferenceValue) currentElement;
            final Asn1SymbolTypeReferenceValue prevSymbolTypeReferenceValue = PsiTreeUtil.getPrevSiblingOfType(symbolTypeReferenceValue, Asn1SymbolTypeReferenceValue.class);
            if (prevSymbolTypeReferenceValue == null) {
                final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(symbolTypeReferenceValue, Asn1ModuleDefinition.class);
                if (moduleDefinition != null) {
                    //All in own module
                    handleSymbolDefinitionFromModule(moduleDefinition, checkFull, key, symbolElements, list);

                    //All in imports
                    if (moduleDefinition.getImportDefinition() != null && moduleDefinition.getImportDefinition().getImportContent() != null) {
                        for (final Asn1ImportElement importElement : moduleDefinition.getImportDefinition().getImportContent().getImportElementList()) {
                            list.addAll(
                                    importElement.getImportExportSymbolList().stream()
                                            .map(item -> item.getImportExportSymbolRef().getReference().resolve())
                                            .filter(item -> item != null && item instanceof Asn1SymbolDefinition)
                                            .map(item -> (Asn1SymbolDefinition) item)
                                            .filter(item -> ArrayUtils.contains(symbolElements, item.getSymbolElement()))
                                            .filter(item -> checkString(key, item.getName(), checkFull))
                                            .collect(Collectors.toList())
                            );
                        }
                    }
                }
            } else {
                final PsiElement resolvedElement = resolveFromMultiReference(prevSymbolTypeReferenceValue);
                if (resolvedElement != null) {
                    if (resolvedElement instanceof Asn1ModuleDefinition) {
                        final Asn1ModuleDefinition moduleDefinition = (Asn1ModuleDefinition) resolvedElement;
                        handleSymbolDefinitionFromModule(moduleDefinition, checkFull, key, symbolElements, list);
                    }
                }
            }
        }

        return currentElement instanceof Asn1SymbolTypeReferenceValue;
    }

    private static void handleSymbolDefinitionFromModule(Asn1ModuleDefinition moduleDefinition, boolean checkFull, @Nullable String key, @NotNull Asn1SymbolElement[] symbolElements, @NotNull List<Asn1SymbolDefinition> list) {
        if (moduleDefinition.getModuleContent() != null) {
            list.addAll(
                    moduleDefinition.getModuleContent().getSymbolDefinitionList().stream()
                            .filter(item -> ArrayUtils.contains(symbolElements, item.getSymbolElement()))
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }
    }
    //endregion

    //region Symbol Definition Field

    /**
     * @param project
     * @param currentElement
     * @param types          Leave empty find all
     * @return
     */
    @NotNull
    public static List<Asn1SymbolDefinitionField> findSymbolDefinitionFields(@NotNull final Project project, @Nullable final PsiElement currentElement,
                                                                             @Nullable final Asn1FieldType... types) {
        return findSymbolDefinitionFields(project, currentElement, true, null, types);
    }

    /**
     * @param project
     * @param currentElement
     * @param checkFull
     * @param key
     * @param types          Leave empty to find all
     * @return
     */
    @NotNull
    public static List<Asn1SymbolDefinitionField> findSymbolDefinitionFields(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull,
                                                                             @Nullable final String key, @Nullable final Asn1FieldType... types) {
        final List<Asn1SymbolDefinitionField> list = new ArrayList<>();
        final Asn1FieldType[] usedTypes = types == null || types.length <= 0 ? Asn1FieldType.values() : types;

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ModuleDefinition> moduleDefinitions = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ModuleDefinition.class);
                for (final Asn1ModuleDefinition moduleDefinition : moduleDefinitions) {
                    if (moduleDefinition.getModuleContent() != null) {
                        for (final Asn1SymbolDefinition symbolDefinition : moduleDefinition.getModuleContent().getSymbolDefinitionList()) {
                            handleSymbolDefinitionFieldFromSymbolDefinition(symbolDefinition, checkFull, key, usedTypes, list);
                        }
                    }
                }
            });
        } else {
            if (!handleSymbolDefinitionFieldInQualifiedReference(currentElement, checkFull, key, usedTypes, list)) {
                if (!handleSymbolDefinitionFieldInReference(currentElement, checkFull, key, usedTypes, list)) {
                    //Future Use
                }
            }
        }

        return list;
    }

    private static boolean handleSymbolDefinitionFieldInQualifiedReference(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                                           @Nullable final String key, @NotNull final Asn1FieldType[] fieldTypes, @NotNull final List<Asn1SymbolDefinitionField> list) {
        if (currentElement instanceof Asn1SymbolTypeReferenceValue) {
            final Asn1SymbolTypeReferenceValue symbolTypeReferenceValue = (Asn1SymbolTypeReferenceValue) currentElement;
            final Asn1SymbolTypeReferenceValue prevSymbolTypeReferenceValue = PsiTreeUtil.getPrevSiblingOfType(symbolTypeReferenceValue, Asn1SymbolTypeReferenceValue.class);
            if (prevSymbolTypeReferenceValue == null) {
                //TODO
            } else {
                final PsiElement resolvedElement = resolveFromMultiReference(prevSymbolTypeReferenceValue);
                if (resolvedElement != null) {
                    if (resolvedElement instanceof Asn1SymbolDefinition) {
                        final Asn1SymbolDefinition symbolDefinition = (Asn1SymbolDefinition) resolvedElement;
                        handleSymbolDefinitionFieldFromSymbolDefinition(symbolDefinition, checkFull, key, fieldTypes, list);
                    }
                }
            }
        }

        return currentElement instanceof Asn1SymbolTypeReferenceValue;
    }

    private static boolean handleSymbolDefinitionFieldInTypeValue(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                                  @Nullable final String key, @NotNull final Asn1FieldType[] fieldTypes, @NotNull final List<Asn1SymbolDefinitionField> list) {
        final Asn1SymbolValueTypeContentLine symbolValueTypeContentLine = PsiTreeUtil.getParentOfType(currentElement, Asn1SymbolValueTypeContentLine.class);
        if (symbolValueTypeContentLine != null) {
            final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(currentElement, Asn1SymbolDefinition.class);
            //Symbol, definition of type value definition with symbol type
            if (symbolDefinition != null && symbolDefinition.getSymbolElement() == Asn1SymbolElement.ValueDefinition &&
                    symbolDefinition.getSymbolIdentifierType() != null && symbolDefinition.getSymbolIdentifierType().getSymbolTypeReference() != null) {
                final List<Asn1SymbolTypeReferenceValue> typeReferenceValueList = symbolDefinition.getSymbolIdentifierType().getSymbolTypeReference().getSymbolTypeReferenceValueList();
                if (!typeReferenceValueList.isEmpty()) {
                    //Resolve symbol type from last element
                    final PsiElement element = resolveFromMultiReference(typeReferenceValueList.get(typeReferenceValueList.size() - 1));
                    if (element != null && element instanceof Asn1SymbolDefinition && ((Asn1SymbolDefinition) element).getSymbolElement() == Asn1SymbolElement.TypeDefinition) {
                        //Add all from type definition
                        handleSymbolDefinitionFieldFromSymbolDefinition((Asn1SymbolDefinition) element, checkFull, key, fieldTypes, list);
                    }
                }
            }
        }

        return symbolValueTypeContentLine != null;
    }

    private static boolean handleSymbolDefinitionFieldInArgument(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                                 @Nullable final String key, @NotNull final Asn1FieldType[] fieldTypes, @NotNull final List<Asn1SymbolDefinitionField> list) {
        final Asn1ArgumentForField argumentForField = PsiTreeUtil.getParentOfType(currentElement, Asn1ArgumentForField.class);
        if (argumentForField != null) {
            final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(argumentForField, Asn1SymbolDefinition.class);
            //Symbol, definition of type value definition with symbol type
            if (symbolDefinition != null) {
                handleSymbolDefinitionFieldFromSymbolDefinition(symbolDefinition, checkFull, key, fieldTypes, list);
            }
        }

        return argumentForField != null;
    }

    private static boolean handleSymbolDefinitionFieldInReference(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                                  @Nullable final String key, @NotNull final Asn1FieldType[] fieldTypes, @NotNull final List<Asn1SymbolDefinitionField> list) {
        if (currentElement instanceof Asn1SymbolFieldReference) {
            if (!handleSymbolDefinitionFieldInConstructor(currentElement, checkFull, key, fieldTypes, list)) {
                if (!handleSymbolDefinitionFieldInDefinedBy(currentElement, checkFull, key, fieldTypes, list)) {
                    if (!handleSymbolDefinitionFieldInArgument(currentElement, checkFull, key, fieldTypes, list)) {
                        if (!handleSymbolDefinitionFieldInTypeValue(currentElement, checkFull, key, fieldTypes, list)) {
                            //Future Use
                        }
                    }
                }
            }
        }

        return currentElement instanceof Asn1SymbolFieldReference;
    }

    private static boolean handleSymbolDefinitionFieldInConstructor(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                                    @Nullable final String key, @NotNull final Asn1FieldType[] fieldTypes, @NotNull final List<Asn1SymbolDefinitionField> list) {
        final Asn1SymbolConstructor symbolConstructor = PsiTreeUtil.getParentOfType(currentElement, Asn1SymbolConstructor.class);
        if (symbolConstructor != null) {
            final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(symbolConstructor, Asn1SymbolDefinition.class);
            //Symbol, definition of type value definition with symbol type
            if (symbolDefinition != null && symbolDefinition.getSymbolElement() == Asn1SymbolElement.ObjectClassDefinition) {
                handleSymbolDefinitionFieldFromSymbolDefinition(symbolDefinition, checkFull, key, fieldTypes, list);
            }
        }

        return symbolConstructor != null;
    }

    private static boolean handleSymbolDefinitionFieldInDefinedBy(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                                    @Nullable final String key, @NotNull final Asn1FieldType[] fieldTypes, @NotNull final List<Asn1SymbolDefinitionField> list) {
        final Asn1SymbolTypeDefinedBy symbolTypeDefinedBy = PsiTreeUtil.getParentOfType(currentElement, Asn1SymbolTypeDefinedBy.class);
        if (symbolTypeDefinedBy != null) {
            final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(symbolTypeDefinedBy, Asn1SymbolDefinition.class);
            //Symbol, definition of type value definition with symbol type
            if (symbolDefinition != null) {
                handleSymbolDefinitionFieldFromSymbolDefinition(symbolDefinition, checkFull, key, fieldTypes, list);
            }
        }

        return symbolTypeDefinedBy != null;
    }

    private static void handleSymbolDefinitionFieldFromSymbolDefinition(Asn1SymbolDefinition symbolDefinition, boolean checkFull, @Nullable String key, @Nullable Asn1FieldType[] types, List<Asn1SymbolDefinitionField> list) {
        if (symbolDefinition.getSymbolContent() != null) {
            list.addAll(
                    symbolDefinition.getSymbolContent().getSymbolDefinitionFieldList().stream()
                            .filter(item -> ArrayUtils.contains(types, item.getFieldType()))
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }
    }
    //endregion

    //region Symbol Constant
    @NotNull
    public static List<Asn1SymbolConstantElement> findSymbolConstants(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findSymbolConstants(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1SymbolConstantElement> findSymbolConstants(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull,
                                                                      @Nullable final String key) {
        final List<Asn1SymbolConstantElement> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ModuleDefinition> moduleDefinitions = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ModuleDefinition.class);
                for (final Asn1ModuleDefinition moduleDefinition : moduleDefinitions) {
                    if (moduleDefinition.getModuleContent() != null) {
                        for (final Asn1SymbolDefinition symbolDefinition : moduleDefinition.getModuleContent().getSymbolDefinitionList()) {
                            handleSymbolConstantFromSymbolDefinition(symbolDefinition, checkFull, key, list);
                        }
                    }
                }
            });
        } else {
            if (!handleSymbolConstantInReference(currentElement, checkFull, key, list)) {
                //Future Use
            }
        }

        return list;
    }

    private static boolean handleSymbolConstantInReference(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                           @Nullable final String key, @NotNull final List<Asn1SymbolConstantElement> list) {
        if (currentElement instanceof Asn1SymbolTypeReferenceValue) {
            final Asn1SymbolTypeReferenceValue symbolTypeReferenceValue = (Asn1SymbolTypeReferenceValue) currentElement;
            final Asn1SymbolTypeReferenceValue prevSymbolTypeReferenceValue = PsiTreeUtil.getPrevSiblingOfType(symbolTypeReferenceValue, Asn1SymbolTypeReferenceValue.class);
            if (prevSymbolTypeReferenceValue == null) {
                if (!handleSymbolConstantInReferenceInValueType(symbolTypeReferenceValue, checkFull, key, list)) {
                    if (!handleSymbolConstantInReferenceInModifier(symbolTypeReferenceValue, checkFull, key, list)) {
                        if (!handleSymbolConstantInReferenceInSymbolDefinition(symbolTypeReferenceValue, checkFull, key, list)) {
                            //Future Use
                        }
                    }
                }
            } else {
                final PsiElement resolvedElement = resolveFromMultiReference(prevSymbolTypeReferenceValue);
                if (resolvedElement != null) {
                    if (resolvedElement instanceof Asn1SymbolDefinition) {
                        final Asn1SymbolDefinition symbolDefinition = (Asn1SymbolDefinition) resolvedElement;
                        handleSymbolConstantFromSymbolDefinition(symbolDefinition, checkFull, key, list);
                    } else if (resolvedElement instanceof Asn1SymbolDefinitionField) {
                        final Asn1SymbolDefinitionField symbolDefinitionField = (Asn1SymbolDefinitionField) resolvedElement;
                        handleSymbolConstantFromSymbolDefinitionField(symbolDefinitionField, checkFull, key, list);
                    }
                }
            }
        }

        return currentElement instanceof Asn1SymbolTypeReferenceValue;
    }

    private static boolean handleSymbolConstantInReferenceInModifier(Asn1SymbolTypeReferenceValue symbolTypeReferenceValue, final boolean checkFull,
                                                                     @Nullable final String key, @NotNull final List<Asn1SymbolConstantElement> list) {
        final Asn1ModifierElement modifierElement = PsiTreeUtil.getParentOfType(symbolTypeReferenceValue, Asn1ModifierElement.class);
        if (modifierElement != null && modifierElement.getSymbolTypeReference() != null) {
            final PsiElement element = resolveFromMultiReference(modifierElement.getSymbolTypeReference());
            if (element != null) {
                if (element instanceof Asn1SymbolDefinitionField) {
                    handleSymbolConstantFromSymbolDefinitionField((Asn1SymbolDefinitionField) element, checkFull, key, list);
                } else if (element instanceof Asn1SymbolDefinition) {
                    handleSymbolConstantFromSymbolDefinition((Asn1SymbolDefinition) element, checkFull, key, list);
                }
            } else {
                final Asn1SymbolDefinitionField symbolDefinitionField = PsiTreeUtil.getParentOfType(modifierElement, Asn1SymbolDefinitionField.class);
                if (symbolDefinitionField != null) {
                    handleSymbolConstantFromSymbolDefinitionField(symbolDefinitionField, checkFull, key, list);
                } else {
                    final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(modifierElement, Asn1SymbolDefinition.class);
                    if (symbolDefinition != null) {
                        handleSymbolConstantFromSymbolDefinition(symbolDefinition, checkFull, key, list);
                    }
                }
            }
        }

        return modifierElement != null;
    }

    private static boolean handleSymbolConstantInReferenceInSymbolDefinition(Asn1SymbolTypeReferenceValue symbolTypeReferenceValue, final boolean checkFull,
                                                                             @Nullable final String key, @NotNull final List<Asn1SymbolConstantElement> list) {
        final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(symbolTypeReferenceValue, Asn1SymbolDefinition.class);
        if (symbolDefinition != null) {
            if (symbolDefinition.getSymbolElement() != null && symbolDefinition.getSymbolElement() == Asn1SymbolElement.ValueDefinition) {
                final PsiElement element = resolveType(symbolDefinition.getSymbolIdentifierType());
                if (element != null) {
                    if (element instanceof Asn1SymbolDefinition && ((Asn1SymbolDefinition) element).getSymbolConstantDefinition() != null &&
                            (((Asn1SymbolDefinition) element).getSymbolElement() == Asn1SymbolElement.TypeDefinition ||
                                    ((Asn1SymbolDefinition) element).getSymbolElement() == Asn1SymbolElement.ObjectClassDefinition)) {
                        handleSymbolConstantFromSymbolDefinition((Asn1SymbolDefinition) element, checkFull, key, list);
                    }
                } else {
                    if (symbolDefinition.getSymbolIdentifierType() != null) {
                        final PsiElement identifierType = resolveType(symbolDefinition.getSymbolIdentifierType());
                        if (identifierType != null && identifierType instanceof Asn1SymbolDefinition && ((Asn1SymbolDefinition) identifierType).getSymbolElement() == Asn1SymbolElement.TypeDefinition) {
                            handleSymbolConstantFromSymbolDefinition((Asn1SymbolDefinition) identifierType, checkFull, key, list);
                        }
                    }
                }
            }
        }

        return symbolDefinition != null;
    }

    private static boolean handleSymbolConstantInReferenceInValueType(Asn1SymbolTypeReferenceValue symbolTypeReferenceValue, final boolean checkFull,
                                                                      @Nullable final String key, @NotNull final List<Asn1SymbolConstantElement> list) {
        final Asn1SymbolValueTypeContentLine symbolValueTypeContentLine = PsiTreeUtil.getParentOfType(symbolTypeReferenceValue, Asn1SymbolValueTypeContentLine.class);
        if (symbolValueTypeContentLine != null && symbolValueTypeContentLine.getSymbolFieldReference() != null) {
            final PsiElement element = resolveFromMultiReference(symbolValueTypeContentLine.getSymbolFieldReference());
            if (element != null && element instanceof Asn1SymbolDefinitionField) {
                handleSymbolConstantFromSymbolDefinitionField((Asn1SymbolDefinitionField) element, checkFull, key, list);
            }
        }

        return symbolValueTypeContentLine != null;
    }

    private static void handleSymbolConstantFromSymbolDefinitionField(Asn1SymbolDefinitionField symbolDefinitionField, boolean checkFull, @Nullable String key, @NotNull List<Asn1SymbolConstantElement> list) {
        if (symbolDefinitionField.getSymbolConstantDefinition() != null) {
            list.addAll(
                    symbolDefinitionField.getSymbolConstantDefinition().getSymbolConstantElementList().stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }
    }

    private static void handleSymbolConstantFromSymbolDefinition(Asn1SymbolDefinition symbolDefinition, boolean checkFull, @Nullable String key, @NotNull List<Asn1SymbolConstantElement> list) {
        if (symbolDefinition.getSymbolConstantDefinition() != null) {
            list.addAll(
                    symbolDefinition.getSymbolConstantDefinition().getSymbolConstantElementList().stream()
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }
    }
    //endregion

    //region Parameter (Type)
    @NotNull
    public static List<Asn1ParameterForType> findTypeParameters(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findTypeParameters(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ParameterForType> findTypeParameters(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull,
                                                                @Nullable final String key) {
        final List<Asn1ParameterForType> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ModuleDefinition> moduleDefinitions = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ModuleDefinition.class);
                for (final Asn1ModuleDefinition moduleDefinition : moduleDefinitions) {
                    if (moduleDefinition.getModuleContent() != null) {
                        for (final Asn1SymbolDefinition symbolDefinition : moduleDefinition.getModuleContent().getSymbolDefinitionList()) {
                            handleTypeParameterFromSymbolDefinition(symbolDefinition, checkFull, key, list);
                        }
                    }
                }
            });
        } else {
            if (!handleTypeParameterInReferenceType(currentElement, checkFull, key, list)) {
                //for future use
            }
        }

        return list;
    }

    private static boolean handleTypeParameterInReferenceType(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                              @Nullable final String key, @NotNull final List<Asn1ParameterForType> list) {
        final Asn1SymbolTypeReference symbolTypeReference = PsiTreeUtil.getParentOfType(currentElement, Asn1SymbolTypeReference.class);
        if (symbolTypeReference != null && symbolTypeReference.getSymbolTypeReferenceValueList().size() == 1) {
            final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(symbolTypeReference, Asn1SymbolDefinition.class);
            if (symbolDefinition != null) {
                handleTypeParameterFromSymbolDefinition(symbolDefinition, checkFull, key, list);
            }
        }

        return symbolTypeReference != null;
    }

    private static void handleTypeParameterFromSymbolDefinition(Asn1SymbolDefinition symbolDefinition, boolean checkFull, @Nullable String key, List<Asn1ParameterForType> list) {
        if (symbolDefinition.getParameterDefinition() != null) {
            list.addAll(
                    symbolDefinition.getParameterDefinition().getParameterElementList().stream()
                            .filter(item -> item.getParameterForType() != null)
                            .map(Asn1ParameterElement::getParameterForType)
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }
    }
    //endregion

    //region Parameter (Set)
    @NotNull
    public static List<Asn1ParameterForSet> findSetParameters(@NotNull final Project project, @Nullable final PsiElement currentElement) {
        return findSetParameters(project, currentElement, true, null);
    }

    @NotNull
    public static List<Asn1ParameterForSet> findSetParameters(@NotNull final Project project, @Nullable final PsiElement currentElement, final boolean checkFull,
                                                              @Nullable final String key) {
        final List<Asn1ParameterForSet> list = new ArrayList<>();

        if (currentElement == null) {
            visitFiles(project, psiFile -> {
                final Collection<Asn1ModuleDefinition> moduleDefinitions = PsiTreeUtil.findChildrenOfType(psiFile, Asn1ModuleDefinition.class);
                for (final Asn1ModuleDefinition moduleDefinition : moduleDefinitions) {
                    if (moduleDefinition.getModuleContent() != null) {
                        for (final Asn1SymbolDefinition symbolDefinition : moduleDefinition.getModuleContent().getSymbolDefinitionList()) {
                            handleSetParameterFromSymbolDefinition(symbolDefinition, checkFull, key, list);
                        }
                    }
                }
            });
        } else {
            if (!handleSetParameterInReferenceType(currentElement, checkFull, key, list)) {
                //for future use
            }
        }

        return list;
    }

    private static boolean handleSetParameterInReferenceType(@NotNull final PsiElement currentElement, final boolean checkFull,
                                                             @Nullable final String key, @NotNull final List<Asn1ParameterForSet> list) {
        final Asn1SymbolTypeReference symbolTypeReference = PsiTreeUtil.getParentOfType(currentElement, Asn1SymbolTypeReference.class);
        if (symbolTypeReference != null && symbolTypeReference.getSymbolTypeReferenceValueList().size() == 1) {
            final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(symbolTypeReference, Asn1SymbolDefinition.class);
            if (symbolDefinition != null) {
                handleSetParameterFromSymbolDefinition(symbolDefinition, checkFull, key, list);
            }
        }

        return symbolTypeReference != null;
    }

    private static void handleSetParameterFromSymbolDefinition(Asn1SymbolDefinition symbolDefinition, boolean checkFull, @Nullable String key, List<Asn1ParameterForSet> list) {
        if (symbolDefinition.getParameterDefinition() != null) {
            list.addAll(
                    symbolDefinition.getParameterDefinition().getParameterElementList().stream()
                            .filter(item -> item.getParameterForSet() != null)
                            .map(Asn1ParameterElement::getParameterForSet)
                            .filter(item -> checkString(key, item.getName(), checkFull))
                            .collect(Collectors.toList())
            );
        }
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

    @Nullable
    public static PsiElement resolveFromMultiReference(@NotNull final PsiElement psiElement) {
        for (final PsiReference psiReference : psiElement.getReferences()) {
            final PsiElement resolvedElement = psiReference.resolve();
            if (resolvedElement != null)
                return resolvedElement;
        }

        return null;
    }

    @Nullable
    public static PsiElement resolveType(@Nullable final Asn1SymbolType symbolType) {
        if (symbolType == null || symbolType.getSymbolTypeReference() == null)
            return null;

        return resolveReference(symbolType.getSymbolTypeReference());
    }

    @Nullable
    public static PsiElement resolveReference(@Nullable final Asn1SymbolTypeReference symbolTypeReference) {
        if (symbolTypeReference == null || symbolTypeReference.getSymbolTypeReferenceValueList().isEmpty())
            return null;

        return resolveFromMultiReference(symbolTypeReference.getSymbolTypeReferenceValueList().get(symbolTypeReference.getSymbolTypeReferenceValueList().size() - 1));
    }
    //endregion

    private Asn1ReferenceUtils() {
    }
}
