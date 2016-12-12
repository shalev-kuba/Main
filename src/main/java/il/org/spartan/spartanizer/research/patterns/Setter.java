package il.org.spartan.spartanizer.research.patterns;

import static il.org.spartan.lisp.*;

import org.eclipse.jdt.core.dom.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.research.*;
import il.org.spartan.spartanizer.research.patterns.common.*;

/** @author Ori Marcovitch
 * @since 2016 */
public class Setter extends JavadocMarkerNanoPattern<MethodDeclaration> {
  private static final UserDefinedTipper<Expression> tipper = TipperFactory.patternTipper("this.$N", "", "");

  @Override public boolean prerequisites(final MethodDeclaration ¢) {
    if (parameters(¢) == null || parameters(¢).size() != 1 || body(¢) == null || iz.static¢(¢) || iz.constructor(¢) || statements(¢).size() != 1)
      return false;
    final Assignment $ = az.assignment(expression(az.expressionStatement(onlyOne(statements(¢)))));
    return $ != null && (iz.name(left($)) || tipper.canTip(left($))) && wizard.same(right($), name(onlyOne(parameters(¢))));
  }
}
