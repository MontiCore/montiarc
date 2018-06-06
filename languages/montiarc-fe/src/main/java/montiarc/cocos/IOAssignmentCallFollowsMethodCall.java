/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;

/**
 * This coco checks whether the call keyword is followed by a method call only and vice versa.
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class IOAssignmentCallFollowsMethodCall implements MontiArcASTIOAssignmentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTIOAssignmentCoCo#check(montiarc._ast.ASTIOAssignment)
   */
  @Override
  public void check(ASTIOAssignment node) {
    if (node.isCall()) {
      if (!iOAssignmentContainsMethodCall(node)) {
        Log.error("0xMA090 The 'call' keyword has to be followed by a method call.",
            node.get_SourcePositionStart());
      }
    }
    else if(!node.isCall() && !node.isAssignment()) {
      if(iOAssignmentContainsMethodCall(node)) {
        Log.error("0xMA091 A method call must be put after the 'call' keyword.",
            node.get_SourcePositionStart()); 
      }
    }
  }
  
  private boolean iOAssignmentContainsMethodCall(ASTIOAssignment assignment) {
    if (assignment.getValueList().isPresent()) {
      if (!assignment.getValueList().get().getAllValuations().isEmpty()) {
        ASTExpression expr = assignment.getValueList().get().getAllValuations().get(0)
            .getExpression();
        if (expr.callExpressionIsPresent()) {
          return true;
        }
      }
    }
    return false;
  }
  
}
