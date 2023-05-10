/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.dse;

import de.monticore.expressions.assignmentexpressions._prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montiarc.generator.helper.ComponentHelper;
import montiarc.generator.helper.dse.ComponentHelperDseValue;
import org.codehaus.commons.nullanalysis.NotNull;

public class DseCommonAssignmentJavaPrinter extends AssignmentExpressionsPrettyPrinter {

  protected boolean printComments;

  ComponentHelper componentHelper;
  ComponentHelperDseValue componentHelperDseValue;

  public DseCommonAssignmentJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
    this.printComments = printComments;
    this.componentHelper = new ComponentHelper();
    this.componentHelperDseValue = new ComponentHelperDseValue();
  }

  @Override
  public void handle(de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.EQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PLUSEQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.MINUSEQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.STAREQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.SLASHEQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.AND_EQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PIPEEQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.ROOFEQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.GTGTEQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.GTGTGTEQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.LTLTEQUALS || node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PERCENTEQUALS) { // opt: 0 req: 2
      getPrinter().print(componentHelper.printExpression(node.getLeft()));

      if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.LTLTEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("<<=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PIPEEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("|=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PLUSEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("+=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.SLASHEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("/=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.MINUSEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("-=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.STAREQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("*=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.GTGTGTEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print(">>>=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.EQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("= montiarc.rte.dse.AnnotatedValue.newAnnoValue(");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.GTGTEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print(">>=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.AND_EQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("&=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.ROOFEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("^=");
      }
      else if (node.getOperator() == de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PERCENTEQUALS) {
        getPrinter().stripTrailing();
        getPrinter().print("%=");
      }
    }

    node.getRight().accept(getTraverser());

    getPrinter().print(",");

    getPrinter().print(componentHelperDseValue.printExpression(node.getRight()));

    getPrinter().println(");");

    getPrinter().print("montiarc.rte.log.Log.trace( \"");
    getPrinter().print(componentHelper.printExpression(node.getLeft()));
    getPrinter().print("\" + \" Expr: \" + ");
    node.getLeft().accept(getTraverser());
    getPrinter().print(" + \"Value: \" + ");
    getPrinter().print(componentHelperDseValue.printExpression(node.getLeft()));
    getPrinter().println(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public boolean isPrintComments() {
    return this.printComments;
  }
}
