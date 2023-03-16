/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [Hab16] CV2: Types start with an upper-case letter.
 * (p. 71, lst. 3.51)
 */
public class ComponentTypeNameCapitalization implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType component) {
    Preconditions.checkNotNull(component);
    if(NameCapitalizationHelper.isNotUpperCase(component.getName())) {
      NameCapitalizationHelper.warning(ArcError.COMPONENT_NAME_UPPER_CASE, component, component.getName());
    }
  }
}