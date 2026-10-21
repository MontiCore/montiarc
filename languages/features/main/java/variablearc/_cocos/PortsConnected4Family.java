/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Checks that every port of a component is used by at least one connector of
 * the component or of the super component it inherits the port from in every
 * configuration in which the port is present and the component is
 * decomposed, i.e., has a present subcomponent. Unused inherited ports are
 * reported at the component.
 */
public class PortsConnected4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public PortsConnected4Family(@NotNull WithinComponentResolver withinComponentResolver,
                               @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    if (this.withinComponentResolver.getAll(component, SubcomponentSymbol.class).isEmpty()) return;

    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr decomposed = this.decomposed(solver, component);
    for (PortSymbol port : this.withinComponentResolver.getAll(component, PortSymbol.class)) {
      List<CompKindExpression> path = this.withinComponentResolver.superPathTo(component, port).orElseThrow();
      Optional<BoolExpr> connected = this.connected(solver, component, path, port);
      if (connected.isEmpty()) continue;
      BoolExpr violation = solver.and(decomposed, this.presenceResolver.presenceOf(component, port), solver.not(connected.get()));
      String message = port.isIncoming() ? ArcError.IN_PORT_UNUSED.format(port.getName())
        : ArcError.OUT_PORT_UNUSED.format(port.getName());
      switch (solver.check(violation)) {
        case SATISFIABLE, UNKNOWN -> {
          if (path.isEmpty()) {
            Log.warn(message, port.getAstNode().get_SourcePositionStart(), port.getAstNode().get_SourcePositionEnd());
          } else {
            Log.warn(message, node.get_SourcePositionStart(), node.getHead().get_SourcePositionEnd());
          }
        }
        case UNSATISFIABLE -> { }
      }
    }
  }

  /**
   * @param path the super components along which the port is inherited
   * @return the condition under which a connector of the component type or of
   * a super component on the path uses the port, or empty if one of them has
   * no AST, e.g., as it is loaded from a symbol file, such that its
   * connectors are unknown
   */
  protected Optional<BoolExpr> connected(@NotNull FormulaSolver solver,
                                         @NotNull ComponentTypeSymbol component,
                                         @NotNull List<CompKindExpression> path,
                                         @NotNull PortSymbol port) {
    List<ComponentTypeSymbol> types = new ArrayList<>();
    types.add(component);
    path.forEach(superComponent -> types.add(superComponent.getTypeInfo()));
    List<BoolExpr> connections = new ArrayList<>();
    for (int i = 0; i < types.size(); i++) {
      ComponentTypeSymbol type = types.get(i);
      if (!type.isPresentAstNode() || !(type.getAstNode() instanceof ASTArcComponentType ast)) return Optional.empty();
      for (ASTConnector connector : VariableArcElements.of(ast, ASTConnector.class)) {
        if (!this.connects(type, connector, port)) continue;
        BoolExpr presence = this.presenceResolver.presenceOf(type, connector);
        for (int j = i - 1; j >= 0; j--) {
          presence = this.presenceResolver.liftFromSuper(types.get(j), path.get(j), presence);
        }
        connections.add(presence);
      }
    }
    return Optional.of(solver.or(connections));
  }

  protected boolean connects(@NotNull ComponentTypeSymbol component,
                             @NotNull ASTConnector connector,
                             @NotNull PortSymbol port) {
    return Stream.concat(Stream.of(connector.getSource()), connector.getTargetList().stream())
      .filter(access -> !access.isPresentComponent())
      .map(ASTPortAccess::getPort)
      .anyMatch(name -> this.withinComponentResolver.resolveMany(component, name, PortSymbol.class).contains(port));
  }

  protected BoolExpr decomposed(@NotNull FormulaSolver solver, @NotNull ComponentTypeSymbol component) {
    return solver.or(this.withinComponentResolver.getAll(component, SubcomponentSymbol.class).stream()
      .map(subcomponent -> this.presenceResolver.presenceOf(component, subcomponent))
      .toList());
  }
}
