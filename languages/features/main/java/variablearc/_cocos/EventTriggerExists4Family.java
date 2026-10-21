/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._cocos.ArcAutomatonASTMsgEventCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.Optional;

/**
 * Checks that the transition event triggers exist, i.e., that an input port
 * of the event's name is present in every configuration in which the event
 * is present.
 */
public class EventTriggerExists4Family implements ArcAutomatonASTMsgEventCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public EventTriggerExists4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                   @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTMsgEvent node) {
    Preconditions.checkNotNull(node);
    Optional<ComponentTypeSymbol> enclosing = this.withinComponentResolver.enclosingComponentOf(node.getEnclosingScope());
    if (enclosing.isEmpty()) return;

    ComponentTypeSymbol component = enclosing.get();
    String message = ArcAutomataError.CANT_FIND_MSG_EVENT_SYMBOL.format(node.getName());
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    List<BoolExpr> ports = this.withinComponentResolver.resolveMany(component, node.getName(), PortSymbol.class).stream()
      .filter(PortSymbol::isIncoming)
      .map(port -> this.presenceResolver.presenceOf(component, port))
      .toList();
    switch (solver.check(solver.and(this.presenceResolver.presenceOf(component, node), solver.not(solver.or(ports))))) {
      case SATISFIABLE -> Log.error(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }
}
