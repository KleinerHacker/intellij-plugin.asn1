package org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.util;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.*;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ClassDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1FileReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectClassDefinitionFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectClassDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.type.Asn1TagType;
import org.pcsoft.plugin.intellij.asn1.type.Asn1TaggingType;

import javax.swing.*;

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
        final ASTNode astNode = element.getNode().findChildByType(Asn1GenElementFactory.NAME);
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

    static String getName(final Asn1ModuleDefinitionName moduleDefinitionName) {
        return moduleDefinitionName.getText();
    }
    //endregion

    //region Element - Import
    //region Import Element file
    static String getName(final Asn1ImportElementFile importElementFile) {
        return getNameNodeText(importElementFile);
    }

    static PsiElement setName(final Asn1ImportElementFile importElementFile, final String newName) {
        final ASTNode astNode = getNameNode(importElementFile);
        if (astNode != null) {
            //TODO
        }

        return importElementFile;
    }

    static PsiElement getNameIdentifier(final Asn1ImportElementFile importElementFile) {
        final ASTNode astNode = getNameNode(importElementFile);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1ImportElementFile importElementFile) {
        return new Asn1FileReference(importElementFile);
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
                final PsiFile containingFile = classDefinition.getContainingFile();
                return containingFile == null ? null : containingFile.getName();
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

    //region Element - Class Field Definition
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

    static String getName(final Asn1ObjectClassDefinitionName objectClassDefinitionName) {
        return objectClassDefinitionName.getText();
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
                final PsiFile containingFile = objectClassDefinition.getContainingFile();
                return containingFile == null ? null : containingFile.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.AbstractClass;
            }
        };
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

    //region Element - Object Class Field Definition
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

    //region Element - Object Definition
    static String getName(final Asn1ObjectDefinition objectDefinition) {
        return getNameNodeText(objectDefinition.getObjectDefinitionName());
    }

    static PsiElement setName(final Asn1ObjectDefinition objectDefinition, final String newName) {
        final ASTNode astNode = getNameNode(objectDefinition.getObjectDefinitionName());
        if (astNode != null) {
            //TODO
        }

        return objectDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectDefinition objectDefinition) {
        final ASTNode astNode = getNameNode(objectDefinition.getObjectDefinitionName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static String getName(final Asn1ObjectDefinitionName objectDefinitionName) {
        return objectDefinitionName.getText();
    }

    static ItemPresentation getPresentation(final Asn1ObjectDefinition objectDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return objectDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final PsiFile containingFile = objectDefinition.getContainingFile();
                return containingFile == null ? null : containingFile.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Variable;
            }
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
}
