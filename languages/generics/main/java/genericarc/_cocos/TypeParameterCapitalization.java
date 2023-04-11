/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTArcTypeParameter;
import montiarc.util.GenericArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [Hab16] CV2: Types start with an upper-case letter. (p. 71, lst. 3.51)
 */
public class TypeParameterCapitalization implements GenericArcASTArcTypeParameterCoCo {

  @Override
  public void check(@NotNull ASTArcTypeParameter p) {
    Preconditions.checkNotNull(p);

    if (p.getName().length() > 0 && !Character.isUpperCase(p.getName().codePointAt(0))) {
      Log.warn(GenericArcError.TYPE_PARAMETER_UPPER_CASE.toString(),
        p.get_SourcePositionStart(), p.get_SourcePositionEnd()
      );
    }
  }
}