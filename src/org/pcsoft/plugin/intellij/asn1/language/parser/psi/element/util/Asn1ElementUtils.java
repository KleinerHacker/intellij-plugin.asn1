package org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.util;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.ElementBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportExportSymbolRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModifierElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForSet;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForSetIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterForTypeIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolConstantElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolConstantIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolContentVersion;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolFieldIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolType;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeForAssignment;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeForSet;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeForValue;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolTypeReferenceValue;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueBoolean;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueInteger;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueObjectIdentifierElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueReal;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueSetElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueSetElementContent;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1SymbolValueString;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TagDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ModuleDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ReferenceUtils;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1SetParameterReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1SymbolConstantReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1SymbolDefinitionFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1SymbolDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1TypeParameterReference;
import org.pcsoft.plugin.intellij.asn1.type.Asn1FieldType;
import org.pcsoft.plugin.intellij.asn1.type.Asn1Modifier;
import org.pcsoft.plugin.intellij.asn1.type.Asn1SymbolElement;
import org.pcsoft.plugin.intellij.asn1.type.Asn1TagType;
import org.pcsoft.plugin.intellij.asn1.type.Asn1TaggingType;

import javax.swing.Icon;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 */
public interface Asn1ElementUtils {

    static String getNameNodeText(final PsiElement element) {
        final ASTNode astNode = getNameNode(element);
        if (astNode != null) {
            return astNode.getText();
        } else {
            return null;
        }
    }

    static ASTNode getNameNode(final PsiElement element) {
        if (element == null || element.getNode() == null)
            return null;

        ASTNode astNode = element.getNode().findChildByType(Asn1GenElementFactory.NAME);
        if (astNode != null) {
            return astNode;
        } else {
            return null;
        }
    }

    //region Element - Module Definition
    static String getName(final Asn1ModuleDefinition moduleDefinition) {
        return getNameNodeText(moduleDefinition.getModuleIdentifier());
    }

    static PsiElement setName(final Asn1ModuleDefinition moduleDefinition, final String newName) {
        final ASTNode astNode = getNameNode(moduleDefinition.getModuleIdentifier());
        if (astNode != null) {
            //TODO
        }

        return moduleDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1ModuleDefinition moduleDefinition) {
        final ASTNode astNode = getNameNode(moduleDefinition.getModuleIdentifier());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ModuleDefinition moduleDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return moduleDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return moduleDefinition.getContainingFile() == null ? null : moduleDefinition.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return Asn1Icons.FILE;
            }
        };
    }

    static String getName(final Asn1ModuleIdentifier moduleIdentifier) {
        return moduleIdentifier.getText();
    }
    //endregion

    //region Element - Import / Export
    //region Import Element Module
    static String getName(final Asn1ModuleRef moduleRef) {
        return getNameNodeText(moduleRef);
    }

    static PsiElement setName(final Asn1ModuleRef moduleRef, final String newName) {
        final ASTNode astNode = getNameNode(moduleRef);
        if (astNode != null) {
            //TODO
        }

        return moduleRef;
    }

    static PsiElement getNameIdentifier(final Asn1ModuleRef moduleRef) {
        final ASTNode astNode = getNameNode(moduleRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    @NotNull
    static PsiReference getReference(final Asn1ModuleRef moduleRef) {
        return new Asn1ModuleDefinitionReference(moduleRef);
    }
    //endregion

    //region Import / Export Element Type Reference
    static String getName(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        return getNameNodeText(importExportSymbolRef);
    }

    static PsiElement setName(final Asn1ImportExportSymbolRef importExportSymbolRef, final String newName) {
        final ASTNode astNode = getNameNode(importExportSymbolRef);
        if (astNode != null) {
            //TODO
        }

        return importExportSymbolRef;
    }

    static PsiElement getNameIdentifier(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        final ASTNode astNode = getNameNode(importExportSymbolRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    @NotNull
    static PsiReference getReference(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        return new Asn1SymbolDefinitionReference(importExportSymbolRef);
    }
    //endregion

    //endregion

    //region Symbol Definition
    static String getName(final Asn1SymbolDefinition symbolDefinition) {
        return getNameNodeText(symbolDefinition.getSymbolIdentifier());
    }

    static PsiElement setName(final Asn1SymbolDefinition symbolDefinition, final String newName) {
        final ASTNode astNode = getNameNode(symbolDefinition.getSymbolIdentifier());
        if (astNode != null) {
            //TODO
        }

        return symbolDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1SymbolDefinition symbolDefinition) {
        final ASTNode astNode = getNameNode(symbolDefinition.getSymbolIdentifier());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1SymbolDefinition symbolDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return symbolDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return symbolDefinition.getContainingFile() == null ? null : symbolDefinition.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                final Asn1SymbolElement symbolElement = symbolDefinition.getSymbolElement();
                if (symbolElement == null)
                    return null;
                if (symbolElement == Asn1SymbolElement.CopyDefinition) {
                    final Asn1SymbolElement symbolElementFromReferenceType = symbolDefinition.getSymbolElementFromReferenceType();
                    if (symbolElementFromReferenceType != null) {
                        return ElementBase.buildRowIcon(symbolElement.getIcon(), symbolElementFromReferenceType.getIcon());
                    }
                }

                return symbolElement.getIcon();
            }
        };
    }

    @Nullable
    static Asn1SymbolElement getSymbolElement(final Asn1SymbolDefinition symbolDefinition) {
        if (symbolDefinition.getSymbolIdentifierType() != null) {
            if (symbolDefinition.getSymbolTypeForValue() != null) {
                return Asn1SymbolElement.ValueDefinition;
            } else if (symbolDefinition.getSymbolTypeForSet() != null) {
                if (!symbolDefinition.getSymbolTypeForSet().getSymbolValueSet().getSymbolValueSetElementList().isEmpty()) {
                    final Asn1SymbolValueSetElement element = symbolDefinition.getSymbolTypeForSet().getSymbolValueSet().getSymbolValueSetElementList().get(0);
                    if (!element.getSymbolValueSetElementContentList().isEmpty()) {
                        final Asn1SymbolValueSetElementContent content = element.getSymbolValueSetElementContentList().get(0);
                        if (content.getSymbolTypeReference() != null) {
                            return Asn1SymbolElement.ObjectSetDefinition;
                        } else if (content.getSymbolValue() != null) {
                            return Asn1SymbolElement.ValueSetDefinition;
                        }
                    }
                } else {
                    return Asn1SymbolElement.SetDefinition;
                }
            }
        } else if (symbolDefinition.getSymbolTypeForAssignment() != null) {
            if (symbolDefinition.getSymbolTypeForAssignment().getSymbolType() != null) {
                if (symbolDefinition.getSymbolTypeForAssignment().getSymbolType().getSymbolTypeNative() != null) {
                    return Asn1SymbolElement.TypeDefinition;
                } else if (symbolDefinition.getSymbolTypeForAssignment().getSymbolType().getSymbolTypeReference() != null &&
                        !symbolDefinition.getSymbolTypeForAssignment().getSymbolType().getSymbolTypeReference().getSymbolTypeReferenceValueList().isEmpty()) {
                    return Asn1SymbolElement.CopyDefinition;
                }
            } else if (symbolDefinition.getSymbolTypeForAssignment().getSymbolTypeKeyword() != null) {
                if (symbolDefinition.getSymbolTypeForAssignment().getSymbolTypeKeyword().getText().equals("ENUMERATED")) {
                    return Asn1SymbolElement.EnumeratedDefinition;
                } else if (symbolDefinition.getSymbolTypeForAssignment().getSymbolTypeKeyword().getText().equals("CLASS")) {
                    return Asn1SymbolElement.ObjectClassDefinition;
                } else if (symbolDefinition.getSymbolTypeForAssignment().getSymbolTypeKeyword().getText().equals("CHOICE")) {
                    return Asn1SymbolElement.ChoiceDefinition;
                }
            }
        }

        return null;
    }

    @Nullable
    static Asn1SymbolElement getSymbolElementFromReferenceType(final Asn1SymbolDefinition symbolDefinition) {
        if (symbolDefinition.getSymbolTypeForAssignment() == null || symbolDefinition.getSymbolTypeForAssignment().getSymbolType() == null ||
                symbolDefinition.getSymbolTypeForAssignment().getSymbolType().getSymbolTypeReference() == null)
            return null;

        final List<Asn1SymbolTypeReferenceValue> referenceValueList = symbolDefinition.getSymbolTypeForAssignment().getSymbolType().getSymbolTypeReference().getSymbolTypeReferenceValueList();
        final Asn1SymbolTypeReferenceValue referenceValue = referenceValueList.get(referenceValueList.size() - 1);
        final PsiElement resolvedElement = Asn1ReferenceUtils.resolveFromMultiReference(referenceValue);
        if (resolvedElement != null) {
            if (resolvedElement instanceof Asn1SymbolDefinition) {
                final Asn1SymbolElement symbolElement = ((Asn1SymbolDefinition) resolvedElement).getSymbolElement();
                if (symbolElement == Asn1SymbolElement.CopyDefinition) {
                    return ((Asn1SymbolDefinition) resolvedElement).getSymbolElementFromReferenceType();
                } else {
                    return symbolElement;
                }
            } else if (resolvedElement instanceof Asn1SymbolDefinitionField) {
                return Asn1SymbolElement.FieldTypeDefinition;
            }
        }

        return null;
    }

    static String getName(final Asn1SymbolIdentifier symbolIdentifier) {
        return getNameNodeText(symbolIdentifier);
    }

    //region PSI Elements
    @NotNull
    static Asn1SymbolIdentifier getSymbolIdentifier(final Asn1SymbolDefinition symbolDefinition) {
        return PsiTreeUtil.getChildOfType(symbolDefinition, Asn1SymbolIdentifier.class);
    }

    @Nullable
    static Asn1SymbolType getSymbolIdentifierType(final Asn1SymbolDefinition symbolDefinition) {
        return PsiTreeUtil.getChildOfType(symbolDefinition, Asn1SymbolType.class);
    }

    @Nullable
    static Asn1TagDefinition getTagDefinition(final Asn1SymbolDefinition symbolDefinition) {
        return PsiTreeUtil.getChildOfType(symbolDefinition, Asn1TagDefinition.class);
    }

    @Nullable
    static Asn1SymbolTypeForSet getSymbolTypeForSet(final Asn1SymbolDefinition symbolDefinition) {
        return PsiTreeUtil.getChildOfType(symbolDefinition, Asn1SymbolTypeForSet.class);
    }

    @Nullable
    static Asn1SymbolTypeForValue getSymbolTypeForValue(final Asn1SymbolDefinition symbolDefinition) {
        return PsiTreeUtil.getChildOfType(symbolDefinition, Asn1SymbolTypeForValue.class);
    }

    @Nullable
    static Asn1SymbolTypeForAssignment getSymbolTypeForAssignment(final Asn1SymbolDefinition symbolDefinition) {
        return PsiTreeUtil.getChildOfType(symbolDefinition, Asn1SymbolTypeForAssignment.class);
    }

    @Nullable
    static Asn1ParameterDefinition getParameterDefinition(final Asn1SymbolDefinition symbolDefinition) {
        return PsiTreeUtil.getChildOfType(symbolDefinition, Asn1ParameterDefinition.class);
    }
    //endregion
    //endregion

    //region Symbol Definition Field
    static String getName(final Asn1SymbolDefinitionField symbolDefinitionField) {
        return getNameNodeText(symbolDefinitionField.getSymbolFieldIdentifier());
    }

    static PsiElement setName(final Asn1SymbolDefinitionField symbolDefinitionField, final String newName) {
        final ASTNode astNode = getNameNode(symbolDefinitionField.getSymbolFieldIdentifier());
        if (astNode != null) {
            //TODO
        }

        return symbolDefinitionField;
    }

    static PsiElement getNameIdentifier(final Asn1SymbolDefinitionField symbolDefinitionField) {
        final ASTNode astNode = getNameNode(symbolDefinitionField.getSymbolFieldIdentifier());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1SymbolDefinitionField symbolDefinitionField) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return symbolDefinitionField.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(symbolDefinitionField, Asn1SymbolDefinition.class);
                if (symbolDefinition != null) {
                    return symbolDefinition.getName();
                }

                return symbolDefinitionField.getContainingFile() == null ? null : symbolDefinitionField.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                final Asn1FieldType fieldType = symbolDefinitionField.getFieldType();
                if (fieldType == null)
                    return null;
                if (symbolDefinitionField.getSymbolContent() != null) {
                    final Asn1SymbolElement symbolElement = symbolDefinitionField.getSymbolElement();
                    if (symbolElement != null) {
                        return ElementBase.buildRowIcon(fieldType.getIcon(), symbolElement.getIcon());
                    } else {
                        return ElementBase.buildRowIcon(fieldType.getIcon(), AllIcons.Nodes.AnonymousClass);
                    }
                }

                return fieldType.getIcon();
            }
        };
    }

    @Nullable
    static Asn1FieldType getFieldType(final Asn1SymbolDefinitionField symbolDefinitionField) {
        if (symbolDefinitionField.getSymbolFieldIdentifier().getPrevSibling() != null && symbolDefinitionField.getSymbolFieldIdentifier().getPrevSibling().getText().equals("&")) {
            return Asn1FieldType.ObjectClassField;
        } else {
            return Asn1FieldType.TypeField;
        }
    }

    @Nullable
    static Asn1SymbolElement getSymbolElement(final Asn1SymbolDefinitionField symbolDefinitionField) {
        if (symbolDefinitionField.getSymbolType() != null) {
            if (symbolDefinitionField.getSymbolType().getSymbolTypeNative() != null) {
                return Asn1SymbolElement.TypeDefinition;
            } else if (symbolDefinitionField.getSymbolType().getSymbolTypeReference() != null) {
                return Asn1SymbolElement.CopyDefinition; //TODO
            }
        } else if (symbolDefinitionField.getSymbolTypeKeyword() != null) {
            if (symbolDefinitionField.getSymbolTypeKeyword().getText().equals("ENUMERATED")) {
                return Asn1SymbolElement.EnumeratedDefinition;
            } else if (symbolDefinitionField.getSymbolTypeKeyword().getText().equals("CLASS")) {
                return Asn1SymbolElement.ObjectClassDefinition;
            } else if (symbolDefinitionField.getSymbolTypeKeyword().getText().equals("CHOICE")) {
                return Asn1SymbolElement.ChoiceDefinition;
            }
        }

        return null;
    }

    static String getName(final Asn1SymbolFieldIdentifier symbolFieldIdentifier) {
        return getNameNodeText(symbolFieldIdentifier);
    }

    @Nullable
    static Integer getVersionNumber(final Asn1SymbolContentVersion symbolContentVersion) {
        final ASTNode astNode = symbolContentVersion.getNode().findChildByType(Asn1GenElementFactory.NUMBER);
        if (astNode != null) {
            try {
                return Integer.parseInt(astNode.getText());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
    //endregion

    //region References
    static String getName(final Asn1SymbolTypeReferenceValue symbolTypeReferenceQualifier) {
        return getNameNodeText(symbolTypeReferenceQualifier);
    }

    static PsiElement setName(final Asn1SymbolTypeReferenceValue symbolTypeReferenceQualifier, final String newName) {
        final ASTNode astNode = getNameNode(symbolTypeReferenceQualifier);
        if (astNode != null) {
            //TODO
        }

        return symbolTypeReferenceQualifier;
    }

    static PsiElement getNameIdentifier(final Asn1SymbolTypeReferenceValue symbolTypeReferenceQualifier) {
        final ASTNode astNode = getNameNode(symbolTypeReferenceQualifier);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    @NotNull
    static PsiReference[] getReferences(final Asn1SymbolTypeReferenceValue symbolTypeReferenceQualifier) {
        return new PsiReference[]{
                new Asn1SymbolDefinitionReference(symbolTypeReferenceQualifier),
                new Asn1SymbolDefinitionFieldReference(symbolTypeReferenceQualifier),
                new Asn1ModuleDefinitionReference(symbolTypeReferenceQualifier),
                new Asn1SymbolConstantReference(symbolTypeReferenceQualifier),
                new Asn1TypeParameterReference(symbolTypeReferenceQualifier),
                new Asn1SetParameterReference(symbolTypeReferenceQualifier)
        };
    }

    static String getName(final Asn1SymbolFieldReference symbolFieldReference) {
        return getNameNodeText(symbolFieldReference);
    }

    static PsiElement setName(final Asn1SymbolFieldReference symbolValueTypeFieldReference, final String newName) {
        final ASTNode astNode = getNameNode(symbolValueTypeFieldReference);
        if (astNode != null) {
            //TODO
        }

        return symbolValueTypeFieldReference;
    }

    static PsiElement getNameIdentifier(final Asn1SymbolFieldReference symbolFieldReference) {
        final ASTNode astNode = getNameNode(symbolFieldReference);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    @NotNull
    static PsiReference[] getReferences(final Asn1SymbolFieldReference symbolFieldReference) {
        return new PsiReference[]{
                new Asn1SymbolDefinitionFieldReference(symbolFieldReference)
        };
    }
    //endregion

    //region Element - Tag Definition
    static int getTagNumber(final Asn1TagDefinition tagDefinition) {
        final ASTNode astNode = tagDefinition.getNode().findChildByType(Asn1GenElementFactory.NUMBER);
        if (astNode != null) {
            return Integer.parseInt(astNode.getText());
        } else {
            return -1;
        }
    }

    static Asn1TaggingType getTaggingType(final Asn1TagDefinition tagDefinition) {
        final ASTNode astNode = tagDefinition.getNode().findChildByType(Asn1CustomElementFactory.KEYWORD);
        if (astNode != null) {
            try {
                return Asn1TaggingType.valueOf(astNode.getText().toUpperCase());
            } catch (IllegalArgumentException e) {
                return Asn1TaggingType.INHERITED;
            }
        } else {
            return Asn1TaggingType.INHERITED;
        }
    }

    static Asn1TagType getTagType(final Asn1TagDefinition tagDefinition) {
        final ASTNode astNode = tagDefinition.getNode().findChildByType(Asn1CustomElementFactory.KEYWORD);
        if (astNode != null) {
            return Asn1TagType.APPLICATION;
        } else {
            return Asn1TagType.CONSTRUCTED;
        }
    }
    //endregion

    //region Constants
    static String getName(final Asn1SymbolConstantElement symbolConstantElement) {
        return getNameNodeText(symbolConstantElement.getSymbolConstantIdentifier());
    }

    static PsiElement setName(final Asn1SymbolConstantElement symbolConstantElement, final String newName) {
        final ASTNode astNode = getNameNode(symbolConstantElement.getSymbolConstantIdentifier());
        if (astNode != null) {
            //TODO
        }

        return symbolConstantElement;
    }

    static PsiElement getNameIdentifier(final Asn1SymbolConstantElement symbolConstantElement) {
        final ASTNode astNode = getNameNode(symbolConstantElement.getSymbolConstantIdentifier());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1SymbolConstantElement symbolConstantElement) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return symbolConstantElement.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(symbolConstantElement, Asn1SymbolDefinition.class);
                if (symbolDefinition != null) {
                    return symbolDefinition.getName();
                }

                return symbolConstantElement.getContainingFile() == null ? null : symbolConstantElement.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return ElementBase.overlayIcons(AllIcons.Nodes.Field, AllIcons.Nodes.FinalMark);
            }
        };
    }

    static String getName(final Asn1SymbolConstantIdentifier symbolConstantIdentifier) {
        return getNameNodeText(symbolConstantIdentifier);
    }
    //endregion

    //region Values
    static Boolean getBoolean(final Asn1SymbolValueBoolean symbolValueBoolean) {
        if (symbolValueBoolean.getText().equals("TRUE"))
            return true;
        else if (symbolValueBoolean.getText().equals("FALSE"))
            return false;

        return null;
    }

    static BigInteger getInteger(final Asn1SymbolValueInteger symbolValueInteger) {
        try {
            return new BigInteger(symbolValueInteger.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static BigDecimal getDecimal(final Asn1SymbolValueReal symbolValueReal) {
        try {
            return new BigDecimal(symbolValueReal.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static String getString(final Asn1SymbolValueString symbolValueString) {
        return symbolValueString.getText().substring(1, symbolValueString.getText().length() - 1).replace("\"\"", "\"");
    }

    static String getIdentifier(final Asn1SymbolValueObjectIdentifierElement symbolValueObjectIdentifierElement) {
        final ASTNode astNode = symbolValueObjectIdentifierElement.getNode().findChildByType(Asn1GenElementFactory.NAME);
        if (astNode != null) {
            return astNode.getText();
        }

        return null;
    }

    static BigInteger getNumber(final Asn1SymbolValueObjectIdentifierElement symbolValueObjectIdentifierElement) {
        final ASTNode astNode = symbolValueObjectIdentifierElement.getNode().findChildByType(Asn1GenElementFactory.NUMBER);
        if (astNode != null) {
            try {
                return new BigInteger(astNode.getText());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
    //endregion

    //region Modifier
    @Nullable
    static Asn1Modifier getModifier(final Asn1ModifierElement modifierDefinitionElement) {
        final ASTNode astNode = modifierDefinitionElement.getNode().findChildByType(Asn1CustomElementFactory.KEYWORD);
        if (astNode != null) {
            switch (astNode.getText()) {
                case "OPTIONAL":
                    return Asn1Modifier.OPTIONAL;
                case "DEFAULT":
                    return Asn1Modifier.DEFAULT;
                case "UNIQUE":
                    return Asn1Modifier.UNIQUE;
            }
        }

        return null;
    }
    //endregion

    //region Parameter
    static String getName(final Asn1ParameterForType parameterForType) {
        return getNameNodeText(parameterForType.getParameterForTypeIdentifier());
    }

    static PsiElement setName(final Asn1ParameterForType parameterForType, final String newName) {
        final ASTNode astNode = getNameNode(parameterForType.getParameterForTypeIdentifier());
        if (astNode != null) {
            //TODO
        }

        return parameterForType;
    }

    static PsiElement getNameIdentifier(final Asn1ParameterForType parameterForType) {
        final ASTNode astNode = getNameNode(parameterForType.getParameterForTypeIdentifier());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ParameterForType parameterForType) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return parameterForType.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(parameterForType, Asn1SymbolDefinition.class);
                if (symbolDefinition != null)
                    return symbolDefinition.getName();

                return parameterForType.getContainingFile() == null ? null : parameterForType.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return ElementBase.overlayIcons(AllIcons.Nodes.Parameter, AllIcons.Nodes.StaticMark);
            }
        };
    }

    static String getName(final Asn1ParameterForTypeIdentifier parameterForTypeIdentifier) {
        return getNameNodeText(parameterForTypeIdentifier.getNavigationElement());
    }

    static String getName(final Asn1ParameterForSet parameterForSet) {
        return getNameNodeText(parameterForSet.getParameterForSetIdentifier());
    }

    static PsiElement setName(final Asn1ParameterForSet parameterForSet, final String newName) {
        final ASTNode astNode = getNameNode(parameterForSet.getParameterForSetIdentifier());
        if (astNode != null) {
            //TODO
        }

        return parameterForSet;
    }

    static PsiElement getNameIdentifier(final Asn1ParameterForSet parameterForSet) {
        final ASTNode astNode = getNameNode(parameterForSet.getParameterForSetIdentifier());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ParameterForSet parameterForSet) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return parameterForSet.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1SymbolDefinition symbolDefinition = PsiTreeUtil.getParentOfType(parameterForSet, Asn1SymbolDefinition.class);
                if (symbolDefinition != null)
                    return symbolDefinition.getName();

                return parameterForSet.getContainingFile() == null ? null : parameterForSet.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Parameter;
            }
        };
    }

    static String getName(final Asn1ParameterForSetIdentifier parameterForSetIdentifier) {
        return getNameNodeText(parameterForSetIdentifier);
    }
    //endregion
}
