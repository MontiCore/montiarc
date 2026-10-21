/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._cocos.ArcBasisASTConnectorCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.Optional;

/**
 * Checks that a connector is not present in any configuration in which its
 * enclosing component is atomic, i.e., has no present subcomponent.
 */
public class AtomicNoConnector4Family implements ArcBasisASTConnectorCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public AtomicNoConnector4Family(@NotNull WithinComponentResolver withinComponentResolver,
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
    BoolExpr violation = solver.and(
      this.presenceResolver.presenceOf(component, connector),
      solver.not(this.decomposed(solver, component, connector.getEnclosingScope()))
    );
    switch (solver.check(violation)) {
      case SATISFIABLE -> Log.error(ArcError.CONNECTORS_IN_ATOMIC.toString(),
        connector.get_SourcePositionStart(), connector.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(ArcError.CONNECTORS_IN_ATOMIC.toString(),
        connector.get_SourcePositionStart(), connector.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }

  protected BoolExpr decomposed(@NotNull FormulaSolver solver, @NotNull ComponentTypeSymbol component, @NotNull IScope scope) {
    return solver.or(this.withinComponentResolver.getAll(scope, SubcomponentSymbol.class).stream()
      .map(subcomponent -> this.presenceResolver.presenceOf(component, subcomponent))
      .toList());
  }
}
