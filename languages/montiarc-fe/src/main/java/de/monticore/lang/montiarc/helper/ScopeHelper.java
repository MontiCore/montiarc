package de.monticore.lang.montiarc.helper;

import java.util.HashSet;
import java.util.Set;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Scopes;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/44

// TODO remove this class. Currently required because of missing functionality
// in the Scope class

public class ScopeHelper {
  
  /**
   * Resolves all Symbols of the given kind within the given scope tree.
   * 
   * @param scope the scope tree
   * @param kind
   * @return
   */
  public static <T extends Symbol> Set<T> resolveManyDown(Scope scope, SymbolKind kind) {
    Set<T> set = new HashSet<>();
    resolveManyDown(scope, kind, set);
    return set;
  }
  
  private static void resolveManyDown(Scope scope, SymbolKind kind, Set<? extends Symbol> set) {
    set.addAll(scope.resolveLocally(kind));
    for (Scope s : scope.getSubScopes()) {
      resolveManyDown(s, kind, set);
    }
  }
  
  /**
   * Resolves all Symbols of the given kind within the global scope.
   * 
   * @param scope part of the global scope
   * @param kind
   * @return
   */
  public static <T extends Symbol> Set<T> resolveMany(Scope scope, SymbolKind kind) {
    Set<T> set = new HashSet<>();
    resolveManyDown(Scopes.getGlobalScope(scope).get(), kind, set);
    return set;
  }
  
}
