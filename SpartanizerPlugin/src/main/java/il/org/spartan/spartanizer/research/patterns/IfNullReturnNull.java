package il.org.spartan.spartanizer.research.patterns;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.research.*;
import il.org.spartan.spartanizer.tipping.*;

/** Find if(X == null) return null; <br>
 * Find if(null == X) return null; <br>
 * @author Ori Marcovitch
 * @year 2016 */
public final class IfNullReturnNull extends NanoPatternTipper<IfStatement> {
  private static final List<UserDefinedTipper<IfStatement>> tippers = new ArrayList<>();

  public IfNullReturnNull() {
    if (!tippers.isEmpty())
      return;
    tippers.add(TipperFactory.tipper("if($X == null) return null;", "returnNullIfNull($X);", ""));
    tippers.add(TipperFactory.tipper("if(null == $X) return null;", "returnNullIfNull($X);", ""));
  }

  @Override public String description(@SuppressWarnings("unused") final IfStatement __) {
    return "replace with #default #deault x";
  }

  @Override public boolean canTip(final IfStatement s) {
    for (final UserDefinedTipper<IfStatement> ¢ : tippers)
      if (¢.canTip(s))
        return true;
    return false;
  }

  @Override public Tip tip(final IfStatement s) throws TipperFailure {
    Logger.logNP(s, "IfNullReturnNull");
    for (final UserDefinedTipper<IfStatement> ¢ : tippers)
      if (¢.canTip(s))
        return ¢.tip(s);
    assert false;
    return null;
  }
}
