package il.org.spartan.ispartanizer.plugin.leonidas.GenericPsiTypes;


import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.util.Key;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.impl.ResolveScopeManager;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.JavaElementType;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract class representing generic psi element.
 * @author michalcohen
 * @since 11-01-17
 */
public abstract class GenericPsi extends LeafPsiElement implements PsiJavaToken {

    //final int myHC = CompositePsiElement.ourHC++;
    PsiElement inner;
    PsiFile containingFile;

    protected GenericPsi(PsiElement inner, String text) {
        super(JavaElementType.DUMMY_ELEMENT, text);
        this.inner = inner;
        this.containingFile = inner.getContainingFile();

    }
    @Override
    public IElementType getTokenType() {
        return getElementType();
    }

    /* @Override
     public final int hashCode() {
         return myHC;
     }
 */
    /*@Override
    public PsiFile getContainingFile() {
        return containingFile;
    }

    @Override
    public boolean isValid() {
        return true;
    }
*/
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return inner.getUserData(key);
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        inner.putUserData(key, value);
    }

    /**
     * checks if a different element conforms with the represented generic type.
     *
     * @param e - the element to be checked
     * @return true iff e is of the generic type
     */
    public abstract boolean generalizes(PsiElement e);

    @Override
    public boolean isValid() {
        return true;
    }


    @Override
    public PsiElement getParent() {
        return inner.getParent();
    }

    @Override
    public PsiElement getNextSibling() {
        return inner.getNextSibling();
    }//buggy

    @Override
    public PsiElement getPrevSibling() {
        return inner.getPrevSibling();
    }//buggy

    @Override
    public PsiFile getContainingFile() {
        return inner.getContainingFile();
    }


    //maybe problematic
    /*@Override
    public PsiReference findReferenceAt(int offset) {
        return SharedPsiElementImplUtil.findReferenceAt(this, offset);
    }
*/
    @Override
    public PsiElement copy() {
        ASTNode elementCopy = copyElement();
        return SourceTreeToPsiMap.treeElementToPsi(elementCopy);
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    //maybe problematic
    /*
    @Override
    public PsiReference getReference() {
        return null;
    }


    @Override
    @NotNull
    public PsiReference[] getReferences() {
        return SharedPsiElementImplUtil.getReferences(this);
    }
    */
    @Override
    public void delete() throws IncorrectOperationException {
        //LOG.assertTrue(getTreeParent() != null);
        CheckUtil.checkWritable(this);
        getTreeParent().deleteChildInternal(this);
        invalidate();
    }

    @Override
    public void checkDelete() throws IncorrectOperationException {
        CheckUtil.checkWritable(this);
    }

    @Override
    public void deleteChildRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Override
    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        return inner.replace(newElement);
    }

    public String toString() {
        return "PsiElement" + "(" + getElementType().toString() + ")";
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitElement(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return true;
    }

    @Override
    public PsiElement getContext() {
        return getParent();
    }

    @Override
    public PsiElement getNavigationElement() {
        return this;
    }

    @Override
    public PsiElement getOriginalElement() {
        return this;
    }

    @Override
    public boolean isPhysical() {
        PsiFile file = getContainingFile();
        return file != null && file.isPhysical();
    }

    @Override
    @NotNull
    public GlobalSearchScope getResolveScope() {
        return ResolveScopeManager.getElementResolveScope(this);
    }

    @Override
    @NotNull
    public SearchScope getUseScope() {
        return ResolveScopeManager.getElementUseScope(this);
    }

    @Override
    @NotNull
    public Project getProject() {
        Project project = ProjectCoreUtil.theOnlyOpenProject();
        if (project != null) {
            return project;
        }
        final PsiManager manager = getManager();
        if (manager == null) return null;
        return manager.getProject();
    }

    @Override
    @NotNull
    public Language getLanguage() {
        return getElementType().getLanguage();
    }

    @Override
    public ASTNode getNode() {
        return this;
    }

    @Override
    public PsiElement getPsi() {
        return this;
    }

    @Override
    public ItemPresentation getPresentation() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void navigate(boolean requestFocus) {
        final Navigatable descriptor = PsiNavigationSupport.getInstance().getDescriptor(this);
        if (descriptor != null) {
            descriptor.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return PsiNavigationSupport.getInstance().canNavigate(this);
    }

    @Override
    public boolean canNavigateToSource() {
        return canNavigate();
    }

    @Override
    public boolean isEquivalentTo(final PsiElement another) {
        return this == another;
    }

}
