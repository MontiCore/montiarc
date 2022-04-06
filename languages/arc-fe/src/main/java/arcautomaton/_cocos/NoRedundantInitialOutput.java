/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis.util.ArcError;
import de.monticore.scbasis._ast.ASTSCState;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.*;

/**
 * Checks that each state is declared as initial only once. This allows that multiple states may be initial. However,
 * as for every initial state there is only one AST declaration, this coco asserts that the initial output for a
 * specific state (defined by the {@link de.monticore.scbasis._ast.ASTSCSAnte} block) is uniquely defined.
 *
 * This coco is a special case of {@link OneInitialStateAtMax}. If you use the latter, then you do not have to use this
 * coco.
 */
public class NoRedundantInitialOutput implements ArcAutomatonASTArcStatechartCoCo {

  @Override
  public void check(ASTArcStatechart statechart) {
    Set<String> duplicates = new HashSet<>();
    for(ASTSCState state: statechart.streamInitialStates().collect(toList())) {
      if (duplicates.contains(state.getName())) {
        Log.error(ArcError.REDUNDANT_INITIAL_DECLARATION.format(state.getName()),
          state.get_SourcePositionStart(), state.get_SourcePositionEnd()
        );
      } else {
        duplicates.add(state.getName());
      }
    }
  }
}