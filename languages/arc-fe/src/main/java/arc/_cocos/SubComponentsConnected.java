/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._ast.ASTConnector;
import arc._symboltable.ComponentInstanceSymbol;
import arc._symboltable.ComponentSymbol;
import arc._symboltable.PortSymbol;
import arc.util.ArcError;
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
public class SubComponentsConnected implements ArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    final ComponentSymbol compSymbol = node.getSymbol();
    final Collection<String> targets = this.getTargetNames(node);
    final Collection<String> sources = this.getSourceNames(node);
    for (ComponentInstanceSymbol subSymbol : compSymbol.getSubComponents()) {
      if (subSymbol.getType().loadSymbol().isPresent()) {
        // --------- INCOMING PORTS ----------
        Collection<String> subInputPorts =
          this.getNames(subSymbol.getTypeInfo().getAllIncomingPorts());
        subInputPorts = subInputPorts.stream()
          .map(s -> subSymbol.getName() + "." + s)
          .collect(Collectors.toList());
        subInputPorts.removeAll(targets);
        for (String port : subInputPorts) {
          SourcePosition sourcePosition = this.getSourcePosition(compSymbol, node, port);
          if (sources.contains(port)) {
            Log.error(
              String.format(ArcError.INCOMING_PORT_AS_SOURCE.toString(), port,
                subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
          } else {
            Log.error(
              String.format(ArcError.INCOMING_PORT_NOT_CONNECTED.toString(), port,
                subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
          }
        }
        // --------- INCOMING PORTS ----------
        Collection<String> subOutputPorts
          = this.getNames(subSymbol.getTypeInfo().getAllOutgoingPorts());
        subOutputPorts = subOutputPorts.stream()
          .map(s -> subSymbol.getName() + "." + s)
          .collect(Collectors.toList());
        subOutputPorts.removeAll(sources);
        for (String port : subOutputPorts) {
          SourcePosition sourcePosition = this.getSourcePosition(compSymbol, node, port);
          if (targets.contains(port)) {
            Log.error(
              String.format(ArcError.OUTGOING_PORT_AS_TARGET.toString(), port,
                subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
          } else {
            Log.error(
              String.format(ArcError.OUTGOING_PORT_NOT_CONNECTED.toString(), port,
                subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
          }
        }
      } else {
        Log.error(String.format(ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE.toString(),
          subSymbol.getType().getName(), subSymbol.getFullName()),
          subSymbol.getAstNode().get_SourcePositionStart());
      }
    }
  }

  protected Collection<String> getNames(Collection<PortSymbol> ports) {
    return ports.stream().map(PortSymbol::getName).collect(Collectors.toList());
  }

  protected Collection<String> getSourceNames(ASTComponent node) {
    return node.getConnectors().stream().map(ASTConnector::getSourceName)
      .collect(Collectors.toList());
  }

  protected Collection<String> getTargetNames(ASTComponent node) {
    return node.getConnectors().stream().map(ASTConnector::getTargetsNames)
      .flatMap(Collection::stream).collect(Collectors.toList());
  }

  protected SourcePosition getSourcePosition(ComponentSymbol symbol,
    ASTComponent node, String port) {
    return symbol.getPort(port.split("\\.")[1]).map(p -> p.getAstNode().get_SourcePositionStart())
      .orElse(node.get_SourcePositionEnd());
  }
}