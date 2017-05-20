/* Part of the "Spartan Blog"; mutate the rest / but leave this line as is */
package fluent.ly;

import static org.junit.Assert.*;

import static fluent.ly.azzert.*;

import java.util.*;

import org.junit.*;

import il.org.spartan.*;

/** A collection of <code><b>static</b></code> functions for converting from one
 * aggregate type to another.
 * @author Yossi Gil
 * @since Jul 8, 2014 */
public enum as {
  ;
  /** Converts a sequence of values into an array.
   * @param <T> some arbitrary type
   * @param $ some sequence of values of the type parameter
   * @return parameter, organized as an array with entries whose type is the
   *         type parameter */
  @SafeVarargs public static <T> T[] array(final T... $) {
    return $;
  }
  /** Convert an array of {@link Integer}s into an {@link Iterable}. For
   * example, to print the first Fibonacci numbers multiplied by the first prime
   * numbers, write:
   *
   * <pre>
   * for (Integer f: asIterable(1,1,2,3,5,8,13)
   *    for (Integer p: asIterable(2,3,5,7,11,13)
   *       System.out.println(f*p)
   * </pre>
   *
   * @param is what to iterate on (recall that a list of arguments of the same
   *        type is isomorphic to array parameters in Java
   * @return an {@link Iterable} over the array, which can then be used to to
   *         iterate over the parameter(s) */
  public static Iterable<Integer> asIterable(final Integer... is) {
    // Create an object of a new <em>anonymous</em> class that
    // <code><b>implements</b></code> {@link Iterable}
    return () -> new Iterator<Integer>() {
      int current;

      @Override public boolean hasNext() {
        return current < is.length;
      }
      @Override public Integer next() {
        return is[current++];
      }
    };
  }
  public static Iterable<Integer> asIterableLambda(final Integer... is) {
    return () -> new Iterator<Integer>() {
      int current;

      @Override public boolean hasNext() {
        return current < is.length;
      }
      @Override public Integer next() {
        return is[current++];
      }
    };
  }
  /** Converts a boolean into a bit value
   * @param $ some boolean value
   * @return 1 if the parameter is true, 0 otherwise */
  public static int bit(final boolean $) {
    return $ ? 1 : 0;
  }
  /** C like conversion of a reference to an {@link Object} into a 0/1 bit.
   * @param ¢ some object
   * @return <code>0</code> if the parameter is <code><b>null</b></code>.
   *         <code>1</code> otherwise.
   * @see as#bit(Object) */
  public static int bit(final Object ¢) {
    return ¢ == null ? 0 : 1;
  }
  /** Converts a sequence of integer values into an array.
   * @param $ some sequence of values of the type parameter
   * @return parameters, organized as an array with entries whose type is the
   *         type parameter */
  public static int[] intArray(final int... $) {
    return $;
  }
  /** Return a compact representation of a list of {@link Integer}s as an array
   * of type <code><b>int</b></code>.
   * @param is the list to be converted, none of the elements in it can be
   *        <code><b>null</b></code>
   * @return an array of <code><b>int</b></code>. representing the input. */
  public static int[] ints(final List<Integer> is) {
    final int[] $ = new int[is.size()];
    for (int ¢ = 0; ¢ < $.length; ++¢)
      $[¢] = is.get(¢).intValue();
    return $;
  }
  /** Creates an iterable for an array of objects
   * @param <T> an arbitrary type
   * @param ¢ what to iterate on
   * @return an {@link Iterable} over the parameter */
  @SafeVarargs public static <T> Iterator<T> iterator(final T... ¢) {
    return as.list(¢).iterator();
  }
  /** Converts an array of <code><b>int</b></code> values into a {@link List}
   * of non-<code><b>null</b></code> {@link Integer}s.
   * @param is what to covert
   * @return parameter, converted to the {@link List} of non-
   *         <code><b>int</b></code> {@link Integer}s form. */
  public static List<Integer> list(final int... is) {
    final List<Integer> $ = new ArrayList<>();
    for (final int ¢ : is)
      $.add(fluent.ly.box.it(¢));
    return $;
  }
  /** Converts an {@link Iterable} of a given type into a {@link List} of values
   * of this type.
   * @param <T> type of items to be converted
   * @param $ what to convert
   * @return parameter, converted to the {@link List} of the given type */
  public static <T> List<T> list(final Iterable<? extends T> $) {
    return accumulate.to(new ArrayList<T>()).add($).elements();
  }
  /** Converts a sequence of objects of some common type T into a {@link List}
   * of values
   * @param <T> type of objects to be converted
   * @param $ what to covert
   * @return result parameter, converted into a {@link List} */
  @SafeVarargs public static <T> List<T> list(final T... $) {
    return accumulate.to(new ArrayList<T>()).add($).elements();
  }
  /** Converts a sequence of objects of a given type into a {@link Set} of
   * values
   * @param <T> type of objects to be converted
   * @param ¢ what to covert
   * @return parameter, converted into a {@link Set} */
  @SafeVarargs public static <T> Set<? extends T> set(final T... ¢) {
    return accumulate.to(new HashSet<T>()).add(¢).elements();
  }
  /** Converts a value, which can be either a <code><b>null</b></code> or
   * references to valid instances, into a {@link NonNull}
   * @param $ some value
   * @return parameter, after bing to a non-null string. */
  public static String string(final Object $) {
    return $ == null ? "null" : as.string($ + "");
  }
  /** Converts a {@link String}, which can be either a <code><b>null</b></code>
   * or an actual String, into a {@link NonNull} String.
   * @param $ some value
   * @return parameter, after bing to a non-null string. */
  public static String string(final String $) {
    return $ != null ? $ : "null";
  }
  /** Converts an {@link Iterable} into an array of {@link String}.
   * @param os what to covert
   * @return an array of the parameter values, each converted to i
   *         {@link String} */
  public static String[] strings(final Iterable<?> os) {
    final List<String> $ = new ArrayList<>();
    for (final Object ¢ : os)
      if (¢ != null)
        $.add(¢ + "");
    return Utils.cantBeNull($.toArray(new String[$.size()]));
  }

  static Iterable<Integer> asIterableEssence(final Integer... is) {
    return () -> new Iterator<Integer>() {
      int current;

      @Override public boolean hasNext() {
        return current < is.length;
      }
      @Override public Integer next() {
        return is[current++];
      }
    };
  }

  // No values in an 'enum' which serves as a name space for a collection of
  // 'static' functions.
  /** A static nested class hosting unit tests for the nesting class Unit test
   * for the containing class. Note the naming convention: a) names of test
   * methods do not use are not prefixed by "test". This prefix is redundant. b)
   * test methods begin with the name of the method they check.
   * @author Yossi Gil
   * @since 2014-05-31 */
  @SuppressWarnings("static-method")
  public static class TEST {
    @Test public void asBitOfFalse() {
      azzert.that(as.bit(false), is(0));
    }
    @Test public void asBitOfTrue() {
      azzert.that(as.bit(true), is(1));
    }
    @Test public void asIntArraySimple() {
      final int[] is = as.intArray(100, 200, 200, 12, 13, 0);
      assertArrayEquals(is, as.ints(as.list(is)));
    }
    @Test public void asListSimple() {
      // direct call `as.list(12, 13, 14)` kills Travis --or
      final List<Integer> is = as.list(new int[] { 12, 13, 14 });
      azzert.that(is.get(0), is(fluent.ly.box.it(12)));
      azzert.that(is.get(1), is(fluent.ly.box.it(13)));
      azzert.that(is.get(2), is(fluent.ly.box.it(14)));
      azzert.that(is.size(), is(3));
    }
    @Test public void stringOfNull() {
      azzert.that(as.string(null), is("null"));
    }
    @Test public void stringWhenToStringReturnsNull() {
      azzert.that(as.string(new Object() {
        @Override public String toString() {
          return null;
        }
      }), is("null"));
    }
  }
}