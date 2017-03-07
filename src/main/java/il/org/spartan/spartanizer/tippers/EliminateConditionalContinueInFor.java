package il.org.spartan.spartanizer.tippers;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.tipping.*;

/** Eliminate conditional continue before last statement in a for loop toList
 * Issue #1014
 * @author Dor Ma'ayan <tt>dor.d.ma@gmail.com</tt>
 * @since 2017-01-04 */
public class EliminateConditionalContinueInFor extends EagerTipper<ForStatement>//
    implements TipperCategory.Shortcircuit {
  private static final long serialVersionUID = 1319731512145811654L;

  @Override public String description(final ForStatement ¢) {
    return "Eliminate conditional continue before last statement in the for loop about " + ¢.getExpression();
  }

  @Override public Tip tip(final ForStatement ¢) {
    return EliminateConditionalContinueAux.actualReplacement(az.block(¢.getBody()), ¢, getClass());
  }
}