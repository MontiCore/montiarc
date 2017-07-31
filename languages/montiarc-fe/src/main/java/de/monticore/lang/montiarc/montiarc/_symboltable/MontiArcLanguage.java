/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.montiarc._symboltable;

import de.monticore.lang.montiarc.adapter.CDFieldSymbol2JavaFieldFilter;
import de.monticore.lang.montiarc.adapter.CDTypeSymbol2JavaTypeFilter;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;

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
    // is done in generated TOP-language addResolver(new
    // CommonResolvingFilter<ComponentSymbol>(ComponentSymbol.class,
    // ComponentSymbol.KIND));
    addResolver(new CommonResolvingFilter<ComponentInstanceSymbol>(ComponentInstanceSymbol.KIND));
    addResolver(new CommonResolvingFilter<PortSymbol>(PortSymbol.KIND));
    addResolver(new CommonResolvingFilter<ConnectorSymbol>(ConnectorSymbol.KIND));
    addResolver(new CommonResolvingFilter<ComponentVariableSymbol>(ComponentVariableSymbol.KIND));
    
    // Java/P
    addResolver(new CommonResolvingFilter<>(JavaBehaviorSymbol.KIND));
    addResolver(new CommonResolvingFilter<>(JavaVariableReferenceSymbol.KIND));
    
    addResolver(CommonResolvingFilter.create(JTypeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(JFieldSymbol.KIND));
    addResolver(CommonResolvingFilter.create(JMethodSymbol.KIND));
    
    //TODO enable to resolve type arguments of subcomponents
    addResolver(new CDTypeSymbol2JavaTypeFilter());
    addResolver(new CDFieldSymbol2JavaFieldFilter());

    setModelNameCalculator(new MontiArcModelNameCalculator());
  }
  
  @Override
  protected MontiArcModelLoader provideModelLoader() {
    return new MontiArcModelLoader(this);
  }
  
}
