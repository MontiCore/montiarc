/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis._cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbehaviorbasis.BehaviorError;
import arcbehaviorbasis._ast.ASTArcBehaviorElement;

import java.util.stream.Stream;

public class OnlyOneBehavior implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType type) {
    streamBehaviors(type).skip(1).findAny().ifPresent(behavior -> BehaviorError.MULTIPLE_BEHAVIOR.logAt(behavior, type.getName()));
  }

  /**
   * finds how many behavior specifications this component has as direct children
   * @param type component to search through
   * @return all behavior elements that are in the component's body
   */
  static Stream<ASTArcElement> streamBehaviors(ASTComponentType type){
    return type.getBody()
        .streamArcElements()
        .filter(element -> element instanceof ASTArcBehaviorElement);
  }
}