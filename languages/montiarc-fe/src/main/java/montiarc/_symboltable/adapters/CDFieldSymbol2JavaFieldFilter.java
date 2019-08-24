/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import com.google.common.base.Preconditions;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;

//TODO required for resolving types of an expression that uses cd stuff. See
//de.monticore.automaton.ioautomaton.TypeCompatibilityChecker for further
//information.
public class CDFieldSymbol2JavaFieldFilter
    extends TransitiveAdaptedResolvingFilter<JavaFieldSymbol> {
  
  public CDFieldSymbol2JavaFieldFilter() {
    super(CDFieldSymbol.KIND, JavaFieldSymbol.class, JavaFieldSymbol.KIND);
  }
  
  @Override
  public Symbol translate(Symbol adaptee) {
    Preconditions.checkArgument(adaptee instanceof CDFieldSymbol);
    return new CDFieldSymbol2JavaField((CDFieldSymbol) adaptee);
  }
}
