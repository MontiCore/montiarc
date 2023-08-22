/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._ast.ASTModeAutomaton;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * This context-condition checks that an atomic component has no mode automata.
 */
public class ModeAutomataInDecomposedComponent implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isAtomic()) return;

    for (ASTArcElement e : node.getBody().getArcElementList()) {
      if (e instanceof ASTModeAutomaton) {
        Log.error(ModesError.MODE_AUTOMATON_IN_ATOMIC_COMPONENT.format(), e.get_SourcePositionStart(), e.get_SourcePositionEnd());
      }
    }
  }

}
