package il.org.spartan.refactoring.utils;

import static il.org.spartan.azzert.*;
import static il.org.spartan.refactoring.utils.into.*;
import static il.org.spartan.refactoring.utils.Plant.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

import il.org.spartan.*;
import il.org.spartan.refactoring.java.*;

@SuppressWarnings({ "javadoc", "static-method" }) public class PlantTest {
  @Test public void plantintoLess() {
    azzert.that(subject.pair(into.e("a + 2"), into.e("b")).to(InfixExpression.Operator.LESS), iz("a+2<b"));
  }

  @Test public void plantintoNull() {
    final String s = "a?b:c";
    final Expression e = e(s);
    azzert.notNull(e);
    final Expression e1 = plant(e).into(null);
    azzert.notNull(e1);
    azzert.that(e1, iz(s));
  }

  @Test public void plantintoReturn() {
    final Expression e = into.e("2");
    final Plant plant = plant(e);
    plant.into(e.getAST().newReturnStatement());
    azzert.that(plant.into(e.getAST().newReturnStatement()), iz("2"));
  }

  @Test public void plus() {
    final Expression e = into.e("a + 2 < b");
    final Expression plus = extract.firstPlus(e);
    azzert.that(plus.toString(), stringType.isNot(plus), is(true));
    azzert.that(e.toString(), stringType.isNot(plus), is(true));
  }
}
