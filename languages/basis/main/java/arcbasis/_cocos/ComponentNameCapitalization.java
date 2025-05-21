/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [Hab16] CV2: Types start with an upper-case letter.
 * (p. 71, lst. 3.51)
 */
public class ComponentNameCapitalization implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType component) {
    Preconditions.checkNotNull(component);
    if(NameCapitalizationHelper.isNotUpperCase(component.getName())) {
      NameCapitalizationHelper.warning(ArcError.COMPONENT_LOWER_CASE, component, component.getName());
    }
  }
}