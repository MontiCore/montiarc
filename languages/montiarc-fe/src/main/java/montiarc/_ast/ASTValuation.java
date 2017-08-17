package montiarc._ast;

import de.monticore.java.javadsl._ast.ASTExpression;
import montiarc._ast.ASTValuationTOP;

public interface ASTValuation extends ASTValuationTOP {
  public ASTExpression getExpression();
}
