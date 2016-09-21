package il.org.spartan.spartanizer.wrings;

import static il.org.spartan.lisp.*;

import org.eclipse.jdt.core.dom.*;

import static il.org.spartan.spartanizer.ast.step.*;

import il.org.spartan.spartanizer.assemble.*;
import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.wringing.*;

/** Replaces, e.g., <code>Integer x=new Integer(2);</code> with
 * <code>Integer x=Integer.valueOf(2);</code>, more generally new of of any
 * boxed primitive types/{@link String} with recommended factory method
 * <code>valueOf()</code>
 * @author Ori Roth <code><ori.rothh [at] gmail.com></code>
 * @since 2016-04-06 */
public final class ClassInstanceCreationValueTypes extends ReplaceCurrentNode<ClassInstanceCreation> implements Kind.SyntacticBaggage {
  @Override public String description(final ClassInstanceCreation ¢) {
    return "Use factory method " + ¢.getType() + ".valueOf() instead of new ";
  }

  @Override public ASTNode replacement(final ClassInstanceCreation c) {
    if (arguments(c).size() != 1)
      return null;
    final Type t = c.getType();
    if (!wizard.isValueType(t))
      return null;
    SimpleName n = hop.simpleName(t);
    final MethodInvocation $ = subject.operand(duplicate.of(n)).toMethod("valueOf");
    arguments($).add(duplicate.of(first(arguments(c))));
    return $;
  }
}