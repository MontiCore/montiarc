/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;


/**
 * ensures that there is exactly one initial state
 */
public class OneInitialStateAtLeast implements ArcAutomatonASTArcStatechartCoCo {

  @Override
  public void check(@NotNull ASTArcStatechart automaton) {
    Preconditions.checkNotNull(automaton);
    if(automaton.streamInitialStates().count() == 0L){
      BehaviorError.NO_INITIAL_STATE.logAt(automaton, automaton.getEnclosingScope().getName());
    }
  }
}