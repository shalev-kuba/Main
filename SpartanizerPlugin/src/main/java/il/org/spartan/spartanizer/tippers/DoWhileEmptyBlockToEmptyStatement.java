package il.org.spartan.spartanizer.tippers;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.tipping.*;
import il.org.spartan.utils.Example;

import static il.org.spartan.utils.Example.*;

/** replaces a do statement followed by an empty block with a do statement
 * followed by a semicolon.
 * in development, see issue #1125
 * @author Niv Shalmon <tt>shalmon.niv@gmail.com</tt>
 * @since 2017-03-22 */
public class DoWhileEmptyBlockToEmptyStatement extends ReplaceCurrentNode<DoStatement> //
    implements TipperCategory.SyntacticBaggage {
  private static final long serialVersionUID = 2013380508545213297L;

  @Override public ASTNode replacement(final DoStatement ¢) {
    DoStatement $ = copy.of(¢);
    $.setBody($.getAST().newEmptyStatement());
    return $;
  }

  @Override protected boolean prerequisite(final DoStatement ¢) {
    final Statement $ = ¢.getBody();
    return iz.block($) && iz.emptyBlock(az.block($));
  }

  @Override public Example[] examples() {
    return new Example[] {
        convert("do{}while(x();y();z());")//
            .to("do;while(x();y();z());"), //
        ignores("do{f();g();}while(x();y();z());"), //
        ignores("do;while(x();y();z());")//
    };
  }

  @Override public String description(@SuppressWarnings("unused") final DoStatement __) {
    return "replaces a for statment followed by an empty block with a for statment followed by a semicolon";
  }
}
