/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * This context-condition checks that at an atomic component has at most one
 * behavior description.
 */
public class AtomicMaxOneBehavior implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isAtomic()) return;

    boolean first = true;

    for (ASTArcElement e : node.getBody().getArcElementList()) {

      if (e instanceof ASTArcBehaviorElement) {
        if (first) first = false;
        else Log.error(ArcError.MULTIPLE_BEHAVIOR.toString(), e.get_SourcePositionStart(), e.get_SourcePositionEnd());
      }
    }
  }
}
