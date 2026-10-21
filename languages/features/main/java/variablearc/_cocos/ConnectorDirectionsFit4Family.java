/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTConnectorCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Checks that the source of a connector is an incoming port of the enclosing
 * component or an outgoing port of a subcomponent, and that each target is
 * an outgoing port of the enclosing component or an incoming port of a
 * subcomponent, in every configuration in which the connector and the port
 * are present.
 */
public class ConnectorDirectionsFit4Family implements ArcBasisASTConnectorCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public ConnectorDirectionsFit4Family(@NotNull WithinComponentResolver withinComponentResolver,
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
    ASTPortAccess source = connector.getSource();

    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr connectorPresence = this.presenceResolver.presenceOf(component, connector);
    Predicate<PortSymbol> sourceMismatch = source.isPresentComponent() ? PortSymbol::isIncoming : PortSymbol::isOutgoing;
    this.report(solver, solver.and(connectorPresence, this.presenceOfPorts(solver, component, source, sourceMismatch)),
      ArcError.SOURCE_DIRECTION_MISMATCH.format(source.getQName()), source);
    for (ASTPortAccess target : connector.getTargetList()) {
      Predicate<PortSymbol> targetMismatch = target.isPresentComponent() ? PortSymbol::isOutgoing : PortSymbol::isIncoming;
      this.report(solver, solver.and(connectorPresence, this.presenceOfPorts(solver, component, target, targetMismatch)),
        ArcError.TARGET_DIRECTION_MISMATCH.format(target.getQName()), target);
    }
  }

  protected void report(@NotNull FormulaSolver solver,
                        @NotNull BoolExpr violation,
                        @NotNull String message,
                        @NotNull ASTPortAccess access) {
    switch (solver.check(violation)) {
      case SATISFIABLE -> Log.error(message, access.get_SourcePositionStart(), access.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(message, access.get_SourcePositionStart(), access.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }

  /**
   * @return the condition under which a port that the port access may refer
   * to and that matches the filter is present
   */
  protected BoolExpr presenceOfPorts(@NotNull FormulaSolver solver,
                                     @NotNull ComponentTypeSymbol component,
                                     @NotNull ASTPortAccess access,
                                     @NotNull Predicate<PortSymbol> filter) {
    return solver.or(this.withinComponentResolver.resolvePortAccess(component, access).stream()
      .filter(candidate -> filter.test(candidate.port()))
      .map(candidate -> this.presenceResolver.presenceOf(component, candidate))
      .toList());
  }
}
