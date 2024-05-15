/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._symboltable.Component2TypeSymbolAdapter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.types3.util.TypeContextCalculator;

import java.util.Optional;

public class ArcBasisTypeContextCalculator extends TypeContextCalculator {

  @Override
  public Optional<TypeSymbol> getEnclosingType(IScope enclosingScope) {
    Optional<TypeSymbol> enclosingType = Optional.empty();

    for (IScope scope = enclosingScope; scope != null && enclosingType.isEmpty(); scope = scope.getEnclosingScope()) {
      if (scope.isPresentSpanningSymbol() && this.getTypeDispatcher().isBasicSymbolsType(scope.getSpanningSymbol())) {
        // Default behavior: enclosing scope is type
        enclosingType = Optional.of(this.getTypeDispatcher().asBasicSymbolsType(scope.getSpanningSymbol()));
      } else if (scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof ComponentSymbol) {
        // Enclosing scope is ComponentType
        enclosingType = Optional.of(new Component2TypeSymbolAdapter((ComponentSymbol) scope.getSpanningSymbol()));
      }
    }

    return enclosingType;
  }
}
