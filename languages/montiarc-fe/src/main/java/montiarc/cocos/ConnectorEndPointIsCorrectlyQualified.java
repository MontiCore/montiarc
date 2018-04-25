/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.function.Predicate;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTConnector;
import montiarc._cocos.MontiArcASTConnectorCoCo;

/**
 * @implements [Hab16] CO1: Connectors may not pierce through component interfaces. (p. 60, Lst.
 * 3.33)
 * @implements [Hab16] CO2: A simple connector’s source is an outgoing port of the referenced
 * component type and is therefore not qualified. (p. 61, Lst. 3.34)
 * @author Crispin Kirchner
 */
public class ConnectorEndPointIsCorrectlyQualified
    implements MontiArcASTConnectorCoCo {
  
  private void checkEndpointCorrectlyQualified(ASTQualifiedName name,
      Predicate<Integer> predicate, String errorMessage) {
    if (!predicate.test(name.getParts().size())) {
      Log.error(String.format(errorMessage, name.toString()), name.get_SourcePositionStart());
    }
  }
  
  /**
   * Ensure that the connector endpoint is of the form `rootComponentPort' or `subComponent.port'
   */
  private void checkEndPointMaximallyTwiceQualified(ASTQualifiedName name) {
    checkEndpointCorrectlyQualified(name, i -> i <= 2 && i > 0,
        "0xMA070 Connector end point \"%s\" must only consist of an optional component name and a port name");
  }
  
  /**
   * @see montiarc._cocos.MontiArcASTConnectorCoCo#check(montiarc._ast.ASTConnector)
   */
  @Override
  public void check(ASTConnector node) {
    checkEndPointMaximallyTwiceQualified(node.getSource());
    
    for (ASTQualifiedName name : node.getTargets()) {
      checkEndPointMaximallyTwiceQualified(name);
    }
  }
  
}
