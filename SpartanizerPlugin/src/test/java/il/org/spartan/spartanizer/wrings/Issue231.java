package il.org.spartan.spartanizer.wrings;

import static il.org.spartan.spartanizer.wrings.TrimmerTestsUtils.*;

import org.junit.*;
import org.junit.runners.*;

/** @author Yossi Gil
 * @since 2016 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
@SuppressWarnings({ "static-method", "javadoc" }) //
public class Issue231 {
  @Test public void chocolate1() {
    trimmingOf("a ? x.f(b) : y.f(b)")//
        .gives("(a?x:y).f(b)");
  }

  @Test public void chocolate2() {
    trimmingOf("a ? myClass.f(b) : yourClass.f(b)")//
        .gives("(a?myClass:yourClass).f(b)") //
        .stays();

  }

  @Test public void vanilla1() {
    trimmingOf("a ? Class.f(b) : y.f(b)")//
        .stays();
  }

  @Test public void vanilla2() {
    trimmingOf("a ? MyClass.f(b) : y.f(b)")//
        .stays();
  }
  @Test public void vanilla3() {
    trimmingOf("a ? MyClass.f(b) : YourClass.f(b)")//
        .stays();
  }
  @Test public void vanilla4() {
    trimmingOf("a ? x.f(b) : YourClass.f(b)")//
        .stays();
  }
}
