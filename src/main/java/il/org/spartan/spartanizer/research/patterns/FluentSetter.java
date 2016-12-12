package il.org.spartan.spartanizer.research.patterns;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.research.*;
import il.org.spartan.spartanizer.research.patterns.common.*;

/** @author Ori Marcovitch
 * @since 2016 */
public class FluentSetter extends JavadocMarkerNanoPattern<MethodDeclaration> {
  private static final UserDefinedTipper<Block> tipper = TipperFactory.patternTipper("{this.$N = $N2; return this;}", "", "");

  @Override protected boolean prerequisites(final MethodDeclaration ¢) {
    if (step.parameters(¢).size() != 1 || step.body(¢) == null)
      return false;
    @SuppressWarnings("unchecked") final List<Statement> ss = ¢.getBody().statements();
    if (ss.size() != 2 || !iz.expressionStatement(ss.get(0)))
      return false;
    final Expression e = az.expressionStatement(ss.get(0)).getExpression();
    if (!iz.assignment(e))
      return false;
    final Assignment $ = az.assignment(e);
    return (iz.name($.getLeftHandSide()) || tipper.canTip(step.body(¢))) && wizard.same($.getRightHandSide(), step.parameters(¢).get(0).getName());
  }
}
