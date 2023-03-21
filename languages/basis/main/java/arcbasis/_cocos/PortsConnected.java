/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * CV5: In decomposed components all ports should be used in at least one connector.<br>
 * DIFFERENCE to CV6: CV5 checks that in and out ports are connected <em>within</em> the
 * (non-atomic) component itself while CV6 checks that a subcomponent is connected in its <em>outer
 * context</em> (i.e. the outer component).
 * <p>
 * Implements [Hab16] CV5: In decomposed components, all ports should be used
 * in at least one connector. (p.71 Lst. 3.52)
 */
public class PortsConnected implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol symbol = node.getSymbol();

    if (symbol.isAtomic()) {
      return;
    }

    Collection<String> sources = node.getConnectors().stream()
      .filter(p -> !p.getSource().isPresentComponent())
      .map(ASTConnector::getSourceName)
      .collect(Collectors.toSet());
    Collection<String> targets = node.getConnectors().stream()
      .map(ASTConnector::getTargetList)
      .flatMap(Collection::stream)
      .filter(p -> !p.isPresentComponent())
      .map(ASTPortAccess::getQName)
      .collect(Collectors.toSet());

    // --------- INCOMING PORTS ----------
    Collection<PortSymbol> incoming = symbol.getAllIncomingPorts();
    for (PortSymbol port : incoming) {
      if (!sources.contains(port.getName()) && !targets.contains(port.getName())) {
        Log.warn(ArcError.IN_PORT_UNUSED.format(port.getName()),
          port.getAstNode().get_SourcePositionStart(), port.getAstNode().get_SourcePositionEnd()
        );
      }
    }

    // --------- OUTGOING PORTS ----------
    Collection<PortSymbol> outgoing = symbol.getAllOutgoingPorts();
    for (PortSymbol port : outgoing) {
      if (!sources.contains(port.getName()) && !targets.contains(port.getName())) {
        Log.warn(ArcError.OUT_PORT_UNUSED.format(port.getName()),
          port.getAstNode().get_SourcePositionStart(), port.getAstNode().get_SourcePositionEnd()
        );
      }
    }
  }
}