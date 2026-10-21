/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.NoComponentReferenceCycle;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Checks that no subcomponent closes a reference cycle in some configuration
 * of the component type, i.e., that the subcomponents along the cycle are not
 * present together. Their presence is lifted along the cycle into the
 * vocabulary of the component type.
 */
public class NoComponentReferenceCycle4Family extends NoComponentReferenceCycle {

  protected final PresenceResolver presenceResolver;

  public NoComponentReferenceCycle4Family(@NotNull PresenceResolver presenceResolver) {
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  /**
   * @param types        the component types of the cycle, starting and
   *                     ending with the checked component type
   * @param satisfiable whether the cycle is present in some configuration,
   *                     otherwise its presence cannot be decided
   */
  protected record Cycle(@NotNull List<ComponentTypeSymbol> types, boolean satisfiable) { }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    for (SubcomponentSymbol subcomponent : component.getSubcomponents()) {
      if (!subcomponent.isTypePresent()) continue;

      Optional<Cycle> cycle = this.findCycle(solver, component, List.of(subcomponent),
        this.presenceResolver.presenceOf(component, subcomponent));
      if (cycle.isEmpty()) continue;

      String message = ArcError.COMPONENT_REFERENCE_CYCLE.format(node.getName(), printCycle(cycle.get().types()));
      if (cycle.get().satisfiable()) {
        Log.error(message, subcomponent.getAstNode().get_SourcePositionStart(), subcomponent.getAstNode().get_SourcePositionEnd());
      } else {
        Log.warn(message, subcomponent.getAstNode().get_SourcePositionStart(), subcomponent.getAstNode().get_SourcePositionEnd());
      }
    }
  }

  /**
   * @param path     the subcomponents from the component type to the
   *                 currently instantiated component type
   * @param presence the presence of the subcomponents of the path in the
   *                 vocabulary of the component type
   * @return a cycle through the path back to the component type that is
   * present in some configuration, otherwise one whose presence cannot be
   * decided, if any
   */
  protected Optional<Cycle> findCycle(@NotNull FormulaSolver solver,
                                      @NotNull ComponentTypeSymbol component,
                                      @NotNull List<SubcomponentSymbol> path,
                                      @NotNull BoolExpr presence) {
    FormulaSolver.Satisfiability satisfiability = solver.check(presence);
    if (satisfiability == FormulaSolver.Satisfiability.UNSATISFIABLE) return Optional.empty();

    List<ComponentTypeSymbol> types = this.typesOf(path);
    ComponentTypeSymbol type = types.get(types.size() - 1);
    if (this.isSame(type, component)) {
      List<ComponentTypeSymbol> cycle = new ArrayList<>();
      cycle.add(component);
      for (int i = types.size() - 2; i >= 0; i--) {
        cycle.add(types.get(i));
      }
      cycle.add(component);
      return Optional.of(new Cycle(cycle, satisfiability == FormulaSolver.Satisfiability.SATISFIABLE));
    }
    if (types.subList(0, types.size() - 1).stream().anyMatch(visited -> this.isSame(visited, type))) {
      return Optional.empty();
    }

    Optional<Cycle> undecided = Optional.empty();
    for (SubcomponentSymbol next : type.getSubcomponents()) {
      if (!next.isTypePresent()) continue;
      List<SubcomponentSymbol> extended = new ArrayList<>(path);
      extended.add(next);
      BoolExpr extendedPresence = solver.and(presence,
        this.liftAlong(component, path, this.presenceResolver.presenceOf(type, next)));
      Optional<Cycle> cycle = this.findCycle(solver, component, extended, extendedPresence);
      if (cycle.isPresent() && cycle.get().satisfiable()) return cycle;
      if (undecided.isEmpty()) {
        undecided = cycle;
      }
    }
    return undecided;
  }

  /**
   * @return the formula in the vocabulary of the type instantiated by the
   * last subcomponent of the path lifted into the vocabulary of the component
   * type
   */
  protected BoolExpr liftAlong(@NotNull ComponentTypeSymbol component,
                               @NotNull List<SubcomponentSymbol> path,
                               @NotNull BoolExpr formula) {
    List<ComponentTypeSymbol> types = this.typesOf(path);
    BoolExpr lifted = formula;
    for (int i = path.size() - 1; i >= 0; i--) {
      lifted = this.presenceResolver.lift(i == 0 ? component : types.get(i - 1), path.get(i), lifted);
    }
    return lifted;
  }

  protected List<ComponentTypeSymbol> typesOf(@NotNull List<SubcomponentSymbol> path) {
    return path.stream().map(subcomponent -> subcomponent.getType().getTypeInfo()).toList();
  }

  protected boolean isSame(@NotNull ComponentTypeSymbol type, @NotNull ComponentTypeSymbol other) {
    return type.getFullName().equals(other.getFullName());
  }
}
