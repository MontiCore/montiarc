/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * This context-condition checks that at an atomic component has no connectors.
 */
public class AtomicNoConnector implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isAtomic()) return;

    for (ASTArcElement e : node.getBody().getArcElementList()) {

      if (e instanceof ASTConnector) {
        Log.warn(ArcError.CONNECTORS_IN_ATOMIC.toString(), e.get_SourcePositionStart(), e.get_SourcePositionEnd());
      }
    }
  }
}
