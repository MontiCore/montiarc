/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc._cocos;

import arcbasis._ast.ASTArcComponentType;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._ast.ASTSDPort;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._cocos.SDBasisASTSequenceDiagramCoCo;
import de.monticore.sd2arc.trafo.EmbeddingComponent;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Interactions in the sequence diagram should match the connectors in the embedding component.
 */
public class ImpliedConnectorsFitEmbeddingComponentCoCo implements SDBasisASTSequenceDiagramCoCo {

  public static final String MESSAGE_ERROR = "0xB5101: "
    + "Cannot observe '%s -> %s' as the connector does not exist in the embedding component";

  @Override
  public void check(ASTSequenceDiagram node) {
    if (!EmbeddingComponent.isEmbedded(node))
      return;

    Map<ASTSDPort, ASTSDPort> impliedConnectors = getImpliedConnectors(node);

    for (Map.Entry<ASTSDPort, ASTSDPort> entry : impliedConnectors.entrySet()) {
      if (!containsConnector(EmbeddingComponent.get(node), getQName(entry.getKey()), getQName(entry.getValue()))) {
        Log.error(String.format(MESSAGE_ERROR, getQName(entry.getKey()), getQName(entry.getValue())), entry.getKey().get_SourcePositionStart(), entry.getValue().get_SourcePositionEnd());
      }
    }
  }

  protected String getQName(ASTSDPort node) {
    return node.getName() + "." + node.getPort();
  }

  protected Map<ASTSDPort, ASTSDPort> getImpliedConnectors(ASTSequenceDiagram diagram) {
    Map<ASTSDPort, ASTSDPort> targetSource = new HashMap<>();
    for (ASTSDSendMessage connector : diagram.getSDBody().streamSDElements()
      .filter(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDSendMessage)
      .map(SD4ComponentsMill.typeDispatcher()::asSDBasisASTSDSendMessage)
      .toList()) {
      if (connector.isPresentSDTarget()
        && SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(connector.getSDTarget())
        && connector.isPresentSDSource()
        && SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(connector.getSDSource())) {
        ASTSDPort astSource = SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(connector.getSDSource());
        ASTSDPort astTarget = SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(connector.getSDTarget());

        targetSource.put(astSource, astTarget);
      }
    }
    return targetSource;
  }

  protected boolean containsConnector(ASTArcComponentType componentType, String source, String target) {
    return componentType.getConnectors().parallelStream()
      .filter(connector -> connector.getSource().getQName().equals(source))
      .anyMatch(connector -> connector.streamTarget().anyMatch(t -> t.getQName().equals(target)));
  }
}
