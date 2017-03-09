package de.montiarcautomaton.generator.codegen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import _templates.de.montiarcautomaton.lib.AbstractAtomicComponent;
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
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.ajava._parser.AJavaAntlrParser.BehaviorEmbedding_eofContext;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaLanguageFamily;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarcbehavior._ast.ASTBehaviorEmbedding;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.templateclassgenerator.util.GeneratorInterface;
import de.se_rwth.commons.Names;

/**
 * This class generates code for an maa model.
 * 
 * @author Gerrit Leonhardt
 */
public class MAAGenerator {
  
  protected static Scope createSymTab(Path... modelPaths) {
    ModelingLanguageFamily fam = new AJavaLanguageFamily();
    List<Path> mps = new ArrayList<>(Arrays.asList(modelPaths));
    mps.add(Paths.get("src/main/resources/defaultTypes"));
    final ModelPath mp = new ModelPath(mps);
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
   * Generates maa code for the given model.
   * 
   * @param simpleName the simple model name e.g. BumperControl
   * @param packageName the package name e.g. bumperbot
   * @param modelPath Path of models e.g. src/main/resources/models
   * @param fqnModelName full qualified name of model e.g.
   * /bumperbot/BumpControl.maa
   * @param targetPath Path where the models should be generated to e.g.
   * target/generated-source/
   * @param hwcPath
   */
  public static void generateModel(String simpleName, String packageName, String modelPath,
      String fqnModelName, String targetPath, File hwcPath) {
    
    Scope symTab = createSymTab(Paths.get(modelPath), Paths.get(getBasedirFromModelAndTargetPath(modelPath, targetPath)+"target/librarymodels/"));
    String model = packageName + "." + simpleName;
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(model, ComponentSymbol.KIND).get();
    
    final ComponentHelper compHelper = new ComponentHelper(comp);
    Path filePath;
    
    // gen component input
    String inputName = comp.getName() + "Input";
    filePath = getPath(targetPath, packageName, inputName);
    // pass all arguments instead of comp for better readability in the template
    ComponentInput.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(),
        comp.getImports(),
        comp.getName(), inputName, comp.getIncomingPorts());
    
    // gen component result
    String resultName = comp.getName() + "Result";
    filePath = getPath(targetPath, packageName, resultName);
    // pass all arguments instead of comp for better readability in the template
    ComponentResult.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(),
        comp.getImports(),
        comp.getName(), resultName, comp.getOutgoingPorts());
    
    // gen behavior implementations
    boolean existsHWC = false;
    String implName = comp.getName() + "Impl";
    
    existsHWC = TransformationHelper.existsHandwrittenClass(IterablePath.from(hwcPath, "java"),
        packageName + "." + implName);
    
    filePath = getPath(targetPath, packageName, implName);
    Collection<AutomatonSymbol> automatons = ScopeHelper
        .<AutomatonSymbol> resolveManyDown(comp.getSpannedScope(), AutomatonSymbol.KIND);
    if (automatons.size() > 1) {
      throw new RuntimeException("Only one automaton per component supported.");
    }
    
    ASTComponent compAST = (ASTComponent) comp.getAstNode().get();
    Optional<ASTBehaviorEmbedding> behaviorEmbedding = getBehaviorEmbedding(compAST);
    if (behaviorEmbedding.isPresent()) {
      for (Entry<Class<?>, GeneratorInterface> e : BehaviorGeneratorsMap.behaviorGenerators
          .entrySet()) {
        if (e.getKey().equals(behaviorEmbedding.get().getClass())) {
          e.getValue().generate(filePath, compAST, comp);
        }
      }
    }
    
    filePath = getPath(targetPath, packageName, comp.getName());

    // gen component
    if (comp.isAtomic()) {
      
      // default implementation
      if (!existsHWC) {
        Path implPath = getPath(targetPath, packageName, implName);
        AbstractAtomicComponent.generate(implPath, compAST, compHelper, packageName, implName,
            inputName, resultName, comp.getConfigParameters());
      }

      // pass all arguments instead of comp for better readability in the
      // template
      AtomicComponent.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(),
          comp.getImports(), comp.getName(),
          resultName, inputName, implName, comp.getIncomingPorts(), comp.getOutgoingPorts(),
          comp.getConfigParameters());
    }
    else {
      // pass all arguments instead of comp for better readability in the
      // template
      
      ComposedComponent.generate(filePath, comp.getAstNode().get(), compHelper,
          comp.getPackageName(), comp.getImports(), comp.getName(),
          comp.getIncomingPorts(), comp.getOutgoingPorts(), comp.getSubComponents(),
          comp.getConnectors());
    }
    
    // gen deploy
    if (compHelper.isDeploy()) {
      String deployName = "Deploy" + comp.getName();
      filePath = getPath(targetPath, packageName, deployName);
      Deploy.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(),
          comp.getName(), deployName);
    }
  }
  
  private static Optional<ASTBehaviorEmbedding> getBehaviorEmbedding(ASTComponent cmp) {
    List<ASTElement> elements = cmp.getBody().getElements();
    for (ASTElement e : elements) {
      if (e instanceof ASTBehaviorEmbedding) {
        return Optional.of((ASTBehaviorEmbedding) e);
      }
    }
    return Optional.empty();
  }
  
  /**
   * Compares the two paths and returns the common path. The common path is the
   * basedir.
   * 
   * @param modelPath
   * @param targetPath
   * @return
   */
  private static String getBasedirFromModelAndTargetPath(String modelPath, String targetPath) {
    String basedir = "";
    
    for (int i = 0; i < modelPath.length(); i++) {
      if (modelPath.charAt(i) == targetPath.charAt(i)) {
        basedir += modelPath.charAt(i);
      }
      else {
        break;
      }
      
    }
    return basedir;
  }
  
}
