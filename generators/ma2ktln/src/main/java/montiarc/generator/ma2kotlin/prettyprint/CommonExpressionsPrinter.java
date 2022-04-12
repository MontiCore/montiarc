/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._visitor.MontiArcFullPrettyPrinter;

public class CommonExpressionsPrinter extends CommonExpressionsPrettyPrinter {

  final MontiArcFullPrettyPrinter standard = new MontiArcFullPrettyPrinter();
  final SymbolPrinter helper;

  public CommonExpressionsPrinter(IndentPrinter printer) {
    super(printer);
    helper = new SymbolPrinter(printer);
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    if (!helper.findAndConsumeSymbol(
        node.getEnclosingScope(),
        helper.clearComments(standard.prettyprint(node)),
        helper::printPortQuery,
        null)) {
      super.handle(node);
    }
  }

  @Override
  public void handle(ASTBooleanNotExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print(".inv()");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
