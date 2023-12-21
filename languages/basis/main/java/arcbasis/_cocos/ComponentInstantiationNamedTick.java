/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;
import arcbasis._ast.ASTComponentInstantiation;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComponentInstantiationNamedTick implements ArcBasisASTComponentInstantiationCoCo {

  @Override
  public void check(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    for (String s : node.getInstancesNames()) {
      if (s.equals("Tick")) {
        Log.error(ArcError.COMPONENTINSTANCE_NAMED_TICK.format(), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    }
  }
}