/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcComputeError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that an init block of a component is not present in any
 * configuration in which the component is atomic, i.e., has no present
 * subcomponent, and a preceding init block is present.
 */
public class MaxOneInit4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public MaxOneInit4Family(@NotNull WithinComponentResolver withinComponentResolver,
                           @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    List<ASTArcInit> inits = VariableArcElements.of(node, ASTArcInit.class);
    if (inits.size() < 2) return;

    List<BoolExpr> preceding = new ArrayList<>();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr atomic = solver.not(this.decomposed(solver, component));
    for (ASTArcInit init : inits) {
      BoolExpr presence = this.presenceResolver.presenceOf(component, init);
      if (!preceding.isEmpty()) {
        BoolExpr violation = solver.and(atomic, presence, solver.or(preceding));
        switch (solver.check(violation)) {
          case SATISFIABLE -> Log.error(ArcComputeError.MULTIPLE_INIT.toString(),
            init.get_SourcePositionStart(), init.get_SourcePositionEnd());
          case UNKNOWN -> Log.warn(ArcComputeError.MULTIPLE_INIT.toString(),
            init.get_SourcePositionStart(), init.get_SourcePositionEnd());
          case UNSATISFIABLE -> { }
        }
      }
      preceding.add(presence);
    }
  }

  protected BoolExpr decomposed(@NotNull FormulaSolver solver, @NotNull ComponentTypeSymbol component) {
    return solver.or(this.withinComponentResolver.getAll(component, SubcomponentSymbol.class).stream()
      .map(subcomponent -> this.presenceResolver.presenceOf(component, subcomponent))
      .toList());
  }
}
