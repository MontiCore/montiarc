/* (c) https://github.com/MontiCore/monticore */
package mceffect.graph;

import arcbasis._symboltable.ComponentTypeSymbol;
import mceffect.EffectAbstractTest;
import java.util.Optional;
import mceffect.MCEffectTool;
import mceffect._ast.ASTMCEffect;
import mceffect.effect.EffectStorage;
import mceffect.effect.SimpleEffectStorage;
import montiarc.MontiArcTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EffectGraphTest extends EffectAbstractTest {

  @BeforeEach
  public void setup() {
    MCEffectTool.initMills();
    new MontiArcTool().run(new String[] {"-mp", modelPath});
  }

  @Test
  public void testEffectGraph() {
    // Given
    ASTMCEffect effect = parseEffect(modelPath + "steamboiler/SteamBoiler.eff");
    Optional<ComponentTypeSymbol> compSymbol =
        compResolver.apply("mceffect.steamboiler.SteamBoiler");
    Assertions.assertTrue(compSymbol.isPresent());
    Assertions.assertNotNull(effect);

    // When
    EffectStorage effectStorage = new SimpleEffectStorage(effect, compResolver, portResolver);
    EffectGraph graph = new EffectGraph(compSymbol.get(), effectStorage);

    // Then
    Assertions.assertEquals(13, graph.graph.vertexSet().size());
    Assertions.assertEquals(10, graph.graph.edgeSet().size());
  }
}
