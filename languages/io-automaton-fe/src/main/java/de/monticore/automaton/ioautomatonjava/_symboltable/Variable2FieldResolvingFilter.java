package de.monticore.automaton.ioautomatonjava._symboltable;

import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

public class Variable2FieldResolvingFilter extends TransitiveAdaptedResolvingFilter<JavaFieldSymbol> {

  public Variable2FieldResolvingFilter() {
    super(VariableSymbol.KIND, JavaFieldSymbol.class, JavaFieldSymbol.KIND);
  }

  @Override
  public Symbol translate(Symbol s) {    
    return new Variable2FieldAdapter((VariableSymbol) s);
  }
  
}
