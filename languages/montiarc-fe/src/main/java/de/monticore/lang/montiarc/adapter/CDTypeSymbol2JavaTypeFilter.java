package de.monticore.lang.montiarc.adapter;

import com.google.common.base.Preconditions;

import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

//TODO required for resolving types of an expression that uses cd stuff. See
//de.monticore.automaton.ioautomaton.TypeCompatibilityChecker for further
//information.
public class CDTypeSymbol2JavaTypeFilter extends TransitiveAdaptedResolvingFilter<JavaTypeSymbol> {

  public CDTypeSymbol2JavaTypeFilter() {
    super(CDTypeSymbol.KIND, JavaTypeSymbol.class, JavaTypeSymbol.KIND);
  }

  @Override
  public Symbol translate(Symbol adaptee) {
    Preconditions.checkArgument(adaptee instanceof CDTypeSymbol);
    return new CDTypeSymbol2JavaType((CDTypeSymbol)adaptee);
  }
}
