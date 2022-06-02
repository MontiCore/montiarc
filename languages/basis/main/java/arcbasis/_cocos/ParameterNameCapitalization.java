/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that parameters start with a lower-case letter.
 */
public class ParameterNameCapitalization implements ArcBasisASTArcParameterCoCo {

  @Override
  public void check(@NotNull ASTArcParameter parameter) {
    Preconditions.checkNotNull(parameter);
    if(NameCapitalizationHelper.isNotLowerCase(parameter.getName())) {
      NameCapitalizationHelper.warning(ArcError.PARAMETER_LOWER_CASE, parameter, parameter.getName(),
        parameter.getEnclosingScope().getSpanningSymbol().getName());
    }
  }
}