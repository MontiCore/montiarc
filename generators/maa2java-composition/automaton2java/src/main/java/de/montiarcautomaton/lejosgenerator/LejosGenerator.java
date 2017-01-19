package de.montiarcautomaton.lejosgenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import _templates.mc.montiarcautomaton.lejos.lib.AutomatonImplMain;
import de.montiarcautomaton.lejosgenerator.helper.AutomatonHelper;
import de.montiarcautomaton.lejosgenerator.helper.ComponentHelper;
import de.monticore.ModelingLanguageFamily;
import de.monticore.automaton.ioautomaton.JavaHelper;
import de.monticore.automaton.ioautomaton.ScopeHelper;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarcautomaton._symboltable.MontiArcAutomatonLanguageFamily;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;

/**
 * This class generates code for the lejos platform for an maa model.
 * 
 * @author Gerrit Leonhardt
 */
public class LejosGenerator {
  
  protected static Scope createSymTab(String modelPath) {
    ModelingLanguageFamily fam = new MontiArcAutomatonLanguageFamily();
    final ModelPath mp = new ModelPath(Paths.get(modelPath), Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
  
  /**
   * Computes the target path of the generated java file.
   * @param targetPath the path of the target folder
   * @param packageName the package name of the model
   * @param name the model name
   * @return
   */
  private static Path getPath(String targetPath, String packageName, String name) {
    return Paths.get(targetPath, Names.getPathFromPackage(packageName), name + ".java");
  }
  
  /**
   * Generates lejos code for the given model.
   * 
   * @param simpleName the simple model name e.g. BumperControl
   * @param packageName the package name e.g.
   *          bumperbot
   * @param modelPath Path of models e.g. src/main/resources/models
   * @param fqnModelName full qualified name of model e.g.
   *          /bumperbot/BumpControl.maa
   * @param targetPath Path where the models should be generated to
   *          e.g. target/generated-source/
   */
  public static void generateModel(String simpleName, String packageName, String modelPath, String fqnModelName, String targetPath) {
    Scope symTab = createSymTab(modelPath);
    String model = packageName + "." + simpleName;
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(model, ComponentSymbol.KIND).get();
    
    Path filePath;
    
    String resultName = comp.getName() + "Result";
    String inputName = comp.getName() + "Input";
    
    // gen behavior implementations
    String implName = comp.getName() + "Impl";
    Collection<AutomatonSymbol> automatons = ScopeHelper.<AutomatonSymbol> resolveManyDown(comp.getSpannedScope(), AutomatonSymbol.KIND);
    if (automatons.size() > 1) {
      throw new RuntimeException("Only one automaton per component supported.");
    }
    for (AutomatonSymbol automaton : automatons) {
      AutomatonHelper helper = new AutomatonHelper(automaton, comp);
      filePath = getPath(targetPath, packageName, implName);
      // pass all arguments instead of comp for better readability in the template
      AutomatonImplMain.generate(filePath, automaton.getAstNode().get(), helper, comp.getPackageName(), comp.getImports(), 
          comp.getName(), resultName, inputName, implName, comp.getIncomingPorts(), helper.getVariables(), helper.getStates(), comp.getConfigParameters());
    }
  }
  
}
