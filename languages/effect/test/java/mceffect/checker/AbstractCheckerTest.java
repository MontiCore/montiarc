/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;

import arcbasis._symboltable.ComponentTypeSymbol;
import mceffect.EffectAbstractTest;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import mceffect.MCEffectTool;
import mceffect._ast.ASTMCEffect;
import mceffect.effect.Effect;
import mceffect.effect.EffectStorage;
import mceffect.effect.SimpleEffectStorage;
import mceffect.graph.EffectGraph;
import montiarc.MontiArcTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractCheckerTest extends EffectAbstractTest {

  private EffectChecker checker;
  private EffectStorage storage;

  private ComponentTypeSymbol mainComp;

  @BeforeEach
  public void setup() {
    MCEffectTool.initMills();
  }

  public void init(String effectFile, String mainComp) {
    new MontiArcTool().run(new String[] {"-i", modelPath + "demo2/"});
    ASTMCEffect effect = parseEffect(modelPath + "/demo2/" + effectFile);

    storage = new SimpleEffectStorage(effect, compResolver, portResolver);
    this.mainComp = compResolver.apply(mainComp).orElse(null);

    Assertions.assertNotNull(this.mainComp);

    EffectGraph graph = new EffectGraph(this.mainComp, storage);
    checker = new FullEffectChecker(graph);
  }

  public void checkEffect(int position, EffectCheckResult.Status result, boolean trace) {
    List<Effect> mainEffects = storage.getEffectsOfComponent(mainComp);
    EffectCheckResult res = checker.check(mainEffects.get(position));
    Assertions.assertEquals(result, res.getStatus());
    Assertions.assertEquals(res.isPresentTrace(), trace);
    Log.info(res.printDescription(), this.getClass().getSimpleName());
  }
}
