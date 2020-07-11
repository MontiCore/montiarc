/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * CV5: In decomposed components all ports should be used in at least one connector.<br>
 * DIFFERENCE to CV6: CV5 checks that in and out ports are connected <em>within</em> the
 * (non-atomic) component itself while CV6 checks that a subcomponent is connected in its <em>outer
 * context</em> (i.e. the outer component).
 *
 * @implements [Hab16] CV5: In decomposed components, all ports should be used in at least one
 * connector. (p.71 Lst. 3.52)
 */
public class PortUsage implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponentType node '%s' has no symbol. "
        + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol symbol = node.getSymbol();

    // CoCo does not apply to atomic components
    if (symbol.getSubComponents().isEmpty()) {
      return;
    }

    Collection<String> sources = node.getConnectors().stream()
        .map(ASTConnector::getSourceName)
        .collect(Collectors.toList());
    Collection<String> targets = node.getConnectors().stream()
        .map(ASTConnector::getTargetsNames)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    // --------- INCOMING PORTS ----------
    Collection<String> incomingPorts = this.getNamesOfPorts(symbol.getAllIncomingPorts());
    incomingPorts.removeAll(sources);
    for (String port : incomingPorts) {
      final SourcePosition sourcePosition = this.getSourcePosition(symbol, node, port);
      if (targets.contains(port)) {
        Log.error(
            String.format(ArcError.INCOMING_PORT_AS_TARGET.toString(), port, symbol.getFullName()),
            sourcePosition);
      }
      else {
        Log.warn(
            String.format(ArcError.INCOMING_PORT_NO_FORWARD.toString(), port, symbol.getFullName()),
            sourcePosition);
      }
    }
    // --------- OUTGOING PORTS ----------
    Collection<String> outgoingPorts = this.getNamesOfPorts(symbol.getAllOutgoingPorts());
    outgoingPorts.removeAll(targets);
    for (String port : outgoingPorts) {
      final SourcePosition sourcePosition = this.getSourcePosition(symbol, node, port);
      if (sources.contains(port)) {
        Log.error(
            String.format(ArcError.OUTGOING_PORT_AST_SOURCE.toString(), port, symbol.getFullName()),
            sourcePosition);
      }
      else {
        Log.warn(
            String.format(ArcError.OUTGOING_PORT_NO_FORWARD.toString(), port, symbol.getFullName()),
            sourcePosition);
      }
    }
  }

  protected Collection<String> getNamesOfPorts(Collection<PortSymbol> ports) {
    return ports.stream().map(PortSymbol::getName).collect(Collectors.toList());
  }

  protected SourcePosition getSourcePosition(ComponentTypeSymbol symbol,
      ASTComponentType node, String port) {
    return symbol.getPort(port).map(p -> p.getAstNode().get_SourcePositionStart())
        .orElse(node.get_SourcePositionEnd());
  }
}