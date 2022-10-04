/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
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
        && ((MethodSymbol) symbol.get()).isIsConstructor()
    ) {
      Preconditions.checkState(node.getExpression() instanceof ASTFieldAccessExpression);
      this.getPrinter().print("new ");
      // At the moment we need to call the Name of the class to instantiate as a qualification before the constructor
      // call in MontiArc models. But Java does not do this, so we have to strip this qualification.
      // See https://git.rwth-aachen.de/monticore/montiarc/core/-/merge_requests/677
      ((ASTFieldAccessExpression) node.getExpression()).getExpression().accept(getTraverser());
    } else {
      node.getExpression().accept(getTraverser());
    }
    node.getArguments().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
