package de.monticore.automaton.ioautomatonjava._symboltable;

import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.CommonAdaptedResolvingFilter;

public class Variable2FieldResolvingFilter extends CommonAdaptedResolvingFilter<JavaFieldSymbol> {

  public Variable2FieldResolvingFilter() {
    super(VariableSymbol.KIND, JavaFieldSymbol.class, JavaFieldSymbol.KIND);
  }

  @Override
  protected Symbol createAdapter(Symbol s) {    
    return new Variable2FieldAdapter((VariableSymbol) s);
  }
  
}
