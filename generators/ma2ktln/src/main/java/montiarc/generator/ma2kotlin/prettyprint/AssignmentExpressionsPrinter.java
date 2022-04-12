/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._visitor.MontiArcFullPrettyPrinter;

import java.util.HashMap;
import java.util.Map;

public class AssignmentExpressionsPrinter extends AssignmentExpressionsPrettyPrinter {

  /**
   * Contains bitwise operators.
   * They are different from java in kotlin.
   */
  protected final static Map<Integer, String> OPERATORS;

  static {
    OPERATORS = new HashMap<>();
    OPERATORS.put(ASTConstantsAssignmentExpressions.AND_EQUALS, "and");
    OPERATORS.put(ASTConstantsAssignmentExpressions.PIPEEQUALS, "or");
    OPERATORS.put(ASTConstantsAssignmentExpressions.ROOFEQUALS, "xor");
    OPERATORS.put(ASTConstantsAssignmentExpressions.GTGTEQUALS, "shr");
    OPERATORS.put(ASTConstantsAssignmentExpressions.GTGTGTEQUALS, "ushr");
    OPERATORS.put(ASTConstantsAssignmentExpressions.LTLTEQUALS, "shl");
  }

  final MontiArcFullPrettyPrinter standard = new MontiArcFullPrettyPrinter();
  final SymbolPrinter helper;

  public AssignmentExpressionsPrinter(IndentPrinter printer) {
    super(printer);
    helper = new SymbolPrinter(printer);
  }

  @Override
  public void handle(ASTAssignmentExpression node) {
    if (node.getOperator() == ASTConstantsAssignmentExpressions.EQUALS
        && helper.findAndConsumeSymbol(
        node.getEnclosingScope(),
        helper.clearComments(standard.prettyprint(node.getLeft())),
        helper::printPort,
        null)) {
      getPrinter().print(".pushMsg(Message(");
      node.getRight().accept(getTraverser());
      getPrinter().print("))");
    } else {
      handleNotPort(node);
    }
  }

  protected void handleNotPort(ASTAssignmentExpression node) {
    if (OPERATORS.containsKey(node.getOperator())) {
      node.getLeft().accept(getTraverser());
      getPrinter().print(" = ");
      node.getLeft().accept(getTraverser());
      getPrinter().print(" ");
      getPrinter().print(OPERATORS.get(node.getOperator()));
      getPrinter().print(" ");
      node.getRight().accept(getTraverser());
    } else {
      super.handle(node);
    }
  }
}