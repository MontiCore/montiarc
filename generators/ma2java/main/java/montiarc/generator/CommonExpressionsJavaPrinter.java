/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class CommonExpressionsJavaPrinter extends CommonExpressionsPrettyPrinter {

  public CommonExpressionsJavaPrinter(@NotNull IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(@NotNull ASTCallExpression node) {
    Preconditions.checkNotNull(node);
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    Optional<ISymbol> symbol = node.getDefiningSymbol();
    if (symbol.isPresent()
        && symbol.get() instanceof MethodSymbol
        && ((MethodSymbol) symbol.get()).isIsConstructor()) {
      this.getPrinter().print("new ");
    }
    node.getExpression().accept(getTraverser());
    node.getArguments().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
