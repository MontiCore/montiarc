/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class ExpressionsBasisPrinter extends ExpressionsBasisPrettyPrinter {
  protected final SymbolPrinter helper;

  public ExpressionsBasisPrinter(IndentPrinter printer) {
    super(printer);
    helper = new SymbolPrinter(printer);
  }

  @Override
  public void handle(ASTNameExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (!helper.findAndConsumeSymbol(node.getEnclosingScope(), node.getName(), helper::printPortQuery, helper::printVariable)) {
      super.handle(node);
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
