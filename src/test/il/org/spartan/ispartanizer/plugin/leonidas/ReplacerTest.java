package il.org.spartan.ispartanizer.plugin.leonidas;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import il.org.spartan.ispartanizer.auxilary_layer.PsiRewrite;
import il.org.spartan.ispartanizer.auxilary_layer.Wrapper;
import il.org.spartan.ispartanizer.auxilary_layer.iz;
import il.org.spartan.ispartanizer.plugin.EncapsulatingNode;
import il.org.spartan.ispartanizer.tippers.TipperTest;

import java.util.Map;

/**
 * @author AnnaBel7
 * @since 09/01/2017.
 */
public class ReplacerTest extends TipperTest {

    private static final String TIPPER_FILE_NAME_1 = "RemoveCurlyBracesFromIfStatement.java";
    private static final String TIPPER_FILE_NAME_2 = "RemoveCurlyBracesFromWhileStatement.java";
    private static final String TIPPER_FILE_NAME_3 = "IfDoubleNot.java";
    private static final String COND = "x==0";
    private static final String THEN = "return x;";
    private static final Replacer replacer = new Replacer();

    public void testGetReplacer1() {
        PsiIfStatement ifStatement = createTestIfStatement(COND, THEN);
        PsiTreeTipperBuilder tipperBuilder = null;
        try {
            tipperBuilder = new PsiTreeTipperBuilderImpl().buildTipperPsiTree(TIPPER_FILE_NAME_1);
        } catch (Exception ignore) {
        }
        PsiRewrite rewrite = new PsiRewrite().project(ifStatement.getProject()).psiFile(ifStatement.getContainingFile());
        PsiIfStatement newIfStatement = createTestIfStatementNoBraces(COND, THEN);
        assertTrue(PsiTreeMatcher.match(EncapsulatingNode.buildTreeFromPsi(Replacer.getReplacer(ifStatement, tipperBuilder, rewrite)), EncapsulatingNode.buildTreeFromPsi(newIfStatement)));
        assertFalse(PsiTreeMatcher.match(EncapsulatingNode.buildTreeFromPsi(Replacer.getReplacer(ifStatement, tipperBuilder, rewrite)), EncapsulatingNode.buildTreeFromPsi(ifStatement)));
    }

    public void testGetReplacer2() {
        PsiWhileStatement whileStatement = createTestWhileStatementFromString("while(" + COND + "){" + THEN + "}");
        PsiTreeTipperBuilder tipperBuilder = null;
        try {
            tipperBuilder = new PsiTreeTipperBuilderImpl().buildTipperPsiTree(TIPPER_FILE_NAME_2);
        } catch (Exception ignore) {
        }
        assertNotNull(tipperBuilder);
        PsiRewrite rewrite = new PsiRewrite().project(whileStatement.getProject()).psiFile(whileStatement.getContainingFile());
        PsiWhileStatement newWhileStatement = createTestWhileStatementFromString("while(" + COND + ")" + THEN + "");
        PsiElement n = Replacer.getReplacer(whileStatement, tipperBuilder, rewrite);
        assertTrue(PsiTreeMatcher.match(EncapsulatingNode.buildTreeFromPsi(n), EncapsulatingNode.buildTreeFromPsi(newWhileStatement)));
        assertFalse(PsiTreeMatcher.match(EncapsulatingNode.buildTreeFromPsi(n), EncapsulatingNode.buildTreeFromPsi(whileStatement)));
    }

    public void testGetReplace3() {
        PsiIfStatement ifStatement = createTestIfStatement("!(!(" + COND + "))", THEN);
        PsiTreeTipperBuilder tipperBuilder = null;
        try {
            tipperBuilder = new PsiTreeTipperBuilderImpl().buildTipperPsiTree(TIPPER_FILE_NAME_3);
        } catch (Exception ignore) {
        }
        assertNotNull(tipperBuilder);
        PsiRewrite rewrite = new PsiRewrite().project(ifStatement.getProject()).psiFile(ifStatement.getContainingFile());
        PsiIfStatement newIfStatement = createTestIfStatement(COND, THEN);
        PsiElement n = Replacer.getReplacer(ifStatement, tipperBuilder, rewrite);
        assertTrue(PsiTreeMatcher.match(EncapsulatingNode.buildTreeFromPsi(n), EncapsulatingNode.buildTreeFromPsi(newIfStatement)));
        assertFalse(PsiTreeMatcher.match(EncapsulatingNode.buildTreeFromPsi(n), EncapsulatingNode.buildTreeFromPsi(ifStatement)));
    }

    public void testReplace() {
        PsiIfStatement ifStatement = createTestIfStatement(COND, THEN);
        PsiElement parent = ifStatement.getParent();
        PsiTreeTipperBuilder tipperBuilder = null;
        try {
            tipperBuilder = new PsiTreeTipperBuilderImpl().buildTipperPsiTree(TIPPER_FILE_NAME_1);
        } catch (Exception ignore) {
        }
        PsiRewrite rewrite = new PsiRewrite().project(ifStatement.getProject()).psiFile(ifStatement.getContainingFile());
        PsiIfStatement newIfStatement = createTestIfStatementNoBraces(COND, THEN);
        EncapsulatingNode x = Replacer.replace(ifStatement, tipperBuilder, rewrite);
        EncapsulatingNode y = EncapsulatingNode.buildTreeFromPsi(newIfStatement);
        assertTrue(PsiTreeMatcher.match(x, y));
        assertTrue(PsiTreeMatcher.match(EncapsulatingNode.buildTreeFromPsi(PsiTreeUtil.findChildOfType(parent, PsiIfStatement.class)), EncapsulatingNode.buildTreeFromPsi(newIfStatement)));

    }

    public void testExtractInfo1() {
        PsiIfStatement b = createTestIfStatement("booleanExpression()", "statement();");
        Wrapper<Integer> count = new Wrapper<>(0);
        b.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitCodeBlock(PsiCodeBlock block) {
                super.visitCodeBlock(block);
                block.putUserData(KeyDescriptionParameters.NO_OF_STATEMENTS, Amount.EXACTLY_ONE);
            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression methodCallExpression) {
                super.visitMethodCallExpression(methodCallExpression);
                methodCallExpression.putUserData(KeyDescriptionParameters.GENERIC_NAME, methodCallExpression.getMethodExpression().getText());
                methodCallExpression.putUserData(KeyDescriptionParameters.ID, count.get());

                count.set(count.get() + 1);
            }
        });
        EncapsulatingNode n = EncapsulatingNode.buildTreeFromPsi(b);
        Pruning.prune(n);

        PsiIfStatement y = createTestIfStatement("true", " int y = 5; ");
        assertTrue(PsiTreeMatcher.match(n, EncapsulatingNode.buildTreeFromPsi(y)));
        Map<Integer, PsiElement> m = Replacer.extractInfo(n, y);
        assertTrue(iz.expression(m.get(0)));
        assertTrue(iz.statement(m.get(1)));
    }

}