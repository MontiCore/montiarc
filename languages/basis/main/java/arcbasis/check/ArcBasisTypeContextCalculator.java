/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentType2TypeSymbolAdapter;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.types3.util.TypeContextCalculator;

import java.util.Optional;

public class ArcBasisTypeContextCalculator extends TypeContextCalculator {

  public static void init() {
    setDelegate(new ArcBasisTypeContextCalculator());
  }

  @Override
  protected Optional<TypeSymbol> _getEnclosingType(IScope enclosingScope) {
    Optional<TypeSymbol> enclosingType = Optional.empty();

    for (IScope scope = enclosingScope; scope != null && enclosingType.isEmpty(); scope = scope.getEnclosingScope()) {
      //TODO: use TypeDispatcher as soon as it is fixed
      if (scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof TypeSymbol) {
        // Default behavior: enclosing scope is type
        enclosingType = Optional.of((TypeSymbol) scope.getSpanningSymbol());
      } else if (scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof ComponentTypeSymbol) {
        // Enclosing scope is ComponentType
        enclosingType = Optional.of(new ComponentType2TypeSymbolAdapter((ComponentTypeSymbol) scope.getSpanningSymbol()));
      }
    }

    return enclosingType;
  }
}
