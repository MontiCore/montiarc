/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;

import mceffect.effect.Effect;
import mceffect.effect.EffectKind;
import mceffect.graph.EffectEdge;
import mceffect.graph.EffectGraph;
import mceffect.graph.EffectNode;
import org.jgrapht.GraphPath;

public class PotentialEffectChecker implements EffectChecker {
  private final EffectGraph graph;

  public PotentialEffectChecker(EffectGraph graph) {
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
            "There is a path between the source and the target port without 'no effect' transitions.\n";
        return new EffectCheckResult(effect, EffectCheckResult.Status.CORRECT, path, description);
      }
    }
    description =
        "There is no path between the source and the target port without 'no effect' transitions.\n";
    return new EffectCheckResult(effect, EffectCheckResult.Status.INCORRECT, description);
  }

  @Override
  public boolean isApplicable(Effect effect) {
    return effect.getEffectKind() == EffectKind.POTENTIAL && effect.getComponent().isDecomposed();
  }
}
