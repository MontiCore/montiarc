/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc._cocos;

import arcbasis._ast.ASTComponentInstance;
import de.monticore.lang.sdbasis._ast.ASTSDObject;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._cocos.SDBasisASTSequenceDiagramCoCo;
import de.monticore.sd2arc.trafo.EmbeddingComponent;
import de.se_rwth.commons.logging.Log;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sequence diagram component list should match embedding component's subcomponents
 */
public class SubcomponentExistsInEmbeddingComponentCoCo implements SDBasisASTSequenceDiagramCoCo {

  public static final String MESSAGE_ERROR = "0xB5100: "
    + "The embedding component has no subcomponent '%s'";

  @Override
  public void check(ASTSequenceDiagram node) {
    if (!EmbeddingComponent.isEmbedded(node)) return;

    Set<String> subcomponents = EmbeddingComponent.get(node).getSubComponents().stream().map(ASTComponentInstance::getName).collect(Collectors.toSet());

    for (ASTSDObject object : node.getSDObjectList()) {
      if (!subcomponents.contains(object.getName())) {
        Log.error(String.format(MESSAGE_ERROR, object.getName()), object.get_SourcePositionStart(), object.get_SourcePositionEnd());
      }
    }
  }
}
