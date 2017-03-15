/* TODO: Yossi Gil {@code Yossi.Gil@GMail.COM} please add a description
  @author Yossi Gil {@code Yossi.Gil@GMail.COM}
 * @since Oct 3, 2016 */
package il.org.spartan.spartanizer.annotations;

import java.util.*;
import java.util.Map.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.java.namespace.*;

public final class EnvNestedHandler extends ENVTestEngineAbstract {
  public EnvNestedHandler(final ASTNode $) {
    userProvidedSet = null;
    n = $;
    testSet = generateSet();
    runTest();
  }

  /** Manual testing mode, to test the test engine. Comparing annotations with
   * provided set, instead of a Set generated by Environment. Used to test the
   * test engine itself.
   * @param ¢ - Node that will be searched for suitable annotations.
   * @param es - Set to compare against. */
  public EnvNestedHandler(final ASTNode ¢, final LinkedHashSet<Entry<String, Binding>> es) {
    assert es != null : "The provided Set for manual testing is null!";
    userProvidedSet = es;
    n = ¢;
    runTest();
  }

  public EnvNestedHandler(final String ¢) {
    userProvidedSet = null;
    n = getCompilationUnit(¢);
    testSet = generateSet();
    runTest();
  }

  /** Manual testing mode, to test the test engine. Comparing annotations with
   * provided set, instead of a Set generated by Environment. Used to test the
   * test engine itself.
   * @param ¢
   * @param es */
  public EnvNestedHandler(final String ¢, final LinkedHashSet<Entry<String, Binding>> es) {
    assert es != null : "The provided Set for manual testing is null!";
    userProvidedSet = es;
    n = getCompilationUnit(¢);
    runTest();
  }

  @Override protected LinkedHashSet<Entry<String, Binding>> buildEnvironmentSet(@SuppressWarnings("unused") final BodyDeclaration __) {
    return null;
  }

  @Override protected void handler(final Annotation ¢) {
    handler(az.singleMemberAnnotation(¢));
  }

  /** Parse the outer annotation to get the inner ones. Add to the flat Set.
   * Compare uses() and declares() output to the flat Set.
   * @param whatThisGlobalStaticVariableDoing JD */
  private void handler(final SingleMemberAnnotation a) {
    if (a == null || !"OutOfOrderflatENV".equals(a.getTypeName() + ""))
      return;
    foundTestedAnnotation = true;
    a.accept(new ASTVisitor(true) {
      @SuppressWarnings("unchecked") List<MemberValuePair> values(final NormalAnnotation ¢) {
        return ¢.values();
      }

      @Override public boolean visit(final NormalAnnotation ¢) {
        if (isNameId(¢.getTypeName()))
          addTestSet(values(¢));
        return true;
      }
    });
  }
}
