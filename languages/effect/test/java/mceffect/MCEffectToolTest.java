/* (c) https://github.com/MontiCore/monticore */
package mceffect;

import org.junit.jupiter.api.Test;

public class MCEffectToolTest extends EffectAbstractTest {

  @Test
  public void TestMCEffectTool() {
    //Given
    String[] args =
        new String[] {
          "-mp", modelPath + "demo2/", "-mc", "checker.B", "-e", "checker/BasicEffect.eff"
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
          "test/resources/mceffect/demo1/",
          "-mc",
          "steamboiler.SteamBoiler",
          "-e",
          "steamboiler/SteamBoiler.eff",
        };

    // When & Then
    MCEffectTool.main(args);
  }
}
