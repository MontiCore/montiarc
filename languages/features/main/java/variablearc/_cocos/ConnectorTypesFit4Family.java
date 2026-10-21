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
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Checks that the type of a connector's source port is compatible to the type
 * of each of its target ports, in every configuration in which the connector
 * and both ports are present. Port types are bound by the enclosing
 * component, e.g., by the type arguments of generic subcomponents and super
 * components.
 */
public class ConnectorTypesFit4Family implements ArcBasisASTConnectorCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public ConnectorTypesFit4Family(@NotNull WithinComponentResolver withinComponentResolver,
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
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr connectorPresence = this.presenceResolver.presenceOf(component, connector);
    List<PortInContext> sources = this.candidates(solver, component, connector.getSource());
    for (ASTPortAccess target : connector.getTargetList()) {
      for (PortInContext targetPort : this.candidates(solver, component, target)) {
        this.checkCandidate(solver, connectorPresence, sources, targetPort, target);
      }
    }
  }

  protected void checkCandidate(@NotNull FormulaSolver solver,
                                @NotNull BoolExpr connectorPresence,
                                @NotNull List<PortInContext> sources,
                                @NotNull PortInContext target,
                                @NotNull ASTPortAccess targetAccess) {
    Optional<String> possibleMismatch = Optional.empty();
    for (PortInContext source : sources) {
      if (source.type().isObscureType() || target.type().isObscureType()
        || SymTypeRelations.isCompatible(target.type(), source.type())) {
        continue;
      }
      String message = ArcError.CONNECTOR_TYPE_MISMATCH.format(target.type().print(), source.type().print());
      switch (solver.check(solver.and(connectorPresence, source.presence(), target.presence()))) {
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

  protected List<PortInContext> candidates(@NotNull FormulaSolver solver,
                                           @NotNull ComponentTypeSymbol component,
                                           @NotNull ASTPortAccess access) {
    List<PortInContext> candidates = new ArrayList<>();
    for (PortAccessCandidate candidate : this.withinComponentResolver.resolvePortAccess(component, access)) {
      this.withinComponentResolver.typeOf(component, candidate).ifPresent(type ->
        candidates.add(new PortInContext(type, this.presenceResolver.presenceOf(component, candidate))));
    }
    return candidates;
  }

  protected record PortInContext(@NotNull SymTypeExpression type, @NotNull BoolExpr presence) {}
}
