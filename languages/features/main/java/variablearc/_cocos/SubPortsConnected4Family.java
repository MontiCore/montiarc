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
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.stream.Stream;

/**
 * Checks that every port of a subcomponent is used by at least one connector
 * of the enclosing component in every configuration in which the
 * subcomponent and its port are present.
 */
public class SubPortsConnected4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public SubPortsConnected4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                  @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    List<ASTConnector> connectors = VariableArcElements.of(node, ASTConnector.class);

    FormulaSolver solver = this.presenceResolver.solverFor(component);
    for (SubcomponentSymbol subcomponent : component.getSubcomponents()) {
      if (!subcomponent.isTypePresent()) continue;
      ComponentTypeSymbol type = subcomponent.getType().getTypeInfo();
      for (PortSymbol port : this.withinComponentResolver.getAll(type, PortSymbol.class)) {
        this.check(solver, component, connectors, subcomponent, port);
      }
    }
  }

  protected void check(@NotNull FormulaSolver solver,
                       @NotNull ComponentTypeSymbol component,
                       @NotNull List<ASTConnector> connectors,
                       @NotNull SubcomponentSymbol subcomponent,
                       @NotNull PortSymbol port) {
    BoolExpr connected = solver.or(connectors.stream()
      .filter(connector -> this.connects(component, connector, subcomponent, port))
      .map(connector -> this.presenceResolver.presenceOf(component, connector))
      .toList());
    BoolExpr violation = solver.and(this.presenceResolver.presenceOf(component, subcomponent, port), solver.not(connected));
    String name = subcomponent.getName() + "." + port.getName();
    String message = port.isIncoming() ? ArcError.IN_PORT_NOT_CONNECTED.format(name)
      : ArcError.OUT_PORT_NOT_CONNECTED.format(name);
    SourcePosition start = subcomponent.getAstNode().get_SourcePositionStart();
    SourcePosition end = subcomponent.getAstNode().get_SourcePositionEnd();
    switch (solver.check(violation)) {
      case SATISFIABLE -> {
        if (port.isIncoming()) Log.error(message, start, end);
        else Log.warn(message, start, end);
      }
      case UNKNOWN -> Log.warn(message, start, end);
      case UNSATISFIABLE -> { }
    }
  }

  protected boolean connects(@NotNull ComponentTypeSymbol component,
                             @NotNull ASTConnector connector,
                             @NotNull SubcomponentSymbol subcomponent,
                             @NotNull PortSymbol port) {
    return Stream.concat(Stream.of(connector.getSource()), connector.getTargetList().stream())
      .filter(ASTPortAccess::isPresentComponent)
      .filter(access -> this.withinComponentResolver.resolveMany(component, access.getComponent(), SubcomponentSymbol.class)
        .contains(subcomponent))
      .anyMatch(access -> this.withinComponentResolver.resolveMany(subcomponent.getType().getTypeInfo(), access.getPort(), PortSymbol.class)
        .contains(port));
  }
}
