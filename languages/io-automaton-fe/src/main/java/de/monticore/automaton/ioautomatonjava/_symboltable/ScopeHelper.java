package de.monticore.automaton.ioautomatonjava._symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

public class ScopeHelper {
  
  public static Collection<Symbol> resolve(Scope scope, SymbolKind kind) {
    List<Symbol> allSymbols = new ArrayList<>();
    while (scope != null) {
      Collection<Symbol> symbols = scope.resolveLocally(kind);
      allSymbols.addAll(symbols);
      scope = scope.getEnclosingScope().orElse(null);
    }
    return allSymbols;
  }
  
}
