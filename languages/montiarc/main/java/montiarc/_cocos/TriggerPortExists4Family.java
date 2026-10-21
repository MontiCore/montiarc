/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import arcprepost._ast.ASTArcPrePost;
import arcprepost._cocos.ArcPrePostASTArcPrePostCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.PrePostError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.Optional;

/**
 * Checks that the trigger of a pre/post specification is an incoming port in
 * every configuration in which the specification is present.
 */
public class TriggerPortExists4Family implements ArcPrePostASTArcPrePostCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public TriggerPortExists4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                  @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcPrePost node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    if (!node.isPresentTrigger()) return;
    Optional<ComponentTypeSymbol> enclosing = this.withinComponentResolver.enclosingComponentOf(node.getEnclosingScope());
    if (enclosing.isEmpty()) return;

    ComponentTypeSymbol component = enclosing.get();
    String trigger = node.getTrigger();
    List<PortSymbol> ports = this.withinComponentResolver.resolveMany(component, trigger, PortSymbol.class);
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr present = this.presenceResolver.presenceOf(component, node);
    BoolExpr absent = solver.not(solver.or(ports.stream()
      .map(port -> this.presenceResolver.presenceOf(component, port))
      .toList()));
    BoolExpr outgoing = solver.or(ports.stream()
      .filter(port -> !port.isIncoming())
      .map(port -> this.presenceResolver.presenceOf(component, port))
      .toList());
    this.report(solver, solver.and(present, absent), PrePostError.TRIGGER_MISSING_PORT.format(trigger), node);
    this.report(solver, solver.and(present, outgoing), PrePostError.TRIGGER_NOT_AN_IN_PORT.format(trigger), node);
  }

  protected void report(@NotNull FormulaSolver solver,
                        @NotNull BoolExpr violation,
                        @NotNull String message,
                        @NotNull ASTArcPrePost node) {
    switch (solver.check(violation)) {
      case SATISFIABLE -> Log.error(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }
}
