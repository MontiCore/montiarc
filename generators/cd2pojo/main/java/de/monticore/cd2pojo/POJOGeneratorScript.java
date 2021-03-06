/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The script class that integrates with se-groovy-maven plugin to run the
 * pojo generator groovy script during an application's build process to execute
 * the pojo generator.
 */
public class POJOGeneratorScript extends Script implements GroovyRunner {
  
  protected static final String[] DEFAULT_IMPORTS = {};

  protected static final String LOG = "POJOGeneratorScript";

  /** Whether the generator should terminate before the generation process in case of errors in the models. */
  protected static boolean failBeforeGenerationIfModelErrors = true;

  public static void main(String[] args){
    Log.enableFailQuick(false);
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.reset();
    CD4CodeMill.init();

    List<File> modelPath = Arrays.stream(args[0].split(",\\s+")).map(File::new).peek(File::mkdirs).collect(Collectors.toList());
    File output = Paths.get(args[1]).toFile();
    boolean enableClass2mc = args[args.length - 1].equals("-c2mc");

    Log.debug("Model Path:  " + modelPath.toString(), POJOGeneratorScript.class.getName());
    Log.debug("Output Path: " + output.toString(), POJOGeneratorScript.class.getName());
    if(enableClass2mc) {
      Log.debug("Class2MC is enabled.", POJOGeneratorScript.class.getName());
      initClass2MC();
    }

    generate(modelPath, output);

    Log.enableFailQuick(true);
  }

  
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
    
    config.getValueMap().forEach(builder::addVariable);
    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }

  /**
   * Gets called by Groovy Script. Generates compo
   * nent artifacts for each
   * component in {@code modelPath} to {@code targetFilepath}
   */
  public static void generate(List<File> modelPath, File targetFilepath) {
    List<Path> fqnMP = modelPath.stream().map(File::getAbsolutePath).map(Paths::get).collect(Collectors.toList());
    POJOGeneratorTool tool = new POJOGeneratorTool(targetFilepath.toPath(), Paths.get(""),
      failBeforeGenerationIfModelErrors);
    tool.generateCDTypesInPaths(fqnMP);
  }

  /**
   * Adds support to resolve Java types, methods, and fields from .class files as MontiCore types, methods, and fields.
   */
  public static void initClass2MC() {
    CD4CodeMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    CD4CodeMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
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

  @Override
  public Object run() {
    return true;
  }
}