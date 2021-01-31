/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import groovy.lang.Script;
import montiarc.util.Modelfinder;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * The script class that integrates with se-groovy-maven plugin to run the
 * pojo generator groovy script during an applications build process to execute
 * the pojo generator.
 */
public class POJOGeneratorScript extends Script implements GroovyRunner {
  
  protected static final String[] DEFAULT_IMPORTS = {};
  
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
    
    // we add the configuration object as property with a special property name
    builder.addVariable(POJOConfiguration.CONFIGURATION_PROPERTY, config);
    
    config.getAllValues().forEach((key, value) -> builder.addVariable(key, value));
    
    // after adding everything we override a couple of known variable bindings
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
    List<String> foundModels = Modelfinder.getModelsInModelPath(fqnMP, "cd");
    POJOGeneratorTool tool = new POJOGeneratorTool(targetFilepath.toPath(), Paths.get(""));
    tool.generateCDTypesInPath(foundModels, fqnMP.toPath());
  }
  
  /**
   * @see groovy.lang.Script#run()
   */
  @Override
  public Object run() {
    return true;
  }
  
}