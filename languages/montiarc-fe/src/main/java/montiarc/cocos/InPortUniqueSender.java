/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * @implements [Hab16] R1: Each outgoing port of a component type definition is
 * used at most once as target of a connector. (p. 63, Lst. 3.36)
 * @implements [Hab16] R2: Each incoming port of a subcomponent is used at most
 * once as target of a connector. (p. 62, Lst. 3.37)
 * @author Crispin Kirchner
 */
public class InPortUniqueSender implements MontiArcASTComponentCoCo {


  @Override
  public void check(ASTComponent node) {
    List<String> connectorTargets = new ArrayList<>();
    String targetString;

    for (ASTConnector connector : node.getConnectors()) {
      for (ASTQualifiedName target : connector.getTargetsList()) {
        targetString = target.toString();
        if (connectorTargets.contains(targetString)) {
          Log.error(
              String.format("0xMA005 target port \"%s\" already in use.",
                  targetString),
              target.get_SourcePositionStart());
        }
        else {
          connectorTargets.add(targetString);
        }
      }
    }
  }
}
