/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;


import mceffect.effect.Effect;
import mceffect.graph.EffectEdge;
import mceffect.graph.EffectNode;
import org.jgrapht.GraphPath;

public class EffectCheckResult {
  private final String description;
  private final Status status;
  private final Effect effect;
  private GraphPath<EffectNode, EffectEdge> graphTrace = null;

  public EffectCheckResult(
          Effect effect, Status status, GraphPath<EffectNode, EffectEdge> trace, String description) {
    this.status = status;
    this.graphTrace = trace;
    this.effect = effect;
    this.description = description;
  }

  public EffectCheckResult(Effect effect, Status status, String description) {
    this.status = status;
    this.effect = effect;
    this.description = description;
  }

  public Status getStatus() {
    return status;
  }

  public boolean isUnknown() {
    return status == Status.UNKNOWN;
  }

  public boolean isNotApplicable() {
    return status == Status.NOT_APPLICABLE;
  }

  public boolean isPresentTrace() {
    return graphTrace != null;
  }

  public String printDescription() {

    return description + (isPresentTrace() ? printTrace() : "");
  }

  private String printTrace() {

    StringBuilder res = new StringBuilder();
    for (EffectEdge edge : graphTrace.getEdgeList()) {
      res.append(edge.getSource()).append(" ");
      if (edge.isConnectorEdge()) {
        res.append("-> ");
      } else if (edge.hasNoEffect()) {
        res.append("(no effect) ");
      } else if (edge.hasMandatoryEffect()) {
        res.append("(mandatory effect) ");
      } else if (edge.hasPotentialEffect()) {
        res.append("(potential effect) ");
      }
    }
    res.append(graphTrace.getEndVertex());
    return "trace: " + res;
  }

  public enum Status {
    CORRECT,
    INCORRECT,
    UNKNOWN,
    NOT_APPLICABLE
  }
}
