package il.org.spartan.refactoring.utils;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;

/**
 * A visitor for {@link Comment} nodes. Preserves the comment content (using the source code)
 * and its end location row in the source.
 * 
 * @author Ori Roth
 *
 */
public class CommentVisitor extends ASTVisitor {
  private CompilationUnit cu;   // compilation unit
  private String s;             // source code
  private String c;             // comment content
  private int er;               // comment end row
  public CommentVisitor(CompilationUnit compilationUnit, String source) {

    super();
    this.cu = compilationUnit;
    this.s = source;
  }
  public boolean visit(LineComment node) {

    int sp = node.getStartPosition();
    int ep = sp + node.getLength();
    c = new StringBuilder(s).substring(sp, ep).toString();
    er = cu.getLineNumber(node.getStartPosition() + node.getLength()) - 1;
  
    return true;
  }
  public boolean visit(BlockComment node) {

    int sp = node.getStartPosition();
    int ep = sp + node.getLength();
    c = new StringBuilder(s).substring(sp, ep).toString();
    er = cu.getLineNumber(node.getStartPosition() + node.getLength()) - 1;
  
    return true;
  }
  public boolean visit(Javadoc node) {
    int sp = node.getStartPosition();
    int ep = sp + node.getLength();
    c = new StringBuilder(s).substring(sp, ep).toString();
    er = cu.getLineNumber(node.getStartPosition() + node.getLength()) - 1;
  
    return true;
  }
  public void preVisit(ASTNode node) {
  }
  public String getContent() {
    return c;
  }
  public int getEndRow() {
    return er;
  }
}
