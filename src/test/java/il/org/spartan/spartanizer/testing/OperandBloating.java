package il.org.spartan.spartanizer.testing;

import static il.org.spartan.azzert.*;

import static il.org.spartan.lisp.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.jface.text.*;
import org.eclipse.text.edits.*;

import il.org.spartan.*;
import il.org.spartan.bloater.*;
import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.engine.nominal.*;
import il.org.spartan.spartanizer.utils.*;
import il.org.spartan.utils.*;

public class OperandBloating extends TrimmingOperand {
  ASTNode ast;
  String xclassText;

  public OperandBloating(final String inner) {
    super(inner);
  }

  public OperandBloating(final ASTNode inner, final String classText) {
    super(classText);
    ast = inner;
  }

  @Override public OperandBloating gives(final String $) {
    assert $ != null;
    final Wrap w = Wrap.find(get());
    final String wrap = w.on(get());
    final CompilationUnit u = (CompilationUnit) makeAST.COMPILATION_UNIT.from(wrap);
    final ASTRewrite r = ASTRewrite.create(u.getAST());
    SingleFlater.in(u).from(new InflaterProvider()).go(r, TestUtilsBloating.textEditGroup);
    try {
      final IDocument doc = new Document(wrap);
      r.rewriteAST(doc, null).apply(doc);
      final String unpeeled = doc.get(), $1 = rename((CompilationUnit) makeAST.COMPILATION_UNIT.from(Wrap.find($).on($))) + "",
          unpeeled1 = rename((CompilationUnit) makeAST.COMPILATION_UNIT.from(unpeeled)) + "";
      if ((rename((CompilationUnit) makeAST.COMPILATION_UNIT.from(wrap)) + "").equals(unpeeled1))
        azzert.fail("Nothing done on " + get());
      final String peeled1 = w.off(unpeeled1);
      if (peeled1.equals(get()))
        azzert.that("No Bloating of " + get(), peeled1, is(not(get())));
      if (tide.clean(peeled1).equals(tide.clean(get())))
        azzert.that("Bloatong of " + get() + "is just reformatting", tide.clean(get()), is(not(tide.clean(peeled1))));
      if (!$1.equals(peeled1) && !trivia.essence(peeled1).equals(trivia.essence($1))) {
        copyPasteReformat("  .gives(\"%s\") //\nCompare with\n .gives(\"%s\") //\n", //
            trivia.escapeQuotes(trivia.essence(peeled1)), //
            trivia.escapeQuotes(trivia.essence($1)));
        azzert.that(trivia.essence(peeled1), is(trivia.essence($1)));
      }
      return new OperandBloating($1);
    } catch (MalformedTreeException | IllegalArgumentException | BadLocationException ¢) {
      monitor.logProbableBug(this, ¢);
    }
    return null;
  }

  public OperandBloating givesWithBinding(final String $) {
    assert $ != null;
    final CompilationUnit u = az.compilationUnit(ast);
    final String wrap = get();
    final ASTRewrite r = ASTRewrite.create(u.getAST());
    SingleFlater.in(u).usesDisabling(false).from(new InflaterProvider()).go(r, TestUtilsBloating.textEditGroup);
    try {
      final String $1 = rename((CompilationUnit) makeAST.COMPILATION_UNIT.from(Wrap.find($).on($))) + "";
      final IDocument doc = new Document(wrap);
      r.rewriteAST(doc, null).apply(doc);
      final String unpeeled = rename((CompilationUnit) makeAST.COMPILATION_UNIT.from(doc)) + "";
      if (wrap.equals(unpeeled))
        azzert.fail("Nothing done on " + get());
      if (unpeeled.equals(get()))
        azzert.that("No trimming of " + get(), unpeeled, is(not(get())));
      if (!$1.equals(unpeeled) && !trivia.essence(unpeeled).equals(trivia.essence($1))) {
        copyPasteReformat("  .gives(\"%s\") //\nCompare with\n .gives(\"%s\") //\n", //
            trivia.escapeQuotes(trivia.essence(unpeeled)), //
            trivia.escapeQuotes(trivia.essence($1)));
        azzert.that(trivia.essence(unpeeled), is(trivia.essence($1)));
      }
      return new OperandBloating(createCUWithBinding(unpeeled), unpeeled);
    } catch (MalformedTreeException | IllegalArgumentException | BadLocationException ¢) {
      monitor.logProbableBug(¢);
    }
    return null;
  }

  /** @param $ java code
   * @param f tested method name. expanders will be applied only for this method
   * @return */
  public OperandBloating givesWithBinding(final String $, final String f) {
    assert $ != null;
    final CompilationUnit u = az.compilationUnit(ast);
    final String wrap = get();
    final ASTRewrite r = ASTRewrite.create(u.getAST());
    MethodDeclaration m = getMethod(u, f);
    SingleFlater.in(m).usesDisabling(false).from(new InflaterProvider()).go(r, TestUtilsBloating.textEditGroup);
    try {
      final IDocument doc = new Document(wrap);
      r.rewriteAST(doc, null).apply(doc);
      final String unpeeled = doc.get();
      if (wrap.equals(unpeeled))
        azzert.fail("Nothing done on " + get());
      if (unpeeled.equals(get()))
        azzert.that("No trimming of " + get(), unpeeled, is(not(get())));
      m = getMethod(az.compilationUnit(makeAST.COMPILATION_UNIT.from(unpeeled)), f);
      String s = m + "";
      if (!$.equals(s) && !trivia.essence(s).equals(trivia.essence($))) {
        copyPasteReformat("  .gives(\"%s\") //\nCompare with\n .gives(\"%s\") //\n", //
            trivia.escapeQuotes(trivia.essence(s)), //
            trivia.escapeQuotes(trivia.essence($)));
        azzert.that(trivia.essence(s), is(trivia.essence($)));
      }
      final ASTParser p = make.COMPILATION_UNIT.parser(unpeeled);
      p.setResolveBindings(true);
      return new OperandBloating(az.compilationUnit(p.createAST(null)), unpeeled);
    } catch (MalformedTreeException | IllegalArgumentException | BadLocationException ¢) {
      monitor.logProbableBug(this, ¢);
    }
    return null;
  }

  /** Rename all the SimpleNames in a compilation-unit to toList consistent
   * names : v1,v2,....
   * @author Dor Ma'ayan
   * @since 19-01-2017
   * @param b
   * @return */
  private static CompilationUnit rename(final CompilationUnit u) {
    if (u == null)
      return null;
    TestUtilsBloating.counter = 0;
    final CompilationUnit $ = copy.of(u);
    $.accept(new ASTVisitor(true) {
      @Override public void preVisit(final ASTNode an) {
        if (!iz.simpleName(an))
          return;
        az.simpleName(an).setIdentifier("v" + TestUtilsBloating.counter);
        ++TestUtilsBloating.counter;
      }
    });
    return $;
  }

  private static MethodDeclaration getMethod(final CompilationUnit u, final String f) {
    final List<MethodDeclaration> $ = descendants.whoseClassIs(MethodDeclaration.class).suchThat(λ -> λ.getName().getIdentifier().equals(f)).from(u);
    if ($.isEmpty())
      azzert.fail("No such method Exists");
    return first($);
  }

  private static CompilationUnit createCUWithBinding(final String text) {
    final ASTParser $ = make.COMPILATION_UNIT.parser(text);
    $.setResolveBindings(true);
    return az.compilationUnit($.createAST(null));
  }

  private void checkSame() {
    if (get().isEmpty())
      return;
    final Wrap w = Wrap.find(get());
    final String wrap = w.on(get());
    final CompilationUnit u = (CompilationUnit) makeAST.COMPILATION_UNIT.from(wrap);
    final ASTRewrite r = ASTRewrite.create(u.getAST());
    SingleFlater.in(u).from(new InflaterProvider()).go(r, TestUtilsBloating.textEditGroup);
    try {
      final IDocument doc = new Document(wrap);
      r.rewriteAST(doc, null).apply(doc);
      final String unpeeled = doc.get();
      final String peeled = w.off(unpeeled);
      if (wrap.equals(peeled) || trivia.essence(get()).equals(trivia.essence(peeled)))
        return;
      copyPasteReformat("\n .gives(\"%s\") //\nCompare with\n  .gives(\"%s\") //\n", //
          trivia.escapeQuotes(trivia.essence(peeled)), //
          trivia.escapeQuotes(trivia.essence(get())));
      azzert.that(trivia.essence(peeled), is(trivia.essence(get())));
    } catch (MalformedTreeException | IllegalArgumentException | BadLocationException ¢) {
      monitor.logProbableBug(this, ¢);
    }
  }

  private void checkSameWithBinding() {
    final String wrap = get();
    final CompilationUnit u = az.compilationUnit(ast);
    final ASTRewrite r = ASTRewrite.create(u.getAST());
    SingleFlater.in(u).from(new InflaterProvider()).go(r, TestUtilsBloating.textEditGroup);
    try {
      final IDocument doc = new Document(wrap);
      r.rewriteAST(doc, null).apply(doc);
      final String unpeeled = doc.get();
      if (wrap.equals(unpeeled) || trivia.essence(get()).equals(trivia.essence(unpeeled)))
        return;
      copyPasteReformat("\n .gives(\"%s\") //\nCompare with\n  .gives(\"%s\") //\n", //
          trivia.escapeQuotes(trivia.essence(unpeeled)), //
          trivia.escapeQuotes(trivia.essence(get())));
      azzert.that(trivia.essence(unpeeled), is(trivia.essence(get())));
    } catch (MalformedTreeException | IllegalArgumentException | BadLocationException ¢) {
      monitor.logProbableBug(this, ¢);
    }
  }

  @Override public void stays() {
    checkSame();
  }

  public void staysWithBinding() {
    checkSameWithBinding();
  }
}