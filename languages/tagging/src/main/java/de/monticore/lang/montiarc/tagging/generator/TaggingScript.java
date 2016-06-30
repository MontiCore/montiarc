/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.tagging.generator;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

/**
 * TODO: Write me!
 *
 * @author Robert Heim, Michael von Wenckstern
 */
public class TaggingScript extends Script implements GroovyRunner {
  
  protected static final String[] DEFAULT_IMPORTS = {  };
  
  protected static final String LOG = "TaggingScript";

  public TaggingScript() {
    super();
  }

  /**
   * @see GroovyRunner#run(String,
   * Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(TaggingScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));

    // configuration
    TaggingConfiguration config = TaggingConfiguration.withConfiguration(configuration);

    // we add the configuration object as property with a special property
    // name
    builder.addVariable(TaggingConfiguration.CONFIGURATION_PROPERTY, config);

    config.getAllValues().forEach((key, value) -> builder.addVariable(key, value));

    // after adding everything we override a couple of known variable
    // bindings
    // to have them properly typed in the script
    builder.addVariable(TaggingConfiguration.Options.MODELPATH.toString(),
        config.getModelPath());
    builder.addVariable(TaggingConfiguration.Options.OUT.toString(),
        config.getOut());
    builder.addVariable(TaggingConfiguration.Options.MODEL.toString(), config.getModel());
    builder.addVariable(TaggingConfiguration.Options.MODELS.toString(), config.getModels());
//    builder
//        .addVariable(TaggingConfiguration.Options.HANDCODEDPATH.toString(), config.getHWCPath());
//
    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }

  public void generate(final List<File> modelPaths, File outputDirectory) {
    for (File modelPath : modelPaths) {
      File fqnMP = Paths.get(modelPath.getAbsolutePath()).toFile();
      // gen MontiArc
      List<String> modelsInModelPath = Modelfinder.getModelsInModelPath(fqnMP, "tagschema");
      for (String model : modelsInModelPath) {
        TagSchemaGenerator.generate(model, modelPath.getAbsolutePath(), outputDirectory.getAbsolutePath());
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
   * @see Script#run()
   */
  @Override
  public Object run() {
    return true;
  }
  
}
