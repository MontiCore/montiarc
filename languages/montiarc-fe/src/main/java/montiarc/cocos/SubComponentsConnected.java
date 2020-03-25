/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * CV6: All ports of subcomponents should be used in at least one connector.<br>
 * DIFFERENCE to CV5: CV5 checks that in and out ports are connected
 * <em>within</em> the (non-atomic) component itself while CV6 checks that a
 * subcomponent is connected in its <em>outer context</em> (i.e. the outer
 * component).
 *
 * @implements [Hab16] CV6: All ports of subcomponents should be used in at
 * least one connector. (p.72 Lst. 3.53)
 * @implements [Hab16] R3: Full qualified subcomponent types exist in the named package. (p. 63,
 * Lst. 3.38)
 * @implements [Hab16] R4: Unqualified subcomponent types either exist in the current package or are
 * imported using an import statement. (p. 64, Lst. 3.39)
 */
public class SubComponentsConnected implements MontiArcASTComponentCoCo {

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
  private Collection<String> getSourceNames(ASTComponent node) {
    return node.getConnectors().stream()
        .map(e -> e.getSource().toString()).collect(
            Collectors.toList());
  }

  /**
   * Collect the names of the connectors targets
   * @return The collected target names
   */
  private Collection<String> getTargetNames(ASTComponent node) {
    return node.getConnectors().stream()
        .map(ASTConnector::getTargetsList)
        .flatMap(Collection::stream).map(
            ASTQualifiedName::toString).collect(Collectors.toList());
  }

  /*
  1. If a port is used in a connector, then there should be no error
  2. If a port is used in the wrong way in a connector, then there should be an
    error, possibly
  3. If the port is unused there should be an error
   */
  
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbolOpt().get();
    // Implemented on the symTab as it takes auto-instantiation into account
    // which is not reflected
    // in the AST.

    Collection<String> outerConnectorTargets
        = getTargetNames(node);
    Collection<String> outerConnectorSources
        = getSourceNames(node);

    for (ComponentInstanceSymbol sub : componentSymbol.getSubComponents()) {
      // ------- IN PORTS -------
      // in ports must be connected
      // outer.in->sub.in
      // outer.AnySub.out->sub.in
      // connectors with sub.in as target occur in outer.connectors or as
      // outer.AnySub.simpleconnectors
      if (sub.getComponentType().existsReferencedSymbol()) {
        Collection<String> remainingSubInputPortNames
            = getNames(sub.getComponentType().getAllIncomingPorts());
        // Connectors in the outer context always refer to the ports in a
        // relative-qualified way (e.g.
        // sub.portX) and hence we must prefix the remaining ones with sub's
        // name to compare sets of
        // relative-qualified names
        remainingSubInputPortNames = remainingSubInputPortNames.stream()
                             .map(s -> sub.getName() + "." + s)
                             .collect(Collectors.toList());

        remainingSubInputPortNames.removeAll(outerConnectorTargets);

        for (String subInputPortName : remainingSubInputPortNames) {
          SourcePosition sourcePosition
              = sub.getComponentType().getPort(subInputPortName.split("\\.")[1])
                    .map(Symbol::getSourcePosition)
                    .orElse(node.get_SourcePositionStart());
          if(outerConnectorSources.contains(subInputPortName)){
            Log.error(
                String.format("0xMA104 The incoming port %s of subcomponent " +
                                  "%s of component type %s is used as the " +
                                  "source of a connector. Incoming ports of " +
                                  "subcomponents may only be used as the " +
                                  "target of a connector from an incoming " +
                                  "port of the component defining the " +
                                  "connector, or an output port of another " +
                                  "subcomponent.", 
                    subInputPortName, sub.getName(), 
                    sub.getComponentType().getFullName()), 
                sourcePosition);
          } else {
            Log.error(
                String.format("0xMA059 Incoming port %s of subcomponent %s is not used!",
                    subInputPortName, sub.getFullName()),
                sourcePosition);
          }
        }

        // ------- OUT PORTS -------
        // sub.out->outer.out
        // sub.out->outer.AnySub.in
        // connectors with sub.out as source occur as outer.connectors or
        // outer.AnySub.simpleConnectors
        Collection<String> remainingSubOutputPortNames
            = getNames(sub.getComponentType().getAllOutgoingPorts());
        // Connectors in the outer context always refer to the ports in a
        // relative-qualified way (e.g.
        // sub.portX) and hence we must prefix the remaining ones with sub's
        // name to compare sets of
        // relative-qualified names
        remainingSubOutputPortNames = remainingSubOutputPortNames.stream()
                              .map(s -> sub.getName() + "." + s)
                              .collect(Collectors.toList());

        remainingSubOutputPortNames.removeAll(outerConnectorSources);

        for (String subOutputPortName : remainingSubOutputPortNames) {
          SourcePosition sourcePosition
              = sub.getComponentType().getPort(subOutputPortName.split("\\.")[1])
                    .map(Symbol::getSourcePosition)
                    .orElse(node.get_SourcePositionStart());
          if(outerConnectorTargets.contains(subOutputPortName)){
            Log.error(
                String.format("0xMA105 The outgoing port %s of subcomponent " +
                                  "%s of component type %s is used as the " +
                                  "target of a connector. Outgoing ports of " +
                                  "subcomponents may only be used as the " +
                                  "source of a connector to an outgoing " +
                                  "port of the component defining the " +
                                  "connector, or an input port of another " +
                                  "subcomponent.",
                    subOutputPortName, sub.getName(),
                    sub.getComponentType().getFullName()),
                sourcePosition);
          } else {
            Log.error(
                String.format("0xMA060 Outgoing port %s of subcomponent %s is not used!",
                    subOutputPortName, sub.getFullName()),
                sourcePosition);
          }
        }
      }
      else {
        Log.error(String.format("0xMA004 Used Subcomponent %s does not exist!",
            sub.getName()), sub.getAstNode().get().get_SourcePositionStart());
      }
    }
  }
}
