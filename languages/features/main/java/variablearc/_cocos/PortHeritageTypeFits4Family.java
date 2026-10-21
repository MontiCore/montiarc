/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.Optional;

/**
 * Checks that the ports a component type declares fit the inherited ports of
 * the same name, i.e., have compatible types and the same direction, in every
 * configuration in which both are present.
 */
public class PortHeritageTypeFits4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public PortHeritageTypeFits4Family(@NotNull WithinComponentResolver withinComponentResolver,
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
    List<CompKindExpression> parents = component.getSuperComponentsList();
    List<CompKindExpression> boundParents = this.withinComponentResolver.getSuperComponents(
      this.withinComponentResolver.kindOf(component));
    for (PortSymbol port : component.getPorts()) {
      BoolExpr portPresence = this.presenceResolver.presenceOf(component, port);
      for (int i = 0; i < parents.size(); i++) {
        this.checkPort(solver, component, port, portPresence, parents.get(i), boundParents.get(i));
      }
    }
  }

  /**
   * @param parent      a super component of the component type
   * @param boundParent the super component with the type parameters bound by
   *                    the component type
   */
  protected void checkPort(@NotNull FormulaSolver solver,
                           @NotNull ComponentTypeSymbol component,
                           @NotNull PortSymbol port,
                           @NotNull BoolExpr portPresence,
                           @NotNull CompKindExpression parent,
                           @NotNull CompKindExpression boundParent) {
    for (PortSymbol inheritedPort : this.withinComponentResolver.resolveMany(parent.getTypeInfo(), port.getName(), PortSymbol.class)) {
      Optional<SymTypeExpression> inheritedPortType = this.withinComponentResolver.typeOf(boundParent, inheritedPort);
      if (inheritedPortType.isEmpty()) continue;

      BoolExpr inheritedPortPresence = this.presenceResolver.liftFromSuper(component, parent,
        this.presenceResolver.presenceOf(parent.getTypeInfo(), inheritedPort));
      BoolExpr bothPresent = solver.and(portPresence, inheritedPortPresence);

      if (port.isIncoming() && !SymTypeRelations.isCompatible(inheritedPortType.get(), port.getType())) {
        this.report(solver, bothPresent, ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH, port);
      } else if (!port.isIncoming() && !SymTypeRelations.isCompatible(port.getType(), inheritedPortType.get())) {
        this.report(solver, bothPresent, ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH, port);
      }
      if (inheritedPort.isIncoming() != port.isIncoming()) {
        this.report(solver, bothPresent, ArcError.HERITAGE_PORT_DIRECTION_MISMATCH, port);
      }
    }
  }

  protected void report(@NotNull FormulaSolver solver,
                        @NotNull BoolExpr violation,
                        @NotNull ArcError error,
                        @NotNull PortSymbol port) {
    switch (solver.check(violation)) {
      case SATISFIABLE -> Log.error(error.toString(), port.getSourcePosition());
      case UNKNOWN -> Log.warn(error.toString(), port.getSourcePosition());
      case UNSATISFIABLE -> { }
    }
  }
}
