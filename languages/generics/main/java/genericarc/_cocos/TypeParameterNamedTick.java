/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTArcTypeParameter;
import montiarc.util.ArcError;
import montiarc.util.GenericArcError;
import org.codehaus.commons.nullanalysis.NotNull;

public class TypeParameterNamedTick implements GenericArcASTArcTypeParameterCoCo {

  @Override
  public void check(@NotNull ASTArcTypeParameter p) {
    Preconditions.checkNotNull(p);

    if (!p.getName().isEmpty() && p.getName().equals("Tick")) {
          Log.error(ArcError.TYPEPARAMETERS_NAMED_TICK.format(p.getName()), p.get_SourcePositionStart(), p.get_SourcePositionEnd());
    }
  }
}