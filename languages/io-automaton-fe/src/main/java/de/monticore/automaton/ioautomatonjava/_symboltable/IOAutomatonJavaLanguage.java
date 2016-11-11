package de.monticore.automaton.ioautomatonjava._symboltable;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonResolvingFilter;
import de.monticore.automaton.ioautomaton._symboltable.GuardResolvingFilter;
import de.monticore.automaton.ioautomaton._symboltable.IOAutomatonModelNameCalculator;
import de.monticore.automaton.ioautomaton._symboltable.InitialStateDeclarationResolvingFilter;
import de.monticore.automaton.ioautomaton._symboltable.StateResolvingFilter;
import de.monticore.automaton.ioautomaton._symboltable.TransitionResolvingFilter;
import de.monticore.automaton.ioautomaton._symboltable.VariableResolvingFilter;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

public class IOAutomatonJavaLanguage extends IOAutomatonJavaLanguageTOP {
  public static final String FILE_ENDING = "ioautomaton";
  
  public IOAutomatonJavaLanguage() {
    super("IOAutomaton Language", FILE_ENDING);
    setModelNameCalculator(new IOAutomatonModelNameCalculator());
  }
  
  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new IOAutomatonJavaModelLoader(this);
  }
  
  @Override
  public Optional<IOAutomatonJavaSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolverConfiguration, MutableScope enclosingScope) {
    return Optional.of(new IOAutomatonJavaSymbolTableCreator(resolverConfiguration, enclosingScope));
  }
  
  @Override
  protected void initResolvingFilters() {
    addResolver(new VariableResolvingFilter());
    addResolver(new AutomatonResolvingFilter());
    addResolver(new StateResolvingFilter());
    addResolver(new InitialStateDeclarationResolvingFilter());
    addResolver(new TransitionResolvingFilter());
    addResolver(new GuardResolvingFilter());
    
    addResolver(new Variable2FieldResolvingFilter());
  }
  
}
