/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Warns when a behavior block (e.g., compute/automata) is declared in a decomposed component
 * (i.e., component that declare subcomponents).
 */
public class BehaviorInDecomposed implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (node.getSymbol().isDecomposed() && node.getBehavior().isPresent()) {
      Log.warn(
        ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR.toString(),
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
      );
    }
  }
}
