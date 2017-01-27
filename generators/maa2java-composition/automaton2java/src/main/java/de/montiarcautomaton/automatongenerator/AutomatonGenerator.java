package de.montiarcautomaton.automatongenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import _templates.mc.montiarcautomaton.automaton.lib.AutomatonImplMain;
import de.montiarcautomaton.automatongenerator.helper.AutomatonHelper;
import de.monticore.ModelingLanguageFamily;
import de.monticore.ast.ASTNode;
import de.monticore.automaton.ioautomaton.JavaHelper;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarcautomaton._symboltable.MontiArcAutomatonLanguageFamily;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;

/**
 * This class generates code for the lejos platform for an maa model.
 * 
 * @author Gerrit Leonhardt
 */
public class AutomatonGenerator {
  
  protected static Scope createSymTab(String modelPath) {
    ModelingLanguageFamily fam = new MontiArcAutomatonLanguageFamily();
    final ModelPath mp = new ModelPath(Paths.get(modelPath),
        Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
  
  /**
   * Computes the target path of the generated java file.
   * 
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
   * @param packageName the package name e.g. bumperbot
   * @param modelPath Path of models e.g. src/main/resources/models
   * @param fqnModelName full qualified name of model e.g.
   * /bumperbot/BumpControl.maa
   * @param targetPath Path where the models should be generated to e.g.
   * target/generated-source/
   */
  public static void generateModel(String simpleName, String packageName, String modelPath,
      String fqnModelName, String targetPath) {
    Scope symTab = createSymTab(modelPath);
    String model = packageName + "." + simpleName;
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(model, ComponentSymbol.KIND).get();
    String implName = comp.getName() + "Impl";
    Path filePath = getPath(targetPath, packageName, implName);
    
    doGenerate(filePath, comp.getAstNode().get(), comp);
  }
  
  public static void doGenerate(Path filepath, ASTNode node, CommonSymbol symbol) {
    if (symbol.getKind().isKindOf(ComponentSymbol.KIND)) {
      ComponentSymbol comp = (ComponentSymbol) symbol;
      Collection<AutomatonSymbol> ajava = comp.getSpannedScope()
          .<AutomatonSymbol> resolveLocally(AutomatonSymbol.KIND);
      AutomatonSymbol automaton = ajava.iterator().next();
      
      String inputName = comp.getName() + "Input";
      String resultName = comp.getName() + "Result";
      String implName = comp.getName() + "Impl";
      AutomatonHelper helper = new AutomatonHelper(automaton, comp);
      
      AutomatonImplMain.generate(filepath, node, helper, comp.getPackageName(), comp.getImports(),
          comp.getName(),
          resultName, inputName, implName, comp.getIncomingPorts(), helper.getVariables(),
          helper.getStates(), comp.getConfigParameters());
    }
    
  }
  
}
