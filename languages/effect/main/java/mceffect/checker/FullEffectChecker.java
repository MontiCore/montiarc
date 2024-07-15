/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;


import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import mceffect.effect.Effect;
import mceffect.graph.EffectGraph;

public class FullEffectChecker implements EffectChecker {

  private final List<EffectChecker> checkerList = new ArrayList<>();

  public FullEffectChecker(EffectGraph graph) {
    checkerList.add(new NoEffectChecker(graph));
    checkerList.add(new PotentialEffectChecker(graph));
    checkerList.add(new SemiMandatoryEffectChecker(graph));
    checkerList.add(new BasicEffectChecker());
  }

  @Override
  public EffectCheckResult check(Effect effect) {
    EffectCheckResult res = null;
    for (EffectChecker checker : checkerList) {
      res = checker.check(effect);
      Log.info(
          "checking effect rule '"
              + effect
              + "'"
              + " of "
              + effect.getComponent().getName()
              + " with the "
              + checker.getClass().getSimpleName()
              + "......"
              + res.getStatus().name(),
          this.getClass().getSimpleName());

      if (!(res.isNotApplicable() || res.isUnknown())) {
        return res;
      }
    }
    String description = "The currently implemented effect checkers were unable to produce results";

    if (res == null) {
      return new EffectCheckResult(effect, EffectCheckResult.Status.UNKNOWN, description);
    } else {
      return res;
    }
  }

  @Override
  public boolean isApplicable(Effect effect) {
    return true;
  }
}
