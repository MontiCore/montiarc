package de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions;

import de.monticore.lang.montiarc.montiarcautomaton._ast.ASTBehaviorEmbedding;
import de.monticore.lang.montiarc.montiarcautomaton._cocos.MontiArcAutomatonASTBehaviorEmbeddingCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if the name of an IO-Automaton starts with an
 * uppercase letter.
 * 
 * @author Gerrit Leonhardt
 */
public class AutomatonUppercase implements MontiArcAutomatonASTBehaviorEmbeddingCoCo {


  @Override
  public void check(ASTBehaviorEmbedding node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xAB130 The name of the automaton should start with an uppercase letter.", node.get_SourcePositionStart());
    }    
  }
  
}
