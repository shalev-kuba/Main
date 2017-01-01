package il.org.spartan.spartanizer.research.patterns;

import static il.org.spartan.spartanizer.tippers.TrimmerTestsUtils.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

/** @author Ori Marcovitch
 * @since 2016 */
@SuppressWarnings("static-method")
public class TakeDefaultToTest {
  @Test public void basic() {
    trimmingOf("return ¢ != null ? ¢ : \"\";")//
        .withTipper(ConditionalExpression.class, new DefaultsTo())//
        .gives("return default¢(¢).to(\"\");")//
        .stays();
  }
  
  @Test public void basic2() {
    trimmingOf("return hiChars == null ? 1 : hiChars.length;")//
        .withTipper(ConditionalExpression.class, new DefaultsTo())//
        .gives("return default¢(¢).to(\"\");")//
        .stays();
  }
  
}
