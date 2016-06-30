/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.simplegenerator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import configure.pojo.DexPojoScript;
import de.montiarc.simplegenerator.codegen.ComponentGenerator;
import de.monticore.ModelingLanguageFamily;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.lang.montiarc.montiarc._symboltable.MaWithCdLanguageFamily;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;

/**
 * TODO: Write me!
 *
 * @author Robert Heim
 */
public class MontiArcScript extends Script implements GroovyRunner {
  
  protected static final String[] DEFAULT_IMPORTS = { "de.monticore.lang.montiarc.montiarc._ast" };
  
  protected static final String LOG = "MontiArcScript";
  
  /**
   * @see de.se_rwth.commons.groovy.GroovyRunner#run(java.lang.String,
   * de.se_rwth.commons.configuration.Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(MontiArcScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));
        
    // configuration
    MontiArcConfiguration config = MontiArcConfiguration.withConfiguration(configuration);
    
    // we add the configuration object as property with a special property
    // name
    builder.addVariable(MontiArcConfiguration.CONFIGURATION_PROPERTY, config);
    
    config.getAllValues().forEach((key, value) -> builder.addVariable(key, value));
    
    // after adding everything we override a couple of known variable
    // bindings
    // to have them properly typed in the script
    builder.addVariable(MontiArcConfiguration.Options.MODELPATH.toString(),
        config.getModelPath());
    builder.addVariable(MontiArcConfiguration.Options.OUT.toString(),
        config.getOut());
    builder.addVariable(MontiArcConfiguration.Options.MODEL.toString(), config.getModel());
    builder.addVariable(MontiArcConfiguration.Options.MODELS.toString(), config.getModels());
    builder
        .addVariable(MontiArcConfiguration.Options.HANDCODEDPATH.toString(), config.getHWCPath());
    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }
  
  private GlobalScope initSymTab(final List<File> modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }
    
    // TODO the java default types should come from JavaDSL
    p.add(Paths.get("src/main/resources/defaultTypes"));
    final ModelPath mp = new ModelPath(p);
    
    ModelingLanguageFamily fam = new MaWithCdLanguageFamily();
    fam.addModelingLanguage(new JavaDSLLanguage());
    
    GlobalScope gs = new GlobalScope(mp, fam);
    
    return gs;
  }
  
  public void generate(final List<File> modelPaths, String model, File outputDirectory,
      Optional<String> hwcPath) {
      
    // setup
    GlobalExtensionManagement glex = GlexSetup.create();
    GlobalScope globalScope = initSymTab(modelPaths);
    // List<ComponentSymbol> cmpSymbols =
    // globalScope.resolveLocally(ComponentSymbol.KIND);
    
    // for (ComponentSymbol comp : cmpSymbols){
    // resolve model
    Optional<ComponentSymbol> compSym = globalScope.resolve(model, ComponentSymbol.KIND);
    if (!compSym.isPresent()) {
      error("Could not load model " + model);
    }
    ComponentSymbol comp = compSym.get();
    ASTComponent ast = (ASTComponent) comp.getAstNode().get();
    
    // generate
    ComponentGenerator.generate(glex, ast, comp, outputDirectory, hwcPath);
    // }
  }
  
  public void generate(final List<File> modelPaths, File outputDirectory,
      Optional<String> hwcPath) {
    for (File modelPath : modelPaths) {
      File fqnMP = Paths.get(modelPath.getAbsolutePath()).toFile();
      // gen MontiArc
      List<String> modelsInModelPath = Modelfinder.getModelsInModelPath(fqnMP, MontiArcLanguage.FILE_ENDING);
      for (String model : modelsInModelPath) {
        generate(modelPaths, model, outputDirectory, hwcPath);
      }
      // gen CD
      modelsInModelPath = Modelfinder.getModelsInModelPath(fqnMP, CD4AnalysisLanguage.FILE_ENDING);
      for (String model : modelsInModelPath) {
        Multimap<String, String> generatorArguments = ArrayListMultimap.create();
        generatorArguments.put("modelPath", fqnMP.getAbsolutePath());
        generatorArguments.put("out", outputDirectory.getAbsolutePath());
        // generatorArguments.put("targetPath", "test/source");
        // generatorArguments.put("templatePath", "test/source");
        generatorArguments.put("model", model);
        
        ConfigurationPropertiesMapContributor propertiesContributor = ConfigurationPropertiesMapContributor
            .fromSplitMap(generatorArguments);
            
        Configuration configuration = ConfigurationContributorChainBuilder.newChain()
            .add(propertiesContributor).build();
        // TODO wait for DEx to use MC 4.4+
        //new DexPojoScript().run(Optional.of(configuration));
      }
    }
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
