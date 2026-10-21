/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.List;

/**
 * Checks that the expression of an expression statement is valid for every
 * combination of the symbols its names may refer to in the configurations in
 * which the statement is present.
 */
public class ExpressionStatementIsValid4Family extends ExpressionStatementIsValid {

  protected final TypeCheck4Family typeCheck;

  public ExpressionStatementIsValid4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTExpressionStatement node) {
    Preconditions.checkNotNull(node);
    this.typeCheck.check(node, node.getEnclosingScope(), List.of(node.getExpression()), () -> super.check(node));
  }
}
