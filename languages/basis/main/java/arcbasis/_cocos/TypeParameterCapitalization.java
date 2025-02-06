/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._cocos.TypeParametersASTTypeParameterCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [Hab16] CV2: Types start with an upper-case letter. (p. 71, lst. 3.51)
 */
public class TypeParameterCapitalization implements TypeParametersASTTypeParameterCoCo {

  @Override
  public void check(@NotNull ASTTypeParameter p) {
    Preconditions.checkNotNull(p);

    if (p.getName().length() > 0 && !Character.isUpperCase(p.getName().codePointAt(0))) {
      Log.warn(ArcError.TYPE_PARAMETER_UPPER_CASE.toString(),
        p.get_SourcePositionStart(), p.get_SourcePositionEnd()
      );
    }
  }
}