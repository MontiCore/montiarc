package montiarc.cocos;

import java.util.Collection;
import java.util.stream.Collectors;

import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;
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
  
  private Collection<String> getNamesOfPorts(Collection<PortSymbol> ports) {
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
    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbolOpt().get();

    /*
    1. If the port is outgoing, then it should only occur as the target of a connector
      defined in this component.
      It should only be connected to from an output port of a subcomponent
    2. If the port is incoming it should only be used as the source of a connector
      defined in this component.
      It should only connect to a subcomponents input port.
     */


    // %%%%%%%%%%%%%%%% CV5 %%%%%%%%%%%%%%%%
    if (componentSymbol.isDecomposed()) {

      Collection<String> connectorSources = getSourceNames(componentSymbol.getConnectors());
      Collection<String> connectorTargets = getTargetNames(componentSymbol.getConnectors());

      // --------- IN PORTS ----------
      // check that in-ports are used within the component
      // in->out or in->sub.in (both only occur as normal connectors where the in ports must be the
      // source)

      Collection<String> remainingInPortNames
          = getNamesOfPorts(componentSymbol.getAllIncomingPorts());

//      if (componentSymbol.isInnerComponent()) {
//        // ports not connected by the inner component itself might be connected from the parent
//        // component or any of the parent's subcomponents' simple connectors
//        ComponentSymbol componentUsingSubComp
//            = (ComponentSymbol) componentSymbol.getEnclosingScope()
//            .getSpanningSymbol().get();
//        connectorSources.addAll(getSourceNames(componentUsingSubComp.getConnectors()));
//      }

      remainingInPortNames.removeAll(connectorSources);
      for (String inPortName : remainingInPortNames) {
        final SourcePosition sourcePosition
            = componentSymbol.getPort(inPortName).map(Symbol::getSourcePosition)
                  .orElse(node.get_SourcePositionStart());

        if(connectorTargets.contains(inPortName)){
          Log.error(
              String.format("0xMA098 Incoming port %s of component %s is used as the " +
                                "target of a connector, defined by the same " +
                                "component. Incoming ports may only be used " +
                                "as the source of a connector to a " +
                                "subcomponents incoming port.",
                  inPortName, componentSymbol.getName()), sourcePosition);
        } else {
          Log.warn(String.format("0xMA057 Port %s is not used!", inPortName), sourcePosition);
        }
      }

      // --------- OUT PORTS ----------
      // check that out-ports are connected (i.e. they are targets of connectors)
      // they might be connected using normal connectors (in->out or sub.out->out)
      // or using simple connectors (sub.out->out) (note that simple connectors only allow the
      // subcomponents outgoing ports as source)

      Collection<String> remainingOutPortNames
          = getNamesOfPorts(componentSymbol.getAllOutgoingPorts());

      remainingOutPortNames.removeAll(connectorTargets);
      for (String outPortName : remainingOutPortNames) {
        final SourcePosition sourcePosition
            = componentSymbol.getPort(outPortName).map(Symbol::getSourcePosition)
                  .orElse(node.get_SourcePositionStart());

        if(connectorSources.contains(outPortName)){
          Log.error(
              String.format("0xMA097 Outgoing port %s of component %s is used as the " +
                                "source of a connector, defined by the same " +
                                "component. Outgoing ports may only be used " +
                                "as the target of a connector from a " +
                                "subcomponents outgoing port",
                  outPortName, componentSymbol.getName()), sourcePosition);
        } else {
          Log.warn(String.format("0xMA058 Port %s is not used!", outPortName),
              sourcePosition);
        }
      }
    }
  }
}
