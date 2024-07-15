/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;

import mceffect.effect.Effect;
import mceffect.effect.EffectKind;
import mceffect.graph.EffectEdge;
import mceffect.graph.EffectGraph;
import mceffect.graph.EffectNode;
import org.jgrapht.GraphPath;

public class NoEffectChecker implements EffectChecker {
  private final EffectGraph graph;

  public NoEffectChecker(EffectGraph graph) {
    this.graph = graph;
  }

  @Override
  public EffectCheckResult check(Effect effect) {
    String description = "";
    if (!isApplicable(effect)) {
      return new EffectCheckResult(effect, EffectCheckResult.Status.NOT_APPLICABLE, description);
    }

    for (GraphPath<EffectNode, EffectEdge> path :
        graph.getAllPath(effect.getComponent(), effect.getFrom(), effect.getTo())) {

      if (path.getEdgeList().stream().noneMatch(EffectEdge::hasNoEffect)) {
        description =
            "There is a path between the source and the target ports without no effect transitions";
        return new EffectCheckResult(effect, EffectCheckResult.Status.INCORRECT, path, description);
      }
    }
    description =
        "There is no path between the source and target ports without 'no effect' transitions";
    return new EffectCheckResult(effect, EffectCheckResult.Status.CORRECT, description);
  }

  @Override
  public boolean isApplicable(Effect effect) {
    return effect.getEffectKind() == EffectKind.NO && effect.getComponent().isDecomposed();
  }
}
