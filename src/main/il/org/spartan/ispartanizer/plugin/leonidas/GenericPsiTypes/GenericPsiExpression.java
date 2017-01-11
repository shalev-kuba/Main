package il.org.spartan.ispartanizer.plugin.leonidas.GenericPsiTypes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import il.org.spartan.ispartanizer.auxilary_layer.iz;
import il.org.spartan.ispartanizer.plugin.leonidas.KeyDescriptionParameters;

/**
 * Created by melanyc on 1/11/2017.
 */
public class GenericPsiExpression extends GenericPsi {
    PsiType t;

    public GenericPsiExpression(PsiType evalType, PsiElement e) {
        super(e);
        this.t = evalType;
    }

    @Override
    public String toString() {
        return "Generic expression" + inner.getUserData(KeyDescriptionParameters.ORDER);
    }


    public PsiType evaluationType() {
        return t;
    }

    @Override
    public boolean isOfGenericType(PsiElement e) {
        return iz.expression(e);
    }
}
