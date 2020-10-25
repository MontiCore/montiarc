/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.montiarcautomaton.generator.codegen.xtend.MAAGenerator;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import montiarc.util.DirectoryUtil;
import montiarc.util.Modelfinder;
import de.monticore.cd2pojo.POJOGenerator;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._symboltable.IMontiArcScope;
import montiarc._symboltable.MontiArcLanguage;

/**
 * Extends {@link MontiArcTool} with generate capabilities.
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
    String basedir = DirectoryUtil.getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(), target.getAbsolutePath());
    IMontiArcScope symTab = initSymbolTable(modelPath, Paths.get(basedir + LIBRARY_MODELS_FOLDER).toFile(),
      hwcPath);

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);

      // 2. parse + resolve model
      Log.info("Parsing model:" + qualifiedModelName, "MontiArcGeneratorTool");
      ComponentTypeSymbol comp =
        symTab.resolveComponentType(qualifiedModelName).get();

      // 3. check cocos
      Log.info("Check model: " + qualifiedModelName, "MontiArcGeneratorTool");
      checkCoCos(comp.getAstNode());

      // 4. generate
      Log.info("Generate model: " + qualifiedModelName, "MontiArcGeneratorTool");
      getInstance().generateAll(Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName())).toFile(), hwcPath, comp);
    }

    // gen cd
    generatePOJOs(modelPath, target);

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