/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import montiarc.util.ArcError;
import de.se_rwth.commons.logging.Log;

public class NoBehaviorInComposedComponents implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType type) {
    if(type.getSymbol().isDecomposed() && OnlyOneBehavior.streamBehaviors(type).findAny().isPresent()) {
      OnlyOneBehavior.streamBehaviors(type).forEach(
        behavior -> Log.error(
          ArcError.BEHAVIOR_IN_COMPOSED_COMPONENT.format(type.getName()),
          behavior.get_SourcePositionStart(), behavior.get_SourcePositionEnd()
        )
      );
    }
  }
}