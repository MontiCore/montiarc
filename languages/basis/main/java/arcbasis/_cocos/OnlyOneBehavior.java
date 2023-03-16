/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;

import java.util.stream.Stream;

/**
 * Checks that there is at most one behavior declaration for a component type.
 */
public class OnlyOneBehavior implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType type) {
    if(streamBehaviors(type).count() >= 2L) {
      streamBehaviors(type).forEach(
        behavior -> Log.error(
          ArcError.MULTIPLE_BEHAVIOR.format(type.getName()),
          behavior.get_SourcePositionStart(), behavior.get_SourcePositionEnd())
      );
    }
  }

  /**
   * finds how many behavior specifications this component has as direct children
   * @param type component to search through
   * @return all behavior elements that are in the component's body
   */
  static Stream<ASTArcElement> streamBehaviors(ASTComponentType type){
    return type.getBody()
        .streamArcElements()
        .filter(ASTArcBehaviorElement.class::isInstance);
  }
}