/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.FeedbackStrongCausality;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.causality.CausalityResolver;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks that no feedback loop without a strongly causal port is present in
 * any configuration. The loops are cycles in the graph of the connectors of
 * the component and the effects between the ports of its subcomponents; a
 * cycle is present in a configuration if all its edges are present.
 */
public class FeedbackStrongCausality4Family extends FeedbackStrongCausality {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;
  protected final CausalityResolver causalityResolver;

  public FeedbackStrongCausality4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                        @NotNull PresenceResolver presenceResolver,
                                        @NotNull CausalityResolver causalityResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
    this.causalityResolver = Preconditions.checkNotNull(causalityResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    Map<String, Map<String, List<BoolExpr>>> edges = new HashMap<>();
    this.addConnectorEdges(solver, component, node, edges);
    this.addEffectChainEdges(solver, component, edges);

    Multimap<String, String> graph = HashMultimap.create();
    edges.forEach((source, targets) -> graph.putAll(source, targets.keySet()));

    for (List<String> cycle : this.findAllCycles(graph)) {
      List<BoolExpr> presence = new ArrayList<>();
      for (int i = 0; i < cycle.size(); i++) {
        presence.add(solver.or(edges.get(cycle.get(i)).get(cycle.get((i + 1) % cycle.size()))));
      }
      cycle.add(cycle.getFirst());
      String message = ArcError.FEEDBACK_CAUSALITY + ": " + String.join(" -> ", cycle);
      switch (solver.check(solver.and(presence))) {
        case SATISFIABLE -> Log.error(message);
        case UNKNOWN -> Log.warn(message);
        case UNSATISFIABLE -> { }
      }
    }
  }

  protected void addConnectorEdges(@NotNull FormulaSolver solver,
                                   @NotNull ComponentTypeSymbol component,
                                   @NotNull ASTArcComponentType node,
                                   @NotNull Map<String, Map<String, List<BoolExpr>>> edges) {
    for (ASTConnector connector : VariableArcElements.of(node, ASTConnector.class)) {
      BoolExpr presence = this.presenceResolver.presenceOf(component, connector);
      for (ASTPortAccess target : connector.getTargetList()) {
        this.addEdge(edges, connector.getSource().getQName(), target.getQName(), presence);
      }
    }
  }

  protected void addEffectChainEdges(@NotNull FormulaSolver solver,
                                     @NotNull ComponentTypeSymbol component,
                                     @NotNull Map<String, Map<String, List<BoolExpr>>> edges) {
    for (SubcomponentSymbol subcomponent : component.getSubcomponents()) {
      if (!subcomponent.isTypePresent()) continue;
      ComponentTypeSymbol type = subcomponent.getType().getTypeInfo();
      List<PortSymbol> ports = this.withinComponentResolver.getAll(type, PortSymbol.class);
      for (PortSymbol incoming : ports.stream().filter(PortSymbol::isIncoming).toList()) {
        for (PortSymbol outgoing : ports.stream().filter(PortSymbol::isOutgoing).toList()) {
          BoolExpr effect = this.causalityResolver.effectOf(solver, type, incoming, outgoing);
          if (effect.isFalse()) continue;
          this.addEdge(edges,
            subcomponent.getName() + "." + incoming.getName(),
            subcomponent.getName() + "." + outgoing.getName(),
            solver.and(this.presenceResolver.presenceOf(component, subcomponent),
              this.presenceResolver.lift(component, subcomponent, effect)));
        }
      }
    }
  }

  protected void addEdge(@NotNull Map<String, Map<String, List<BoolExpr>>> edges,
                         @NotNull String source,
                         @NotNull String target,
                         @NotNull BoolExpr presence) {
    edges.computeIfAbsent(source, s -> new HashMap<>()).computeIfAbsent(target, t -> new ArrayList<>()).add(presence);
  }
}
