/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc._cocos;

import de.monticore.lang.sd4components._ast.ASTSDComponent;
import de.monticore.lang.sd4components._ast.ASTSDPort;
import de.monticore.lang.sd4components._ast.ASTSDTick;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._cocos.SDBasisASTSequenceDiagramCoCo;
import de.monticore.sd2arc.codegen.SDHelper;
import de.monticore.sd2arc.util.SD2ArcError;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Synchronous ports carry a message in every tick. Hence, a tick block has to state every synchronous port whose
 * interactions may not be omitted (match complete or visible), otherwise the diagram can never be fulfilled.
 */
public class TickBlockStatesConstrainedSyncPortsCoCo implements SDBasisASTSequenceDiagramCoCo {

  protected SDHelper helper = new SDHelper();

  @Override
  public void check(ASTSequenceDiagram node) {
    for (ASTSDTick tick : node.getSDBody().streamSDElements()
      .filter(ASTSDTick.class::isInstance)
      .map(ASTSDTick.class::cast)
      .toList()) {
      Set<String> stated = tick.streamSDSendMessages()
        .filter(ASTSDSendMessage::isPresentSDSource)
        .map(ASTSDSendMessage::getSDSource)
        .filter(ASTSDPort.class::isInstance)
        .map(ASTSDPort.class::cast)
        .map(p -> p.getName() + "." + p.getPort())
        .collect(Collectors.toSet());

      for (ASTSDComponent component : helper.getVisibleComponents(node)) {
        for (PortSymbol port : helper.getComponentPorts(component)) {
          String qName = component.getName() + "." + port.getName();
          if (port.isOutgoing() && helper.isSyncPort(port) && !helper.isFree(node, component, port)
            && !stated.contains(qName)) {
            Log.error(SD2ArcError.TICK_BLOCK_OMITS_SYNC_PORT.format(qName), tick.get_SourcePositionStart(), tick.get_SourcePositionEnd());
          }
        }
      }
    }
  }
}
