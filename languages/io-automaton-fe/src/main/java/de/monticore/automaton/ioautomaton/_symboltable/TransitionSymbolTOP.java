/* generated from model null*/
/* generated by template symboltable.ScopeSpanningSymbol*/



package de.monticore.automaton.ioautomaton._symboltable;

import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

import java.util.Collection;

public class TransitionSymbolTOP extends TransitionSymbolEMPTY {

  public TransitionSymbolTOP(String name) {
    super(name);
  }

 /* generated by template symboltable.symbols.Attributes*/




 /* generated by template symboltable.symbols.GetterSetter*/







  public Collection<GuardSymbol> getGuard() {
    return sortSymbolsByPosition(getSpannedScope().resolveLocally(GuardSymbol.KIND));
  }



}
