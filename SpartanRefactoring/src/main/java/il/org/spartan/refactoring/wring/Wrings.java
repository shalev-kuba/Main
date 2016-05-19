package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.ExpressionComparator.nodesCount;
import static il.org.spartan.refactoring.utils.Funcs.asBlock;
import static il.org.spartan.refactoring.utils.Funcs.duplicate;
import static il.org.spartan.refactoring.utils.Funcs.elze;
import static il.org.spartan.refactoring.utils.Funcs.removeAll;
import static il.org.spartan.refactoring.utils.Funcs.then;
import static il.org.spartan.refactoring.utils.Restructure.duplicateInto;
import static il.org.spartan.utils.Utils.last;
import static org.eclipse.jdt.core.dom.ASTNode.BREAK_STATEMENT;
import static org.eclipse.jdt.core.dom.ASTNode.CONTINUE_STATEMENT;
import static org.eclipse.jdt.core.dom.ASTNode.RETURN_STATEMENT;
import static org.eclipse.jdt.core.dom.ASTNode.THROW_STATEMENT;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.text.edits.TextEditGroup;

import il.org.spartan.refactoring.utils.Collect;
import il.org.spartan.refactoring.utils.ExpressionComparator;
import il.org.spartan.refactoring.utils.Extract;
import il.org.spartan.refactoring.utils.Is;
import il.org.spartan.refactoring.utils.LiteralParser;
import il.org.spartan.refactoring.utils.Subject;
import il.org.spartan.refactoring.utils.expose;

/**
 * A number of utility functions common to all wrings.
 *
 * @author Yossi Gil
 * @since 2015-07-17
 */
public enum Wrings {
  ;
  static void rename(final SimpleName oldName, final SimpleName newName, final MethodDeclaration d, final ASTRewrite r,
      final TextEditGroup g) {
    new LocalInliner(oldName, r, g).byValue(newName)//
        .inlineInto(Collect.usesOf(oldName).in(d).toArray(new Expression[] {}));
  }
  static void addAllReplacing(final List<Statement> to, final List<Statement> from, final Statement substitute, final Statement by1,
      final List<Statement> by2) {
    for (final Statement s : from)
      if (s != substitute)
        duplicateInto(s, to);
      else {
        duplicateInto(by1, to);
        duplicateInto(by2, to);
      }
  }
  static IfStatement blockIfNeeded(final IfStatement s, final ASTRewrite r, final TextEditGroup g) {
    if (!Is.blockRequired(s))
      return s;
    final Block b = Subject.statement(s).toBlock();
    r.replace(s, b, g);
    return (IfStatement) b.statements().get(0);
  }
  static Expression eliminateLiteral(final InfixExpression e, final boolean b) {
    final List<Expression> operands = Extract.allOperands(e);
    removeAll(b, operands);
    switch (operands.size()) {
      case 0:
        return e.getAST().newBooleanLiteral(b);
      case 1:
        return duplicate(operands.get(0));
      default:
        return Subject.operands(operands).to(e.getOperator());
    }
  }
  static boolean endsWithSequencer(final Statement s) {
    return Is.sequencer(Extract.lastStatement(s));
  }
  static ListRewrite insertAfter(final Statement where, final List<Statement> what, final ASTRewrite r, final TextEditGroup g) {
    final ListRewrite $ = r.getListRewrite(where.getParent(), Block.STATEMENTS_PROPERTY);
    for (int i = what.size() - 1; i >= 0; --i)
      $.insertAfter(what.get(i), where, g);
    return $;
  }
  static ListRewrite insertBefore(final Statement where, final List<Statement> what, final ASTRewrite r, final TextEditGroup g) {
    final ListRewrite $ = r.getListRewrite(where.getParent(), Block.STATEMENTS_PROPERTY);
    for (final Statement s : what)
      $.insertBefore(s, where, g);
    return $;
  }
  static IfStatement invert(final IfStatement s) {
    return Subject.pair(elze(s), then(s)).toNot(s.getExpression());
  }
  static int length(final ASTNode... ns) {
    int $ = 0;
    for (final ASTNode n : ns)
      $ += n.toString().length();
    return $;
  }
  static int size(final ASTNode... ns) {
    int $ = 0;
    for (final ASTNode n : ns)
      $ += nodesCount(n);
    return $;
  }
  static IfStatement makeShorterIf(final IfStatement s) {
    final List<Statement> then = Extract.statements(then(s));
    final List<Statement> elze = Extract.statements(elze(s));
    final IfStatement inverse = invert(s);
    if (then.isEmpty())
      return inverse;
    final IfStatement main = duplicate(s);
    if (elze.isEmpty())
      return main;
    final int rankThen = Wrings.sequencerRank(last(then));
    final int rankElse = Wrings.sequencerRank(last(elze));
    return rankElse <= rankThen && (rankThen != rankElse || Wrings.thenIsShorter(s)) ? main : inverse;
  }
  static boolean mixedLiteralKind(final List<Expression> es) {
    if (es.size() <= 2)
      return false;
    int previousKind = -1;
    for (final Expression e : es)
      if (e instanceof NumberLiteral || e instanceof CharacterLiteral) {
        final int currentKind = new LiteralParser(e.toString()).type();
        assert currentKind >= 0;
        if (previousKind == -1)
          previousKind = currentKind;
        else if (previousKind != currentKind)
          return true;
      }
    return false;
  }
  static ASTRewrite replaceTwoStatements(final ASTRewrite r, final Statement what, final Statement by, final TextEditGroup g) {
    final Block parent = asBlock(what.getParent());
    final List<Statement> siblings = Extract.statements(parent);
    final int i = siblings.indexOf(what);
    siblings.remove(i);
    siblings.remove(i);
    siblings.add(i, by);
    final Block $ = parent.getAST().newBlock();
    duplicateInto(siblings, expose.statements($));
    r.replace(parent, $, g);
    return r;
  }
  static boolean shoudlInvert(final IfStatement s) {
    final int rankThen = sequencerRank(Extract.lastStatement(then(s)));
    final int rankElse = sequencerRank(Extract.lastStatement(elze(s)));
    return rankElse > rankThen || rankThen == rankElse && !Wrings.thenIsShorter(s);
  }
  static boolean thenIsShorter(final IfStatement s) {
    final Statement then = then(s);
    final Statement elze = elze(s);
    if (elze == null)
      return true;
    final int s1 = ExpressionComparator.lineCount(then);
    final int s2 = ExpressionComparator.lineCount(elze);
    if (s1 < s2)
      return true;
    if (s1 > s2)
      return false;
    assert s1 == s2;
    final int n2 = Extract.statements(elze).size();
    final int n1 = Extract.statements(then).size();
    if (n1 < n2)
      return true;
    if (n1 > n2)
      return false;
    assert n1 == n2;
    final IfStatement $ = invert(s);
    return positivePrefixLength($) >= positivePrefixLength(invert($));
  }
  private static int positivePrefixLength(final IfStatement $) {
    return Wrings.length($.getExpression(), then($));
  }
  private static int sequencerRank(final ASTNode n) {
    switch (n.getNodeType()) {
      default:
        return -1;
    }
  }
}
