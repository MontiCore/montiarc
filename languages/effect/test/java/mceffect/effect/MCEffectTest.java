/* (c) https://github.com/MontiCore/monticore */
package mceffect.effect;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.tagging.ISymbolTagger;
import mceffect.EffectAbstractTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import de.monticore.tagging.SimpleSymbolTagger;
import de.monticore.tagging.TagRepository;
import mceffect.MCEffectTool;
import mceffect._ast.ASTMCEffect;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MCEffectTest extends EffectAbstractTest {

  @BeforeEach
  public void setup() throws IOException {
    // Do this at the beginning to ensure Mills do not clash
    var tagUnit = TagRepository.loadTagModel(Path.of(modelPath, "steamboiler","SteamBoiler.tag").toFile());
    Assertions.assertTrue(tagUnit.isPresent());

    MCEffectTool.initMills();
    new MontiArcTool().run(new String[] {"-mp", modelPath});
  }

  @Test
  public void montiArcEffectStorageTest() {
    // Given
    String effectFile = "steamboiler/SteamBoiler.eff";
    ASTMCEffect effect = parseEffect(modelPath + effectFile);

    // When
    SimpleEffectStorage storage = new SimpleEffectStorage(effect, compResolver, portResolver);

    // Then
    Assertions.assertEquals(4, storage.effectMap.size());
    checkEffects(storage);
  }

  @Test
  @Disabled("Tagging is problematic, the Tags are not found...")
  public void taggingEffectStorageTest() {
    // See the setup() for Loading the Tags
    PortSymbol waterIn = MontiArcMill.globalScope().resolvePort("mceffect.steamboiler.WaterTank.waterIn").get();
    ISymbolTagger tagger = new SimpleSymbolTagger(TagRepository::getLoadedTagUnits);
    Assertions.assertFalse(tagger.getTags(waterIn).isEmpty());

    // Todo: Enable this, and implement the ToDo in TaggingEffectStorage
    //TaggingEffectStorage storage = new TaggingEffectStorage(tagger);
    // checkEffects(storage);
  }

  private void checkEffects(EffectStorage storage) {
    // Given
    Optional<ComponentTypeSymbol> wTank = compResolver.apply("mceffect.steamboiler.WaterTank");
    Assertions.assertTrue(wTank.isPresent());

    // When
    List<Effect> wTankEffects = storage.getEffectsOfComponent(wTank.get());
    Assertions.assertEquals(1, wTankEffects.size());
    Effect e = wTankEffects.get(0);

    // Then
    Assertions.assertEquals("WaterTank", e.getComponent().getName());
    Assertions.assertEquals(EffectKind.MANDATORY, e.getEffectKind());
    Assertions.assertEquals("waterIn", e.getFrom().getName());
    Assertions.assertEquals("waterOut", e.getTo().getName());
    Assertions.assertTrue(e.isCheck());
    Assertions.assertFalse(e.isEnsure());
    Assertions.assertEquals("check mandatory effect: waterIn => waterOut;", e.toString());
  }
}
