/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.dse;

import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class DseExpressionBasisPrettyPrinterValue extends ExpressionsBasisPrettyPrinter {

  protected boolean printComments;

  public DseExpressionBasisPrettyPrinterValue(@NotNull IndentPrinter printer,
                                              boolean printComments) {
    super(printer, printComments);
    this.printComments = printComments;
  }

  @Override
  public void handle(de.monticore.expressions.expressionsbasis._ast.ASTNameExpression node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print(node.getName() + ".getValue()");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}