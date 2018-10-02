package montiarc.cocos;

import de.monticore.symboltable.types.references.TypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.VariableSymbol;

import java.util.Collection;

public class GenericInitValues implements MontiArcASTComponentCoCo {

  @Override public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                  "symbol. Did you forget to run the " +
                  "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol symbol = (ComponentSymbol) node.getSymbolOpt().get();
    for (VariableSymbol variableSymbol : symbol.getVariables()) {
      if (variableSymbol.getValuation().isPresent() &&
          variableSymbol.getTypeReference().getReferencedSymbol() != null &&
          variableSymbol.getTypeReference().getReferencedSymbol().isFormalTypeParameter()) {
        Log.error(String.format(
            "0xMA047 The variable %s is pure genetic and must not have an initial value assigned",
            variableSymbol.getName()),
            variableSymbol.getValuation().get().get_SourcePositionStart());
      }
    }
  }
}
