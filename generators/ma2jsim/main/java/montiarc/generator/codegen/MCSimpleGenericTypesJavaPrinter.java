/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.Optional;

public class MCSimpleGenericTypesJavaPrinter extends MCSimpleGenericTypesPrettyPrinter {

  public MCSimpleGenericTypesJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTMCBasicGenericType node) {
    if (node.getNameList().size() == 1) {
      Optional<TypeSymbol> type = ((IMontiArcScope) node.getEnclosingScope()).resolveType(node.getNameList().get(0));
      if (type.isPresent() && MontiArcMill.typeDispatcher().isOOSymbolsOOType(type.get())) {
        getPrinter().print(type.get().getFullName());

        // Append generics
        Iterator<ASTMCTypeArgument> iter_mCTypeArgument = node.getMCTypeArgumentList().iterator();
        this.getPrinter().print("<");
        if (iter_mCTypeArgument.hasNext()) {
          iter_mCTypeArgument.next().accept(this.getTraverser());

          while (iter_mCTypeArgument.hasNext()) {
            this.getPrinter().stripTrailing();
            this.getPrinter().print(",");
            iter_mCTypeArgument.next().accept(this.getTraverser());
          }
        }

        this.getPrinter().stripTrailing();
        this.getPrinter().print(">");
        return;
      }
    }
    super.handle(node);
  }
}
