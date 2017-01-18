package de.montiarcautomaton.generator.codegen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map.Entry;

import _templates.de.montiarcautomaton.lib.AtomicComponent;
import _templates.de.montiarcautomaton.lib.ComponentInput;
import _templates.de.montiarcautomaton.lib.ComponentResult;
import _templates.de.montiarcautomaton.lib.ComposedComponent;
import _templates.de.montiarcautomaton.lib.Deploy;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.montiarcautomaton.generator.util.BehaviorGeneratorsMap;
import de.monticore.ModelingLanguageFamily;
import de.monticore.automaton.ioautomaton.JavaHelper;
import de.monticore.automaton.ioautomaton.ScopeHelper;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarcautomaton._symboltable.MontiArcAutomatonLanguageFamily;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.templateclassgenerator.util.GeneratorInterface;
import de.se_rwth.commons.Names;

/**
 * This class generates code for an maa model.
 * 
 * @author Gerrit Leonhardt
 */
public class MAAGenerator {
  
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
    
    final ComponentHelper compHelper = new ComponentHelper(comp);
    Path filePath;
    
    
    // gen component input
    String inputName = comp.getName() + "Input";
    filePath = getPath(targetPath, packageName, inputName);
    // pass all arguments instead of comp for better readability in the template
    ComponentInput.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(), comp.getImports(),
        comp.getName(), inputName, comp.getIncomingPorts());
    
    // gen component result
    String resultName = comp.getName() + "Result";
    filePath = getPath(targetPath, packageName, resultName);
    // pass all arguments instead of comp for better readability in the template
    ComponentResult.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(), comp.getImports(),
        comp.getName(), resultName, comp.getOutgoingPorts());

    // gen behavior implementations
    String implName = comp.getName() + "Impl";
    Collection<AutomatonSymbol> automatons = ScopeHelper.<AutomatonSymbol> resolveManyDown(comp.getSpannedScope(), AutomatonSymbol.KIND);
    if (automatons.size() > 1) {
      throw new RuntimeException("Only one automaton per component supported.");
    }
    
    for(Entry<SymbolKind, GeneratorInterface> e : BehaviorGeneratorsMap.behaviorGenerators.entrySet()) {
      Collection<CommonSymbol> behaviorSymbol = comp.getSpannedScope().resolveLocally(e.getKey());
      if(!behaviorSymbol.isEmpty()) {
        e.getValue().generate(filePath, comp.getAstNode().get(), comp);
      }
    }
    
    // gen component
    filePath = getPath(targetPath, packageName, comp.getName());
    if (comp.isAtomic()) {
      // pass all arguments instead of comp for better readability in the template
      AtomicComponent.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(), comp.getImports(), comp.getName(), 
          resultName, inputName, implName, comp.getIncomingPorts(), comp.getOutgoingPorts(), comp.getConfigParameters());
    }
    else {
      // pass all arguments instead of comp for better readability in the template
      ComposedComponent.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(), comp.getImports(), comp.getName(), 
          comp.getIncomingPorts(), comp.getOutgoingPorts(), comp.getSubComponents(), comp.getConnectors());
    }
    
    // gen deploy
    if (compHelper.isDeploy()) {
      String deployName = "Deploy" + comp.getName();
      filePath = getPath(targetPath, packageName, deployName);
      Deploy.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(), comp.getName(), deployName);
    }
  }
  
}
