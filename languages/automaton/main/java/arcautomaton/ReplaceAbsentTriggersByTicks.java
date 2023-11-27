/* (c) https://github.com/MontiCore/monticore */
package arcautomaton;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._visitor.ArcAutomatonHandler;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis.trafo.SourcePositionUtil;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeVisitor2;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Adds the {@code Tick} as a trigger to transitions without triggers in event automatons.
 * <br>
 * In event automatons, transitions without a trigger event implicitly
 * represent transitions that are triggered by ticks. E.g.:
 * <pre>{@code
 *   component A {
 *     port out int o;
 *
 *     <<timed>> automaton {
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
 * Usage: Add this as the handler for {@code ArcAutomaton} and visitor for {@code SCTransition4Code} to a traverser.
 */
public class ReplaceAbsentTriggersByTicks implements ArcAutomatonHandler, SCTransitions4CodeVisitor2 {

  protected ArcAutomatonTraverser traverser;

  @Override
  public ArcAutomatonTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull ArcAutomatonTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void traverse(ASTArcStatechart node) {
    // Only continue traversal if the automaton is an event automaton, i.e. <<timed>>
    if (node.isPresentStereotype() && node.getStereotype().contains("timed")) {
      ArcAutomatonHandler.super.traverse(node);
    }
  }

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
