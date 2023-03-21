/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis._cocos.ComponentNameCapitalization;
import com.google.common.base.Preconditions;
import genericarc._ast.ASTArcTypeParameter;
import montiarc.util.GenericArcError;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [Hab16] CV2: Types start with an upper-case letter. (p. 71, lst. 3.51)
 *
 * @see ComponentNameCapitalization
 */
public class GenericTypeParameterNameCapitalization implements GenericArcASTArcTypeParameterCoCo {

  @Override
  public void check(@NotNull ASTArcTypeParameter typeParameter) {

    Preconditions.checkNotNull(typeParameter);
    if (NameCapitalizationHelper.isNotUpperCase(typeParameter.getName())) {
      NameCapitalizationHelper.warning(GenericArcError.TYPE_PARAMETER_UPPER_CASE_LETTER, typeParameter,
        typeParameter.getName(), typeParameter.getEnclosingScope().getSpanningSymbol().getName());
    }
  }
}