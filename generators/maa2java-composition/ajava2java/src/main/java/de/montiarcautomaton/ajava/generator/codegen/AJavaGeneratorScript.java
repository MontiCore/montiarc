/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.ajava.generator.codegen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

import de.monticore.ModelingLanguageFamily;
import de.monticore.automaton.ioautomaton.JavaHelper;
import de.monticore.cd2pojo.POJOGenerator;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaLanguageFamily;
import de.monticore.lang.montiarc.ajava.cocos.AJavaCoCos;
import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarcautomaton._symboltable.MontiArcAutomatonLanguage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.templateclassgenerator.Modelfinder;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;

public class AJavaGeneratorScript extends Script implements GroovyRunner {
  
  protected static final String[] DEFAULT_IMPORTS = {};
  
  protected static final String LOG = "AJavaGeneratorScript";
  
  /**
   * @see de.se_rwth.commons.groovy.GroovyRunner#run(java.lang.String,
   * de.se_rwth.commons.configuration.Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(AJavaGeneratorScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));
    
    AJavaConfiguration config = AJavaConfiguration.withConfiguration(configuration);
    
    builder.addVariable(AJavaConfiguration.CONFIGURATION_PROPERTY, config);
    
    config.getAllValues().forEach((key, value) -> builder.addVariable(key, value));
    
    // after adding everything we override a couple of known variable
    // bindings
    // to have them properly typed in the script
    builder.addVariable(AJavaConfiguration.Options.MODELPATH.toString(),
        config.getModelPath());
    builder.addVariable(AJavaConfiguration.Options.OUT.toString(),
        config.getOut());
    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }
  
  /**
   * Generates ajava implementatino class.
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
  public void generate(String simpleName, String packageName, String modelPath, String fqnModelName, String targetPath) {
    //check cocos
    cocoCheck(simpleName, packageName, modelPath);
    
    // generate
    AJavaGenerator.generateModel(simpleName, packageName, modelPath, fqnModelName, targetPath);
  }
  
  /**
   * Checks all cocos of the given model.
   * 
   * @param simpleName the simple model name e.g. BumperControl
   * @param packageName the package name e.g.
   *          bumperbot
   * @param modelPath Path of models e.g. src/main/resources/models
   */
  public void cocoCheck(String simpleName, String packageName, String modelPath) {   
    // check cocos
    GlobalScope globalScope = initSymTab(modelPath); 
    String model = Names.getQualifiedName(packageName, simpleName);
    Optional<ComponentSymbol> compSym = globalScope.resolve(model, ComponentSymbol.KIND);
    if (!compSym.isPresent()) {
      error("Could not load model " + model);
    }
    ComponentSymbol comp = compSym.get();
    ASTMontiArcNode ast = (ASTMontiArcNode) comp.getAstNode().get();  
    AJavaCoCos.createChecker().checkAll(ast);
  }
  
  /**
   * Gets called by Groovy Script. Generates Template Classes for all templates
   * in {@code modelPath} to {@code targetFilepath}
   * 
   * @param modelPath
   * @param fqnTemplateName
   */
  public void generate(File modelPath, File targetFilepath) {
    File fqnMP = Paths.get(modelPath.getAbsolutePath()).toFile();
    List<String> foundModels = Modelfinder.getModelsInModelPath(fqnMP, MontiArcAutomatonLanguage.FILE_ENDING);
    // gen maa
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);
      String modelName = Names.getFileName(Names.getPathFromQualifiedName(model) + File.separator + simpleName, MontiArcAutomatonLanguage.FILE_ENDING);
      Log.info("Check model: " + modelName, "AJavaGeneratorScript");

      //      cocoCheck(simpleName, packageName, modelPath.getAbsolutePath());
      Log.info("Generate model: " + modelName, "AJavaGeneratorScript");
      generate(simpleName, packageName, modelPath.getAbsolutePath(), modelName, targetFilepath.getAbsolutePath());
    }
    
    // gen cd
    foundModels = Modelfinder.getModelsInModelPath(fqnMP, CD4AnalysisLanguage.FILE_ENDING);
    for (String model :  foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);
      
      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new POJOGenerator(outDir, Paths.get(fqnMP.getAbsolutePath()), model, Names.getQualifiedName(packageName, simpleName)).generate();
      
    }
    
  }
  
  private static GlobalScope initSymTab(String modelPath) {
    ModelingLanguageFamily fam = new AJavaLanguageFamily();
    final ModelPath mp = new ModelPath(Paths.get(modelPath), Paths.get("src/main/resources/defaultTypes"));
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
  
  
}
