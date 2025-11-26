/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc._cocos;

import arcbasis._ast.ASTPortAccess;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._ast.ASTSDPort;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._cocos.SDBasisASTSequenceDiagramCoCo;
import de.monticore.sd2arc.trafo.EmbeddingComponent;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Warn if an interaction is without a trigger for an unconnected port in an embedded sequence diagram.
 */
public class ObserveOnUnconnectedPortCoCo implements SDBasisASTSequenceDiagramCoCo {

  public static final String MESSAGE_ERROR = "0xB5102: "
    + "The port '%s' is not connected to anything, thus the interaction can never be observed. Did you forget to add the trigger keyword?";

  @Override
  public void check(ASTSequenceDiagram node) {
    List<ASTSDPort> targets = node.getSDBody().streamSDElements()
      .filter(e -> SD4ComponentsMill.typeDispatcher().isSDBasisASTSDSendMessage(e))
      .filter(e -> !SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDMessage(SD4ComponentsMill.typeDispatcher().asSDBasisASTSDSendMessage(e).getSDAction()) || !SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDMessage(SD4ComponentsMill.typeDispatcher().asSDBasisASTSDSendMessage(e).getSDAction()).isTrigger())
      .filter(e -> SD4ComponentsMill.typeDispatcher().asSDBasisASTSDSendMessage(e).isPresentSDTarget())
      .map(e -> SD4ComponentsMill.typeDispatcher().asSDBasisASTSDSendMessage(e).getSDTarget())
      .filter(e -> SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(e))
      .map(e -> SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(e))
      .collect(Collectors.toList());

    Set<String> connectedPorts = getConnectedPorts(node);

    for (ASTSDPort target : targets) {
      if (!connectedPorts.contains(target.getName() + "." + target.getPort())) {
        Log.error(String.format(MESSAGE_ERROR, target.getName() + "." + target.getPort()), target.get_SourcePositionStart(), target.get_SourcePositionEnd());
      }
    }
  }

  protected Set<String> getConnectedPorts(ASTSequenceDiagram node) {
    if (EmbeddingComponent.isEmbedded(node)) {
      return EmbeddingComponent.get(node).getConnectors().stream().flatMap(c -> c.getTargetList().stream()).map(ASTPortAccess::getQName).collect(Collectors.toSet());
    }

    // Get implied connectors
    Set<String> targets = new LinkedHashSet<>();
    for (ASTSDSendMessage connector : node.getSDBody().streamSDElements()
      .filter(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDSendMessage)
      .map(SD4ComponentsMill.typeDispatcher()::asSDBasisASTSDSendMessage)
      .collect(Collectors.toList())) {
      if (connector.isPresentSDTarget()
        && SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(connector.getSDTarget())
        && connector.isPresentSDSource()
        && SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(connector.getSDSource())) {
        ASTSDPort astTarget = SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(connector.getSDTarget());
        targets.add(astTarget.getName() + "." + astTarget.getPort());
      }
    }
    return targets;
  }
}
