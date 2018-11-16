package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
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
 * @author ahaber, Robert Heim
 */
public class SubComponentsConnected implements MontiArcASTComponentCoCo {
  
  private Collection<String> getNames(Collection<PortSymbol> ports) {
    return ports.stream().map(p -> p.getName())
        .collect(Collectors.toList());
  }
  
  private Collection<String> getSourceNames(Collection<ConnectorSymbol> connectors) {
    return connectors.stream().map(c -> c.getSource()).collect(Collectors.toList());
  }
  
  private Collection<String> getTargetNames(Collection<ConnectorSymbol> connectors) {
    return connectors.stream().map(c -> c.getTarget()).collect(Collectors.toList());
  }
  
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
    ComponentSymbol entry = (ComponentSymbol) node.getSymbolOpt().get();
    // Implemented on the symTab as it takes auto-instantiation into account
    // which is not reflected
    // in the AST.
    for (ComponentInstanceSymbol sub : entry.getSubComponents()) {
      // ------- IN PORTS -------
      // in ports must be connected
      // outer.in->sub.in
      // outer.AnySub.out->sub.in
      // connectors with sub.in as target occur in outer.connectors or as
      // outer.AnySub.simpleconnectors
      if (sub.getComponentType().existsReferencedSymbol()) {
        Collection<String> remainingSubIn
            = getNames(sub.getComponentType().getAllIncomingPorts());
        // Connectors in the outer context always refer to the ports in a
        // relative-qualified way (e.g.
        // sub.portX) and hence we must prefix the remaining ones with sub's
        // name to compare sets of
        // relative-qualified names
        remainingSubIn = remainingSubIn.stream()
                             .map(s -> sub.getName() + "." + s)
                             .collect(Collectors.toList());
        
        Collection<String> outerConnectorTargets
            = getTargetNames(entry.getConnectors());
        remainingSubIn.removeAll(outerConnectorTargets);
        if (!remainingSubIn.isEmpty()) {
          remainingSubIn.forEach(p -> Log.error(
              String.format("0xMA059 Port %s of subcomponent %s is not used!",
                  p, sub.getFullName()),
              node.get_SourcePositionStart()));
        }
        // ------- OUT PORTS -------
        // sub.out->outer.out
        // sub.out->outer.AnySub.in
        // connectors with sub.out as source occur as outer.connectors or
        // outer.AnySub.simpleConnectors
        Collection<String> remainingSubOut
            = getNames(sub.getComponentType().getAllOutgoingPorts());
        // Connectors in the outer context always refer to the ports in a
        // relative-qualified way (e.g.
        // sub.portX) and hence we must prefix the remaining ones with sub's
        // name to compare sets of
        // relative-qualified names
        remainingSubOut = remainingSubOut.stream()
                              .map(s -> sub.getName() + "." + s)
                              .collect(Collectors.toList());
        
        Collection<String> outerConnectorSources
            = getSourceNames(entry.getConnectors());
        remainingSubOut.removeAll(outerConnectorSources);
        
        if (!remainingSubOut.isEmpty()) {
          // qualified sources of simple connectors
          remainingSubOut.forEach(p -> Log.error(
              String.format("0xMA060 Port %s of subcomponent %s is not used!", p,
                  sub.getFullName()),
              node.get_SourcePositionStart()));
        }
      }
      else {
        Log.error("0xMA004 Used Subcomponent "+ sub.getName()+ " does not exist!", sub.getAstNode().get().get_SourcePositionStart());
      }
    }
  }
}
