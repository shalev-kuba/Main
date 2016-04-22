package il.org.spartan.refactoring.utils;

import static il.org.spartan.refactoring.utils.Funcs.right;
import static il.org.spartan.refactoring.utils.Funcs.same;
import static il.org.spartan.utils.Utils.in;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

abstract class HidingDepth extends ScopeManager {
  private int depth = 0;
  private int hideDepth = Integer.MAX_VALUE;

  boolean hidden() {
    return depth >= hideDepth;
  }
  void hide() {
    hideDepth = depth;
  }
  @Override void pop() {
    if (--depth < hideDepth)
      hideDepth = Integer.MAX_VALUE;
  }
  @Override final boolean push() {
    ++depth;
    return !hidden();
  }
}

abstract class ScopeManager extends ASTVisitor {
  @Override public final void endVisit(@SuppressWarnings("unused") final AnnotationTypeDeclaration _) {
    pop();
  }
  @Override public final void endVisit(@SuppressWarnings("unused") final AnonymousClassDeclaration _) {
    pop();
  }
  @Override public final void endVisit(@SuppressWarnings("unused") final Block _) {
    pop();
  }
  @Override public final void endVisit(@SuppressWarnings("unused") final EnhancedForStatement _) {
    pop();
  }
  @Override public final void endVisit(@SuppressWarnings("unused") final ForStatement _) {
    pop();
  }
  @Override public final void endVisit(@SuppressWarnings("unused") final TypeDeclaration _) {
    pop();
  }
  abstract boolean go(final AbstractTypeDeclaration d);
  abstract boolean go(final AnonymousClassDeclaration d);
  abstract boolean go(final EnhancedForStatement s);
  abstract void pop();
  abstract boolean push();
  @Override public final boolean visit(final AnnotationTypeDeclaration d) {
    push();
    return go(d);
  }
  @Override public final boolean visit(final AnonymousClassDeclaration d) {
    push();
    return go(d);
  }
  @Override public final boolean visit(@SuppressWarnings("unused") final Block _) {
    return push();
  }
  @Override public final boolean visit(final EnhancedForStatement s) {
    push();
    return go(s);
  }
  @Override public final boolean visit(final EnumDeclaration d) {
    push();
    return go(d);
  }
  @Override public final boolean visit(@SuppressWarnings("unused") final ForStatement _) {
    return push();
  }
  @Override public final boolean visit(final TypeDeclaration d) {
    push();
    return go(d);
  }
}

class UsesCollector extends HidingDepth {
  @SuppressWarnings("unchecked") private static List<? extends ASTNode> declarations(final AbstractTypeDeclaration d) {
    return d.bodyDeclarations();
  }
  @SuppressWarnings("unchecked") private static List<? extends ASTNode> declarations(final AnonymousClassDeclaration d) {
    return d.bodyDeclarations();
  }

  private final List<SimpleName> result;
  private final SimpleName focus;
  UsesCollector(final List<SimpleName> result, final SimpleName focus) {
    this.result = result;
    this.focus = focus;
  }
  UsesCollector(final UsesCollector c) {
    this(c.result, c.focus);
  }
  @Override protected UsesCollector clone() {
    return new UsesCollector(result, focus);
  }
  void consider(final SimpleName candidate) {
    if (hit(candidate))
      result.add(candidate);
  }
  private boolean declaredBy(final SimpleName n) {
    if (n == focus) {
      result.add(n);
      return false;
    }
    if (!hit(n))
      return false;
    hide();
    return true;
  }
  private boolean declaredIn(final AbstractTypeDeclaration d) {
    d.accept(new ASTVisitor() {
      @Override public boolean visit(final FieldDeclaration d) {
        return !hidden() && !declaredIn(d);
      }
    });
    return hidden();
  }
  private boolean declaredIn(final AnonymousClassDeclaration d) {
    declaresField(d);
    return hidden();
  }
  boolean declaredIn(final FieldDeclaration d) {
    for (final Object o : d.fragments())
      if (declaredIn((VariableDeclarationFragment) o))
        return true;
    return false;
  }
  private boolean declaredIn(final MethodDeclaration d) {
    for (final Object o : d.parameters())
      if (declaredIn((SingleVariableDeclaration) o))
        return true;
    return false;
  }
  private boolean declaredIn(final SingleVariableDeclaration f) {
    return declaredBy(f.getName());
  }
  private boolean declaredIn(final VariableDeclarationFragment f) {
    return declaredBy(f.getName());
  }
  private void declaresField(final ASTNode n) {
    n.accept(new DeclaredInFields(n));
  }
  @Override boolean go(final AbstractTypeDeclaration d) {
    ingore(d.getName());
    return !declaredIn(d) && recurse(declarations(d));
  }
  boolean go(final AnnotationTypeDeclaration d) {
    ingore(d.getName());
    return !declaredIn(d) && recurse(declarations(d));
  }
  @Override boolean go(final AnonymousClassDeclaration d) {
    return !declaredIn(d) && recurse(declarations(d));
  }
  @Override boolean go(final EnhancedForStatement s) {
    final SimpleName name = s.getParameter().getName();
    if (name == focus || !declaredBy(name))
      return true;
    recurse(s.getExpression());
    return recurse(s.getBody());
  }
  private boolean hit(final SimpleName n) {
    return same(n, focus);
  }
  /**
   * This is where we ignore all occurrences of {@link SimpleName} which are not
   * variable names, e.g., class name, function name, field name, etc.
   *
   * @param _ JD
   */
  private void ingore(@SuppressWarnings("unused") final SimpleName _) {
    // We simply ignore the parameter
  }
  @Override public boolean preVisit2(final ASTNode n) {
    return !hidden() && !(n instanceof Type);
  }
  boolean recurse(final ASTNode n) {
    if (n != null && !hidden())
      n.accept(clone());
    return false;
  }
  private boolean recurse(final Iterable<? extends ASTNode> ns) {
    for (final ASTNode n : ns)
      recurse(n);
    return false;
  }
  @Override public boolean visit(final CastExpression e) {
    return recurse(right(e));
  }
  @Override public boolean visit(final FieldAccess n) {
    return recurse(n.getExpression());
  }
  @Override public boolean visit(final MethodDeclaration d) {
    return !declaredIn(d) && recurse(d.getBody());
  }
  @Override public boolean visit(final MethodInvocation i) {
    ingore(i.getName());
    recurse(i.getExpression());
    return recurse(expose.arguments(i));
  }
  @Override public boolean visit(final QualifiedName n) {
    return recurse(n.getQualifier());
  }
  @Override public boolean visit(final SimpleName n) {
    consider(n);
    return false;
  }
  @Override public boolean visit(final SuperMethodInvocation i) {
    ingore(i.getName());
    return recurse(expose.arguments(i));
  }
  @Override public boolean visit(final VariableDeclarationFragment f) {
    return !declaredIn(f) && recurse(f.getInitializer());
  }

  private final class DeclaredInFields extends ASTVisitor {
    private final ASTNode parent;

    DeclaredInFields(final ASTNode parent) {
      this.parent = parent;
    }
    @Override public boolean visit(final FieldDeclaration d) {
      return d.getParent() == parent && !hidden() && !declaredIn(d);
    }
  }
}

class UsesCollectorIgnoreDefinitions extends UsesCollector {
  UsesCollectorIgnoreDefinitions(final List<SimpleName> result, final SimpleName focus) {
    super(result, focus);
  }
  public UsesCollectorIgnoreDefinitions(final UsesCollector c) {
    super(c);
  }
  @Override protected UsesCollectorIgnoreDefinitions clone() {
    return new UsesCollectorIgnoreDefinitions(this);
  }
  @Override public boolean visit(final Assignment a) {
    return recurse(right(a));
  }
  @Override public boolean visit(@SuppressWarnings("unused") final PostfixExpression _) {
    return false;
  }
  @Override public boolean visit(final PrefixExpression it) {
    return !in(it.getOperator(), PrefixExpression.Operator.INCREMENT, PrefixExpression.Operator.DECREMENT);
  }
}