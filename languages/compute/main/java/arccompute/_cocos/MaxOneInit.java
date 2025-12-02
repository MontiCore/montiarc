/* (c) https://github.com/MontiCore/monticore */
package arccompute._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcComputeError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * This CoCo checks that max one init block exist
 */
public class MaxOneInit implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isAtomic()) return;

    boolean first = true;

    for (ASTArcElement e : node.getBody().getArcElementList()) {

      if (e instanceof ASTArcInit) {
        if (first) first = false;
        else Log.error(ArcComputeError.MULTIPLE_INIT.toString(), e.get_SourcePositionStart(), e.get_SourcePositionEnd());
      }
    }
  }
}
