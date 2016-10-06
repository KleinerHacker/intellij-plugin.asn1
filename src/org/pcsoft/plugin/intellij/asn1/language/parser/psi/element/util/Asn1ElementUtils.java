package org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.util;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1AllClassDefinitionRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionFieldName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionFieldRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ClassDefinitionRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValue;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValueName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElementModule;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportElementName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionFieldName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionFieldRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TagDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ValueObjectIdentifierPart;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ValueRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ClassDefinitionFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ClassDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ConstantDefinitionValueReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ModuleDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectClassDefinitionFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectClassDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectValueDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ParameterReference;
import org.pcsoft.plugin.intellij.asn1.type.Asn1TagType;
import org.pcsoft.plugin.intellij.asn1.type.Asn1TaggingType;

import javax.swing.Icon;

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
        ASTNode astNode = element.getNode().findChildByType(Asn1GenElementFactory.NAME_UPPER);
        if (astNode == null) {
            astNode = element.getNode().findChildByType(Asn1GenElementFactory.NAME_LOWER);
            if (astNode == null) {
                astNode = element.getNode().findChildByType(Asn1GenElementFactory.NAME_CAP);
                if (astNode == null) {
                    astNode = element.getNode().findChildByType(Asn1GenElementFactory.NAME_NO_CAP);
                }
            }
        }

        if (astNode != null) {
            return astNode;
        } else {
            return null;
        }
    }

    //region Element - Module Definition
    static String getName(final Asn1ModuleDefinition moduleDefinition) {
        return getNameNodeText(moduleDefinition.getModuleDefinitionName());
    }

    static PsiElement setName(final Asn1ModuleDefinition moduleDefinition, final String newName) {
        final ASTNode astNode = getNameNode(moduleDefinition.getModuleDefinitionName());
        if (astNode != null) {
            //TODO
        }

        return moduleDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1ModuleDefinition moduleDefinition) {
        final ASTNode astNode = getNameNode(moduleDefinition.getModuleDefinitionName());
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

    static String getName(final Asn1ModuleDefinitionName moduleDefinitionName) {
        return moduleDefinitionName.getText();
    }
    //endregion

    //region Element - Import
    //region Import Element Module
    static String getName(final Asn1ImportElementModule importElementModule) {
        return getNameNodeText(importElementModule);
    }

    static PsiElement setName(final Asn1ImportElementModule importElementModule, final String newName) {
        final ASTNode astNode = getNameNode(importElementModule);
        if (astNode != null) {
            //TODO
        }

        return importElementModule;
    }

    static PsiElement getNameIdentifier(final Asn1ImportElementModule importElementModule) {
        final ASTNode astNode = getNameNode(importElementModule);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1ImportElementModule importElementModule) {
        return new Asn1ModuleDefinitionReference(importElementModule);
    }
    //endregion

    //region Import Element Name
    static String getName(final Asn1ImportElementName importElementName) {
        return getNameNodeText(importElementName);
    }

    static PsiElement setName(final Asn1ImportElementName importElementName, final String newName) {
        final ASTNode astNode = getNameNode(importElementName);
        if (astNode != null) {
            //TODO
        }

        return importElementName;
    }

    static PsiElement getNameIdentifier(final Asn1ImportElementName importElementName) {
        final ASTNode astNode = getNameNode(importElementName);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }
    //endregion
    //endregion

    //region Element - Class Definition
    static String getName(final Asn1ClassDefinition classDefinition) {
        return getNameNodeText(classDefinition.getClassDefinitionName());
    }

    static PsiElement setName(final Asn1ClassDefinition classDefinition, final String newName) {
        final ASTNode astNode = getNameNode(classDefinition.getClassDefinitionName());
        if (astNode != null) {
            //TODO
        }

        return classDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1ClassDefinition classDefinition) {
        final ASTNode astNode = getNameNode(classDefinition.getClassDefinitionName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ClassDefinition classDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return classDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(classDefinition, Asn1ModuleDefinition.class);
                return moduleDefinition != null ? moduleDefinition.getName() : classDefinition.getContainingFile() != null ? classDefinition.getContainingFile().getName() : null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Class;
            }
        };
    }

    static String getName(final Asn1ClassDefinitionName classDefinitionName) {
        return classDefinitionName.getText();
    }

    //region Reference - Class Definition
    static String getName(final Asn1ClassDefinitionRef classDefinitionRef) {
        return getNameNodeText(classDefinitionRef);
    }

    static PsiElement setName(final Asn1ClassDefinitionRef objectClassDefinitionRef, final String newName) {
        final ASTNode astNode = getNameNode(objectClassDefinitionRef);
        if (astNode != null) {
            //TODO
        }

        return objectClassDefinitionRef;
    }

    static PsiElement getNameIdentifier(final Asn1ClassDefinitionRef classDefinitionRef) {
        final ASTNode astNode = getNameNode(classDefinitionRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1ClassDefinitionRef classDefinitionRef) {
        return new Asn1ClassDefinitionReference(classDefinitionRef);
    }
    //endregion
    //endregion

    //region Element - Class Definition Field
    static String getName(final Asn1ClassDefinitionField classDefinitionField) {
        return getNameNodeText(classDefinitionField.getClassDefinitionFieldName());
    }

    static PsiElement setName(final Asn1ClassDefinitionField classDefinitionField, final String newName) {
        final ASTNode astNode = getNameNode(classDefinitionField.getClassDefinitionFieldName());
        if (astNode != null) {
            //TODO
        }

        return classDefinitionField;
    }

    static PsiElement getNameIdentifier(final Asn1ClassDefinitionField classDefinitionField) {
        final ASTNode astNode = getNameNode(classDefinitionField.getClassDefinitionFieldName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ClassDefinitionField classDefinitionField) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return classDefinitionField.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ClassDefinition classDefinition = PsiTreeUtil.getParentOfType(classDefinitionField, Asn1ClassDefinition.class);
                return classDefinition == null ? null : classDefinition.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Field;
            }
        };
    }

    static String getName(final Asn1ClassDefinitionFieldName classDefinitionFieldName) {
        return classDefinitionFieldName.getText();
    }

    //region Reference - Class Definition Field
    static String getName(final Asn1ClassDefinitionFieldRef classDefinitionFieldRef) {
        return getNameNodeText(classDefinitionFieldRef);
    }

    static PsiElement setName(final Asn1ClassDefinitionFieldRef classDefinitionFieldRef, final String newName) {
        final ASTNode astNode = getNameNode(classDefinitionFieldRef);
        if (astNode != null) {
            //TODO
        }

        return classDefinitionFieldRef;
    }

    static PsiElement getNameIdentifier(final Asn1ClassDefinitionFieldRef classDefinitionFieldRef) {
        final ASTNode astNode = getNameNode(classDefinitionFieldRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1ClassDefinitionFieldRef classDefinitionFieldRef) {
        return new Asn1ClassDefinitionFieldReference(classDefinitionFieldRef);
    }
    //endregion
    //endregion

    //region Element - Object Class Definition
    static String getName(final Asn1ObjectClassDefinition objectClassDefinition) {
        return getNameNodeText(objectClassDefinition.getObjectClassDefinitionName());
    }

    static PsiElement setName(final Asn1ObjectClassDefinition objectClassDefinition, final String newName) {
        final ASTNode astNode = getNameNode(objectClassDefinition.getObjectClassDefinitionName());
        if (astNode != null) {
            //TODO
        }

        return objectClassDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectClassDefinition objectClassDefinition) {
        final ASTNode astNode = getNameNode(objectClassDefinition.getObjectClassDefinitionName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ObjectClassDefinition objectClassDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return objectClassDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(objectClassDefinition, Asn1ModuleDefinition.class);
                return moduleDefinition != null ? moduleDefinition.getName() : objectClassDefinition.getContainingFile() != null ? objectClassDefinition.getContainingFile().getName() : null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.AbstractClass;
            }
        };
    }

    static String getName(final Asn1ObjectClassDefinitionName objectClassDefinitionName) {
        return objectClassDefinitionName.getText();
    }

    //region Reference - Object Class Definition
    static String getName(final Asn1ObjectClassDefinitionRef objectClassDefinitionRef) {
        return getNameNodeText(objectClassDefinitionRef);
    }

    static PsiElement setName(final Asn1ObjectClassDefinitionRef objectClassDefinitionRef, final String newName) {
        final ASTNode astNode = getNameNode(objectClassDefinitionRef);
        if (astNode != null) {
            //TODO
        }

        return objectClassDefinitionRef;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectClassDefinitionRef objectClassDefinitionRef) {
        final ASTNode astNode = getNameNode(objectClassDefinitionRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1ObjectClassDefinitionRef objectClassDefinitionRef) {
        return new Asn1ObjectClassDefinitionReference(objectClassDefinitionRef);
    }
    //endregion
    //endregion

    //region Element - Object Clasd Definition Field
    static String getName(final Asn1ObjectClassDefinitionField objectClassDefinitionField) {
        return getNameNodeText(objectClassDefinitionField.getObjectClassDefinitionFieldName());
    }

    static PsiElement setName(final Asn1ObjectClassDefinitionField objectClassDefinitionField, final String newName) {
        final ASTNode astNode = getNameNode(objectClassDefinitionField.getObjectClassDefinitionFieldName());
        if (astNode != null) {
            //TODO
        }

        return objectClassDefinitionField;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectClassDefinitionField objectClassDefinitionField) {
        final ASTNode astNode = getNameNode(objectClassDefinitionField.getObjectClassDefinitionFieldName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ObjectClassDefinitionField objectClassDefinitionField) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return objectClassDefinitionField.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ObjectClassDefinition objectClassDefinition = PsiTreeUtil.getParentOfType(objectClassDefinitionField, Asn1ObjectClassDefinition.class);
                return objectClassDefinition == null ? null : objectClassDefinition.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Field;
            }
        };
    }

    static String getName(final Asn1ObjectClassDefinitionFieldName objectClassDefinitionFieldName) {
        return objectClassDefinitionFieldName.getText();
    }

    //region Reference - Object Class Definition Field
    static String getName(final Asn1ObjectClassDefinitionFieldRef objectClassDefinitionFieldRef) {
        return getNameNodeText(objectClassDefinitionFieldRef);
    }

    static PsiElement setName(final Asn1ObjectClassDefinitionFieldRef objectClassDefinitionFieldRef, final String newName) {
        final ASTNode astNode = getNameNode(objectClassDefinitionFieldRef);
        if (astNode != null) {
            //TODO
        }

        return objectClassDefinitionFieldRef;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectClassDefinitionFieldRef objectClassDefinitionFieldRef) {
        final ASTNode astNode = getNameNode(objectClassDefinitionFieldRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1ObjectClassDefinitionFieldRef objectClassDefinitionFieldRef) {
        return new Asn1ObjectClassDefinitionFieldReference(objectClassDefinitionFieldRef);
    }
    //endregion
    //endregion

    //region Element - Object Value Definition
    static String getName(final Asn1ObjectValueDefinition objectValueDefinition) {
        return getNameNodeText(objectValueDefinition.getObjectValueDefinitionName());
    }

    static PsiElement setName(final Asn1ObjectValueDefinition objectValueDefinition, final String newName) {
        final ASTNode astNode = getNameNode(objectValueDefinition.getObjectValueDefinitionName());
        if (astNode != null) {
            //TODO
        }

        return objectValueDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectValueDefinition objectValueDefinition) {
        final ASTNode astNode = getNameNode(objectValueDefinition.getObjectValueDefinitionName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ObjectValueDefinition objectValueDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return objectValueDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(objectValueDefinition, Asn1ModuleDefinition.class);
                return moduleDefinition != null ? moduleDefinition.getName() : objectValueDefinition.getContainingFile() != null ? objectValueDefinition.getContainingFile().getName() : null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Variable;
            }
        };
    }

    static String getName(final Asn1ObjectValueDefinitionName objectValueDefinitionName) {
        return objectValueDefinitionName.getText();
    }

    //region All Class Reference
    static String getName(final Asn1AllClassDefinitionRef allClassDefinitionRef) {
        return getNameNodeText(allClassDefinitionRef);
    }

    static PsiElement setName(final Asn1AllClassDefinitionRef allClassDefinitionRef, final String newName) {
        final ASTNode astNode = getNameNode(allClassDefinitionRef);
        if (astNode != null) {
            //TODO
        }

        return allClassDefinitionRef;
    }

    static PsiElement getNameIdentifier(final Asn1AllClassDefinitionRef allClassDefinitionRef) {
        final ASTNode astNode = getNameNode(allClassDefinitionRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference[] getReferences(final Asn1AllClassDefinitionRef allClassDefinitionRef) {
        return new PsiReference[]{
                new Asn1ClassDefinitionReference(allClassDefinitionRef),
                new Asn1ObjectClassDefinitionReference(allClassDefinitionRef)
        };
    }

    static PsiReference getClassDefinitionReference(final Asn1AllClassDefinitionRef classDefinitionRef) {
        return new Asn1ClassDefinitionReference(classDefinitionRef);
    }

    static PsiReference getObjectClassDefinitionReference(final Asn1AllClassDefinitionRef allClassDefinitionRef) {
        return new Asn1ObjectClassDefinitionReference(allClassDefinitionRef);
    }
    //endregion
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

    //region Value
    //region Value - Object Identifier - Part
    @Nullable
    static String getName(final Asn1ValueObjectIdentifierPart objectIdentifierPart) {
        final ASTNode astNode = getNameNode(objectIdentifierPart);
        if (astNode != null) {
            return astNode.getText();
        } else {
            return null;
        }
    }

    @Nullable
    static Integer getNumber(final Asn1ValueObjectIdentifierPart objectIdentifierPart) {
        final ASTNode astNode = objectIdentifierPart.getNode().findChildByType(Asn1GenElementFactory.NUMBER);
        if (astNode != null) {
            return Integer.parseInt(astNode.getText());
        } else {
            return null;
        }
    }
    //endregion

    //region Value reference
    static String getName(final Asn1ValueRef valueRef) {
        return getNameNodeText(valueRef);
    }

    static PsiElement setName(final Asn1ValueRef valueRef, final String newName) {
        final ASTNode astNode = getNameNode(valueRef);
        if (astNode != null) {
            //TODO
        }

        return valueRef;
    }

    static PsiElement getNameIdentifier(final Asn1ValueRef valueRef) {
        final ASTNode astNode = getNameNode(valueRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference[] getReferences(final Asn1ValueRef valueRef) {
        return new PsiReference[]{
                new Asn1ConstantDefinitionValueReference(valueRef),
                new Asn1ObjectValueDefinitionReference(valueRef)
        };
    }

    static PsiReference getConstantReference(final Asn1ValueRef valueRef) {
        return new Asn1ConstantDefinitionValueReference(valueRef);
    }

    static PsiReference getObjectValueDefinitionReference(final Asn1ValueRef valueRef) {
        return new Asn1ObjectValueDefinitionReference(valueRef);
    }
    //endregion
    //endregion

    //region Parameter
    static String getName(final Asn1ParameterName parameter) {
        return getNameNodeText(parameter);
    }

    static PsiElement setName(final Asn1ParameterName parameter, final String newName) {
        final ASTNode astNode = getNameNode(parameter);
        if (astNode != null) {
            //TODO
        }

        return parameter;
    }

    static PsiElement getNameIdentifier(final Asn1ParameterName parameter) {
        final ASTNode astNode = getNameNode(parameter);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ParameterName parameter) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return parameter.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ObjectClassParameter objectClassParameter = PsiTreeUtil.getParentOfType(parameter, Asn1ObjectClassParameter.class);
                if (objectClassParameter != null) {
                    final Asn1ClassDefinition classDefinition = PsiTreeUtil.getParentOfType(objectClassParameter, Asn1ClassDefinition.class);
                    if (classDefinition != null) {
                        return objectClassParameter.getObjectClassDefinitionRef().getName() + " in " + classDefinition.getName();
                    } else {
                        return objectClassParameter.getObjectClassDefinitionRef().getName();
                    }
                }

                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Parameter;
            }
        };
    }

    //region Reference - Parameter
    static String getName(final Asn1ParameterRef parameterRef) {
        return getNameNodeText(parameterRef);
    }

    static PsiElement setName(final Asn1ParameterRef parameterRef, final String newName) {
        final ASTNode astNode = getNameNode(parameterRef);
        if (astNode != null) {
            //TODO
        }

        return parameterRef;
    }

    static PsiElement getNameIdentifier(final Asn1ParameterRef parameterRef) {
        final ASTNode astNode = getNameNode(parameterRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1ParameterRef parameterRef) {
        return new Asn1ParameterReference(parameterRef);
    }
    //endregion
    //endregion

    //region Constant Definition
    static String getName(final Asn1ConstantDefinitionValue constantDefinitionValue) {
        return getNameNodeText(constantDefinitionValue.getConstantDefinitionValueName());
    }

    static PsiElement setName(final Asn1ConstantDefinitionValue constantDefinitionValue, final String newName) {
        final ASTNode astNode = getNameNode(constantDefinitionValue.getConstantDefinitionValueName());
        if (astNode != null) {
            //TODO
        }

        return constantDefinitionValue;
    }

    static PsiElement getNameIdentifier(final Asn1ConstantDefinitionValue constantDefinitionValue) {
        final ASTNode astNode = getNameNode(constantDefinitionValue.getConstantDefinitionValueName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ConstantDefinitionValue constantDefinitionValue) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return constantDefinitionValue.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Variable;
            }
        };
    }

    static String getName(final Asn1ConstantDefinitionValueName constantDefinitionValueName) {
        return getNameNodeText(constantDefinitionValueName);
    }
    //endregion
}
