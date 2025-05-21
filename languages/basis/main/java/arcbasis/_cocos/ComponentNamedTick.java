/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComponentNamedTick implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType component) {
    Preconditions.checkNotNull(component);
    if (component.getName().equals("Tick")) {
      Log.error(ArcError.COMPONENT_NAMED_TICK.format(), component.get_SourcePositionStart(), component.get_SourcePositionEnd());
    }
  }
}