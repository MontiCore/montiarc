/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;

import mceffect.effect.Effect;

public interface EffectChecker {
  EffectCheckResult check(Effect effect);

  boolean isApplicable(Effect effect);
}
