/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcComputeError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that the first present init block of a component is not present in
 * any configuration in which no compute block of the component is present.
 */
public class NoInitWithoutCompute4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public NoInitWithoutCompute4Family(@NotNull WithinComponentResolver withinComponentResolver,
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
    if (inits.isEmpty()) return;

    List<BoolExpr> preceding = new ArrayList<>();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr noCompute = solver.not(solver.or(VariableArcElements.of(node, ASTArcCompute.class).stream()
      .map(compute -> this.presenceResolver.presenceOf(component, compute))
      .toList()));
    for (ASTArcInit init : inits) {
      BoolExpr presence = this.presenceResolver.presenceOf(component, init);
      BoolExpr violation = solver.and(noCompute, presence, solver.not(solver.or(preceding)));
      switch (solver.check(violation)) {
        case SATISFIABLE -> Log.error(ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE.toString(),
          init.get_SourcePositionStart(), init.get_SourcePositionEnd());
        case UNKNOWN -> Log.warn(ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE.toString(),
          init.get_SourcePositionStart(), init.get_SourcePositionEnd());
        case UNSATISFIABLE -> { }
      }
      preceding.add(presence);
    }
  }
}
