package de.montiarcautomaton.generator.codegen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

import de.monticore.ModelingLanguageFamily;
import de.monticore.cd2pojo.POJOGenerator;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.templateclassgenerator.Modelfinder;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcLanguage;
import montiarc._symboltable.MontiArcLanguageFamily;
import montiarc.cocos.MontiArcCoCos;
import montiarc.helper.JavaHelper;

public class MAAGeneratorScript extends Script implements GroovyRunner {
  
  protected static final String[] DEFAULT_IMPORTS = {};
  
  protected static final String LOG = "MAAGeneratorScript";
  
  /**
   * @see de.se_rwth.commons.groovy.GroovyRunner#run(java.lang.String,
   * de.se_rwth.commons.configuration.Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(MAAGeneratorScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));
    
    // configuration
    MAAConfiguration config = MAAConfiguration
        .withConfiguration(configuration);
    
    // we add the configuration object as property with a special property
    // name
    builder.addVariable(MAAConfiguration.CONFIGURATION_PROPERTY, config);
    
    config.getAllValues().forEach((key, value) -> builder.addVariable(key, value));
    
    // after adding everything we override a couple of known variable
    // bindings
    // to have them properly typed in the script
    builder.addVariable(MAAConfiguration.Options.MODELPATH.toString(),
        config.getModelPath());
    builder.addVariable(MAAConfiguration.Options.OUT.toString(),
        config.getOut());
    builder.addVariable(MAAConfiguration.Options.HANDWRITTENCODEPATH.toString(),
        config.getHWCPath());
    
    GroovyInterpreter g = builder.build();
    g.evaluate(script);
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
   * @param hwcPath 
   */
  public void generate(String simpleName, String packageName, String modelPath, String fqnModelName,
      String targetPath, File hwcPath) {
    // check cocos
    cocoCheck(simpleName, packageName, modelPath, targetPath);
    
    // generate
    MAAGenerator.generateModel(simpleName, packageName, modelPath, fqnModelName, targetPath, hwcPath);
  }
  
  /**
   * Checks all cocos of the given model.
   * 
   * @param simpleName the simple model name e.g. BumperControl
   * @param packageName the package name e.g. bumperbot
   * @param modelPath Path of models e.g. src/main/resources/models
   * @param targetPath 
   */
  public void cocoCheck(String simpleName, String packageName, String modelPath, String targetPath) {
    // check cocos
    GlobalScope globalScope = initSymTab(modelPath, targetPath);
    String model = Names.getQualifiedName(packageName, simpleName);
    Optional<ComponentSymbol> compSym = globalScope.resolve(model, ComponentSymbol.KIND);
    if (!compSym.isPresent()) {
      error("Could not load model " + model);
    }
    ComponentSymbol comp = compSym.get();
    ASTMontiArcNode ast = (ASTMontiArcNode) comp.getAstNode().get();
    MontiArcCoCos.createChecker().checkAll(ast);
  }
  
  /**
   * Gets called by Groovy Script. Generates component artifacts for each
   * component in {@code modelPath} to {@code targetFilepath}
   * 
   * @param modelPath
   * @param fqnTemplateName
   */
  public void generate(File modelPath, File targetFilepath, File hwcPath) {
    File fqnMP = Paths.get(modelPath.getAbsolutePath()).toFile();
    List<String> foundModels = Modelfinder.getModelsInModelPath(fqnMP,
        MontiArcLanguage.FILE_ENDING);
    // gen maa
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);
      String modelName = Names.getFileName(
          Names.getPathFromQualifiedName(model) + File.separator + simpleName,
          MontiArcLanguage.FILE_ENDING);
      Log.info("Check model: " + modelName, "MAAGeneratorScript");
      // TODO enable
      // cocoCheck(simpleName, packageName, modelPath.getAbsolutePath());
      Log.info("Generate model: " + modelName, "MAAGeneratorScript");
      generate(simpleName, packageName, modelPath.getAbsolutePath(), modelName,
          targetFilepath.getAbsolutePath(), hwcPath);
    }
    
    // gen cd
    foundModels = Modelfinder.getModelsInModelPath(fqnMP, CD4AnalysisLanguage.FILE_ENDING);
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);
      
      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new POJOGenerator(outDir, Paths.get(fqnMP.getAbsolutePath()), model,
          Names.getQualifiedName(packageName, simpleName)).generate();
    }
    
  }
  
  private static GlobalScope initSymTab(String modelPath, String targetPath) {
    ModelingLanguageFamily fam = new MontiArcLanguageFamily();
    final ModelPath mp = new ModelPath(Paths.get(modelPath),
        Paths.get("src/main/resources/defaultTypes"), Paths
        .get(getBasedirFromModelAndTargetPath(modelPath, targetPath) + "target/librarymodels/"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
  
  // #######################
  // log functions
  // #######################
  
  public boolean isDebugEnabled() {
    return Log.isDebugEnabled(LOG);
  }
  
  public void debug(String msg) {
    Log.debug(msg, LOG);
  }
  
  public void debug(String msg, Throwable t) {
    Log.debug(msg, t, LOG);
  }
  
  public boolean isInfoEnabled() {
    return Log.isInfoEnabled(LOG);
  }
  
  public void info(String msg) {
    Log.info(msg, LOG);
  }
  
  public void info(String msg, Throwable t) {
    Log.info(msg, t, LOG);
  }
  
  public void warn(String msg) {
    Log.warn(msg);
  }
  
  public void warn(String msg, Throwable t) {
    Log.warn(msg, t);
  }
  
  public void error(String msg) {
    Log.error(msg);
  }
  
  public void error(String msg, Throwable t) {
    Log.error(msg, t);
  }
  
  /**
   * @see groovy.lang.Script#run()
   */
  @Override
  public Object run() {
    return true;
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
