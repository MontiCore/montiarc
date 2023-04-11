/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import com.google.common.base.Preconditions;
import genericarc._ast.ASTArcTypeParameter;
import montiarc.util.GenericArcError;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [Hab16] CV2: Types start with an upper-case letter. (p. 71, lst. 3.51)
 */
public class TypeParameterCapitalization implements GenericArcASTArcTypeParameterCoCo {

  @Override
  public void check(@NotNull ASTArcTypeParameter p) {
    Preconditions.checkNotNull(p);

    if (NameCapitalizationHelper.isNotUpperCase(p.getName())) {
      NameCapitalizationHelper.warning(GenericArcError.TYPE_PARAMETER_UPPER_CASE, p,
        p.getName(), p.getEnclosingScope().getSpanningSymbol().getName());
    }
  }
}