/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import de.montiarc.simplegenerator.MontiArcConfiguration;
import de.montiarc.simplegenerator.MontiArcScript;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class GroovyScriptTest {
  
  /**
   * Parent folder for the generated code
   */
  protected static final String OUTPUT_FOLDER = "target/generated-sources/monticore/codetocompile";
  
  protected static final String MODEL_PATH = "src/test/resources/arc/codegen/deployments";
  
  
  protected static final String HANDWRITTEN_PATH = "src/test/java/";
  
  /**
   * Base generator arguments
   */
  private List<String> generatorArguments = Lists
      .newArrayList(
          getConfigProperty(MontiArcConfiguration.Options.MODELPATH.toString()), MODEL_PATH,
          getConfigProperty(MontiArcConfiguration.Options.OUT.toString()), OUTPUT_FOLDER,
          getConfigProperty(MontiArcConfiguration.Options.HANDCODEDPATH.toString()),
          HANDWRITTEN_PATH,getConfigProperty(MontiArcConfiguration.Options.MODEL.toString()));
  
  protected static final String LOG = "GeneratorTest";
  
  
  @Test
  public void testOneModel(){
    doGenerate("bumperbot.library.Motor");
  }
  
  @Test
  public void testMultipleModels(){
    doGenerate("");
  }
  


  private void doGenerate(String model) {
    Log.info("Runs AST generator test for the model " + model, LOG);
    ClassLoader l = GroovyScriptTest.class.getClassLoader();
    try {
      String script = Resources.asCharSource(
          l.getResource("de/montiarc/simplegenerator/montiarc.groovy"),
          Charset.forName("UTF-8")).read();
      
      Configuration configuration =
          ConfigurationPropertiesMapContributor.fromSplitMap(CLIArguments.forArguments(
              getCLIArguments(model))
              .asMap());
      new MontiArcScript().run(script, configuration);
    }
    catch (IOException e) {
      Log.error("0xA1018 AstGeneratorTest failed: ", e);
    }
  }
  
  public static String getConfigProperty(String property) {
    return new StringBuilder("-").append(property).toString();
  }
  
  private String[] getCLIArguments(String model) {
    List<String> args = Lists.newArrayList(this.generatorArguments);
    args.add(getConfigProperty(MontiArcConfiguration.Options.MODEL.toString()));
    args.add(model);
    return args.toArray(new String[0]);
  }
  
  
}
