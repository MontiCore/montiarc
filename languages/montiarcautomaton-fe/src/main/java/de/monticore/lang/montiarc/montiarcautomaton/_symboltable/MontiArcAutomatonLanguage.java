package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import java.util.Optional;

import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcModelNameCalculator;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.JAttributeSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;

public class MontiArcAutomatonLanguage extends MontiArcAutomatonLanguageTOP {
  public static final String FILE_ENDING = "maa";

  public MontiArcAutomatonLanguage() {
    super("MontiArcAutomaton Language", FILE_ENDING);
  }
  @Override
  protected void initResolvingFilters() {
    super.initResolvingFilters();

    addResolver(new BehaviorEmbeddingResolvingFilter());
    addResolver(new Port2VariableResolvingFilter());
    
    // following resolver are from montiarc:    
    addResolver(new CommonResolvingFilter<ComponentSymbol>(ComponentSymbol.KIND));
    addResolver(new CommonResolvingFilter<ComponentInstanceSymbol>(ComponentInstanceSymbol.KIND));
    addResolver(new CommonResolvingFilter<PortSymbol>(PortSymbol.KIND));
    addResolver(new CommonResolvingFilter<ConnectorSymbol>(ConnectorSymbol.KIND));
    
    addResolver(CommonResolvingFilter.create(JTypeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(JAttributeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(JMethodSymbol.KIND));
    
    setModelNameCalculator(new MontiArcModelNameCalculator());
  }

  @Override
  protected MontiArcAutomatonModelLoader provideModelLoader() {
    return new MontiArcAutomatonModelLoader(this);
  }
  
  @Override
  public Optional<MontiArcAutomatonSymbolTableCreator> getSymbolTableCreator(ResolverConfiguration resolverConfiguration, MutableScope enclosingScope) {
    return Optional.of(new MontiArcAutomatonSymbolTableCreator(resolverConfiguration, enclosingScope));
  }
  
}
