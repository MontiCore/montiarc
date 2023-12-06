/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class ExpressionsBasisJavaPrinter extends ExpressionsBasisPrettyPrinter {

  public ExpressionsBasisJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTNameExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    Optional<VariableSymbol> symbol = ((IMontiArcScope) node.getEnclosingScope()).resolveVariable(node.getName());
    Optional<TypeSymbol> type = ((IMontiArcScope) node.getEnclosingScope()).resolveType(node.getName());
    if (symbol.isPresent() && MontiArcMill.typeDispatcher().isField(symbol.get()) && ((FieldSymbol) symbol.get()).isIsStatic()) {
      getPrinter().print(symbol.get().getFullName());
    } else if (type.isPresent() && MontiArcMill.typeDispatcher().isOOType(type.get())) {
      getPrinter().print(type.get().getFullName());
    } else {
      getPrinter().print(node.getName() + " ");
    }


    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
