/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.dse;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montiarc.generator.helper.ComponentHelper;
import org.codehaus.commons.nullanalysis.NotNull;

public class DseCommonExpressionsJavaPrinterValue extends CommonExpressionsPrettyPrinter {
  ComponentHelper componentHelper;

  public DseCommonExpressionsJavaPrinterValue(@NotNull IndentPrinter printer,
                                              boolean printComments) {
    super(printer, printComments);
    this.componentHelper = new ComponentHelper();
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print(componentHelper.printExpression(node.getExpression()));

    getPrinter().stripTrailing();
    getPrinter().print("." + node.getName() + " ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().stripTrailing();

    getPrinter().print("(");

    node.getLeft().accept(getTraverser());

    getPrinter().print(")");

    boolean isStringComparison = checkString(node.getLeft()) || checkString(node.getRight());

    if (isStringComparison) {
      getPrinter().print(".equals(");
    }
    else {
      getPrinter().print("==");
    }

    node.getRight().accept(getTraverser());

    if (isStringComparison) {
      getPrinter().print(")");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().stripTrailing();
    boolean isStringComparison = checkString(node.getLeft()) || checkString(node.getRight());

    if (isStringComparison) {
      getPrinter().print("!(");

      node.getLeft().accept(getTraverser());
      getPrinter().print(").equals(");

      node.getRight().accept(getTraverser());
      getPrinter().print(")");
    }
    else {
      node.getLeft().accept(getTraverser());
      getPrinter().print("!=");
      node.getRight().accept(getTraverser());
    }

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  private boolean checkString(ASTExpression node) {
    if (node instanceof ASTLiteralExpression) {
      ASTLiteralExpression literal = (ASTLiteralExpression) node;
      if (literal.getLiteral().getClass().toString()
        .equals("class de.monticore.literals.mccommonliterals._ast.ASTStringLiteral")) {
        return true;
      }
    }
    return false;
  }
}