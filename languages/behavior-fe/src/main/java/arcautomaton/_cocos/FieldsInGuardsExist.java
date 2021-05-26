/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcbehaviorbasis.BehaviorError;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;

public class FieldsInGuardsExist implements SCTransitions4CodeASTTransitionBodyCoCo {

  @Override
  public void check(ASTTransitionBody transition) {
    transition.isPresentPre();
    ASTExpression guard = transition.getPre();

    BehaviorError.FIELD_IN_GUARD_MISSING.logAt(guard);
  }
}