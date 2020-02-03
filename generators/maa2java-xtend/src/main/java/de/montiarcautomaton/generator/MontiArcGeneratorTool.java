/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import de.montiarcautomaton.generator.codegen.xtend.MAAGenerator;
import de.monticore.cd2pojo.Modelfinder;
import de.monticore.cd2pojo.POJOGenerator;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcLanguage;

/**
 * Extends {@link MontiArcTool} with generate capabilities.
 *
 * @author Pfeiffer, Wortmann
 * @version $Revision$, $Date$
 */
public class MontiArcGeneratorTool extends MontiArcTool {

  public static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";
  
  private MAAGenerator instance;
  
  /**
   * @return instance
   */
  public MAAGenerator getInstance() {
    if (instance == null) {
      instance = new MAAGenerator();
    }
    return instance;
  }
  
  /**
   * @param instance the instance to set
   */
  public void setInstance(MAAGenerator instance) {
    this.instance = instance;
  }
  
  /**
   * Checks cocos and generates code for all MontiArc models in modelpath to
   * folder target.
   * 
   * @param modelPath Path where MontiArc models are located.
   * @param target Path the code should be generated to.
   * @param hwcPath Path where handwritten component implementations are
   *          located.
   */
  public void generate(File modelPath, File target, File hwcPath) {
    List<String> foundModels = Modelfinder.getModelsInModelPath(modelPath, MontiArcLanguage.FILE_ENDING);
    
    // 1. create symboltable
    Log.info("Initializing symboltable", "MontiArcGeneratorTool");
    String basedir = getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(), target.getAbsolutePath());
    Scope symTab = initSymbolTable(modelPath, Paths.get(basedir + LIBRARY_MODELS_FOLDER).toFile(), hwcPath);
    
    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
      
      // 2. parse + resolve model
      Log.info("Parsing model:" + qualifiedModelName, "MontiArcGeneratorTool");
      ComponentSymbol comp = symTab.<ComponentSymbol> resolve(qualifiedModelName, ComponentSymbol.KIND).get();
      
      // 3. check cocos
      Log.info("Check model: " + qualifiedModelName, "MontiArcGeneratorTool");
      checkCoCos((ASTMontiArcNode) comp.getAstNode().get());
      
      // 4. generate
      Log.info("Generate model: " + qualifiedModelName, "MontiArcGeneratorTool");
      getInstance().generateAll(Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName())).toFile(), hwcPath, comp);
    }
    
    // gen cd
    generatePOJOs(modelPath, target);
    
  }
  
  /**
   * Compares the two paths and returns the common path. The common path is the
   * basedir.
   * 
   * @param modelPath
   * @param targetPath
   * @return
   */
  private String getBasedirFromModelAndTargetPath(String modelPath, String targetPath) {
    String basedir = "";
    StringBuilder sb = new StringBuilder();
    String seperator = File.separator;
    int lastFolderIndex = 0;
    for (int i = 0; i < modelPath.length(); i++) {
      // Assuming a seperator is always length 1
      if (seperator.length() != 1) {
        Log.error("0x???? File seperator should be a single char. Use a less strange system");
      }
      else if (modelPath.charAt(i) == seperator.charAt(0)) {
        lastFolderIndex = i;
      }
      
      if (modelPath.charAt(i) == targetPath.charAt(i)) {
        sb.append(modelPath.charAt(i));
      }
      else {
        // basedir includes the seperator
        basedir = sb.substring(0, lastFolderIndex + 1);
        break;
      }
    }
    return basedir;
  }
  
  private void generatePOJOs(File modelPath, File targetFilepath) {
    List<String> foundModels = Modelfinder.getModelsInModelPath(modelPath, CD4AnalysisLanguage.FILE_ENDING);
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);
      
      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new POJOGenerator(outDir, Paths.get(modelPath.getAbsolutePath()), model, 
          Names.getQualifiedName(packageName, simpleName)).generate();
    }
  }
}
