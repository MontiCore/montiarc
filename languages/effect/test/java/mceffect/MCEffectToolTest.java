/* (c) https://github.com/MontiCore/monticore */
package mceffect;

import org.junit.jupiter.api.Test;

public class MCEffectToolTest extends EffectAbstractTest {
  @Test
  public void TestMCEffectTool() {
    //Given
    String[] args =
        new String[] {
          "-mp", modelPath + "checker/", "-mc", "mceffect.checker.B", "-e", "BasicEffect.eff"
        };

    // When & Then
    MCEffectTool.main(args);
  }

  @Test
  public void TestToolDemo() {
    // Given
    String[] args =
        new String[] {
          "-mp",
          "test/resources/mceffect/demo/steamboiler/",
          "-mc",
          "demo.steamboiler.SteamBoiler",
          "-e",
          "SteamBoiler.eff",
        };

    // When & Then
    MCEffectTool.main(args);
  }
}
