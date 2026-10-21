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
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Checks that the ports and subcomponents referenced by a connector exist in
 * every configuration in which the connector is present. A port of a
 * subcomponent is only required in configurations in which the subcomponent
 * is present; subcomponents without a type are ignored.
 */
public class ConnectorPortsExist4Family implements ArcBasisASTConnectorCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public ConnectorPortsExist4Family(@NotNull WithinComponentResolver withinComponentResolver,
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
    this.checkPresence(solver, component, connectorPresence, connector.getSource());
    for (ASTPortAccess target : connector.getTargetList()) {
      this.checkPresence(solver, component, connectorPresence, target);
    }
  }

  protected void checkPresence(@NotNull FormulaSolver solver,
                               @NotNull ComponentTypeSymbol component,
                               @NotNull BoolExpr connectorPresence,
                               @NotNull ASTPortAccess access) {
    if (!access.isPresentComponent()) {
      BoolExpr portPresence = solver.or(this.withinComponentResolver.resolveMany(access.getEnclosingScope(), access.getPort(), PortSymbol.class).stream()
        .map(port -> this.presenceResolver.presenceOf(component, port))
        .toList());
      this.report(solver, solver.and(connectorPresence, solver.not(portPresence)),
        ArcError.MISSING_PORT.format(access.getQName()), access);
      return;
    }

    List<SubcomponentSymbol> subcomponents = this.withinComponentResolver.resolveMany(access.getEnclosingScope(), access.getComponent(), SubcomponentSymbol.class);
    BoolExpr subcomponentPresence = solver.or(subcomponents.stream()
      .map(subcomponent -> this.presenceResolver.presenceOf(component, subcomponent))
      .toList());
    this.report(solver, solver.and(connectorPresence, solver.not(subcomponentPresence)),
      ArcError.MISSING_SUBCOMPONENT.format(access.getComponent()), access);

    List<BoolExpr> portMissing = new ArrayList<>();
    for (SubcomponentSymbol subcomponent : subcomponents) {
      if (!subcomponent.isTypePresent()) continue;
      portMissing.add(this.presenceOfSubcomponentWithoutPort(solver, component, subcomponent, access.getPort()));
    }
    this.report(solver, solver.and(connectorPresence, solver.or(portMissing)),
      ArcError.MISSING_PORT.format(access.getQName()), access);
  }

  /**
   * @return the condition under which the subcomponent is present, but none
   * of its ports with the given name
   */
  protected BoolExpr presenceOfSubcomponentWithoutPort(@NotNull FormulaSolver solver,
                                                       @NotNull ComponentTypeSymbol component,
                                                       @NotNull SubcomponentSymbol subcomponent,
                                                       @NotNull String port) {
    ComponentTypeSymbol type = subcomponent.getType().getTypeInfo();
    BoolExpr portPresence = solver.or(this.withinComponentResolver.resolveMany(type, port, PortSymbol.class).stream()
      .map(candidate -> this.presenceResolver.presenceOf(component, subcomponent, candidate))
      .toList());
    return solver.and(
      this.presenceResolver.presenceOf(component, subcomponent),
      this.presenceResolver.lift(component, subcomponent, this.presenceResolver.presenceOf(type)),
      solver.not(portPresence)
    );
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
}
