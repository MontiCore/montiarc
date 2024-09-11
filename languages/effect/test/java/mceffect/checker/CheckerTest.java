/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;

import org.junit.jupiter.api.Test;

public class CheckerTest extends AbstractCheckerTest {
  private final String main = "checker.Main";

  @Test
  public void testNoEffectChecker() {
    // Given
    String effect = "checker/NoEffect.eff";

    // When
    init(effect, main);

    // Then
    checkEffect(0, EffectCheckResult.Status.INCORRECT, true);
    checkEffect(1, EffectCheckResult.Status.CORRECT, false);
  }

  @Test
  public void testPotentialEffectChecker() {
    // Given
    String effect = "checker/PotentialEffect.eff";

    // When
    init(effect, main);

    // Then
    checkEffect(0, EffectCheckResult.Status.INCORRECT, false);
    checkEffect(1, EffectCheckResult.Status.CORRECT, true);
  }

  @Test
  public void testSemiMandatoryEffectChecker() {
    // Given
    String effect = "checker/SemiMandatoryEffect.eff";

    // When
    init(effect, main);

    // Then
    checkEffect(0, EffectCheckResult.Status.INCORRECT, false);
    checkEffect(1, EffectCheckResult.Status.CORRECT, true);
  }
}
