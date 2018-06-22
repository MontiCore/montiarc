package montiarc.cocos;

import de.monticore.assignmentexpressions._ast.ASTBinaryAndExpression;
import de.monticore.assignmentexpressions._ast.ASTBinaryXorExpression;
import de.monticore.mcexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.mcexpressions._ast.ASTExpression;

import de.monticore.mcexpressions._ast.ASTInstanceofExpression;
import de.monticore.mcexpressions._cocos.MCExpressionsASTExpressionCoCo;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
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

    if(astioAssignment.isPresentValueList()){
      final ASTValueList astValueList = astioAssignment.getValueList();
      for (ASTValuation astValuation : astValueList.getAllValuations()) {
        ASTExpression node = astValuation.getExpression();

        isError = false;
        String errorMessage = "";
        if (node instanceof ASTInstanceofExpression) {
          isError = true;
          errorMessage = "instanceOf";
        }
        else if (node instanceof ASTBinaryAndExpression) {
          isError = true;
          errorMessage = "binary AND";
        }
        else if (node instanceof ASTBinaryOrOpExpression) {
          isError = true;
          errorMessage = "binary OR";
        }
        else if(node instanceof ASTBinaryXorExpression) {
          isError = true;
          errorMessage = "binary XOR";
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

    expression.accept(new MCExpressionsVisitor()  {
      @Override
      public void visit(ASTInstanceofExpression innerExpression) {
          Log.error("0xMA023 Expression contains forbidden expression: " +
                        "instanceof",
              innerExpression.get_SourcePositionStart());
      }
    });
  }
}
