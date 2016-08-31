package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import com.google.common.base.Preconditions;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;

public class CDFieldSymbol2JavaFieldFilter extends TransitiveAdaptedResolvingFilter<JavaFieldSymbol> {

  public CDFieldSymbol2JavaFieldFilter() {
    super(CDFieldSymbol.KIND, JavaFieldSymbol.class, JavaFieldSymbol.KIND);
  }

  @Override
  protected Symbol createAdapter(Symbol adaptee) {
    Preconditions.checkArgument(adaptee instanceof CDFieldSymbol);
    return new CDFieldSymbol2JavaField((CDFieldSymbol)adaptee);
  }
}
