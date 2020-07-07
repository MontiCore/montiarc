/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import montiarc.util.Modelfinder;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;

/**
 *
 */
public class POJOGeneratorScript extends Script implements GroovyRunner {
  
  protected static final String[] DEFAULT_IMPORTS = {};
  
  protected static final String LOG = "POJOGeneratorScript";
  
  /**
   * @see de.se_rwth.commons.groovy.GroovyRunner#run(java.lang.String,
   * de.se_rwth.commons.configuration.Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(POJOGeneratorScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));
    
    // configuration
    POJOConfiguration config = POJOConfiguration
        .withConfiguration(configuration);
    
    // we add the configuration object as property with a special property
    // name
    builder.addVariable(POJOConfiguration.CONFIGURATION_PROPERTY, config);
    
    config.getAllValues().forEach((key, value) -> builder.addVariable(key, value));
    
    // after adding everything we override a couple of known variable
    // bindings
    // to have them properly typed in the script
    builder.addVariable(POJOConfiguration.Options.MODELPATH.toString(),
        config.getModelPath());
    builder.addVariable(POJOConfiguration.Options.OUT.toString(),
        config.getOut());
    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }
  
  
  
  /**
   * Gets called by Groovy Script. Generates component artifacts for each
   * component in {@code modelPath} to {@code targetFilepath}
   * 
   * @param modelPath
   * @param targetFilepath
   */
  public void generate(File modelPath, File targetFilepath) {
    File fqnMP = Paths.get(modelPath.getAbsolutePath()).toFile();
    List<String> foundModels = Modelfinder.getModelsInModelPath(fqnMP, CD4AnalysisLanguage.FILE_ENDING);
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);
      
      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new POJOGenerator(outDir, Paths.get(fqnMP.getAbsolutePath()), model,
          Names.getQualifiedName(packageName, simpleName)).generate();
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
