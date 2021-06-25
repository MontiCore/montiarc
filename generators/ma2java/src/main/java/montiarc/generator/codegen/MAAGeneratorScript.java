/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import montiarc.MontiArcMill;
import montiarc.generator.MontiArcGeneratorTool;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;
import java.util.List;

public class MAAGeneratorScript extends Script implements GroovyRunner {

  protected static final String[] DEFAULT_IMPORTS = {};

  protected static final String LOG = "MAAGeneratorScript";

  /**
   * @see de.se_rwth.commons.groovy.GroovyRunner#run(java.lang.String,
   * de.se_rwth.commons.configuration.Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    Log.enableFailQuick(false);
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
    MontiArcMill.init();
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(MAAGeneratorScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));

    // configuration
    MAAConfiguration config = MAAConfiguration
        .withConfiguration(configuration);

    // we add the configuration object as property with a special property
    // name
    builder.addVariable(MAAConfiguration.CONFIGURATION_PROPERTY, config);

    config.getValueMap().forEach(builder::addVariable);

    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }

  /**
   * Gets called by Groovy Script. Generates component artifacts for each
   * component in {@code modelPath} to {@code targetFilepath}
   *
   * @param codes paths for handwritten code
   * @param models paths where models are located
   * @param output path to where the output should be generated to
   */
  public void generate(List<File> models, File output, List<File> codes) {
    new MontiArcGeneratorTool().generate(models, output, codes);
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
