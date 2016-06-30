/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import java.util.function.Predicate;

import de.monticore.lang.montiarc.montiarc._ast.ASTConnector;
import de.monticore.lang.montiarc.montiarc._ast.ASTSimpleConnector;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTConnectorCoCo;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTSimpleConnectorCoCo;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;

/**
 * @author Crispin Kirchner
 * 
 * Implementation of CO1 and CO2
 */
public class ConnectorEndPointCorrectlyQualified
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
        "0x44B7E Simple connector source \"%s\" must only consist of a port name.");
  }
  
  /**
   * Ensure that the connector endpoint is of the form `rootComponentPort' or `subComponent.port'
   */
  private void checkEndPointMaximallyTwiceQualified(ASTQualifiedName name) {
    checkEndpointCorrectlyQualified(name, i -> i <= 2 && i > 0,
        "0xDB61C Connector end point \"%s\" must only consist of an optional component name and a port name");
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTConnectorCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTConnector)
   */
  @Override
  public void check(ASTConnector node) {
    checkEndPointMaximallyTwiceQualified(node.getSource());
    
    for (ASTQualifiedName name : node.getTargets()) {
      checkEndPointMaximallyTwiceQualified(name);
    }
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTSimpleConnectorCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTSimpleConnector)
   */
  @Override
  public void check(ASTSimpleConnector node) {
    checkSimpleConnectorSourceUnqualified(node.getSource());
    
    for (ASTQualifiedName name : node.getTargets()) {
      checkEndPointMaximallyTwiceQualified(name);
    }
  }
  
}
