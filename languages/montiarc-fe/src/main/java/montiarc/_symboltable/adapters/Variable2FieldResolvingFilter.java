/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;
import montiarc._symboltable.VariableSymbol;

public class Variable2FieldResolvingFilter
    extends TransitiveAdaptedResolvingFilter<JavaFieldSymbol> {
  
  public Variable2FieldResolvingFilter() {
    super(VariableSymbol.KIND, JavaFieldSymbol.class, JavaFieldSymbol.KIND);
  }
  
  @Override
  public Symbol translate(Symbol s) {
    return new Variable2FieldAdapter((VariableSymbol) s);
  }
  
}
