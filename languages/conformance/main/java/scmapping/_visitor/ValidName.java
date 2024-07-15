/* (c) https://github.com/MontiCore/monticore */
package scmapping._visitor;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.stream.Collectors;
import scmapping.util.MappingUtil;

public class ValidName implements ExpressionsBasisVisitor2, CommonExpressionsVisitor2 {
  private final List<String> validNames;

  public ValidName(List<String> validNames) {
    this.validNames = validNames;
  }

  @Override
  public void visit(ASTNameExpression node) {
    List<String> names =
        validNames.stream().filter(name -> !name.contains(".")).collect(Collectors.toList());
    if (!names.contains(node.getName())) {
      printError(node, node.getName(), names);
    }
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {

    List<String> names =
        validNames.stream().filter(name -> name.contains(".")).collect(Collectors.toList());
    if (!names.contains(MappingUtil.print(node))) {
      printError(node, MappingUtil.print(node), names);
    }
  }

  public void printError(ASTNode node, String name, List<String> alternative) {
    Log.error(
        String.format(
            "0xC2003 Invalid expression \"%s\" at position %s. The alternatives %s could be \n Note: all names relative to "
                + "the concrete Statecharts must be on the  left  side of  rules and names relative to the reference statecharts must be "
                + "on the right side\n",
            name, MappingUtil.printPosition(node), alternative));
  }
}
