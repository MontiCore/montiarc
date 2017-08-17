package montiarc._symboltable;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

public class Port2FieldResolvingFilter extends TransitiveAdaptedResolvingFilter<JavaFieldSymbol> {
  
  public Port2FieldResolvingFilter() {
    super(PortSymbol.KIND, JavaFieldSymbol.class, JavaFieldSymbol.KIND);
  }
  
  @Override
  public Symbol translate(Symbol s) {
    return new Port2FieldAdapter((PortSymbol) s);
  }
  
}
