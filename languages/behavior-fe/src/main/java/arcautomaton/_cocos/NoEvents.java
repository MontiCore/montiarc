/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcbehaviorbasis.BehaviorError;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;

public class NoEvents implements SCTransitions4CodeASTTransitionBodyCoCo {

  @Override
  public void check(ASTTransitionBody node) {
    if(node.isPresentSCEvent()){
      BehaviorError.UNSUPPORTED_EVENT.logAt(node);
    }
  }
}