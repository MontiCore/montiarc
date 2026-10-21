/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.NoInputPortInContext4FamilyVisitor;
import variablearc.presence.PresenceResolver;

import static arcautomaton._cocos.NoOtherInputPortInMsgTransition.CONTEXT;

/**
 * Checks that transitions triggered by message events do not reference input
 * ports other than the port that triggered the respective message event, in
 * any configuration in which the transition and the other input port are
 * present.
 */
public class NoOtherInputPortInMsgTransition4Family implements SCTransitions4CodeASTTransitionBodyCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public NoOtherInputPortInMsgTransition4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                                @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTTransitionBody node) {
    Preconditions.checkNotNull(node);

    if (node.isPresentSCEvent() && node.getSCEvent() instanceof ASTMsgEvent messageEvent) {
      ArcAutomatonTraverser traverser = ArcAutomatonMill.traverser();
      traverser.add4ExpressionsBasis(this.createVisitor(messageEvent.getName()));
      node.accept(traverser);
    }
  }

  protected NoInputPortInContext4FamilyVisitor createVisitor(@NotNull String event) {
    Preconditions.checkNotNull(event);
    Preconditions.checkArgument(!event.isBlank());
    return new NoInputPortInContext4FamilyVisitor(this.withinComponentResolver, this.presenceResolver, CONTEXT,
      port -> port.isIncoming() && !port.getName().equals(event));
  }
}
