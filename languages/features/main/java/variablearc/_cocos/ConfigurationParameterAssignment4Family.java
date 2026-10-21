/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

/**
 * Variability aware extension of {@link ConfigurationParameterAssignment}:
 * the arguments of a subcomponent are only checked in the configurations in
 * which the subcomponent is present, those of the super components and
 * refinements in the configurations of the component type.
 */
public class ConfigurationParameterAssignment4Family extends ConfigurationParameterAssignment {

  protected final PresenceResolver presenceResolver;

  protected FormulaSolver solver;

  protected BoolExpr presence;

  public ConfigurationParameterAssignment4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                                 @NotNull PresenceResolver presenceResolver) {
    super(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
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
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    ComponentTypeSymbol component = this.resolver.enclosingComponentOf(node.getEnclosingScope()).orElseThrow();
    this.solver = this.presenceResolver.solverFor(component);
    this.presence = this.presenceResolver.presenceOf(component, node.getSymbol());
    super.check(node);
  }

  @Override
  protected void report(@NotNull String message, @Nullable SourcePosition start, @Nullable SourcePosition end) {
    switch (this.solver.check(this.presence)) {
      case SATISFIABLE -> super.report(message, start, end);
      case UNKNOWN -> {
        if (start != null && end != null) {
          Log.warn(message, start, end);
        } else {
          Log.warn(message);
        }
      }
      case UNSATISFIABLE -> { }
    }
  }
}
