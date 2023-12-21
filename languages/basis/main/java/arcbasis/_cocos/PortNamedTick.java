/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;
import arcbasis._ast.ASTArcPort;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;

public class PortNamedTick implements ArcBasisASTArcPortCoCo{

  @Override
  public void check(ASTArcPort node) {
    Preconditions.checkNotNull(node);
    if(node.getName().equals("Tick")){
      Log.error(ArcError.PORT_NAMED_TICK.format(), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
