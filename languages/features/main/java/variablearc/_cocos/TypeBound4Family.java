/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._cocos.TypeBound;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

/**
 * Variability aware extension of {@link TypeBound}: the type arguments of a
 * subcomponent instantiation are only checked in the configurations in which
 * the instantiation is present, those of the super components and
 * refinements in the configurations of the component type.
 */
public class TypeBound4Family extends TypeBound {

  protected final PresenceResolver presenceResolver;

  protected FormulaSolver solver;

  protected BoolExpr presence;

  public TypeBound4Family(@NotNull WithinComponentResolver withinComponentResolver,
                          @NotNull PresenceResolver presenceResolver) {
    super(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    ComponentTypeSymbol component = this.resolver.enclosingComponentOf(node.getEnclosingScope()).orElseThrow();
    this.solver = this.presenceResolver.solverFor(component);
    this.presence = this.presenceResolver.presenceOf(component, node);
    super.check(node);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    this.solver = this.presenceResolver.solverFor(node.getSymbol());
    this.presence = this.presenceResolver.presenceOf(node.getSymbol());
    super.check(node);
  }

  @Override
  protected void report(@NotNull String message, @NotNull CompKindExpression typeExpr) {
    switch (this.solver.check(this.presence)) {
      case SATISFIABLE -> super.report(message, typeExpr);
      case UNKNOWN -> Log.warn(message,
        typeExpr.getSourceNode().map(ASTNode::get_SourcePositionStart).orElse(null),
        typeExpr.getSourceNode().map(ASTNode::get_SourcePositionEnd).orElse(null));
      case UNSATISFIABLE -> { }
    }
  }
}
