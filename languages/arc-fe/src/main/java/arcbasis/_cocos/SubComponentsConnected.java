/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

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
public class SubComponentsConnected implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    final ComponentTypeSymbol compSymbol = node.getSymbol();
    final Collection<String> targets = this.getTargetNames(node);
    final Collection<String> sources = this.getSourceNames(node);
    for (ComponentInstanceSymbol subSymbol : compSymbol.getSubComponents()) {
      // --------- INCOMING PORTS ----------
      Collection<String> subInputPorts =
        this.getNames(subSymbol.getType().getTypeInfo().getAllIncomingPorts());
      subInputPorts = subInputPorts.stream()
        .map(s -> subSymbol.getName() + "." + s)
        .collect(Collectors.toList());
      subInputPorts.removeAll(targets);
      for (String port : subInputPorts) {
        SourcePosition sourcePosition = this.getSourcePosition(compSymbol, node, port);
        if (sources.contains(port)) {
          Log.error(
            ArcError.INCOMING_PORT_AS_SOURCE.format(port,
              subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
        } else {
          Log.error(
            ArcError.INCOMING_PORT_NOT_CONNECTED.format(port,
              subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
        }
      }
      // --------- OUTGOING PORTS ----------
      Collection<String> subOutputPorts
        = this.getNames(subSymbol.getType().getTypeInfo().getAllOutgoingPorts());
      subOutputPorts = subOutputPorts.stream()
        .map(s -> subSymbol.getName() + "." + s)
        .collect(Collectors.toList());
      subOutputPorts.removeAll(sources);
      for (String port : subOutputPorts) {
        SourcePosition sourcePosition = this.getSourcePosition(compSymbol, node, port);
        if (targets.contains(port)) {
          Log.error(
            ArcError.OUTGOING_PORT_AS_TARGET.format(port,
              subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
        } else {
          Log.error(
            ArcError.OUTGOING_PORT_NOT_CONNECTED.format(port,
              subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
        }
      }
    }
  }

  protected Collection<String> getNames(Collection<PortSymbol> ports) {
    return ports.stream().map(PortSymbol::getName).collect(Collectors.toList());
  }

  protected Collection<String> getSourceNames(ASTComponentType node) {
    return node.getConnectors().stream().map(ASTConnector::getSourceName)
      .collect(Collectors.toList());
  }

  protected Collection<String> getTargetNames(ASTComponentType node) {
    return node.getConnectors().stream().map(ASTConnector::getTargetsNames)
      .flatMap(Collection::stream).collect(Collectors.toList());
  }

  protected SourcePosition getSourcePosition(ComponentTypeSymbol symbol,
    ASTComponentType node, String port) {
    return symbol.getPort(port.split("\\.")[1]).map(p -> p.getAstNode().get_SourcePositionStart())
      .orElse(node.get_SourcePositionEnd());
  }
}