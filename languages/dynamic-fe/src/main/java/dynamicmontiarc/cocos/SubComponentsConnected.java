/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTModeDeclaration;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._ast.ASTUseStatement;
import dynamicmontiarc.helper.DynamicMontiArcHelper;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._ast.ASTElement;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Uses implementation for [Hab16] CV6 if no mode automaton present.
 * Otherwise checks that in each mode all active components are completely connected
 *
 */
public class SubComponentsConnected implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    if(!DynamicMontiArcHelper.isDynamic(node)){
      new montiarc.cocos.SubComponentsConnected().check(node);
      return;
    }

    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                  "symbol. Did you forget to run the " +
                  "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol symbol = (ComponentSymbol) node.getSymbolOpt().get();

    // There is at least one mode automaton node in the AST
    final Optional<ASTElement> automatonOpt =
        node.getBody().getElementList()
            .stream()
            .filter(e -> e instanceof ASTModeAutomaton)
            .findFirst();

    final ASTModeAutomaton automaton = (ASTModeAutomaton) automatonOpt.get();
    final Collection<String> staticConnectorTargets =
        getTargetNames(node.getConnectors());
    final Collection<String> staticConnectorSources =
        getSourceNames(node.getConnectors());
    final Set<String> modeNames = automaton.getModeNames();

    // Check that the condition is fulfilled for all modes of the
    // embedding component
    for (String modeName : modeNames) {
      final Set<String> activeComponentNames =
          getActiveComponents(automaton, modeName);
      final Set<ComponentInstanceSymbol> activeComponents =
          getComponentInstanceSymbols(symbol, activeComponentNames);

      final List<ASTConnector> connectorsInMode =
          automaton.getConnectorsInMode(modeName);

      // Check that there is a connector for each port
      for (ComponentInstanceSymbol compInstance : activeComponents) {

        if(!compInstance.getComponentType().existsReferencedSymbol()) {
          Log.error(
              String.format(
                  "0xMA004 Used Subcomponent %s does not exist!",
                  compInstance.getName()),
              compInstance.getAstNode().get().get_SourcePositionStart());
          continue;
        }

        final ComponentSymbol component =
            compInstance.getComponentType().getReferencedSymbol();

        Collection<String> remainingSubInputPortNames
            = getNames(component.getAllIncomingPorts());
        // Connectors in the outer context always refer to the ports in a
        // relative-qualified way (e.g.
        // sub.portX) and hence we must prefix the remaining ones with sub's
        // name to compare sets of
        // relative-qualified names
        remainingSubInputPortNames = remainingSubInputPortNames.stream()
            .map(s -> compInstance.getName() + "." + s)
            .collect(Collectors.toList());

        remainingSubInputPortNames.removeAll(staticConnectorTargets);
        remainingSubInputPortNames.removeAll(getTargetNames(connectorsInMode));

        for (String subInputPortName : remainingSubInputPortNames) {
          SourcePosition sourcePosition =
              component.getPort(subInputPortName.split("\\.")[1])
              .map(Symbol::getSourcePosition)
              .orElse(node.get_SourcePositionStart());

          if(staticConnectorSources.contains(subInputPortName)){
            Log.error(
                String.format("0xMA104 The incoming port %s of subcomponent " +
                        "%s of component type %s is used as the " +
                        "source of a connector. Incoming ports of " +
                        "subcomponents may only be used as the " +
                        "target of a connector from an incoming " +
                        "port of the component defining the " +
                        "connector, or an output port of another " +
                        "subcomponent.",
                    subInputPortName,
                    compInstance.getName(),
                    component.getFullName()),
                sourcePosition);
          } else {
            Log.error(
                String.format(
                    "0xMA059 Incoming port %s of subcomponent %s is not " +
                        "used in mode %s!",
                    subInputPortName, component.getFullName(), modeName),
                sourcePosition);
          }
        }

        // ------- OUT PORTS -------
        // sub.out->outer.out
        // sub.out->outer.AnySub.in
        // connectors with sub.out as source occur as outer.connectors or
        // outer.AnySub.simpleConnectors
        Collection<String> remainingSubOutputPortNames
            = getNames(component.getAllOutgoingPorts());
        // Connectors in the outer context always refer to the ports in a
        // relative-qualified way (e.g.
        // sub.portX) and hence we must prefix the remaining ones with sub's
        // name to compare sets of
        // relative-qualified names
        remainingSubOutputPortNames = remainingSubOutputPortNames.stream()
            .map(s -> compInstance.getName() + "." + s)
            .collect(Collectors.toList());

        remainingSubOutputPortNames.removeAll(staticConnectorSources);
        remainingSubOutputPortNames.removeAll(getSourceNames(connectorsInMode));

        for (String subOutputPortName : remainingSubOutputPortNames) {
          SourcePosition sourcePosition
              = component.getPort(subOutputPortName.split("\\.")[1])
              .map(Symbol::getSourcePosition)
              .orElse(node.get_SourcePositionStart());
          if(staticConnectorTargets.contains(subOutputPortName)){
            Log.error(
                String.format("0xMA105 The outgoing port %s of subcomponent " +
                        "%s of component type %s is used as the " +
                        "target of a connector. Outgoing ports of " +
                        "subcomponents may only be used as the " +
                        "source of a connector to an outgoing " +
                        "port of the component defining the " +
                        "connector, or an input port of another " +
                        "subcomponent.",
                    subOutputPortName,
                    compInstance.getName(),
                    component.getFullName()),
                sourcePosition);
          } else {
            Log.error(
                String.format(
                    "0xMA060 Outgoing port %s of subcomponent %s is not " +
                        "used in mode %s!",
                    subOutputPortName, component.getFullName(), modeName),
                sourcePosition);
          }
        }
      }

    }

  }


  private Set<ComponentInstanceSymbol> getComponentInstanceSymbols(
      ComponentSymbol compSymbol, Collection<String> instanceNames){
    Set<ComponentInstanceSymbol> result = new HashSet<>();

    for (String name : instanceNames) {
      final Optional<ComponentInstanceSymbol> subComponent =
          compSymbol.getSubComponent(name);
      subComponent.ifPresent(result::add);
    }

    return result;
  }

  private Set<String> getActiveComponents(
      ASTModeAutomaton automaton,
      String modeName) {

    Set<String> result = new HashSet<>();
    final Set<ASTModeDeclaration> nodes =
        automaton.getModeDeclarationsByName(modeName);

    for (ASTModeDeclaration declNode : nodes) {
      for (ASTUseStatement useStatement : declNode.getUseStatementList()) {
        result.addAll(useStatement.getNameList());
      }
    }

    return result;

  }

  /**
   * Collect the names of the given ports
   * @param ports Collection of ports of which to determine the names
   * @return The names of the ports
   */
  private Collection<String> getNames(Collection<PortSymbol> ports) {
    return ports.stream()
        .map(CommonSymbol::getName)
        .collect(Collectors.toList());
  }

  /**
   * Collect the names of the connectors sources as Strings
   * @return The collected names
   */
  private Collection<String> getSourceNames(List<ASTConnector> connectors) {
    return connectors.stream()
        .map(e -> e.getSource().toString()).collect(
            Collectors.toList());
  }

  /**
   * Collect the names of the connectors targets
   * @return The collected target names
   */
  private Collection<String> getTargetNames(List<ASTConnector> connectors) {
    final List<String> staticConnectorTargets =
        connectors
            .stream()
            .map(ASTConnector::getTargetsList)
            .flatMap(Collection::stream)
            .map(ASTQualifiedName::toString)
            .collect(Collectors.toList());
    return staticConnectorTargets;
  }
}
