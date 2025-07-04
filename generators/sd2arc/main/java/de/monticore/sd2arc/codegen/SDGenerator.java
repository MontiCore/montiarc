/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.codegen;

import com.google.common.base.Preconditions;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.FileReaderWriter;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SDGenerator {

  protected static String FILE_EXTENSION = ".arc";
  protected GeneratorEngine engine;
  protected GeneratorSetup engineSetup;

  public SDGenerator(@NotNull GeneratorSetup setup) {
    this.engineSetup = Preconditions.checkNotNull(setup);
    this.engine = new GeneratorEngine(this.engineSetup);
  }

  public SDGenerator(@NotNull Path targetDir) {
    this(createGeneratorSetup(Preconditions.checkNotNull(targetDir)));
  }

  protected static GeneratorSetup createGeneratorSetup(@NotNull Path targetDir) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir.toFile());
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("helper", new SDHelper());
    glex.setGlobalValue("typeDispatcher", SD4ComponentsMill.typeDispatcher());
    glex.setGlobalValue("prettyPrinter", new SD2ArcPrinter());
    glex.setGlobalValue("arcPrinter", new Arc2ArcPrinter());
    setup.setGlex(glex);
    return setup;
  }

  public void generate(ASTSDArtifact artifact) {
    final String template = "sd2arc.SDArtifact.ftl";
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(artifact.getSequenceDiagram().getSymbol(), "", "").toString()
    );

    String code = getEngine().generateNoA(template, artifact).toString();

    FileReaderWriter.storeInFile(outPath, code);
  }

  protected Path getFileAsPath(
    @NotNull DiagramSymbol sd, @NotNull String prefix, @NotNull String addendum) {
    Preconditions.checkNotNull(sd);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(addendum);
    return Paths.get(
      sd.getPackageName().replaceAll("\\.", "/")
        + "/" + prefix + sd.getName() + addendum + FILE_EXTENSION);
  }

  protected GeneratorEngine getEngine() {
    return engine;
  }

  protected GeneratorSetup getEngineSetup() {
    return this.engineSetup;
  }
}
