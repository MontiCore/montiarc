/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcbehaviorbasis.BehaviorError;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.scstatehierarchy._cocos.SCStateHierarchyASTSCHierarchyBodyCoCo;

public class OneInitialInHierarchicalStates implements SCStateHierarchyASTSCHierarchyBodyCoCo {

  @Override
  public void check(ASTSCHierarchyBody node) {
    OneInitialState.check(node, node.streamSCStateElements(), BehaviorError.ONE_INITIAL_IN_HIERARCHICAL);
  }
}