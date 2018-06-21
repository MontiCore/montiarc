package montiarc.cocos;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTExpressionCoCo;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTGuardExpression;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTValuation;
import montiarc._ast.ASTValueList;
import montiarc._cocos.MontiArcASTGuardExpressionCoCo;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;

/**
 * Context condition for checking, if all expressions used are allowed
 * expressions.
 * 
 * @implements [Wor16] AC5: The automaton’s valuations and assignments use only
 * allowed Java/P modeling elements (Lst. 5.15, p. 101).
 * @author Gerrit Leonhardt
 */
public class UseOfForbiddenExpression implements MontiArcASTIOAssignmentCoCo, MontiArcASTGuardExpressionCoCo {
  
  @Override
  public void check(ASTIOAssignment astioAssignment) {
    boolean isError;

    if(astioAssignment.valueListIsPresent()){
      final ASTValueList astValueList = astioAssignment.getValueList().get();
      for (ASTValuation astValuation : astValueList.getAllValuations()) {
        ASTExpression node = astValuation.getExpression();

        isError = false;
        String errorMessage = "";
        if (node.instanceofTypeIsPresent()) {
          isError = true;
          errorMessage = "instanceOf";
        }
        else if (node.binaryAndOpIsPresent()) {
          isError = true;
          errorMessage = "binary AND";
        }
        else if (node.binaryOrOpIsPresent()) {
          isError = true;
          errorMessage = "binary OR";
        }
        else if(node.binaryXorOpIsPresent()) {
          isError = true;
          errorMessage = "binary XOR";
        }
        else if(node.booleanAndOpIsPresent() || node.booleanNotIsPresent() || node.booleanOrOpIsPresent()) {
          isError = true;
          errorMessage = "boolean AND/NOT/OR";
        }

        if (isError) {
          Log.error("0xMA023 Expression contains forbidden expression: " + errorMessage,
              node.get_SourcePositionStart());
        }
      }
    }
  }

  @Override
  public void check(ASTGuardExpression node) {
    final ASTExpression expression = node.getExpression();

    expression.accept(new JavaDSLVisitor() {
      @Override
      public void visit(ASTExpression innerExpression) {
        if(innerExpression.instanceofTypeIsPresent()){
          Log.error("0xMA023 Expression contains forbidden expression: " +
                        "instanceof",
              innerExpression.get_SourcePositionStart());
        }
      }
    });
  }
}
