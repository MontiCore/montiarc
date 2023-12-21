/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;
import arcbasis._ast.ASTArcParameter;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

public class ParameterNamedTick implements ArcBasisASTArcParameterCoCo {

  @Override
  public void check(@NotNull ASTArcParameter parameter) {
    Preconditions.checkNotNull(parameter);
    if(parameter.getName().equals("Tick")) {
      Log.error(ArcError.PARAMETER_NAMED_TICK.format(parameter.getName()), parameter.get_SourcePositionStart(), parameter.get_SourcePositionEnd());
    }
  }
}