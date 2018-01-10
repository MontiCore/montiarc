package de.montiarcautomaton.generator.codegen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.io.paths.IterablePath;
import de.monticore.templateclassgenerator.util.GeneratorInterface;
import de.se_rwth.commons.Names;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._symboltable.AutomatonSymbol;
import montiarc._symboltable.ComponentSymbol;

/**
 * This class generates code for an maa model.
 * 
 * @author Andreas Wortmann, Jerome Pfeiffer, Gerrit Leonhardt
 */
public class MAAGenerator {
   
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
  public static void generateModel(File targetPath, File hwcPath, ComponentSymbol compSym) {
    
    ComponentSymbol comp = compSym;
    
    String packageName = comp.getPackageName();
    String targetPathName = targetPath.getAbsolutePath();
    
    final ComponentHelper compHelper = new ComponentHelper(comp);
    Path filePath;
    
    // gen component input
    String inputName = comp.getName() + "Input";
    filePath = getPath(targetPathName, packageName, inputName);
    // pass all arguments instead of comp for better readability in the template
    ComponentInput.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(),
        comp.getImports(),
        comp.getName(), inputName, comp.getIncomingPorts());
    
    // gen component result
    String resultName = comp.getName() + "Result";
    filePath = getPath(targetPathName, packageName, resultName);
    // pass all arguments instead of comp for better readability in the template
    ComponentResult.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(),
        comp.getImports(),
        comp.getName(), resultName, comp.getOutgoingPorts());
    
    // gen behavior implementations
    boolean existsHWC = false;
    String implName = comp.getName() + "Impl";
    
    existsHWC = TransformationHelper.existsHandwrittenClass(IterablePath.from(hwcPath, "java"),
        packageName + "." + implName);
    
    filePath = getPath(targetPathName, packageName, implName);
    Collection<AutomatonSymbol> automatons = comp.getSpannedScope().resolveLocally(AutomatonSymbol.KIND);
    if (automatons.size() > 1) {
      throw new RuntimeException("Only one automaton per component supported.");
    }
    
    ASTComponent compAST = (ASTComponent) comp.getAstNode().get();
    Optional<ASTBehaviorElement> behaviorEmbedding = getBehaviorEmbedding(compAST);
    if (behaviorEmbedding.isPresent()) {
      for (Entry<Class<?>, GeneratorInterface> e : BehaviorGeneratorsMap.behaviorGenerators
          .entrySet()) {
        if (e.getKey().equals(behaviorEmbedding.get().getClass())) {
          e.getValue().generate(filePath, compAST, comp);
        }
      }
    }
    
    filePath = getPath(targetPathName, packageName, comp.getName());
    
    // gen component
    if (comp.isAtomic()) {
      
      // default implementation
      if (!existsHWC && !behaviorEmbedding.isPresent()) {
        Path implPath = getPath(targetPathName, packageName, implName);
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
      
      ComposedComponent.generate(filePath, comp.getAstNode().get(), compHelper, comp,
          comp.getPackageName(), comp.getImports(), comp.getName(),
          comp.getIncomingPorts(), comp.getOutgoingPorts(), comp.getSubComponents(),
          comp.getConnectors());
    }
    
    // gen deploy
    if (compHelper.isDeploy()) {
      String deployName = "Deploy" + comp.getName();
      filePath = getPath(targetPathName, packageName, deployName);
      Deploy.generate(filePath, comp.getAstNode().get(), compHelper, comp.getPackageName(),
          comp.getName(), deployName);
    }
  }
  
  private static Optional<ASTBehaviorElement> getBehaviorEmbedding(ASTComponent cmp) {
    List<ASTElement> elements = cmp.getBody().getElements();
    for (ASTElement e : elements) {
      if (e instanceof ASTBehaviorElement) {
        return Optional.of((ASTBehaviorElement) e);
      }
    }
    return Optional.empty();
  }
  
}
