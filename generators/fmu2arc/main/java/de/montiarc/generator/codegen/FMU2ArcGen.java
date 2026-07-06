/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.generator.codegen;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import no.ntnu.ihb.fmi4j.importer.fmi2.Fmu;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkNotNull;

public class FMU2ArcGen {

  protected static String FILE_EXTENSION = ".java";
  protected GeneratorEngine engine;
  protected GeneratorSetup setup;

  public FMU2ArcGen(GeneratorSetup setup) {
    this.setup = checkNotNull(setup);
    this.engine = new GeneratorEngine(this.setup);
  }

  public FMU2ArcGen(@NotNull Path targetDir, String packageName) {
    this(createGeneratorSetup(checkNotNull(targetDir), packageName));
  }

  protected static GeneratorSetup createGeneratorSetup(Path targetDir, String packageName) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir.toFile());
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("packageName", packageName);
    setup.setGlex(glex);
    return setup;
  }

  protected GeneratorEngine getEngine() {
    return this.engine;
  }

  protected GeneratorSetup getEngineSetup() {
    return this.setup;
  }

  /**
   * Generates CompImpl-Class for a given fmu file
   */
  public void generateCompImpl(Fmu fmu, File fmuFile) {
    final String template = "templates.FmuCompImpl.ftl";
    final Path outPath = Paths.get(this.getEngineSetup().getOutputDirectory().getAbsolutePath(), fmu.getName() + "CompImpl" + FILE_EXTENSION);

    getEngine().generateNoA(template, outPath, fmu, fmuFile);
  }

  /**
   * Generates Compute-Class for a given fmu file
   */
  public void generateCompute(Fmu fmu, File fmuFile) {
    final String template = "templates.FmuCompute.ftl";
    final Path outPath = Paths.get(this.getEngineSetup().getOutputDirectory().getAbsolutePath(), fmu.getName() + "Compute" + FILE_EXTENSION);

    getEngine().generateNoA(template, outPath, fmu, fmuFile);
  }

  /**
   * Generates SyncMsg-Class for a given fmu file
   */
  public void generateSyncMsg(Fmu fmu) {
    final String template = "templates.FmuSyncMsg.ftl";
    final Path outPath = Paths.get(this.getEngineSetup().getOutputDirectory().getAbsolutePath(), fmu.getName() + "SyncMsg" + FILE_EXTENSION);

    getEngine().generateNoA(template, outPath, fmu);
  }

  /**
   * Generates Events-Class for a given fmu file
   */
  public void generateEvents(Fmu fmu) {
    final String template = "templates.FmuEvents.ftl";
    final Path outPath = Paths.get(this.getEngineSetup().getOutputDirectory().getAbsolutePath(), fmu.getName() + "Events" + FILE_EXTENSION);

    getEngine().generateNoA(template, outPath, fmu);
  }

  /**
   * Generates Context-Class for a given fmu file
   */
  public void generateContext(Fmu fmu) {
    final String template = "templates.FmuContext.ftl";
    final Path outPath = Paths.get(this.getEngineSetup().getOutputDirectory().getAbsolutePath(), fmu.getName() + "Context" + FILE_EXTENSION);

    getEngine().generateNoA(template, outPath, fmu);
  }

  /**
   * Generates CompBuilder-Class for a given fmu file
   */
  public void generateCompBuilder(Fmu fmu) {
    final String template = "templates.FmuCompBuilder.ftl";
    final Path outPath = Paths.get(this.getEngineSetup().getOutputDirectory().getAbsolutePath(), fmu.getName() + "CompBuilder" + FILE_EXTENSION);

    getEngine().generateNoA(template, outPath, fmu);
  }

  /**
   * Generates Comp-Class for a given fmu file
   */
  public void generateComp(Fmu fmu) {
    final String template = "templates.FmuComp.ftl";
    final Path outPath = Paths.get(this.getEngineSetup().getOutputDirectory().getAbsolutePath(), fmu.getName() + "Comp" + FILE_EXTENSION);

    getEngine().generateNoA(template, outPath, fmu);
  }

  /**
   * Generates Deploy-Class for a given fmu file
   */
  public void generateDeploy(Fmu fmu) {
    final String template = "templates.DeployFmu.ftl";
    final Path outPath = Paths.get(this.getEngineSetup().getOutputDirectory().getAbsolutePath(), "Deploy" + fmu.getName() + FILE_EXTENSION);

    getEngine().generateNoA(template, outPath, fmu);
  }

}
