/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;

public class MAExpressionsBasisPrettyPrinter extends ExpressionsBasisPrettyPrinter {

  public MAExpressionsBasisPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTArguments node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().stripTrailing();
    getPrinter().print("(");

    Iterator<ASTExpression> iter = node.getExpressionList().iterator();
    if (iter.hasNext()) {
      iter.next().accept(getTraverser());
      while (iter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(", ");
        iter.next().accept(getTraverser());
      }
    }

    getPrinter().stripTrailing();
    getPrinter().print(")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
