/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;
import arcbasis._ast.ASTArcField;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

public class FieldNamedTick implements ArcBasisASTArcFieldCoCo {

  @Override
  public void check(@NotNull ASTArcField field) {
    Preconditions.checkNotNull(field);
    if(field.getName().equals("Tick")) {
      Log.error(ArcError.FIELD_NAMED_TICK.format(field.getName()), field.get_SourcePositionStart(), field.get_SourcePositionEnd());
    }
  }
}