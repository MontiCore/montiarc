/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._ast.ASTModeAutomaton;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * This context-condition checks that mode automata have no SCStates
 */
public class ModeAutomatonContainsNoStates implements ModesASTModeAutomatonCoCo {

  @Override
  public void check(@NotNull ASTModeAutomaton node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getSpannedScope());

    if (!node.getSpannedScope().getLocalSCStateSymbols().isEmpty()) {
      Log.error(ModesError.MODE_AUTOMATON_CONTAINS_STATE.format(), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
