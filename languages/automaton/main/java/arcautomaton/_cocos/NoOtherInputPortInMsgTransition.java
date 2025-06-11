/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcautomaton._visitor.NoOtherInputPortInEventContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

import static arcautomaton.ArcAutomatonMill.TICK;
import static arcautomaton.ArcAutomatonMillTOP.typeDispatcher;

/**
 * Checks that transitions triggered by message events do not reference input
 * ports other thant the port that triggered the respective message event
 * (i.e., received the message).
 */
public class NoOtherInputPortInMsgTransition implements SCTransitions4CodeASTTransitionBodyCoCo {

  // A human-readable description of the context in which this check is applied
  protected final static String context = "this message-event triggered transition";

  @Override
  public void check(@NotNull ASTTransitionBody node) {
    Preconditions.checkNotNull(node);

    if (node.isPresentSCEvent()
      && typeDispatcher().isArcAutomatonASTMsgEvent(node.getSCEvent())
      && !typeDispatcher().asArcAutomatonASTMsgEvent(node.getSCEvent())
      .getName().equals(TICK)) {

      String event = typeDispatcher().asArcAutomatonASTMsgEvent(node.getSCEvent()).getName();

      ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
      traverser.add4ExpressionsBasis(createVisitor(event));
      node.accept(traverser);
    }
  }

  protected NoOtherInputPortInEventContextVisitor createVisitor(@NotNull String event) {
    Preconditions.checkNotNull(event);
    Preconditions.checkArgument(!event.isBlank());
    return new NoOtherInputPortInEventContextVisitor(event, context);
  }
}
