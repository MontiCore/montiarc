package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import de.monticore.lang.montiarc.montiarcbehavior._ast.ASTBehaviorImplementation;
import de.monticore.lang.montiarc.montiarcbehavior._cocos.MontiArcBehaviorASTBehaviorImplementationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if the name of an IO-Automaton starts with an
 * uppercase letter.
 * 
 * @author Gerrit
 */
public class AutomatonUppercase implements MontiArcBehaviorASTBehaviorImplementationCoCo {


  @Override
  public void check(ASTBehaviorImplementation node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xAB140 The name of the automaton should start with an uppercase letter.", node.get_SourcePositionStart());
    }    
  }
  
}
