/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolSurrogate;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Checks that no component type extends itself, directly or transitively, in
 * any configuration of its enclosing component type. Inheritance itself is
 * not variable, but inner component types are present only in some
 * configurations, and inner component types of the same name in exclusive
 * variation points are alternative super components.
 */
public class CircularInheritance4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public CircularInheritance4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                    @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol root = node.getSymbol();
    Optional<ComponentTypeSymbol> owner = this.withinComponentResolver.enclosingComponentOf(root.getEnclosingScope());
    String message = ArcError.CIRCULAR_INHERITANCE.format(node.getName());
    FormulaSolver solver = this.presenceResolver.solver();
    Map<ComponentTypeSymbol, List<BoolExpr>> cycles = new LinkedHashMap<>();
    Set<ComponentTypeSymbol> onPath = new HashSet<>();
    onPath.add(root);
    this.collectCycles(solver, owner, root, root, this.presenceOf(solver, owner, root), onPath, cycles);
    for (List<BoolExpr> cycle : cycles.values()) {
      switch (solver.check(solver.or(cycle))) {
        case SATISFIABLE -> Log.error(message, node.getHead().get_SourcePositionStart(), node.getHead().get_SourcePositionEnd());
        case UNKNOWN -> Log.warn(message, node.getHead().get_SourcePositionStart(), node.getHead().get_SourcePositionEnd());
        case UNSATISFIABLE -> { }
      }
    }
  }

  /**
   * Collects the presence of each inheritance path from the root back to
   * itself, grouped by the last component type of the path, i.e., the one
   * that extends the root.
   */
  protected void collectCycles(@NotNull FormulaSolver solver,
                               @NotNull Optional<ComponentTypeSymbol> owner,
                               @NotNull ComponentTypeSymbol root,
                               @NotNull ComponentTypeSymbol next,
                               @NotNull BoolExpr presence,
                               @NotNull Set<ComponentTypeSymbol> onPath,
                               @NotNull Map<ComponentTypeSymbol, List<BoolExpr>> cycles) {
    for (CompKindExpression superComponent : next.getSuperComponentsList()) {
      for (ComponentTypeSymbol parent : this.alternativesOf(owner, superComponent)) {
        if (parent == root) {
          cycles.computeIfAbsent(next, n -> new ArrayList<>()).add(presence);
        } else if (onPath.add(parent)) {
          this.collectCycles(solver, owner, root, parent, solver.and(presence, this.presenceOf(solver, owner, parent)), onPath, cycles);
          onPath.remove(parent);
        }
      }
    }
  }

  /**
   * @return the super component, or, if it is an inner component type of the
   * owner, all inner component types of the owner with its name
   */
  protected List<ComponentTypeSymbol> alternativesOf(@NotNull Optional<ComponentTypeSymbol> owner,
                                                     @NotNull CompKindExpression superComponent) {
    ComponentTypeSymbol type = this.unwrap(superComponent.getTypeInfo());
    if (owner.isPresent() && this.isInnerTypeOf(owner.get(), type)) {
      return this.withinComponentResolver.resolveMany(owner.get(), type.getName(), ComponentTypeSymbol.class).stream()
        .filter(alternative -> this.isInnerTypeOf(owner.get(), alternative))
        .toList();
    }
    return List.of(type);
  }

  protected BoolExpr presenceOf(@NotNull FormulaSolver solver,
                                @NotNull Optional<ComponentTypeSymbol> owner,
                                @NotNull ComponentTypeSymbol type) {
    if (owner.isPresent() && this.isInnerTypeOf(owner.get(), type)) {
      return this.presenceResolver.presenceOf(owner.get(), type);
    }
    return solver.top();
  }

  protected boolean isInnerTypeOf(@NotNull ComponentTypeSymbol owner, @NotNull ComponentTypeSymbol type) {
    return type.getEnclosingScope() == owner.getSpannedScope();
  }

  protected ComponentTypeSymbol unwrap(@NotNull ComponentTypeSymbol type) {
    return type instanceof ComponentTypeSymbolSurrogate surrogate ? surrogate.lazyLoadDelegate() : type;
  }
}
