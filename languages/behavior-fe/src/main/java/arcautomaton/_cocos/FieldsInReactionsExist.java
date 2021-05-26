/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcbehaviorbasis.BehaviorError;
import de.monticore.scactions._ast.ASTSCABody;
import de.monticore.scactions._cocos.SCActionsASTSCABodyCoCo;

/**
 * checks whether the ports or variables used in re-, entry- and exit-actions exist.
 */
public class FieldsInReactionsExist implements SCActionsASTSCABodyCoCo {

  @Override
  public void check(ASTSCABody action) {
    BehaviorError.FIELD_IN_ACTION_MISSING.logAt(action);
  }
}