package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import com.google.common.base.Preconditions;

import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

public class CDTypeSymbol2JavaTypeFilter extends TransitiveAdaptedResolvingFilter<JavaTypeSymbol> {

  public CDTypeSymbol2JavaTypeFilter() {
    super(CDTypeSymbol.KIND, JavaTypeSymbol.class, JavaTypeSymbol.KIND);
  }

  @Override
  protected Symbol createAdapter(Symbol adaptee) {
    Preconditions.checkArgument(adaptee instanceof CDTypeSymbol);
    return new CDTypeSymbol2JavaType((CDTypeSymbol)adaptee);
  }
}
