package montiarc._ast;

import de.monticore.java.javadsl._ast.ASTExpression;
import montiarc._ast.ASTGuardExpressionTOP;

public interface ASTGuardExpression extends ASTGuardExpressionTOP {
  public ASTExpression getExpression();
}
