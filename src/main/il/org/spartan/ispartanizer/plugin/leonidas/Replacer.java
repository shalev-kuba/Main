package il.org.spartan.ispartanizer.plugin.leonidas;

import com.intellij.psi.PsiElement;
import il.org.spartan.ispartanizer.auxilary_layer.PsiRewrite;
import il.org.spartan.ispartanizer.auxilary_layer.step;
import il.org.spartan.ispartanizer.plugin.EncapsulatingNode;
import il.org.spartan.ispartanizer.plugin.EncapsulatingNodeVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides replacing services for a given tree template.
 *
 * @author AnnaBel7 michalcohen
 * @since 09-01-2017
 */
public class Replacer {

    /**
     * This method replaces the given element by the corresponding tree built by PsiTreeTipperBuilder
     *
     * @param treeToReplace - the given tree that matched the "from" tree.
     * @param builder       - PsiTreeTipperBuilder that includes the "from" tree that matched treeToReplace
     * @param r             - Rewrite object
     * @return the replaced element
     */
    public static EncapsulatingNode replace(PsiElement treeToReplace, PsiTreeTipperBuilder builder, PsiRewrite r) {
        //EncapsulatingNode templateReplacingTree = builder.getToPsiTree();
        PsiElement n = getReplacer(treeToReplace, builder, r);
        r.replace(treeToReplace, n);
        return EncapsulatingNode.buildTreeFromPsi(n);
    }

    public static PsiElement getReplacer(PsiElement treeToReplace, PsiTreeTipperBuilder builder, PsiRewrite r) {
        EncapsulatingNode templateMatchingTree = builder.getFromPsiTree();
        Map<Integer, PsiElement> map = extractInfo(templateMatchingTree, treeToReplace);
        EncapsulatingNode templateReplacingTree = builder.getToPsiTree();
        // might not work due to replacing while recursively iterating.
        map.keySet().forEach(d -> {
            templateReplacingTree.accept(new EncapsulatingNodeVisitor() {
                @Override
                public void visit(EncapsulatingNode e) {
                    if (e.getInner().getUserData(KeyDescriptionParameters.ID) != null && Pruning.getStubName(e).isPresent()) {
                        Pruning.getRealParent(e, Pruning.getStubName(e).get()).replace(new EncapsulatingNode(map.get(e.getInner().getUserData(KeyDescriptionParameters.ID))));
                    }
                }
            });
        });

        return templateReplacingTree.getInner();
    }

    /**
     * @param treeTemplate - The root of a tree already been matched.
     * @param treeToMatch  - The patterns from which we extract the IDs
     * @return a mapping between an ID to a PsiElement
     */
    public static Map<Integer, PsiElement> extractInfo(EncapsulatingNode treeTemplate, PsiElement treeToMatch) {
        Map<Integer, PsiElement> mapping = new HashMap<>();
        EncapsulatingNode.Iterator treeTemplateChile = treeTemplate.iterator();
        for (PsiElement treeToMatchChild = treeToMatch.getFirstChild(); treeTemplateChile.hasNext() && treeToMatchChild != null; treeTemplateChile.next(), treeToMatchChild = step.nextSibling(treeToMatchChild)) {
            if (treeTemplateChile.value().getInner().getUserData(KeyDescriptionParameters.ID) != null) {
                mapping.put(treeTemplateChile.value().getInner().getUserData(KeyDescriptionParameters.ID), treeToMatchChild);
            } else {
                mapping.putAll(extractInfo(treeTemplateChile.value(), treeToMatchChild));
            }
        }
        return mapping;
    }
}
