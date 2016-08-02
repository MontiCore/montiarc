package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import java.util.Optional;

import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguage;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguageTOP;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;

public class MontiArcAutomatonLanguage extends MontiArcAutomatonLanguageTOP {
  public static final String FILE_ENDING = "arc";

  public MontiArcAutomatonLanguage() {
    super("MontiArcAutomaton Language", FILE_ENDING);
    setModelNameCalculator(new MontiArcAutomatonModelNameCalculator());
  }

  @Override
  protected MontiArcAutomatonModelLoader provideModelLoader() {
    return new MontiArcAutomatonModelLoader(this);
  }
  
  @Override
  public Optional<MontiArcAutomatonSymbolTableCreator> getSymbolTableCreator(ResolverConfiguration resolverConfiguration, MutableScope enclosingScope) {
    return Optional.of(new MontiArcAutomatonSymbolTableCreator(resolverConfiguration, enclosingScope));
  }

  @Override
  protected void initResolvingFilters() {
    super.initResolvingFilters();
    addResolver(new BehaviorEmbeddingResolvingFilter());
  }
  
}
