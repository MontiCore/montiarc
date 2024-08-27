/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class CommonExpressionsJavaPrinter extends CommonExpressionsPrettyPrinter {

  IArcTypeCalculator tc;

  public CommonExpressionsJavaPrinter(@NotNull IArcTypeCalculator tc, @NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
    this.tc = Preconditions.checkNotNull(tc);
  }

  @Override
  public void handle(@NotNull ASTCallExpression node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    // Use the full information of the function call (the arguments)
    // to better calculate the type of the actual function inside.
    // This is why the TC is called on the CallExpression first.
    this.tc.typeOf(node);
    SymTypeExpression expr = this.tc.typeOf(node.getExpression());
    if (expr.isFunctionType()
      && expr instanceof SymTypeOfFunction
      && ((SymTypeOfFunction) expr).getSymbol() instanceof MethodSymbol
      && ((MethodSymbol) ((SymTypeOfFunction) expr).getSymbol()).isIsConstructor()) {
      Preconditions.checkState(node.getExpression() instanceof ASTFieldAccessExpression);
      this.getPrinter().print("new ");
      // We call constructors in the name space of the respective type (e.g., Object.Object()).
      // But Java uses new instead, so we have to strip this qualification.
      // See https://git.rwth-aachen.de/monticore/montiarc/core/-/merge_requests/677
      ((ASTFieldAccessExpression) node.getExpression()).getExpression().accept(getTraverser());
    } else {
      node.getExpression().accept(getTraverser());
    }
    node.getArguments().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(@NotNull ASTFieldAccessExpression node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    Optional<TypeSymbol> type = ((IMontiArcScope) node.getEnclosingScope()).resolveType(MontiArcMill.prettyPrint(node.getExpression(), false));
    if (type.isPresent() && MontiArcMill.typeDispatcher().isOOSymbolsOOType(type.get())) {
      getPrinter().print(type.get().getFullName());
    } else {
      node.getExpression().accept(getTraverser());
    }

    getPrinter().stripTrailing();
    getPrinter().print(".");
    getPrinter().print(node.getName() + " ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
