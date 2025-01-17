/* (c) https://github.com/MontiCore/monticore */
package arcautomaton;

import arcautomaton._ast.ASTMsgEvent;
import arcbasis.trafo.SourcePositionUtil;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeVisitor2;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Adds the {@code Tick} as a trigger to transitions without triggers in automatons.
 * <br>
 * Transitions without a trigger event implicitly
 * represent transitions that are triggered by ticks. E.g.:
 * <pre>{@code
 *   component A {
 *     port out int o;
 *
 *     automaton {
 *       initial state S;
 *
 *       // The following two transitions are semantically equal:
 *       S -> S / { p = 1; };
 *       S -> S Tick / { p = 1; };
 *       // The first transition will be transformed to the second one in this coco.
 *     }
 *   }
 * }</pre>
 *
 * <br>
 * Usage: Add this as the visitor for {@code SCTransition4Code} to a traverser.
 */
public class ReplaceAbsentTriggersByTicks implements SCTransitions4CodeVisitor2 {

  @Override
  public void visit(ASTTransitionBody node) {
    if (!node.isPresentSCEvent()) {
      node.setSCEvent(buildTickEventFor(node));
    }
  }

  protected ASTMsgEvent buildTickEventFor(@NotNull ASTTransitionBody transitionBody) {
    Preconditions.checkNotNull(transitionBody);

    SourcePosition srcStart = transitionBody.isPresentPre() ?
      SourcePositionUtil.elongate(transitionBody.getPre().get_SourcePositionEnd(), 1) :
      transitionBody.get_SourcePositionStart();
    SourcePosition srcEnd = srcStart.clone();

    return ArcAutomatonMill.msgEventBuilder()
      .setName(ArcAutomatonMill.TICK)
      .set_SourcePositionStart(srcStart)
      .set_SourcePositionEnd(srcEnd)
      .build();
  }

}
