/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPort;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc.util.UniqueNameHelper;
import org.codehaus.commons.nullanalysis.NotNull;

public class UniqueFieldNames implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType component) {
    Preconditions.checkNotNull(component);
    UniqueNameHelper.checkNames(component.getFields(), ASTArcField::getName, component.getName(), ArcError.UNIQUE_FIELD_NAME);
  }
}