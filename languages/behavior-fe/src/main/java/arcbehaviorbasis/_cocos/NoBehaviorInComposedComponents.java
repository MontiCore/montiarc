/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbehaviorbasis.BehaviorError;
import de.monticore.ast.ASTNode;

public class NoBehaviorInComposedComponents implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType type) {
    if(type.getSymbol().isDecomposed() && OnlyOneBehavior.streamBehaviors(type).count() != 0) {
      BehaviorError.BEHAVIOR_IN_COMPOSED_COMPONENT.logAt(
          OnlyOneBehavior.streamBehaviors(type)
            .map(element -> (ASTNode) element)
            .findAny()
            .orElse(type),
          type.getName());
    }
  }
}