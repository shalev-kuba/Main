package il.org.spartan.Leonidas.plugin.leonidas;

import com.google.common.io.Files;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.psi.*;
import il.org.spartan.Leonidas.auxilary_layer.*;
import il.org.spartan.Leonidas.plugin.EncapsulatingNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static il.org.spartan.Leonidas.plugin.leonidas.KeyDescriptionParameters.ID;

/**
 * @author Oren Afek
 * @since 06-01-2017
 */
public class PsiTreeTipperBuilderImpl implements PsiTreeTipperBuilder {

    private static final String FILE_PATH = "/spartanizer/LeonidasTippers/";
    private static final String FROM_METHOD_NAME = "from";
    private static final String TO_METHOD_NAME = "to";
    private static final String LEONIDAS_ANNOTATION_NAME = "il.org.spartan.Leonidas.plugin.leonidas.Leonidas";
    private static final String SHORT_LEONIDAS_ANNOTATION_NAME = "Leonidas";
    private static final String LEONIDAS_ANNOTATION_ORDER = "order";
    private static final String PSI_PACKAGE_PREFIX = "com.intellij.psi.";
    private static final String LEONIDAS_ANNOTATION_VALUE = "value";

    private boolean built;
    private EncapsulatingNode fromTree;
    private EncapsulatingNode toTree;
    private Class<? extends PsiElement> fromRootElementType;
    private String description;
    private int defId;
    private Map<Integer, Integer> mapToDef = new HashMap<>();

    /**
     * Build both the "from" and "to" trees from source code, including pruning.
     *
     * @param fileName - the file name of the Leonidas tipper to build.
     * @return @link{this}
     * @throws IOException in case the file could not be opened.
     */
    @SuppressWarnings("ConstantConditions")
    public PsiTreeTipperBuilderImpl buildTipperPsiTree(String fileName) throws IOException {
        assert (!built);
        PsiJavaFile root = getPsiTreeFromFile(fileName);
        description = Utils.getClassFromFile(root).getDocComment().getText()
                .split("\\n")[1].trim()
                .split("\\*")[1].trim();
        fromTree = buildMethodTree(root, FROM_METHOD_NAME);
        toTree = buildMethodTree(root, TO_METHOD_NAME);
        built = true;
        return this;
    }

    private EncapsulatingNode buildMethodTree(PsiFile root, String methodName) {
        PsiMethod method = getMethodFromTree(root, methodName);
        Class<? extends PsiElement> rootType = getPsiElementTypeFromAnnotation(method);
        if (methodName.equals(FROM_METHOD_NAME))
			fromRootElementType = rootType;
        PsiElement tree = getTreeFromRoot(method, rootType);
        handleStubMethodCalls(tree, methodName);
        EncapsulatingNode e = EncapsulatingNode.buildTreeFromPsi(tree);
        pruneStubChildren(e);
        return e;
    }

    /**
     * Retrieving the "from" tree. This method should only be called after
     *
     * @return the "from" tree
     * @link {@link PsiTreeTipperBuilder}.buildTipperPsiTree was called.
     */
    @Override
    public EncapsulatingNode getFromPsiTree() {
        assert (built);
        return fromTree;
    }

    /**
     * Retrieving the "to" tree. This method should only be called after
     *
     * @return the "to" tree
     * @link {@link PsiTreeTipperBuilder}.buildTipperPsiTree was called.
     */
    @Override
    public EncapsulatingNode getToPsiTree() {
        assert (built);
        return toTree.clone();
    }

    private PsiJavaFile getPsiTreeFromFile(String fileName) throws IOException {
        File file = new File(Utils.fixSpacesProblemOnPath(this.getClass().getResource(FILE_PATH + fileName).getPath()));
        return (PsiJavaFile) PsiFileFactory.getInstance(Utils.getProject()).createFileFromText(fileName,
                FileTypeRegistry.getInstance().getFileTypeByFileName(file.getName()),
                String.join("\n", Files.readLines(file, StandardCharsets.UTF_8)));
    }

    private PsiMethod getMethodFromTree(PsiFile f, String methodName) {
        Wrapper<PsiMethod> result = new Wrapper<>();
        f.accept(new JavaRecursiveElementVisitor() {

            @Override
            public void visitMethod(PsiMethod m) {
                if (step.name(m).equals(methodName))
					result.set(m);
            }
        });
        return result.get();
    }

    private Class<? extends PsiElement> getPsiClass(String s) {
        try {
            //noinspection unchecked
            return (Class<? extends PsiElement>) Class.forName("com.intellij.psi." + s);
        } catch (ClassNotFoundException ignore) {
        }
        return PsiElement.class;
    }

    @SuppressWarnings({"ConstantConditions", "OptionalGetWithoutIsPresent"})
    private Class<? extends PsiElement> getPsiElementTypeFromAnnotation(PsiMethod x) {
        return Arrays.stream(x.getModifierList().getAnnotations())
                .filter(a -> LEONIDAS_ANNOTATION_NAME.equals(a.getQualifiedName()) || SHORT_LEONIDAS_ANNOTATION_NAME.equals(a.getQualifiedName()))
                .map(a -> getPsiClass(a.findDeclaredAttributeValue(LEONIDAS_ANNOTATION_VALUE).getText().replace(".class", "")))
                .findFirst().get();
    }

    private PsiElement getTreeFromRoot(PsiMethod m, Class<? extends PsiElement> rootElementType) {
        Wrapper<PsiElement> result = new Wrapper<>();
        Wrapper<Boolean> stop = new Wrapper<>(false);
        m.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement e) {
                super.visitElement(e);
                if (stop.get() || !iz.ofType(e, rootElementType))
					return;
				result.set(e);
				stop.set(true);
            }
        });
        return result.get();
    }

    private void handleStubMethodCalls(PsiElement innerTree, String outerMethodName) {
        innerTree.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression x) {
                if (!iz.stubMethodCall(x))
					return;
                Integer id;
                if (!outerMethodName.equals(FROM_METHOD_NAME))
					id = step.firstParamterExpression(x) == null ? defId++
							: mapToDef.get(az.integer(step.firstParamterExpression(x)));
				else {
					if (step.firstParamterExpression(x) != null)
						mapToDef.put(az.integer(step.firstParamterExpression(x)), defId);
					id = defId++;
				}
                addOrderToUserData(x, id);
            }
        });
    }

    private void pruneStubChildren(EncapsulatingNode innerTree) {
        Pruning.prune(innerTree);
    }

    private PsiElement addOrderToUserData(PsiElement e, int order) {
        e.putUserData(ID, order);
        return e;
    }

    @Override
    public Class<? extends PsiElement> getRootElementType() {
        return fromRootElementType;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
