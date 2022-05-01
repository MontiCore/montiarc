/* (c) https://github.com/MontiCore/monticore */
package montiarc._visitor;

import com.google.common.base.Strings;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import static java.lang.Byte.MAX_VALUE;

public class CustomCommonExpressionsPrettyPrinter extends CommonExpressionsPrettyPrinter {
  
  public CustomCommonExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }
  
  @Override
  public void handle(ASTCallExpression node) {
    if(Strings.isNullOrEmpty(node.getName())) {
      super.handle(node);
    } else {
      // if a name is set in a callExpression, we assume a typeCheck transformation messed with it.
      // it then originated from a formerly contained fieldAccessExpression.
      CommentPrettyPrinter.printPreComments(node, getPrinter());
      node.getExpression().accept(getTraverser());
      getPrinter().print("." + node.getName());
      node.getArguments().accept(getTraverser());
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}