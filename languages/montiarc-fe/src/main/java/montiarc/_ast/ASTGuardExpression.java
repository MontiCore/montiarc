/* (c) https://github.com/MontiCore/monticore */
package montiarc._ast;

import de.monticore.mcexpressions._ast.ASTExpression;

public interface ASTGuardExpression extends ASTGuardExpressionTOP {
  public ASTExpression getExpression();
}
