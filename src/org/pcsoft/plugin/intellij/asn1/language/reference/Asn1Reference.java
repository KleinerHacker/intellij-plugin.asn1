package org.pcsoft.plugin.intellij.asn1.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by pfeifchr on 28.09.2016.
 */
public abstract class Asn1Reference<T extends PsiNamedElement> extends PsiReferenceBase<PsiNamedElement> implements PsiPolyVariantReference {
    private String key;

    public Asn1Reference(PsiNamedElement element) {
        super(element);
        key = element.getName();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final List<T> list = findByKey(myElement.getProject(), key);
        return list.stream()
                .map(PsiElementResolveResult::new)
                .toArray(PsiElementResolveResult[]::new);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        final ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final List<T> list = findAll(myElement.getProject());
        return list.stream()
                .filter(this::isValidItem)
                .map(this::buildItem)
                .toArray();
    }

    protected abstract List<T> findByKey(final Project project, final String key);
    protected abstract List<T> findAll(final Project project);

    protected abstract boolean isValidItem(final T value);
    protected abstract LookupElementBuilder buildItem(final T value);
}
