package il.org.spartan.spartanizer.research.patterns;

import java.util.*;
import java.util.stream.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.leonidas.*;
import il.org.spartan.spartanizer.utils.*;

/** @author Ori Marcovitch
 * @since 2016 */
public class JDPattern extends JavadocMarkerNanoPattern<MethodDeclaration> {
  static Set<UserDefinedTipper<Expression>> tippers;

  public JDPattern() {
    if (tippers != null)
      return;
    tippers = new HashSet<>();
    tippers.add(TipperFactory.tipper("$X == null", "", ""));
    tippers.add(TipperFactory.tipper("$X != null", "", ""));
    tippers.add(TipperFactory.tipper("null == $X", "", ""));
    tippers.add(TipperFactory.tipper("null == $X", "", ""));
  }

  @Override protected boolean prerequisites(final MethodDeclaration d) {
    if (step.parameters(d) == null || step.parameters(d).isEmpty())
      return false;
    final Set<String> ps = new HashSet<>(step.parameters(d).stream().map(x -> x.getName() + "").collect(Collectors.toList()));
    Set<String> set = new HashSet<>(ps);
    set.addAll(getInfluenced(d, ps));
    final Bool $ = new Bool();
    $.inner = true;
    d.accept(new ASTVisitor() {
      @Override public boolean visit(final IfStatement ¢) {
        return checkContainsParameter(¢.getExpression());
      }

      @Override public boolean visit(final ForStatement ¢) {
        return checkContainsParameter(step.condition(¢)) || checkContainsParameter(step.initializers(¢)) || checkContainsParameter(step.updaters(¢));
      }

      @Override public boolean visit(final WhileStatement ¢) {
        return checkContainsParameter(¢);
      }

      @Override public boolean visit(final EnhancedForStatement ¢) {
        return checkContainsParameter(¢);
      }

      @Override public boolean visit(final TryStatement ¢) {
        return checkContainsParameter(¢);
      }

      @Override public boolean visit(final AssertStatement ¢) {
        return checkContainsParameter(¢);
      }

      @Override public boolean visit(final DoStatement ¢) {
        return checkContainsParameter(¢);
      }

      boolean checkContainsParameter(final ASTNode ¢) {
        if (containsParameter(¢, set))
          $.inner = false;
        return false;
      }

      boolean checkContainsParameter(List<Expression> xs) {
        for (Expression ¢ : xs)
          if (checkContainsParameter(¢))
            return true;
        return false;
      }
    });
    return $.inner;
  }

  /** @param root node to search in
   * @param ss variable names which are influenced by parameters
   * @return */
  static boolean containsParameter(ASTNode root, Set<String> ss) {
    final Bool $ = new Bool();
    $.inner = false;
    root.accept(new ASTVisitor() {
      @Override public boolean visit(final SimpleName n) {
        for (String p : ss)
          if ((n + "").equals(p) && !nullCheckExpression(az.infixExpression(n.getParent())))
            $.inner = true;
        return false;
      }
    });
    return $.inner;
  }

  static Set<String> getInfluenced(MethodDeclaration root, Set<String> ps) {
    final Set<String> $ = new HashSet<>();
    $.addAll(ps);
    step.body(root).accept(new ASTVisitor() {
      @Override public boolean visit(final Assignment n) {
        if (containsParameter(step.right(n), $))
          $.add(extractName(step.left(n)));
        return true;
      }

      @Override public boolean visit(final VariableDeclarationFragment n) {
        if (containsParameter(n.getInitializer(), $))
          $.add(extractName(n.getName()));
        return true;
      }

      @Override public boolean visit(final SingleVariableDeclaration n) {
        if (containsParameter(n.getInitializer(), $))
          $.add(extractName(n.getInitializer()));
        return true;
      }
    });
    return $;
  }

  protected static String extractName(Expression root) {
    final Str str = new Str();
    root.accept(new ASTVisitor() {
      @Override public boolean visit(final SimpleName n) {
        str.inner = n + "";
        return false;
      }
    });
    return str.inner;
  }

  /** [[SuppressWarningsSpartan]] */
  static boolean nullCheckExpression(Expression ¢) {
    if (¢ == null)
      return false;
    for (UserDefinedTipper<Expression> t : tippers)
      if (t.canTip(¢))
        return true;
    return false;
  }

  @Override public String description(final MethodDeclaration ¢) {
    return ¢.getName() + " is a JD method";
  }

  @Override protected String javadoc() {
    return "[[JDPattern]]";
  }
}