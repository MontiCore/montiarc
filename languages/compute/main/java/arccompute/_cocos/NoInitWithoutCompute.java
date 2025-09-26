/* (c) https://github.com/MontiCore/monticore */
package arccompute._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcComputeError;

import java.util.Optional;

/**
 * Checks that init blocks have a corresponding compute behavior
 */
public class NoInitWithoutCompute implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(ASTArcComponentType comp) {
    Optional<ASTArcInit> lastInit = Optional.empty();

    for (ASTArcElement element : comp.getBody().getArcElementList()) {
      if (element instanceof ASTArcCompute) {
        return;
      }
      if (lastInit.isEmpty() && element instanceof ASTArcInit) {
        lastInit = Optional.of((ASTArcInit) element);
      }
    }
    lastInit.ifPresent(astArcInit -> Log.error(ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE.toString(),
      astArcInit.get_SourcePositionStart(), astArcInit.get_SourcePositionEnd()
    ));
  }
}
