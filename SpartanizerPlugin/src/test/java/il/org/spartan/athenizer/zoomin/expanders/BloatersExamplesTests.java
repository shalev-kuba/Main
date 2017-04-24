package il.org.spartan.athenizer.zoomin.expanders;

import static il.org.spartan.spartanizer.testing.TestUtilsBloating.*;

import static java.util.stream.Collectors.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

import il.org.spartan.athenizer.*;
import il.org.spartan.spartanizer.tippers.*;
import il.org.spartan.spartanizer.tipping.*;
import il.org.spartan.spartanizer.traversal.*;
import il.org.spartan.utils.Example.*;
import nano.ly.*;

/** Examples tests for bloaters.
 * @author Ori Roth <tt>ori.rothh@gmail.com</tt>
 * @since 2017-03-27 */
@RunWith(Parameterized.class)
public class BloatersExamplesTests extends ExamplesTests {
  @Override protected void ignores(final Ignores ¢) {
    final Tipper<?>[] ¢1 = { tipper };
    wrap(() -> bloatingOf(¢.get()).using(¢1).stays());
  }

  @Override protected void converts(final Converts ¢) {
    final Tipper<?>[] ¢1 = { tipper };
    wrap(() -> bloatingOf(¢.from()).using(¢1).gives(¢.to()));
  }

  public BloatersExamplesTests(final Tipper<? extends ASTNode> tipper, final String name) {
    super(tipper, name);
  }

  @Parameters(name = "{index}. {1}") //
  public static Collection<Object[]> data() {
    return allTippers().stream().map(λ -> new Object[] { λ, English.name(λ) }).collect(toList());
  }

  /** Get all tippers from {@link Configuration}. Removes duplicate tippers
   * (same class, different templates).
   * @return all tippers to be tested */
  private static Collection<?> allTippers() {
    return InflaterProvider.freshCopyOfAllExpanders().getAllTippers() //
        .stream().collect(toMap(Tipper::getClass, λ -> λ, (t1, t2) -> t1)).values();
  }
}