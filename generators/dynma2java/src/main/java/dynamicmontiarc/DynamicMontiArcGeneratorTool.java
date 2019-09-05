/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc;

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
import dynamicmontiarc.codegen.xtend.DynRecComponentGenerator;
import dynamicmontiarc.codegen.xtend.DynRecDeploy;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcLanguage;

/**
 * Extends {@link DynamicMontiArcTool} with generate capabilities.
 */
public class DynamicMontiArcGeneratorTool extends DynamicMontiArcTool{
  
  public static final String DEFAULT_TYPES_FOLDER = "target/javaLib/";
  public static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";
  
  private MAAGenerator instance;
  
  /**
   * Instance is the MontiArc generator ({@link MAAGenerator}) of the MontiArc
   * core language used during code generation.
   *
   * @return the current instance
   */
  public MAAGenerator getInstance() {
    if (this.instance == null) {
      this.instance = new MAAGenerator();
      this.instance.compGenerator = new DynRecComponentGenerator();
      this.instance.deploy = new DynRecDeploy();
    }
    return this.instance;
  }

  /**
   * Instance is the MontiArc generator ({@link MAAGenerator}) of the MontiArc
   * core language used during code generation.
   *
   * @param instance the instance to set
   */
  public void setInstance(MAAGenerator instance) {
    this.instance = instance;
  }
  
  /**
   * Parses all models in the given model path, checks context conditions, and
   * generates code for these.
   * 
   * @param modelPath the path where MontiArc models are located
   * @param target the path the code should be generated to
   * @param hwcPath the path where handwritten component implementations are
   *                located
   */
  public void generate(File modelPath, File target, File hwcPath) {
    List<String> foundModels = Modelfinder.getModelsInModelPath(modelPath,
        MontiArcLanguage.FILE_ENDING);

    // 1. create symboltable
    Log.info("Initializing symboltable", "DynamicMontiArcGeneratorTool");
    String basedir = getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(), target.getAbsolutePath());
    Scope symTab = initSymbolTable(modelPath, Paths.get(basedir + DEFAULT_TYPES_FOLDER).toFile(), Paths.get(basedir + LIBRARY_MODELS_FOLDER).toFile(), hwcPath);
    
    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
      
      // 2. parse + resolve model
      Log.info("Parsing model:"+ qualifiedModelName, "DynamicMontiArcGeneratorTool");
      ComponentSymbol comp = symTab.<ComponentSymbol> resolve(qualifiedModelName, ComponentSymbol.KIND).get();

      // 3. check cocos
      Log.info("Check model: " + qualifiedModelName, "DynamicMontiArcGeneratorTool");
      checkCoCos((ASTMontiArcNode) comp.getAstNode().get());
      
      // 4. generate
      Log.info("Generate model: " + qualifiedModelName, "DynamicMontiArcGeneratorTool");
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
