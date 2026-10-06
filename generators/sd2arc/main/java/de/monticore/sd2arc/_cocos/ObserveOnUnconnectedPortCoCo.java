/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc._cocos;

import arcbasis._ast.ASTPortAccess;
import de.monticore.lang.sd4components._ast.ASTSDMessage;
import de.monticore.lang.sd4components._ast.ASTSDPort;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._cocos.SDBasisASTSequenceDiagramCoCo;
import de.monticore.sd2arc.trafo.EmbeddingComponent;
import de.monticore.sd2arc.util.SD2ArcError;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Warn if an interaction is without a trigger for an unconnected port in an embedded sequence diagram.
 */
public class ObserveOnUnconnectedPortCoCo implements SDBasisASTSequenceDiagramCoCo {

  @Override
  public void check(ASTSequenceDiagram node) {
    List<ASTSDPort> targets = node.getSDBody().streamSDElements()
      .filter(ASTSDSendMessage.class::isInstance)
      .map(ASTSDSendMessage.class::cast)
      .filter(e -> !(e.getSDAction() instanceof ASTSDMessage message) || !message.isTrigger())
      .filter(ASTSDSendMessage::isPresentSDTarget)
      .map(ASTSDSendMessage::getSDTarget)
      .filter(ASTSDPort.class::isInstance)
      .map(ASTSDPort.class::cast)
      .toList();

    Set<String> connectedPorts = getConnectedPorts(node);

    for (ASTSDPort target : targets) {
      if (!connectedPorts.contains(target.getName() + "." + target.getPort())) {
        Log.error(SD2ArcError.OBSERVE_ON_UNCONNECTED_PORT.format(target.getName() + "." + target.getPort()), target.get_SourcePositionStart(), target.get_SourcePositionEnd());
      }
    }
  }

  protected Set<String> getConnectedPorts(ASTSequenceDiagram node) {
    if (EmbeddingComponent.isEmbedded(node)) {
      return EmbeddingComponent.get(node).getConnectors().stream().flatMap(c -> c.getTargetList().stream()).map(ASTPortAccess::getQName).collect(Collectors.toSet());
    }

    // Get implied connectors
    Set<String> targets = new HashSet<>();
    for (ASTSDSendMessage connector : node.getSDBody().streamSDElements()
      .filter(ASTSDSendMessage.class::isInstance)
      .map(ASTSDSendMessage.class::cast)
      .toList()) {
      if (connector.isPresentSDTarget() && connector.getSDTarget() instanceof ASTSDPort astTarget
        && connector.isPresentSDSource() && connector.getSDSource() instanceof ASTSDPort) {
        targets.add(astTarget.getName() + "." + astTarget.getPort());
      }
    }
    return targets;
  }
}
