/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import montiarc._symboltable.adapters.CDFieldSymbol2JavaFieldFilter;
import montiarc._symboltable.adapters.CDTypeSymbol2JavaTypeFilter;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/51

/**
 * The MontiArc Language
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class MontiArcLanguage extends MontiArcLanguageTOP {
  
  public static final String FILE_ENDING = "arc";
  
  public MontiArcLanguage() {
    super("MontiArc Language", FILE_ENDING);
  }
  
  @Override
  protected void initResolvingFilters() {
    super.initResolvingFilters();
    // is done in generated TOP-language addResolvingFilter(new
    // CommonResolvingFilter<ComponentSymbol>(ComponentSymbol.class,
    // ComponentSymbol.KIND));
    addResolvingFilter(
        new CommonResolvingFilter<ComponentInstanceSymbol>(ComponentInstanceSymbol.KIND));
    addResolvingFilter(new CommonResolvingFilter<PortSymbol>(PortSymbol.KIND));
    addResolvingFilter(new CommonResolvingFilter<ConnectorSymbol>(ConnectorSymbol.KIND));
    addResolvingFilter(new CommonResolvingFilter<VariableSymbol>(VariableSymbol.KIND));
    addResolvingFilter(new CommonResolvingFilter<AutomatonSymbol>(AutomatonSymbol.KIND));
    
    // Java/P
    addResolvingFilter(new CommonResolvingFilter<>(JavaBehaviorSymbol.KIND));
    
    addResolvingFilter(new CommonResolvingFilter<JTypeSymbol>(JTypeSymbol.KIND));
    addResolvingFilter(new CommonResolvingFilter<JFieldSymbol>(JFieldSymbol.KIND));
    addResolvingFilter(new CommonResolvingFilter<JMethodSymbol>(JMethodSymbol.KIND));
    
    // I/O Automaton
    addResolvingFilter(new CommonResolvingFilter<StateSymbol>(StateSymbol.KIND));
    addResolvingFilter(new TransitionResolvingFilter());
    addResolvingFilter(new Variable2FieldResolvingFilter());
    addResolvingFilter(new Port2FieldResolvingFilter());
    
    // TODO enable to resolve type arguments of subcomponents
    addResolvingFilter(new CDTypeSymbol2JavaTypeFilter());
    addResolvingFilter(new CDFieldSymbol2JavaFieldFilter());
    
    setModelNameCalculator(new MontiArcModelNameCalculator());
  }
  
  @Override
  protected MontiArcModelLoader provideModelLoader() {
    return new MontiArcModelLoader(this);
  }
  
}
