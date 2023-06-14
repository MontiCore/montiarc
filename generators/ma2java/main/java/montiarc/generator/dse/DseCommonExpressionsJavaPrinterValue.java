/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.dse;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
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
}