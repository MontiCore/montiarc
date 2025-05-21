/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._ast.ASTModeAutomaton;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * This context-condition checks that a component has at most one mode automaton.
 */
public class MaxOneModeAutomaton implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (node.getSymbol().isAtomic()) return;

    boolean first = true;

    for (ASTArcElement e : node.getBody().getArcElementList()) {
      if (e instanceof ASTModeAutomaton) {
        if (first) first = false;
        else
          Log.error(ModesError.MULTIPLE_MODE_AUTOMATA.format(), e.get_SourcePositionStart(), e.get_SourcePositionEnd());
      }
    }
  }
}
