package il.org.spartan.Leonidas.plugin.tippers;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import icons.Icons;
import il.org.spartan.Leonidas.auxilary_layer.PsiRewrite;
import il.org.spartan.Leonidas.plugin.Toolbox;
import il.org.spartan.Leonidas.plugin.tipping.Tip;
import il.org.spartan.Leonidas.plugin.tipping.Tipper;
import il.org.spartan.Leonidas.plugin.tipping.TipperCategory;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

/**
 * Represents a tipper that changes the code of the user to a code that need the creation of
 * a special environment.
 *
 * @author Roey Maor, michalcohen
 * @since 26-12-2016
 */
public abstract class NanoPatternTipper implements Tipper, TipperCategory.Nanos {

    /**
     * @param e the PsiElement on which the tip will be applied
     * @return an element tip to apply on e.
     */
    @NotNull
    public Tip tip(final PsiElement e) {
        PsiDirectory srcDir = e.getContainingFile().getContainingDirectory();
        try {
            srcDir.checkCreateSubdirectory("spartanizer");
            Object[] options = {"Accept",
                    "Cancel"};

            if (JOptionPane.showOptionDialog(new JFrame(),
                    "You are about to apply a nano pattern.\nPlease notice that nano pattern tippers are "
                            + "code transformations that require adding a '.java' file "
                            + "to your project directory.\nTo apply the tip, press the Accept button.",
                    "SpartanizerUtils", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Icons.Leonidas,
					options, options[1]) == 1)
                return new Tip(description(e), e, this.getClass()) {
                    @Override
                    public void go(PsiRewrite r) {
                    }
				};
        } catch (Exception ignored) {
        }
        return new Tip(description(e), e, this.getClass()) {
            @Override
            public void go(PsiRewrite r) {
                PsiElement e_tag = createReplacement(e);
                new WriteCommandAction.Simple(e.getProject(), e.getContainingFile()) {
                    @Override
                    protected void run() throws Throwable {
                        if (!canTip(e)) return;
                        if ((!Toolbox.getInstance().playground) && (!Toolbox.getInstance().testing))
                            createEnvironment(e);
                        e.replace(e_tag);
                    }
                }.execute();
            }
        };
    }

    /**
     * This method should be override in order to create the psi element that will
     * replace e.
     *
     * @param e - the element to be replaced
     * @return the PsiElement that will replace e.
     */
    public abstract PsiElement createReplacement(PsiElement e);

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ResultOfMethodCallIgnored"})
    private PsiFile createUtilsFile(PsiElement e, PsiDirectory d) throws IOException {
        URL is = getClass().getResource("/spartanizer/SpartanizerUtils.java");
        File file = new File(is.getPath());
        FileType type = FileTypeRegistry.getInstance().getFileTypeByFileName(file.getName());
        file.setReadable(true, false);
        PsiFile pf = PsiFileFactory.getInstance(e.getProject()).createFileFromText("SpartanizerUtils.java", type, IOUtils.toString(new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/spartanizer/SpartanizerUtils.java")))));
        d.add(pf);
        Arrays.stream(d.getFiles()).filter(f -> "SpartanizerUtils.java".equals(f.getName())).findFirst().get().getVirtualFile().setWritable(false);
        Toolbox.getInstance().excludeFile(pf);
        return pf;
    }

    /**
     * @param e the PsiElement that the tip is applied to
     * @return the PsiFile in which e is contained
     * @throws IOException if for some reason writing to the users disk throws exception.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private PsiFile insertSpartanizerUtils(PsiElement e) throws IOException {
        PsiFile pf;
        PsiDirectory srcDir = e.getContainingFile().getContainingDirectory();
        // creates the directory and adds the file if needed
        try {
            srcDir.checkCreateSubdirectory("spartanizer");
            pf = createUtilsFile(e, srcDir.createSubdirectory("spartanizer"));
        } catch (IncorrectOperationException x) {
            PsiDirectory pd = Arrays.stream(srcDir.getSubdirectories()).filter(d -> "spartanizer".equals(d.getName())).findAny().get();
            pf = Arrays.stream(pd.getFiles()).noneMatch(f -> "SpartanizerUtils.java".equals(f.getName()))
                    ? createUtilsFile(e, pd)
                    : Arrays.stream(pd.getFiles()).filter(f -> "SpartanizerUtils.java".equals(f.getName())).findFirst()
                    .get();
        }
        return pf;
    }

    /**
     * Inserts "import static spartanizer/SpartanizerUtils/*;" to the users code.
     *
     * @param e - the PsiElement on which the tip is applied.
     * @param f - the psi file in which e is contained.
     */
    @SuppressWarnings("ConstantConditions")
    private void insertImportStatement(PsiElement e, PsiFile f) {
        PsiImportStaticStatement piss = JavaPsiFacade.getElementFactory(e.getProject()).createImportStaticStatement(PsiTreeUtil.getChildOfType(f, PsiClass.class), "*");
        PsiImportList pil = ((PsiJavaFile) e.getContainingFile()).getImportList();
        if (Arrays.stream(pil.getImportStaticStatements()).noneMatch(x -> x.getText().contains("spartanizer")))
            pil.add(piss);

    }

    /**
     * Inserts import statement and copies file in order to make the nano patterns compile
     *
     * @param e - the PsiElement on which the tip is applied.
     * @throws IOException - if for some reason writing new file to the users disk throws exception.
     */
    private void createEnvironment(final PsiElement e) throws IOException {
        insertImportStatement(e, insertSpartanizerUtils(e));
    }

    @NotNull
    @Override
    public String name() {
        return "NanoPatternTipper";
    }

    protected abstract Tip pattern(PsiElement ¢);
}
