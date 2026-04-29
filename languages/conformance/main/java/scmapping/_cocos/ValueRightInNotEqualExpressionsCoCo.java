/* (c) https://github.com/MontiCore/monticore */
package scmapping._cocos;


import arcbasis._ast.ASTArcComponentType;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTNotEqualsExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.conformance.util.AutomataUtils;
import scmapping.util.ConformanceError;
import scmapping.util.MappingUtil;

import java.util.ArrayList;
import java.util.List;

import static montiarc.conformance.util.AutomataUtils.print;
import static scmapping.util.MappingUtil.printPosition;

public class ValueRightInNotEqualExpressionsCoCo
    implements CommonExpressionsASTNotEqualsExpressionCoCo {

  private final List<String> validLeftNames = new ArrayList<>();

  public ValueRightInNotEqualExpressionsCoCo(ASTArcComponentType refAut, ASTArcComponentType conAut) {
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
        Log.error(ConformanceError.VALUE_RIGHT_IN_NOT_EQUAL_EXPRESSIONS.format(left.getName(), printPosition(node)));
      }
    } else if (node.getLeft() instanceof ASTFieldAccessExpression) {
      if (!validLeftNames.contains(MappingUtil.print(node.getLeft()))) {
        Log.error(
            ConformanceError.VALUE_RIGHT_IN_NOT_EQUAL_EXPRESSIONS.format(print(node.getRight()), printPosition(node.getLeft())));
      }
    } else {
      Log.error(ConformanceError.VALUE_RIGHT_IN_NOT_EQUAL_EXPRESSIONS.format(print(node.getRight()), printPosition(node.getLeft())));
    }
  }
}
