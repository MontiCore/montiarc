/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._cocos.TypeParametersASTTypeParameterCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

public class TypeParameterNamedTick implements TypeParametersASTTypeParameterCoCo {

  @Override
  public void check(@NotNull ASTTypeParameter p) {
    Preconditions.checkNotNull(p);

    if (!p.getName().isEmpty() && p.getName().equals("Tick")) {
          Log.error(ArcError.TYPEPARAMETERS_NAMED_TICK.format(p.getName()), p.get_SourcePositionStart(), p.get_SourcePositionEnd());
    }
  }
}