/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.symboltable.ISymbol;
import modes._ast.ASTArcMode;
import modes._ast.ASTModeAutomaton;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ModeHelper {

  public List<ASTSCTransition> filterTransitionsForSourceMode(List<ASTSCTransition> transitions, String srcMode) {
    return transitions.stream()
      .filter(tr -> tr.getSource().getName().equals(srcMode))
      .collect(Collectors.toList());
  }

  public List<ASTSCTransition> getTransitionsForTickEventFromState(ASTModeAutomaton modeAutomaton, ASTArcMode srcMode) {
    Preconditions.checkArgument(srcMode.isPresentSymbol());
    return getTransitionsWithoutEvent(modeAutomaton).stream()
      .filter(tr -> tr.getSource().getNameSymbol().equals(srcMode.getSymbol()))
      .collect(Collectors.toList());
  }

  public List<ASTSCTransition> getTransitionsWithoutEvent(ASTModeAutomaton modeAutomaton) {
    BehaviorHelper behaviorHelper = new BehaviorHelper();
    return behaviorHelper.filterTransitionsWithoutTrigger(getTransitions(modeAutomaton).stream());
  }

  public List<PortSymbol> getInPortsNotTriggeringAnyTransition(ASTModeAutomaton sc, ASTArcComponentType comp) {
    List<String> triggeringPorts = getTransitionsForPortEvents(comp, sc).keySet().stream()
      .map(ISymbol::getName)
      .map(String::toLowerCase).collect(Collectors.toList());
    return comp.getSymbol().getAllIncomingPorts().stream()
      .filter(p -> !triggeringPorts.contains(p.getName().toLowerCase()))
      .collect(Collectors.toList());
  }

  protected Timing determineTiming(ASTModeAutomaton modeAutomaton) {
    if (!modeAutomaton.isPresentStereotype()) return Timing.DEFAULT;
    return modeAutomaton.getStereotype().streamValues()
      .map(v -> Timing.of(v.getName()))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst().orElse(Timing.DEFAULT);
  }

  public List<ASTSCTransition> getTransitions(ASTModeAutomaton ast) {
    return ast.getSCStatechartElementList().stream()
      .filter(e -> e instanceof ASTSCTransition)
      .map(e -> (ASTSCTransition) e)
      .collect(Collectors.toList());
  }

  public int getTransitionIndex(ASTSCTransition transition, ASTModeAutomaton automaton) {
    return getTransitions(automaton).indexOf(transition);
  }

  public List<ASTArcMode> getModes(ASTModeAutomaton ast) {
    return ast.getSCStatechartElementList().stream()
      .filter(e -> e instanceof ASTArcMode)
      .map(e -> (ASTArcMode) e)
      .collect(Collectors.toList());
  }

  public List<ASTArcMode> getInitialModes(ASTModeAutomaton ast) {
    return getModes(ast).stream().filter(m -> m.getSCModifier().isInitial()).collect(Collectors.toList());
  }

  public Map<PortSymbol, List<ASTSCTransition>> getTransitionsForPortEvents(ASTArcComponentType enclosingComponent, ASTModeAutomaton modeAutomaton) {
    BehaviorHelper behaviorHelper = new BehaviorHelper();
    return behaviorHelper.getTransitionsMappedToPortTriggers(enclosingComponent, getTransitions(modeAutomaton).stream());
  }

  public List<ASTComponentInstance> getInstancesFromMode(ASTArcMode ast) {
    return ast.getBody().getElementsOfType(ASTComponentInstantiation.class).stream()
      .flatMap(ASTComponentInstantiationTOP::streamComponentInstances)
      .collect(Collectors.toList());
  }

  public List<SubcomponentSymbol> getInstanceSymbolsFromMode(ASTArcMode ast) {
    return getInstancesFromMode(ast).stream()
      .map(ASTComponentInstance::getSymbol)
      .collect(Collectors.toList());
  }

  public Map<ASTArcMode, List<ASTComponentInstance>> getInstancesFromModes(ASTModeAutomaton ast) {
    return getModes(ast).stream().collect(
      Collectors.toMap(
        Function.identity(),
        this::getInstancesFromMode
      )
    );
  }

  /**
   * Check if the component instance referenced in the given port access is defined within the given mode.
   */
  public boolean instanceInMode(ASTPortAccess portAccess, ASTArcMode mode) {
    if (!portAccess.isPresentComponentSymbol()) return false;
    return portAccess.getComponentSymbol().getEnclosingScope() == mode.getSpannedScope();
  }

  public List<ASTConnector> getConnectors(ASTArcMode ast) {
    return ast.getBody().getElementsOfType(ASTConnector.class);
  }
}
