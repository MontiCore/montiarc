package montiarc._ast;

import de.monticore.java.javadsl._ast.ASTExpression;

public interface ASTGuardExpression extends ASTGuardExpressionTOP {
  public ASTExpression getExpression();
}
