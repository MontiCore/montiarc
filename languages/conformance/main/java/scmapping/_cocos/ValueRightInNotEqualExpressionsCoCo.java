/* (c) https://github.com/MontiCore/monticore */
package scmapping._cocos;


import static montiarc.conformance.util.AutomataUtils.print;
import static scmapping.util.MappingUtil.printPosition;

import arcbasis._ast.ASTComponentType;
import montiarc.conformance.util.AutomataUtils;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTNotEqualsExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import scmapping.util.MappingUtil;

public class ValueRightInNotEqualExpressionsCoCo
    implements CommonExpressionsASTNotEqualsExpressionCoCo {

  private final List<String> validLeftNames = new ArrayList<>();
  private final String errorMessage =
      "0xC2002" +
              " Invalid expression \"%s\" at position %s. The left side of EqualsExpression"
          + " can either be a state, input-port , output-port or global variable\n"
          + "Values must be at the right side";

  public ValueRightInNotEqualExpressionsCoCo(ASTComponentType refAut, ASTComponentType conAut) {
    AutomataUtils.getInPorts(refAut).forEach(p -> validLeftNames.add(p.getName()));
    AutomataUtils.getInPorts(conAut).forEach(p -> validLeftNames.add(p.getName()));
    AutomataUtils.getOutPorts(refAut).forEach(p -> validLeftNames.add(p.getName()));
    AutomataUtils.getOutPorts(conAut).forEach(p -> validLeftNames.add(p.getName()));

    AutomataUtils.getGlobalVariables(refAut).forEach(v -> validLeftNames.add(v.getName()));
    AutomataUtils.getGlobalVariables(conAut).forEach(v -> validLeftNames.add(v.getName()));

    validLeftNames.add(conAut.getName() + ".state");
    validLeftNames.add(refAut.getName() + ".state");
    validLeftNames.add("state");
  }

  @Override
  public void check(ASTNotEqualsExpression node) {
    if (node.getLeft() instanceof ASTNameExpression) {
      ASTNameExpression left = (ASTNameExpression) node.getLeft();
      if (!validLeftNames.contains((left.getName()))) {
        Log.error(String.format(errorMessage, left.getName(), printPosition(node)));
      }
    } else if (node.getLeft() instanceof ASTFieldAccessExpression) {
      if (!validLeftNames.contains(MappingUtil.print(node.getLeft()))) {
        Log.error(
            String.format(errorMessage, print(node.getRight()), printPosition(node.getLeft())));
      }
    } else {
      Log.error(String.format(errorMessage, print(node.getRight()), printPosition(node.getLeft())));
    }
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
