package de.monticore.lang.montiarc.ajava._symboltable;

import java.util.Optional;

import de.monticore.lang.montiarc.ajava._parser.AJavaParser;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcModelNameCalculator;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;

public class AJavaLanguage extends AJavaLanguageTOP {
  public static final String FILE_ENDING = "maa";

  public AJavaLanguage() {
    super("AJava Language", FILE_ENDING);
  }
  
  
  @Override
  protected void initResolvingFilters() {
    super.initResolvingFilters();

    // resolver for montiarc automaton:
//    addResolver(new Port2VariableResolvingFilter());
    
    // following resolver are copied from montiarc:    
    addResolver(new CommonResolvingFilter<ComponentSymbol>(ComponentSymbol.KIND));
    addResolver(new CommonResolvingFilter<ComponentInstanceSymbol>(ComponentInstanceSymbol.KIND));
    addResolver(new CommonResolvingFilter<PortSymbol>(PortSymbol.KIND));
    addResolver(new CommonResolvingFilter<ConnectorSymbol>(ConnectorSymbol.KIND));
    addResolver(new CommonResolvingFilter<>(AJavaDefinitionSymbol.KIND));
    addResolver(new CommonResolvingFilter<>(SimpleVariableSymbol.KIND));
    
    addResolver(CommonResolvingFilter.create(JTypeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(JFieldSymbol.KIND));
    addResolver(CommonResolvingFilter.create(JMethodSymbol.KIND));
    
    setModelNameCalculator(new MontiArcModelNameCalculator());
  }

  @Override
  protected AJavaModelLoader provideModelLoader() {
    return new AJavaModelLoader(this);
  }
  
  @Override
  public Optional<AJavaSymbolTableCreator> getSymbolTableCreator(ResolvingConfiguration resolverConfiguration, MutableScope enclosingScope) {
    return Optional.of(new AJavaSymbolTableCreator(resolverConfiguration, enclosingScope));
  }
  
  /**
   * @see de.monticore.lang.montiarc.ajava._symboltable.AJavaLanguageTOP#getParser()
   */
  @Override
  public AJavaParser getParser() {
    return super.getParser();
  }
}
