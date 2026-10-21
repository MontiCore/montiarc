/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that the first present behavior description of a component is not
 * present in any configuration in which the component is decomposed, i.e.,
 * has a present subcomponent.
 */
public class BehaviorInDecomposed4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public BehaviorInDecomposed4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                     @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    List<ASTArcBehaviorElement> behaviors = VariableArcElements.of(node, ASTArcBehaviorElement.class);
    if (behaviors.isEmpty()) return;

    List<BoolExpr> preceding = new ArrayList<>();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr decomposed = this.decomposed(solver, component);
    for (ASTArcBehaviorElement behavior : behaviors) {
      BoolExpr presence = this.presenceResolver.presenceOf(component, behavior);
      BoolExpr violation = solver.and(decomposed, presence, solver.not(solver.or(preceding)));
      switch (solver.check(violation)) {
        case SATISFIABLE -> Log.error(ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR.toString(),
          behavior.get_SourcePositionStart(), behavior.get_SourcePositionEnd());
        case UNKNOWN -> Log.warn(ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR.toString(),
          behavior.get_SourcePositionStart(), behavior.get_SourcePositionEnd());
        case UNSATISFIABLE -> { }
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
