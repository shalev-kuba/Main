package il.org.spartan.ispartanizer.plugin.leonidas;

import com.intellij.psi.PsiIfStatement;
import il.org.spartan.ispartanizer.auxilary_layer.iz;
import il.org.spartan.ispartanizer.plugin.EncapsulatingNode;
import il.org.spartan.ispartanizer.plugin.EncapsulatingNodeVisitor;
import il.org.spartan.ispartanizer.tippers.TipperTest;

import java.io.IOException;

/**
 * @author Oren Afek
 * @since 08/01/17
 */
public class PsiTreeTipperBuilderTest extends TipperTest {

    private static final String TEST_FILE_NAME = "RemoveCurlyBracesFromIfStatement" + ".java";

    private PsiTreeTipperBuilder $;


    public void testBuildFromTestFileTree() throws Exception {
        $ = new PsiTreeTipperBuilderImpl();
        try {
            $.buildTipperPsiTree(TEST_FILE_NAME);
            EncapsulatingNode actualFrom = $.getFromPsiTree();
            assertTrue(iz.ifStatement(actualFrom.getInner()));
        } catch (IOException ignore) {
            fail();
        }

    }

    public void testPuttingUserData() throws Exception {
        $ = new PsiTreeTipperBuilderImpl();
        try {
            $.buildTipperPsiTree(TEST_FILE_NAME);
            EncapsulatingNode actualFrom = $.getFromPsiTree();
            actualFrom.accept(new EncapsulatingNodeVisitor() {
                @Override
                public void visit(EncapsulatingNode e) {
                    if (iz.methodCallExpression(e.getInner()))
                        assertEquals(Integer.valueOf(0), e.getInner().getUserData(KeyDescriptionParameters.ID));
                }
            });
        } catch (IOException ignore) {
            fail();
        }
    }

    public void testPruning() throws Exception {
        $ = new PsiTreeTipperBuilderImpl();
        try {
            $.buildTipperPsiTree(TEST_FILE_NAME);
            EncapsulatingNode actualFrom = $.getFromPsiTree();
            actualFrom.accept(new EncapsulatingNodeVisitor() {
                @Override
                public void visit(EncapsulatingNode e) {
                    if (iz.methodCallExpression(e.getInner()))
                        assertEquals(0, e.getChildren().size());
                }
            });
        } catch (IOException ignore) {
            fail();
        }
    }

    public void testBuildToTestFileTree() throws Exception {
        $ = new PsiTreeTipperBuilderImpl();
        try {
            $.buildTipperPsiTree(TEST_FILE_NAME);
            EncapsulatingNode actualTo = $.getToPsiTree();
            assertTrue(iz.ifStatement(actualTo.getInner()));
        } catch (IOException ignore) {
            fail();
        }
    }

    public void testGetRootElementType() throws Exception {
        $ = new PsiTreeTipperBuilderImpl();
        try {
            $.buildTipperPsiTree(TEST_FILE_NAME);
            assertEquals(PsiIfStatement.class, $.getRootElementType());
        } catch (IOException ignore) {
            fail();
        }
    }

    public void testGetDescription() throws Exception {
        $ = new PsiTreeTipperBuilderImpl();
        try {
            $.buildTipperPsiTree(TEST_FILE_NAME);
            assertEquals("Remove redundent curly braces".trim(), $.getDescription());
        } catch (IOException ignore) {
            fail();
        }
    }
}
