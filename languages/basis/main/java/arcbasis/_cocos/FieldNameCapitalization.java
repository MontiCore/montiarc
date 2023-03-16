/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [RRW14a] C2: The names of variables [...] start with
 * lower-case letters. (p. 31, Lst. 6.5)
 */
public class FieldNameCapitalization implements ArcBasisASTArcFieldCoCo {

  @Override
  public void check(@NotNull ASTArcField field) {
    Preconditions.checkNotNull(field);
    if(NameCapitalizationHelper.isNotLowerCase(field.getName())) {
      NameCapitalizationHelper.warning(ArcError.VARIABLE_LOWER_CASE, field, field.getName(),
        field.getEnclosingScope().getSpanningSymbol().getName());
    }
  }
}