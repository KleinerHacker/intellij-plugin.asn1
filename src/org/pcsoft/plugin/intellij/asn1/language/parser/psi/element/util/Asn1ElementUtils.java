package org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.util;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugin.intellij.asn1.Asn1Icons;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValue;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ConstantDefinitionValueName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1DefinitiveObjectIdentifierPart;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1DefinitiveString;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ElementDefinitionObjectSetArgumentRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ElementDefinitionRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ElementDefinitionTypeArgumentRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1EnumeratedDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1EnumeratedDefinitionElement;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1EnumeratedDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1FullQualifiedObjectClassDefinitionFieldQualifierRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ImportExportSymbolRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ModuleRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionFieldName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionFieldRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectClassDefinitionRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectSetParameterTypeRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ObjectValueDefinitionName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterName;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ParameterRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TagDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinition;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinitionField;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinitionFieldRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeDefinitionRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeFieldIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeIdentifier;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1TypeParameter;
import org.pcsoft.plugin.intellij.asn1.language.parser.psi.element.Asn1ValueRef;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1CustomElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.parser.token.Asn1GenElementFactory;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ConstantDefinitionValueReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1EnumeratedDefinitionElementReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1EnumeratedDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ModuleDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectClassDefinitionFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectClassDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectSetDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectSetParameterReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1ObjectValueDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1TypeDefinitionFieldReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1TypeDefinitionReference;
import org.pcsoft.plugin.intellij.asn1.language.reference.Asn1TypeParameterReference;
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
        if (element == null || element.getNode() == null)
            return null;

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

    //region Element - Import
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

    static PsiReference getReference(final Asn1ModuleRef moduleRef) {
        return new Asn1ModuleDefinitionReference(moduleRef);
    }
    //endregion

    //region Import Element Type Reference
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

    static PsiReference[] getReferences(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        return new PsiReference[]{
                new Asn1TypeDefinitionReference(importExportSymbolRef),
                new Asn1ObjectClassDefinitionReference(importExportSymbolRef),
                new Asn1ObjectValueDefinitionReference(importExportSymbolRef),
                new Asn1ObjectSetDefinitionReference(importExportSymbolRef),
                new Asn1EnumeratedDefinitionReference(importExportSymbolRef)
        };
    }

    static PsiReference getTypeDefinitionReference(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        return new Asn1TypeDefinitionReference(importExportSymbolRef);
    }

    static PsiReference getObjectClassDefinitionReference(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        return new Asn1ObjectClassDefinitionReference(importExportSymbolRef);
    }

    static PsiReference getObjectValueDefinitionReference(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        return new Asn1ObjectValueDefinitionReference(importExportSymbolRef);
    }

    static PsiReference getObjectSetDefinitionReference(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        return new Asn1ObjectSetDefinitionReference(importExportSymbolRef);
    }

    static PsiReference getEnumeratedDefinitionReference(final Asn1ImportExportSymbolRef importExportSymbolRef) {
        return new Asn1EnumeratedDefinitionReference(importExportSymbolRef);
    }
    //endregion

    //endregion

    //region Element - Class Definition
    static String getName(final Asn1TypeDefinition typeDefinition) {
        return getNameNodeText(typeDefinition.getTypeIdentifier());
    }

    static PsiElement setName(final Asn1TypeDefinition typeDefinition, final String newName) {
        final ASTNode astNode = getNameNode(typeDefinition.getTypeIdentifier());
        if (astNode != null) {
            //TODO
        }

        return typeDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1TypeDefinition typeDefinition) {
        final ASTNode astNode = getNameNode(typeDefinition.getTypeIdentifier());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1TypeDefinition typeDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return typeDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(typeDefinition, Asn1ModuleDefinition.class);
                return moduleDefinition != null ? moduleDefinition.getName() : typeDefinition.getContainingFile() != null ? typeDefinition.getContainingFile().getName() : null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Class;
            }
        };
    }

    static String getName(final Asn1TypeIdentifier typeIdentifier) {
        return typeIdentifier.getText();
    }

    //region Reference - Class Definition
    static String getName(final Asn1TypeDefinitionRef typeDefinitionRef) {
        return getNameNodeText(typeDefinitionRef);
    }

    static PsiElement setName(final Asn1TypeDefinitionRef typeDefinitionRef, final String newName) {
        final ASTNode astNode = getNameNode(typeDefinitionRef);
        if (astNode != null) {
            //TODO
        }

        return typeDefinitionRef;
    }

    static PsiElement getNameIdentifier(final Asn1TypeDefinitionRef typeDefinitionRef) {
        final ASTNode astNode = getNameNode(typeDefinitionRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1TypeDefinitionRef typeDefinitionRef) {
        return new Asn1TypeDefinitionReference(typeDefinitionRef);
    }
    //endregion
    //endregion

    //region Element - Class Definition Field
    static String getName(final Asn1TypeDefinitionField typeDefinitionField) {
        return getNameNodeText(typeDefinitionField.getTypeFieldIdentifier());
    }

    static PsiElement setName(final Asn1TypeDefinitionField typeDefinitionField, final String newName) {
        final ASTNode astNode = getNameNode(typeDefinitionField.getTypeFieldIdentifier());
        if (astNode != null) {
            //TODO
        }

        return typeDefinitionField;
    }

    static PsiElement getNameIdentifier(final Asn1TypeDefinitionField typeDefinitionField) {
        final ASTNode astNode = getNameNode(typeDefinitionField.getTypeFieldIdentifier());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1TypeDefinitionField typeDefinitionField) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return typeDefinitionField.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1TypeDefinition typeDefinition = PsiTreeUtil.getParentOfType(typeDefinitionField, Asn1TypeDefinition.class);
                return typeDefinition == null ? null : typeDefinition.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Field;
            }
        };
    }

    static String getName(final Asn1TypeFieldIdentifier typeFieldIdentifier) {
        return typeFieldIdentifier.getText();
    }

    //region Reference - Class Definition Field
    static String getName(final Asn1TypeDefinitionFieldRef typeDefinitionFieldRef) {
        return getNameNodeText(typeDefinitionFieldRef);
    }

    static PsiElement setName(final Asn1TypeDefinitionFieldRef typeDefinitionFieldRef, final String newName) {
        final ASTNode astNode = getNameNode(typeDefinitionFieldRef);
        if (astNode != null) {
            //TODO
        }

        return typeDefinitionFieldRef;
    }

    static PsiElement getNameIdentifier(final Asn1TypeDefinitionFieldRef typeDefinitionFieldRef) {
        final ASTNode astNode = getNameNode(typeDefinitionFieldRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference getReference(final Asn1TypeDefinitionFieldRef typeDefinitionFieldRef) {
        return new Asn1TypeDefinitionFieldReference(typeDefinitionFieldRef);
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

    //region Element - Object Class Definition Field
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

    //region Reference (Full Qualified) - Object Class Definition Field
    static String getName(final Asn1FullQualifiedObjectClassDefinitionFieldQualifierRef fullQualifiedObjectClassDefinitionFieldQualifierRef) {
        return getNameNodeText(fullQualifiedObjectClassDefinitionFieldQualifierRef);
    }

    static PsiElement setName(final Asn1FullQualifiedObjectClassDefinitionFieldQualifierRef fullQualifiedObjectClassDefinitionFieldQualifierRef, final String newName) {
        final ASTNode astNode = getNameNode(fullQualifiedObjectClassDefinitionFieldQualifierRef);
        if (astNode != null) {
            //TODO
        }

        return fullQualifiedObjectClassDefinitionFieldQualifierRef;
    }

    static PsiElement getNameIdentifier(final Asn1FullQualifiedObjectClassDefinitionFieldQualifierRef fullQualifiedObjectClassDefinitionFieldQualifierRef) {
        final ASTNode astNode = getNameNode(fullQualifiedObjectClassDefinitionFieldQualifierRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference[] getReferences(final Asn1FullQualifiedObjectClassDefinitionFieldQualifierRef fullQualifiedObjectClassDefinitionFieldQualifierRef) {
        return new PsiReference[]{
                new Asn1ObjectClassDefinitionReference(fullQualifiedObjectClassDefinitionFieldQualifierRef),
                new Asn1ObjectValueDefinitionReference(fullQualifiedObjectClassDefinitionFieldQualifierRef)
        };
    }

    static PsiReference getObjectClassDefinitionReference(final Asn1FullQualifiedObjectClassDefinitionFieldQualifierRef fullQualifiedObjectClassDefinitionFieldQualifierRef) {
        return new Asn1ObjectClassDefinitionReference(fullQualifiedObjectClassDefinitionFieldQualifierRef);
    }

    static PsiReference getObjectValueDefinitionReference(final Asn1FullQualifiedObjectClassDefinitionFieldQualifierRef fullQualifiedObjectClassDefinitionFieldQualifierRef) {
        return new Asn1ObjectValueDefinitionReference(fullQualifiedObjectClassDefinitionFieldQualifierRef);
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

    //endregion

    //region Element - Object Set Definition
    static String getName(final Asn1ObjectSetDefinition objectSetDefinition) {
        return getNameNodeText(objectSetDefinition.getObjectSetDefinitionName());
    }

    static PsiElement setName(final Asn1ObjectSetDefinition objectSetDefinition, final String newName) {
        final ASTNode astNode = getNameNode(objectSetDefinition.getObjectSetDefinitionName());
        if (astNode != null) {
            //TODO
        }

        return objectSetDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectSetDefinition objectSetDefinition) {
        final ASTNode astNode = getNameNode(objectSetDefinition.getObjectSetDefinitionName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1ObjectSetDefinition objectSetDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return objectSetDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(objectSetDefinition, Asn1ModuleDefinition.class);
                return moduleDefinition != null ? moduleDefinition.getName() : objectSetDefinition.getContainingFile() != null ? objectSetDefinition.getContainingFile().getName() : null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Advice;
            }
        };
    }

    static String getName(final Asn1ObjectSetDefinitionName objectSetDefinitionName) {
        return objectSetDefinitionName.getText();
    }

    //endregion

    //region Element - Enumerated Definition
    static String getName(final Asn1EnumeratedDefinition enumeratedDefinition) {
        return getNameNodeText(enumeratedDefinition.getEnumeratedDefinitionName());
    }

    static PsiElement setName(final Asn1EnumeratedDefinition enumeratedDefinition, final String newName) {
        final ASTNode astNode = getNameNode(enumeratedDefinition.getEnumeratedDefinitionName());
        if (astNode != null) {
            //TODO
        }

        return enumeratedDefinition;
    }

    static PsiElement getNameIdentifier(final Asn1EnumeratedDefinition enumeratedDefinition) {
        final ASTNode astNode = getNameNode(enumeratedDefinition.getEnumeratedDefinitionName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1EnumeratedDefinition enumeratedDefinition) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return enumeratedDefinition.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1ModuleDefinition moduleDefinition = PsiTreeUtil.getParentOfType(enumeratedDefinition, Asn1ModuleDefinition.class);
                return moduleDefinition != null ? moduleDefinition.getName() : enumeratedDefinition.getContainingFile() != null ? enumeratedDefinition.getContainingFile().getName() : null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Enum;
            }
        };
    }

    static String getName(final Asn1EnumeratedDefinitionName enumeratedDefinitionName) {
        return enumeratedDefinitionName.getText();
    }

    //region Elements
    static String getName(final Asn1EnumeratedDefinitionElement enumeratedDefinitionElement) {
        return getNameNodeText(enumeratedDefinitionElement);
    }

    static PsiElement setName(final Asn1EnumeratedDefinitionElement enumeratedDefinitionElement, final String newName) {
        final ASTNode astNode = getNameNode(enumeratedDefinitionElement);
        if (astNode != null) {
            //TODO
        }

        return enumeratedDefinitionElement;
    }

    static PsiElement getNameIdentifier(final Asn1EnumeratedDefinitionElement enumeratedDefinitionElement) {
        final ASTNode astNode = getNameNode(enumeratedDefinitionElement);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static ItemPresentation getPresentation(final Asn1EnumeratedDefinitionElement enumeratedDefinitionElement) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return enumeratedDefinitionElement.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1EnumeratedDefinition enumeratedDefinition = PsiTreeUtil.getParentOfType(enumeratedDefinitionElement, Asn1EnumeratedDefinition.class);
                return enumeratedDefinition != null ? enumeratedDefinition.getName() : null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Enum;
            }
        };
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

    static String getStringValue(final Asn1DefinitiveString definitiveString) {
        return definitiveString.getText().substring(1, definitiveString.getText().length()-1).replace("\"\"", "\"");
    }

    //region Value - Object Identifier - Part
    @Nullable
    static String getName(final Asn1DefinitiveObjectIdentifierPart definitiveObjectIdentifierPart) {
        final ASTNode astNode = getNameNode(definitiveObjectIdentifierPart);
        if (astNode != null) {
            return astNode.getText();
        } else {
            return null;
        }
    }

    @Nullable
    static Integer getNumber(final Asn1DefinitiveObjectIdentifierPart definitiveObjectIdentifierPart) {
        final ASTNode astNode = definitiveObjectIdentifierPart.getNode().findChildByType(Asn1GenElementFactory.NUMBER);
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
                new Asn1ObjectValueDefinitionReference(valueRef),
                new Asn1EnumeratedDefinitionElementReference(valueRef)
        };
    }

    static PsiReference getConstantReference(final Asn1ValueRef valueRef) {
        return new Asn1ConstantDefinitionValueReference(valueRef);
    }

    static PsiReference getObjectValueDefinitionReference(final Asn1ValueRef valueRef) {
        return new Asn1ObjectValueDefinitionReference(valueRef);
    }

    static PsiReference getEnumeratedDefinitionElementReference(final Asn1ValueRef valueRef) {
        return new Asn1EnumeratedDefinitionElementReference(valueRef);
    }
    //endregion
    //endregion

    //region Parameter
    static String getName(final Asn1ParameterName parameterName) {
        return getNameNodeText(parameterName);
    }

    static PsiElement setName(final Asn1ObjectSetParameter objectSetParameter, final String newName) {
        final ASTNode astNode = getNameNode(objectSetParameter.getParameterName());
        if (astNode != null) {
            //TODO
        }

        return objectSetParameter;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectSetParameter objectSetParameter) {
        final ASTNode astNode = getNameNode(objectSetParameter.getParameterName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static String getName(final Asn1ObjectSetParameter objectSetParameter) {
        return objectSetParameter.getParameterName().getName();
    }

    static ItemPresentation getPresentation(final Asn1ObjectSetParameter objectSetParameter) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return objectSetParameter.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1TypeDefinition typeDefinition = PsiTreeUtil.getParentOfType(objectSetParameter, Asn1TypeDefinition.class);
                if (typeDefinition != null) {
                    return typeDefinition.getName();
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

    static PsiElement setName(final Asn1TypeParameter typeParameter, final String newName) {
        final ASTNode astNode = getNameNode(typeParameter.getParameterName());
        if (astNode != null) {
            //TODO
        }

        return typeParameter;
    }

    static PsiElement getNameIdentifier(final Asn1TypeParameter typeParameter) {
        final ASTNode astNode = getNameNode(typeParameter.getParameterName());
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static String getName(final Asn1TypeParameter typeParameter) {
        return typeParameter.getParameterName().getName();
    }

    static ItemPresentation getPresentation(final Asn1TypeParameter typeParameter) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return typeParameter.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final Asn1TypeDefinition typeDefinition = PsiTreeUtil.getParentOfType(typeParameter, Asn1TypeDefinition.class);
                if (typeDefinition != null) {
                    return typeDefinition.getName();
                }

                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Advice;
            }
        };
    }

    //region Reference - Parameter Type
    static PsiElement setName(final Asn1ObjectSetParameterTypeRef objectSetParameterTypeRef, final String newName) {
        final ASTNode astNode = getNameNode(objectSetParameterTypeRef);
        if (astNode != null) {
            //TODO
        }

        return objectSetParameterTypeRef;
    }

    static PsiElement getNameIdentifier(final Asn1ObjectSetParameterTypeRef objectSetParameterTypeRef) {
        final ASTNode astNode = getNameNode(objectSetParameterTypeRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static String getName(final Asn1ObjectSetParameterTypeRef objectSetParameterTypeRef) {
        return objectSetParameterTypeRef.getText();
    }

    static PsiReference[] getReferences(final Asn1ObjectSetParameterTypeRef objectSetParameterTypeRef) {
        return new PsiReference[]{
                new Asn1TypeParameterReference(objectSetParameterTypeRef),
                new Asn1TypeDefinitionReference(objectSetParameterTypeRef),
                new Asn1ObjectClassDefinitionReference(objectSetParameterTypeRef)
        };
    }

    static PsiReference getTypeParameterReference(final Asn1ObjectSetParameterTypeRef objectSetParameterTypeRef) {
        return new Asn1TypeParameterReference(objectSetParameterTypeRef);
    }

    static PsiReference getTypeDefinitionReference(final Asn1ObjectSetParameterTypeRef objectSetParameterTypeRef) {
        return new Asn1TypeDefinitionReference(objectSetParameterTypeRef);
    }

    static PsiReference getObjectClassDefinitionReference(final Asn1ObjectSetParameterTypeRef objectSetParameterTypeRef) {
        return new Asn1ObjectClassDefinitionReference(objectSetParameterTypeRef);
    }
    //endregion

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
        return new Asn1ObjectSetParameterReference(parameterRef);
    }
    //endregion
    //endregion

    //region Argument Reference
    //region Object Set
    static String getName(final Asn1ElementDefinitionObjectSetArgumentRef elementDefinitionObjectSetArgumentRef) {
        return getNameNodeText(elementDefinitionObjectSetArgumentRef);
    }

    static PsiElement setName(final Asn1ElementDefinitionObjectSetArgumentRef elementDefinitionObjectSetArgumentRef, final String newName) {
        final ASTNode astNode = getNameNode(elementDefinitionObjectSetArgumentRef);
        if (astNode != null) {
            //TODO
        }

        return elementDefinitionObjectSetArgumentRef;
    }

    static PsiElement getNameIdentifier(final Asn1ElementDefinitionObjectSetArgumentRef elementDefinitionObjectSetArgumentRef) {
        final ASTNode astNode = getNameNode(elementDefinitionObjectSetArgumentRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference[] getReferences(final Asn1ElementDefinitionObjectSetArgumentRef elementDefinitionObjectSetArgumentRef) {
        return new PsiReference[]{
                new Asn1ObjectSetParameterReference(elementDefinitionObjectSetArgumentRef),
                new Asn1ObjectSetDefinitionReference(elementDefinitionObjectSetArgumentRef),
        };
    }

    static PsiReference getObjectSetParameterReference(final Asn1ElementDefinitionObjectSetArgumentRef elementDefinitionObjectSetArgumentRef) {
        return new Asn1ObjectSetParameterReference(elementDefinitionObjectSetArgumentRef);
    }

    static PsiReference getObjectSetDefinitionReference(final Asn1ElementDefinitionObjectSetArgumentRef elementDefinitionObjectSetArgumentRef) {
        return new Asn1ObjectSetDefinitionReference(elementDefinitionObjectSetArgumentRef);
    }
    //endregion

    //region Type
    static String getName(final Asn1ElementDefinitionTypeArgumentRef elementDefinitionTypeArgumentRef) {
        return getNameNodeText(elementDefinitionTypeArgumentRef);
    }

    static PsiElement setName(final Asn1ElementDefinitionTypeArgumentRef elementDefinitionTypeArgumentRef, final String newName) {
        final ASTNode astNode = getNameNode(elementDefinitionTypeArgumentRef);
        if (astNode != null) {
            //TODO
        }

        return elementDefinitionTypeArgumentRef;
    }

    static PsiElement getNameIdentifier(final Asn1ElementDefinitionTypeArgumentRef elementDefinitionTypeArgumentRef) {
        final ASTNode astNode = getNameNode(elementDefinitionTypeArgumentRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference[] getReferences(final Asn1ElementDefinitionTypeArgumentRef elementDefinitionTypeArgumentRef) {
        return new PsiReference[]{
                new Asn1TypeParameterReference(elementDefinitionTypeArgumentRef),
                new Asn1ObjectClassDefinitionReference(elementDefinitionTypeArgumentRef),
                new Asn1TypeDefinitionReference(elementDefinitionTypeArgumentRef)
        };
    }

    static PsiReference getTypeParameterReference(final Asn1ElementDefinitionTypeArgumentRef elementDefinitionTypeArgumentRef) {
        return new Asn1TypeParameterReference(elementDefinitionTypeArgumentRef);
    }

    static PsiReference getObjectClassDefinitionReference(final Asn1ElementDefinitionTypeArgumentRef elementDefinitionTypeArgumentRef) {
        return new Asn1ObjectClassDefinitionReference(elementDefinitionTypeArgumentRef);
    }

    static PsiReference getTypeDefinitionReference(final Asn1ElementDefinitionTypeArgumentRef elementDefinitionTypeArgumentRef) {
        return new Asn1TypeDefinitionReference(elementDefinitionTypeArgumentRef);
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

    //region Element Definition
    static String getName(final Asn1ElementDefinitionRef elementDefinitionRef) {
        return getNameNodeText(elementDefinitionRef);
    }

    static PsiElement setName(final Asn1ElementDefinitionRef elementDefinitionRef, final String newName) {
        final ASTNode astNode = getNameNode(elementDefinitionRef);
        if (astNode != null) {
            //TODO
        }

        return elementDefinitionRef;
    }

    static PsiElement getNameIdentifier(final Asn1ElementDefinitionRef elementDefinitionRef) {
        final ASTNode astNode = getNameNode(elementDefinitionRef);
        if (astNode != null) {
            return astNode.getPsi();
        } else {
            return null;
        }
    }

    static PsiReference[] getReferences(final Asn1ElementDefinitionRef elementDefinitionRef) {
        return new PsiReference[]{
                new Asn1ObjectClassDefinitionReference(elementDefinitionRef),
                new Asn1TypeDefinitionReference(elementDefinitionRef),
                new Asn1EnumeratedDefinitionReference(elementDefinitionRef)
        };
    }

    static PsiReference getObjectClassDefinitionReference(final Asn1ElementDefinitionRef elementDefinitionRef) {
        return new Asn1ObjectClassDefinitionReference(elementDefinitionRef);
    }

    static PsiReference getTypeDefinitionReference(final Asn1ElementDefinitionRef elementDefinitionRef) {
        return new Asn1TypeDefinitionReference(elementDefinitionRef);
    }

    static PsiReference getEnumeratedReference(final Asn1ElementDefinitionRef elementDefinitionRef) {
        return new Asn1EnumeratedDefinitionReference(elementDefinitionRef);
    }
    //endregion
}
