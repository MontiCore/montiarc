/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.List;

/**
 * Checks that the expression of a for-each loop is iterable and its elements
 * fit the loop variable for every combination of the symbols its names may
 * refer to in the configurations in which the loop is present.
 */
public class ForEachIsValid4Family extends ForEachIsValid4MA {

  protected final TypeCheck4Family typeCheck;

  public ForEachIsValid4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTEnhancedForControl node) {
    Preconditions.checkNotNull(node);
    this.typeCheck.check(node, node.getEnclosingScope(), List.of(node.getExpression()), () -> super.check(node));
  }
}
