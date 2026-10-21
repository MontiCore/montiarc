/* (c) https://github.com/MontiCore/monticore */
package variablearc.causality;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis.context.PortAccessCandidate;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Resolves the causal properties of component types, i.e., whether they are
 * delayed and which of their incoming ports immediately affect which of their
 * outgoing ports, as conditions in the vocabulary of the component type. The
 * conditions are derived from the presence of the behavior descriptions,
 * connectors, and subcomponents they depend on.
 */
public class CausalityResolver {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  /**
   * The effects by component type, incoming port, and outgoing port, which
   * are calculated once, unless they are cut off by a cyclic component
   * reference; the presence formulas must not change while they are cached
   */
  protected final Map<ComponentTypeSymbol, Map<PortSymbol, Map<PortSymbol, BoolExpr>>> effects = new IdentityHashMap<>();

  /**
   * The number of effects cut off by a cyclic component reference so far
   */
  protected int cuts = 0;

  public CausalityResolver(@NotNull WithinComponentResolver withinComponentResolver,
                           @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  /**
   * @return the condition under which the component type is delayed, i.e., a
   * delayed behavior description of it is present
   */
  public BoolExpr delayOf(@NotNull ComponentTypeSymbol type) {
    Preconditions.checkNotNull(type);
    return this.presenceResolver.delayOf(type);
  }

  /**
   * @param incoming an incoming port of the component type
   * @param outgoing an outgoing port of the component type
   * @return the condition under which the value of the incoming port
   * immediately affects the value of the outgoing port: both ports are present,
   * no delayed behavior description is present, and the component type is
   * atomic or the ports are connected via present connectors and
   * subcomponents whose ports affect each other
   */
  public BoolExpr effectOf(@NotNull FormulaSolver solver,
                           @NotNull ComponentTypeSymbol type,
                           @NotNull PortSymbol incoming,
                           @NotNull PortSymbol outgoing) {
    Preconditions.checkNotNull(solver);
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(incoming);
    Preconditions.checkNotNull(outgoing);
    return this.effectOf(solver, type, incoming, outgoing, new HashSet<>());
  }

  protected BoolExpr effectOf(@NotNull FormulaSolver solver,
                              @NotNull ComponentTypeSymbol type,
                              @NotNull PortSymbol incoming,
                              @NotNull PortSymbol outgoing,
                              @NotNull Set<String> visiting) {
    BoolExpr known = this.effects.getOrDefault(type, Map.of()).getOrDefault(incoming, Map.of()).get(outgoing);
    if (known != null) {
      return known;
    }
    if (!visiting.add(type.getFullName())) {
      this.cuts++;
      return solver.bottom();
    }
    int cutsBefore = this.cuts;
    BoolExpr effect = this.calculateEffectOf(solver, type, incoming, outgoing, visiting);
    if (this.cuts == cutsBefore) {
      this.effects.computeIfAbsent(type, t -> new IdentityHashMap<>())
        .computeIfAbsent(incoming, i -> new IdentityHashMap<>())
        .put(outgoing, effect);
    }
    return effect;
  }

  protected BoolExpr calculateEffectOf(@NotNull FormulaSolver solver,
                                       @NotNull ComponentTypeSymbol type,
                                       @NotNull PortSymbol incoming,
                                       @NotNull PortSymbol outgoing,
                                       @NotNull Set<String> visiting) {
    try {
      BoolExpr ports = solver.and(this.presenceResolver.presenceOf(type, incoming), this.presenceResolver.presenceOf(type, outgoing));
      BoolExpr undelayed = solver.not(this.delayOf(type));
      if (!type.isPresentAstNode() || !(type.getAstNode() instanceof ASTArcComponentType ast)) {
        return type.getEffectChains().containsEntry(incoming, outgoing) ? solver.and(ports, undelayed) : solver.bottom();
      }
      BoolExpr atomic = solver.not(solver.or(this.withinComponentResolver.getAll(type, SubcomponentSymbol.class).stream()
        .map(subcomponent -> this.presenceResolver.presenceOf(type, subcomponent))
        .toList()));
      return solver.and(ports, undelayed, solver.or(atomic, this.pathsOf(solver, type, ast, incoming, outgoing, visiting)));
    } finally {
      visiting.remove(type.getFullName());
    }
  }

  /**
   * @return the condition under which the ports of the component type are
   * connected via a path of present connectors and subcomponents whose ports
   * affect each other
   */
  protected BoolExpr pathsOf(@NotNull FormulaSolver solver,
                             @NotNull ComponentTypeSymbol type,
                             @NotNull ASTArcComponentType ast,
                             @NotNull PortSymbol incoming,
                             @NotNull PortSymbol outgoing,
                             @NotNull Set<String> visiting) {
    Map<PortAccessCandidate, Map<PortAccessCandidate, List<BoolExpr>>> edges = new LinkedHashMap<>();
    for (ASTConnector connector : VariableArcElements.of(ast, ASTConnector.class)) {
      BoolExpr presence = this.presenceResolver.presenceOf(type, connector);
      for (PortAccessCandidate source : this.withinComponentResolver.resolvePortAccess(type, connector.getSource())) {
        for (ASTPortAccess target : connector.getTargetList()) {
          for (PortAccessCandidate candidate : this.withinComponentResolver.resolvePortAccess(type, target)) {
            this.addEdge(edges, source, candidate,
              solver.and(presence, this.presenceResolver.presenceOf(type, source), this.presenceResolver.presenceOf(type, candidate)));
          }
        }
      }
    }
    for (SubcomponentSymbol subcomponent : this.withinComponentResolver.getAll(type, SubcomponentSymbol.class)) {
      if (!subcomponent.isTypePresent()) continue;
      ComponentTypeSymbol subType = subcomponent.getType().getTypeInfo();
      List<PortSymbol> ports = this.withinComponentResolver.getAll(subType, PortSymbol.class);
      for (PortSymbol in : ports.stream().filter(PortSymbol::isIncoming).toList()) {
        for (PortSymbol out : ports.stream().filter(PortSymbol::isOutgoing).toList()) {
          BoolExpr effect = this.effectOf(solver, subType, in, out, visiting);
          if (effect.isFalse()) continue;
          this.addEdge(edges,
            new PortAccessCandidate(Optional.of(subcomponent), in),
            new PortAccessCandidate(Optional.of(subcomponent), out),
            solver.and(this.presenceResolver.presenceOf(type, subcomponent), this.presenceResolver.lift(type, subcomponent, effect)));
        }
      }
    }
    PortAccessCandidate start = new PortAccessCandidate(Optional.empty(), incoming);
    PortAccessCandidate end = new PortAccessCandidate(Optional.empty(), outgoing);
    List<BoolExpr> paths = new ArrayList<>();
    Set<PortAccessCandidate> visited = new HashSet<>(Set.of(start));
    this.collectPaths(solver, edges, start, end, visited, new ArrayList<>(), paths);
    return solver.or(paths);
  }

  protected void collectPaths(@NotNull FormulaSolver solver,
                              @NotNull Map<PortAccessCandidate, Map<PortAccessCandidate, List<BoolExpr>>> edges,
                              @NotNull PortAccessCandidate current,
                              @NotNull PortAccessCandidate end,
                              @NotNull Set<PortAccessCandidate> visited,
                              @NotNull List<BoolExpr> path,
                              @NotNull List<BoolExpr> paths) {
    for (Map.Entry<PortAccessCandidate, List<BoolExpr>> edge : edges.getOrDefault(current, Map.of()).entrySet()) {
      path.add(solver.or(edge.getValue()));
      if (edge.getKey().equals(end)) {
        paths.add(solver.and(path));
      } else if (visited.add(edge.getKey())) {
        this.collectPaths(solver, edges, edge.getKey(), end, visited, path, paths);
        visited.remove(edge.getKey());
      }
      path.remove(path.size() - 1);
    }
  }

  protected void addEdge(@NotNull Map<PortAccessCandidate, Map<PortAccessCandidate, List<BoolExpr>>> edges,
                         @NotNull PortAccessCandidate source,
                         @NotNull PortAccessCandidate target,
                         @NotNull BoolExpr presence) {
    edges.computeIfAbsent(source, s -> new LinkedHashMap<>()).computeIfAbsent(target, t -> new ArrayList<>()).add(presence);
  }
}
