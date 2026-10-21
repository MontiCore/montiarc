/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.List;

/**
 * Checks that the precondition of a transition is boolean for every combination
 * of the symbols its names may refer to in the configurations in which the
 * transition is present.
 */
public class TransitionPreconditionsAreBoolean4Family extends TransitionPreconditionsAreBoolean {

  protected final TypeCheck4Family typeCheck;

  public TransitionPreconditionsAreBoolean4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTTransitionBody node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentPre()) {
      super.check(node);
      return;
    }
    this.typeCheck.check(node, node.getEnclosingScope(), List.of(node.getPre()), () -> super.check(node));
  }
}
