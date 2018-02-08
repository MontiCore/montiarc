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
import montiarc._ast.ASTSimpleConnector;
import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._cocos.MontiArcASTSimpleConnectorCoCo;

/**
 * @author Crispin Kirchner
 * 
 * Implementation of CO1 and CO2
 */
public class ConnectorEndPointIsCorrectlyQualified
    implements MontiArcASTSimpleConnectorCoCo, MontiArcASTConnectorCoCo {
  
  private void checkEndpointCorrectlyQualified(ASTQualifiedName name,
      Predicate<Integer> predicate, String errorMessage) {
    if (!predicate.test(name.getParts().size())) {
      Log.error(String.format(errorMessage, name.toString()), name.get_SourcePositionStart());
    }
  }
  
  /**
   * Ensure that the connector endpoint is of the form `port'
   */
  private void checkSimpleConnectorSourceUnqualified(ASTQualifiedName name) {
    checkEndpointCorrectlyQualified(name, i -> i == 1,
        "0xMA008 Simple connector source \"%s\" must only consist of a port name.");
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
  
  /**
   * @see montiarc._cocos.MontiArcASTSimpleConnectorCoCo#check(montiarc._ast.ASTSimpleConnector)
   */
  @Override
  public void check(ASTSimpleConnector node) {
    checkSimpleConnectorSourceUnqualified(node.getSource());
    
    for (ASTQualifiedName name : node.getTargets()) {
      checkEndPointMaximallyTwiceQualified(name);
    }
  }
  
}
