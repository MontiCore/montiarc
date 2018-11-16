package montiarc.cocos;

import java.util.Collection;
import java.util.stream.Collectors;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;

/**
 * CV5: In decomposed components all ports should be used in at least one connector.<br>
 * DIFFERENCE to CV6: CV5 checks that in and out ports are connected <em>within</em> the
 * (non-atomic) component itself while CV6 checks that a subcomponent is connected in its <em>outer
 * context</em> (i.e. the outer component).
 *
 * @implements [Hab16] CV5: In decomposed components, all ports should be used in at least one
 * connector. (p.71 Lst. 3.52)
 * @author ahaber, Robert Heim
 */
public class PortUsage implements MontiArcASTComponentCoCo {
  
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
    
    // %%%%%%%%%%%%%%%% CV5 %%%%%%%%%%%%%%%%
    if (entry.isDecomposed()) {
      // --------- IN PORTS ----------
      // check that in-ports are used within the component
      // in->out or in->sub.in (both only occur as normal connectors where the in ports must be the
      // source)
      
      Collection<String> remainingPorts = getNames(entry.getAllIncomingPorts());
      
      Collection<String> connectorSources = getSourceNames(entry.getConnectors());
      
      if (entry.isInnerComponent()) {
        // ports not connected by the inner component itself might be connected from the parent
        // component or any of the parent's subcomponents' simple connectors
        ComponentSymbol componentUsingSubComp = (ComponentSymbol) entry.getEnclosingScope()
            .getSpanningSymbol().get();
        connectorSources.addAll(getSourceNames(componentUsingSubComp.getConnectors()));
      }
      
      remainingPorts.removeAll(connectorSources);
      if (!remainingPorts.isEmpty()) {
        remainingPorts.forEach(p -> Log.warn(String.format("0xMA057 Port %s is not used!", p)));
      }
      
      // --------- OUT PORTS ----------
      // check that out-ports are connected (i.e. they are targets of connectors)
      // they might be connected using normal connectors (in->out or sub.out->out)
      // or using simple connectors (sub.out->out) (note that simple connectors only allow the
      // subcomponents outgoing ports as source)
      
      remainingPorts = getNames(entry.getAllOutgoingPorts());
      Collection<String> connectorTargets = getTargetNames(entry.getConnectors());
      
      remainingPorts.removeAll(connectorTargets);
      if (!remainingPorts.isEmpty()) {
        remainingPorts.forEach(p -> Log.warn(String.format("0xMA058 Port %s is not used!", p),
            node.get_SourcePositionStart()));
      }
    }
  }
}
