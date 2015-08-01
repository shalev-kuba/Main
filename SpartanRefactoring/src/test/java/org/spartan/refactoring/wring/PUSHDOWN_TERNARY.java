package org.spartan.refactoring.wring;

import java.util.Collection;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.spartan.refactoring.wring.AbstractWringTest.OutOfScope;
import org.spartan.utils.Utils;

/**
 * Unit tests for {@link Wrings#ADDITION_SORTER}.
 *
 * @author Yossi Gil
 * @since 2014-07-13
 */
@SuppressWarnings({ "javadoc", }) //
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
public class PUSHDOWN_TERNARY {
  static final Wring WRING = Wrings.PUSHDOWN_TERNARY.inner;

  @RunWith(Parameterized.class) //
  public static class OutOfScope extends AbstractWringTest.OutOfScope.Expression {
    static String[][] cases = Utils.asArray(//
        Utils.asArray("Expression vs. Expression", " 6 - 7 < 2 + 1   "), //
        Utils.asArray("Literal vs. Literal", "1 < 102333"), //
        Utils.asArray("Actual example", "next < values().length"), //
        Utils.asArray("No boolean", "a?b:c"), //
        Utils.asArray("F X", "a ? false : c"), //
        Utils.asArray("T X", "a ? true : c"), //
        Utils.asArray("X F", "a ? b : false"), //
        Utils.asArray("X T", "a ? b : true"), //
        Utils.asArray("() F X", "a ?( false):true"), //
        Utils.asArray("() T X", "a ? (((true ))): c"), //
        Utils.asArray("() X F", "a ? b : (false)"), //
        Utils.asArray("() X T", "a ? b : ((true))"), //
        Utils.asArray("Actual example", "!inRange(m, e) ? true : inner.go(r, e)"), //
        Utils.asArray("Method invocation first", "a?b():c"), //
        Utils.asArray("Not same function invocation ", "a?b(x):d(x)"), //
        Utils.asArray("Not same function invocation ", "a?x.f(x):x.d(x)"), //
        new String[] { "identical method call", "a ? y.f(b) :y.f(b)" }, //
        new String[] { "identical function call", "a ? f(b) :f(b)" }, //
        new String[] { "identical assignment", "a ? (b=c) :(b=c)" }, //
        new String[] { "identical increment", "a ? b++ :b++" }, //
        new String[] { "identical addition", "a ? b+d :b+ d" }, //
        new String[] { "function call", "a ? f(b,c) : f(c)" }, //
        new String[] { "a method call", "a ? y.f(c,b) :y.f(c)" }, //
        new String[] { "a method call distinct receiver", "a ? x.f(c) : y.f(d)" }, //
        new String[] { "not on MINUS", "a ? -c :-d", }, //
        new String[] { "not on NOT", "a ? !c :!d", }, //
        new String[] { "not on MINUSMINUS 1", "a ? --c :--d", }, //
        new String[] { "not on MINUSMINUS 2", "a ? c-- :d--", }, //
        new String[] { "not on PLUSPLUS", "a ? x++ :y++", }, //
        new String[] { "not on PLUS", "a ? +x : +y", }, //
        null);
    /** Instantiates the enclosing class ({@link OutOfScope}) */
    public OutOfScope() {
      super(WRING);
    }
    /**
     * Generate test cases for this parameterized class.
     *
     * @return a collection of cases, where each case is an array of three
     *         objects, the test case name, the input, and the file.
     */
    @Parameters(name = DESCRIPTION) //
    public static Collection<Object[]> cases() {
      return collect(cases);
    }
  }

  @RunWith(Parameterized.class) //
  @FixMethodOrder(MethodSorters.NAME_ASCENDING) //
  public static class Wringed extends AbstractWringTest.WringedExpression.Conditional {
    private static String[][] cases = Utils.asArray(//
        new String[] { "almost identical function call", "a ? f(b) :f(c)", "f(a ? b : c)" }, //
        new String[] { "almost identical method call", "a ? y.f(b) :y.f(c)", "y.f(a ? b : c)" }, //
        new String[] { "almost identical two arguments function call 1/2", "a ? f(b,x) :f(c,x)", "f(a ? b : c,x)" }, //
        new String[] { "almost identical two arguments function call 2/2", "a ? f(x,b) :f(x,c)", "f(x,a ? b : c)" }, //
        new String[] { "almost identical assignment", "a ? (b=c) :(b=d)", "b = a ? c : d" }, //
        new String[] { "almost identical 2 addition", "a ? b+d :b+ c", "b+(a ? d : c)" }, //
        new String[] { "almost identical 3 addition", "a ? b+d +x:b+ c + x", "b+(a ? d : c) + x" }, //
        new String[] { "almost identical 4 addition last", "a ? b+d+e+y:b+d+e+x", "b+d+e+(a ? y : x)" }, //
        new String[] { "almost identical 4 addition second", "a ? b+x+e+f:b+y+e+f", "b+(a ? x : y)+e+f" }, //
        null);
    /**
     * Generate test cases for this parameterized class.
     *
     * @return a collection of cases, where each case is an array of three
     *         objects, the test case name, the input, and the file.
     */
    @Parameters(name = DESCRIPTION) //
    public static Collection<Object[]> cases() {
      return collect(cases);
    }
    /** Instantiates the enclosing class ({@link WringedExpression}) */
    public Wringed() {
      super(WRING);
    }
  }
}
