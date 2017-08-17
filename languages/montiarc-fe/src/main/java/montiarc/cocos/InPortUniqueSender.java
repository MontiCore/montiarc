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
import montiarc._ast.ASTSimpleConnector;
import montiarc._ast.ASTSubComponent;
import montiarc._ast.ASTSubComponentInstance;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * Implements R1 and R2
 * 
 * @author Crispin Kirchner
 */
public class InPortUniqueSender implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    InPortUniqueSenderCheck check = new InPortUniqueSenderCheck(node);
    check.check();
  }
  
  private class InPortUniqueSenderCheck {
    private List<String> connectorTargets = new ArrayList<>();
    
    private ASTComponent node;
    
    public InPortUniqueSenderCheck(ASTComponent node) {
      this.node = node;
    }
    
    public void check() {
      checkConnectors();
      checkSimpleConnectors();
    }
    
    private void checkTarget(ASTQualifiedName target) {
      String targetString = target.toString();
      
      if (connectorTargets.contains(targetString)) {
        Log.error(String.format("0x2BD7E target port \"%s\" already in use.", target.toString()),
            target.get_SourcePositionStart());
      }
      else {
        connectorTargets.add(targetString);
      }
    }
    
    private void checkConnectors() {
      for (ASTConnector connector : node.getConnectors()) {
        for (ASTQualifiedName target : connector.getTargets()) {
          checkTarget(target);
        }
      }
    }
    
    private void checkSimpleConnectors() {
      for (ASTSubComponent subComponent : node.getSubComponents()) {
        for (ASTSubComponentInstance instance : subComponent.getInstances()) {
          for (ASTSimpleConnector connector : instance.getConnectors()) {
            for (ASTQualifiedName target : connector.getTargets()) {
              checkTarget(target);
            }
          }
        }
      }
    }
  }
  
}
