/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in CommonExpressions
 * It can be combined with other expressions by creating a DelegatorVisitor
 */
public class ArcDeriveSymTypeOfCommonExpressions extends DeriveSymTypeOfCommonExpressions {

  /**
   * Filters the provided list of functions to those that exists independently of any object.
   * Extends {@link DeriveSymTypeOfCommonExpressions#filterModifiersFunctions(List)} to also include constructors.
   *
   * @param functionSymbols the list to filter
   * @return a list of functions that exist independently of any object.
   */
  @Override
  protected List<FunctionSymbol> filterModifiersFunctions(List<FunctionSymbol> functionSymbols) {
    return functionSymbols.stream()
        .filter(m -> m instanceof MethodSymbol)
        .filter(m -> ((MethodSymbol) m).isIsStatic()
            || ((MethodSymbol) m).isIsConstructor())
        .collect(Collectors.toList());
  }
}
