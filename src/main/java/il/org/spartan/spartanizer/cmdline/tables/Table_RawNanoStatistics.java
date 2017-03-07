package il.org.spartan.spartanizer.cmdline.tables;

import java.lang.reflect.*;
import java.util.*;

import org.eclipse.jdt.core.dom.*;

import static il.org.spartan.spartanizer.ast.navigate.wizard.*;

import il.org.spartan.spartanizer.cmdline.*;
import il.org.spartan.spartanizer.cmdline.nanos.*;
import il.org.spartan.spartanizer.research.*;
import il.org.spartan.spartanizer.research.analyses.*;
import il.org.spartan.tables.*;
import il.org.spartan.utils.*;

/** Generates a table that shows how many times each nano occurred in each
 * project
 * @author orimarco <tt>marcovitch.ori@gmail.com</tt>
 * @since 2017-01-03 */
public class Table_RawNanoStatistics extends FolderASTVisitor {
  private static final SpartAnalyzer spartanalyzer = new SpartAnalyzer().addRejected();
  static final AgileSpartanizer spartanizer = new AgileSpartanizer();
  private static Table pWriter;
  private static final NanoPatternsStatistics npStatistics = new NanoPatternsStatistics();
  static {
    clazz = Table_RawNanoStatistics.class;
    Logger.subscribe(npStatistics::logNPInfo);
  }

  private static void initializeWriter() {
    pWriter = new Table(outputFileName());
  }

  private static String outputFileName() {
    return Table_RawNanoStatistics.class.getSimpleName();
  }

  public static void main(final String[] args)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    FolderASTVisitor.main(args);
    pWriter.close();
  }

  @Override public boolean visit(final CompilationUnit $) {
    try {
      spartanalyzer.fixedPoint(spartanizer.fixedPoint($));
    } catch (final AssertionError __) {
      ___.unused(__);
    }
    return super.visit($);
  }

  @Override public boolean visit(final FieldDeclaration ¢) {
    spartanalyzer.fixedPoint(ast(¢ + ""));
    return true;
  }

  @Override protected void done(final String path) {
    summarizeNPStatistics(path);
    System.err.println(" " + path + " Done");
  }

  public static void summarizeNPStatistics(final String path) {
    if (pWriter == null)
      initializeWriter();
    pWriter.col("Project", path);
    npStatistics.keySet().stream()//
        .sorted(Comparator.comparing(λ -> npStatistics.get(λ).name))//
        .map(npStatistics::get)//
        .forEach(λ -> pWriter.col(λ.name, λ.occurences));
    fillAbsents();
    pWriter.nl();
    npStatistics.clear();
  }

  private static void fillAbsents() {
    spartanalyzer.getAllPatterns().stream()//
        .map(λ -> λ.getClass().getSimpleName())//
        .filter(λ -> !npStatistics.keySet().contains(λ))//
        .forEach(λ -> pWriter.col(λ, 0));
  }
}