/* (c) https://github.com/MontiCore/monticore */
package mceffect.graph;

import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import mceffect.EffectTestBase;
import mceffect.MCEffectTool;
import mceffect._ast.ASTMCEffect;
import mceffect.effect.EffectStorage;
import mceffect.effect.SimpleEffectStorage;
import montiarc.MontiArcTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@Disabled // TODO: Causes System.exit calls / ignores Log setup
public class EffectGraphTest extends EffectTestBase {

  @BeforeEach
  public void setup() {
    MCEffectTool.initMills();
    new MontiArcTool().run(new String[] {"-i", modelPath + "demo1/"});
  }

  @Test
  public void testEffectGraph() {
    // Given
    ASTMCEffect effect = parseEffect(modelPath  + "demo1/" + "steamboiler/SteamBoiler.eff");
    Optional<ComponentTypeSymbol> compSymbol =
        compResolver.apply("steamboiler.SteamBoiler");
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
