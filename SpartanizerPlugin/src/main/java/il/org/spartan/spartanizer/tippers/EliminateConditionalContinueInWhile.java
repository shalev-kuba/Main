package il.org.spartan.spartanizer.tippers;

import org.eclipse.jdt.core.dom.*;
import org.jetbrains.annotations.*;

import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.tipping.*;

/** Eliminate conditional continue before last statement in a while loop toList
 * Issue #1014
 * @author Dor Ma'ayan <tt>dor.d.ma@gmail.com</tt>
 * @since 2017-01-04 */
public class EliminateConditionalContinueInWhile extends EagerTipper<WhileStatement>//
    implements TipperCategory.Shortcircuit {
  private static final long serialVersionUID = -2214012312380722330L;

  @Override @NotNull public String description(@SuppressWarnings("unused") final WhileStatement __) {
    return "Eliminate conditional continue before last statement in the for loop";
  }

  @Override @Nullable public Tip tip(@NotNull final WhileStatement ¢) {
    return EliminateConditionalContinueAux.actualReplacement(az.block(¢.getBody()), ¢, getClass());
  }
}
