/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.assignmentexpressions._prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Improved AssignmentExpressions Pretty Printer that adds spaces around assignment operators.
 */
public class MAAssignmentExpressionsPrettyPrinter extends AssignmentExpressionsPrettyPrinter {

  public MAAssignmentExpressionsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTAssignmentExpression expr) {
    expr.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();
    
    int op = expr.getOperator();
    if (op == ASTConstantsAssignmentExpressions.EQUALS) {
      getPrinter().print(" = ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.PLUSEQUALS) {
      getPrinter().print(" += ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.MINUSEQUALS) {
      getPrinter().print(" -= ");
    }
    else if (op == ASTConstantsAssignmentExpressions.STAREQUALS) {
      getPrinter().print(" *= ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.SLASHEQUALS) {
      getPrinter().print(" /= ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.PERCENTEQUALS) {
      getPrinter().print(" %= ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.AND_EQUALS) {
      getPrinter().print(" &= ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.PIPEEQUALS) {
      getPrinter().print(" |= ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.ROOFEQUALS) {
      getPrinter().print(" ^= ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.LTLTEQUALS) {
      getPrinter().print(" <<= ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.GTGTEQUALS) {
      getPrinter().print(" >>= ");
    } 
    else if (op == ASTConstantsAssignmentExpressions.GTGTGTEQUALS) {
      getPrinter().print(" >>>= ");
    }
    else {
      getPrinter().print(" = ");
    }
    
    expr.getRight().accept(getTraverser());
  }
}
