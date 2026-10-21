/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that the ports a component type declares do not change the timing
 * of the inherited ports of the same name in a forbidden way, i.e., from sync
 * to non-sync for outgoing ports and from non-sync to sync for incoming ports,
 * in any configuration in which both are present. Like
 * {@link arcbasis._cocos.PortHeritageTimingFits}, only the first present
 * inherited port of the name in the order of the super components is
 * considered.
 */
public class PortHeritageTimingFits4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public PortHeritageTimingFits4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                       @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    if (component.isEmptySuperComponents()) return;

    FormulaSolver solver = this.presenceResolver.solverFor(component);
    for (PortSymbol port : component.getPorts()) {
      BoolExpr portPresence = this.presenceResolver.presenceOf(component, port);
      List<BoolExpr> preceding = new ArrayList<>();
      for (CompKindExpression parent : component.getSuperComponentsList()) {
        for (PortSymbol inheritedPort : this.withinComponentResolver.resolveMany(parent.getTypeInfo(), port.getName(), PortSymbol.class)) {
          BoolExpr inheritedPortPresence = this.presenceResolver.liftFromSuper(component, parent,
            this.presenceResolver.presenceOf(parent.getTypeInfo(), inheritedPort));
          BoolExpr overridden = solver.and(portPresence, inheritedPortPresence, solver.not(solver.or(preceding)));
          this.checkTiming(solver, overridden, port, inheritedPort);
          preceding.add(inheritedPortPresence);
        }
      }
    }
  }

  protected void checkTiming(@NotNull FormulaSolver solver,
                             @NotNull BoolExpr overridden,
                             @NotNull PortSymbol port,
                             @NotNull PortSymbol inheritedPort) {
    boolean sync = port.getTiming().matches(Timing.TIMED_SYNC);
    boolean inheritedSync = inheritedPort.getTiming().matches(Timing.TIMED_SYNC);
    if (port.isOutgoing() && inheritedPort.isOutgoing() && inheritedSync && !sync) {
      this.report(solver, overridden, ArcError.INVALID_PORT_TIMING_OVERRIDE.format(port.getName(), "sync", "non-sync"), port);
    } else if (port.isIncoming() && inheritedPort.isIncoming() && !inheritedSync && sync) {
      this.report(solver, overridden, ArcError.INVALID_PORT_TIMING_OVERRIDE.format(port.getName(), "non-sync", "sync"), port);
    }
  }

  protected void report(@NotNull FormulaSolver solver,
                        @NotNull BoolExpr violation,
                        @NotNull String message,
                        @NotNull PortSymbol port) {
    switch (solver.check(violation)) {
      case SATISFIABLE -> Log.error(message, port.getSourcePosition());
      case UNKNOWN -> Log.warn(message, port.getSourcePosition());
      case UNSATISFIABLE -> { }
    }
  }
}
