/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTConnectorCoCo;
import arcbasis.context.PortAccessCandidate;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.Optional;

/**
 * Checks that the timing of a connector's source port is compatible to the
 * timing of each of its target ports, in every configuration in which the
 * connector and both ports are present.
 */
public class ConnectorTimingsFit4Family implements ArcBasisASTConnectorCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public ConnectorTimingsFit4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                    @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTConnector connector) {
    Preconditions.checkNotNull(connector);
    Optional<ComponentTypeSymbol> enclosing = this.withinComponentResolver.enclosingComponentOf(connector.getEnclosingScope());
    if (enclosing.isEmpty()) return;

    ComponentTypeSymbol component = enclosing.get();
    List<PortAccessCandidate> sources = this.withinComponentResolver.resolvePortAccess(component, connector.getSource());

    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr connectorPresence = this.presenceResolver.presenceOf(component, connector);
    for (ASTPortAccess target : connector.getTargetList()) {
      for (PortAccessCandidate targetPort : this.withinComponentResolver.resolvePortAccess(component, target)) {
        this.checkCandidate(solver, component, connectorPresence, sources, targetPort, target);
      }
    }
  }

  protected void checkCandidate(@NotNull FormulaSolver solver,
                                @NotNull ComponentTypeSymbol component,
                                @NotNull BoolExpr connectorPresence,
                                @NotNull List<PortAccessCandidate> sources,
                                @NotNull PortAccessCandidate target,
                                @NotNull ASTPortAccess targetAccess) {
    Optional<String> possibleMismatch = Optional.empty();
    for (PortAccessCandidate source : sources) {
      if (source.port().getTiming().compatible(target.port().getTiming())) {
        continue;
      }
      String message = ArcError.CONNECTOR_TIMING_MISMATCH.format(
        target.port().getTiming().getName(), source.port().getTiming().getName());
      switch (solver.check(solver.and(connectorPresence,
        this.presenceResolver.presenceOf(component, source), this.presenceResolver.presenceOf(component, target)))) {
        case SATISFIABLE -> {
          Log.error(message, targetAccess.get_SourcePositionStart(), targetAccess.get_SourcePositionEnd());
          return;
        }
        case UNKNOWN -> possibleMismatch = possibleMismatch.or(() -> Optional.of(message));
        case UNSATISFIABLE -> { }
      }
    }
    possibleMismatch.ifPresent(message ->
      Log.warn(message, targetAccess.get_SourcePositionStart(), targetAccess.get_SourcePositionEnd()));
  }
}
