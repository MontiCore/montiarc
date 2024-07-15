/* (c) https://github.com/MontiCore/monticore */
package mceffect;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import mceffect._ast.ASTMCEffect;
import mceffect._parser.MCEffectParser;
import mceffect.checker.EffectCheckResult;
import mceffect.checker.EffectChecker;
import mceffect.checker.FullEffectChecker;
import mceffect.effect.Effect;
import mceffect.effect.SimpleEffectStorage;
import mceffect.effect.Utils;
import mceffect.graph.EffectGraph;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;

public class MCFullEffectChecker {

  public static Set<EffectCheckResult> checkMontiArcComponent(
      String mp, String main, Set<String> effects, boolean showGraph) {

    // init resolver
    Function<String, Optional<PortSymbol>> portResolver =
        s -> MontiArcMill.globalScope().resolvePort(s);
    Function<String, Optional<ComponentTypeSymbol>> compResolver =
        s -> MontiArcMill.globalScope().resolveComponentType(s);

    // parse components
    new MontiArcTool().run(new String[] {"-mp", mp});

    // parse and effect
    Set<ASTMCEffect> effectSet = new HashSet<>();
    effects.forEach(eff -> effectSet.add(parseEffect(mp + eff)));
    SimpleEffectStorage storage = new SimpleEffectStorage(effectSet, compResolver, portResolver);

    // resolve main component

    ComponentTypeSymbol mainComp = compResolver.apply(main).orElse(null);
    if (mainComp == null) {
      Log.error("Unable to resolve the main component " + main);
      assert false;
    }

    // build an effect checker
    EffectGraph graph = new EffectGraph(mainComp, storage);
    EffectChecker checker = new FullEffectChecker(graph);

    // check all effects
    Set<EffectCheckResult> resSet = new HashSet<>();
    for (Effect effect : storage.getEffectsOfComponent(mainComp)) {
      EffectCheckResult res = checker.check(effect);
      resSet.add(res);
      Log.info(res.printDescription(), MCFullEffectChecker.class.getSimpleName());
      System.out.println();
    }

    if (showGraph) {
      Utils.visualize(graph.getGraph());
    }
    return resSet;
  }

  public static Set<EffectCheckResult> checkSysMLComponent(
      String mp, String main, Set<String> effects, boolean showGraph) {
    Log.error("Checker for SysML Component not implemented yet.");
    assert false;
    return new HashSet<>();
  }

  public static ASTMCEffect parseEffect(String path) {
    Optional<ASTMCEffect> ast = Optional.empty();
    try {
      ast = new MCEffectParser().parse(path);
    } catch (IOException e) {
      Log.error("Unable to parse the effect file.\n" + e.getMessage());
      assert false;
    }
     assert  ast.isPresent();
    return ast.get();
  }

  public void initMills() {}
}
