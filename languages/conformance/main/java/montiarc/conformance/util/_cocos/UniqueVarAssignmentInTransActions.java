/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util._cocos;

import montiarc.conformance.util.AutomataUtils;
import scmapping.util.MappingUtil;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._cocos.SCBasisASTSCTransitionCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import montiarc._ast.ASTMACompilationUnit;

public class UniqueVarAssignmentInTransActions implements SCBasisASTSCTransitionCoCo {
  private final List<String> varNames = new ArrayList<>();
  private final String errorMessage =
      "SCC001 Invalid expression \"%s\" at position %s. Multiple value assigment to a variable(%s) is not allowed";

  public UniqueVarAssignmentInTransActions(ASTMACompilationUnit aut) {
    aut.getComponentType().getPorts().forEach(p -> varNames.add(p.getName()));
    aut.getComponentType().getFields().forEach(f -> varNames.add(f.getName()));
  }

  @Override
  public void check(ASTSCTransition node) {
    List<String> varNames = new ArrayList<>(this.varNames);
    for (ASTAssignmentExpression expr : AutomataUtils.getActionList(node)) {
      ASTNameExpression left = (ASTNameExpression) expr.getLeft();
      if (!varNames.remove(left.getName())) {
        Log.error(
            String.format(
                errorMessage,
                AutomataUtils.print(expr),
                MappingUtil.printPosition(expr),
                left.getName()));
      }
    }
  }
}
