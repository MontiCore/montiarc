/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class MCBasicTypesJavaPrinter extends MCBasicTypesPrettyPrinter {

  public MCBasicTypesJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTMCQualifiedName node) {
    if (!node.isQualified()) {
      Optional<TypeSymbol> type = ((IMontiArcScope) node.getEnclosingScope()).resolveType(node.getBaseName());
      if (type.isPresent() && MontiArcMill.typeDispatcher().isOOSymbolsOOType(type.get())) {
        getPrinter().print(type.get().getFullName() + " ");
        return;
      }
    }
    super.handle(node);
  }
}
