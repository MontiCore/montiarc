package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

public class Port2VariableResolvingFilter extends TransitiveAdaptedResolvingFilter<VariableSymbol> {

  public Port2VariableResolvingFilter() {
    super(PortSymbol.KIND, VariableSymbol.class, VariableSymbol.KIND);
  }

  @Override
  public Symbol translate(Symbol s) {    
    return new Port2VariableAdapter((PortSymbol) s);
  }
  
}
