/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.helper.ArcAutomatonHelper;
import montiarc.generator.helper.ComponentHelper;
import montiarc.generator.util.Identifier;
import montiarc.util.MA2JavaError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MontiArcGenerator {

  /**
   * This addendum, appended to a component name, makes the name of the java code that represents the component.
   */
  protected final static String COMPONENT_ADDENDUM = "";
  protected static String FILE_EXTENSION = ".java";
  protected GeneratorEngine engine;
  protected GeneratorSetup engineSetup;
  protected Formatter codeFormatter;

  public MontiArcGenerator(@NotNull GeneratorSetup setup) {
    this.engineSetup = Preconditions.checkNotNull(setup);
    this.engine = new GeneratorEngine(this.engineSetup);
    this.codeFormatter = new Formatter();
  }

  public MontiArcGenerator(@NotNull Path targetDir, @NotNull Path hwcPath) {
    this(Preconditions.checkNotNull(targetDir), Collections.singletonList(Preconditions.checkNotNull(hwcPath)));
  }

  public MontiArcGenerator(@NotNull Path targetDir, @NotNull List<Path> hwcPath) {
    this(createGeneratorSetup(Preconditions.checkNotNull(targetDir), Preconditions.checkNotNull(hwcPath)));
  }

  public MontiArcGenerator(@NotNull Path targetDir) {
    this(createGeneratorSetup(Preconditions.checkNotNull(targetDir)));
  }

  protected static GeneratorSetup createGeneratorSetup(@NotNull Path targetDir, @NotNull List<Path> hwcPath) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir.toFile());
    setup.setHandcodedPath(new MCPath(hwcPath));
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("compHelper", new ComponentHelper());
    glex.setGlobalValue("autHelper", new ArcAutomatonHelper());
    glex.setGlobalValue("identifier", new Identifier());
    setup.setGlex(glex);
    return setup;
  }

  protected static GeneratorSetup createGeneratorSetup(@NotNull Path targetDir) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir.toFile());
    setup.setHandcodedPath(new MCPath());
    return setup;
  }

  protected GeneratorEngine getEngine() {
    return engine;
  }

  protected GeneratorSetup getEngineSetup() {
    return this.engineSetup;
  }

  protected Formatter getCodeFormatter() {
    return this.codeFormatter;
  }

  public void generate(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    final String template = "ma2java.component.CompilationUnit.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(ast.getComponentType().getSymbol(), COMPONENT_ADDENDUM);
    final String usedAddendum = existsHwc ? COMPONENT_ADDENDUM + "TOP" : COMPONENT_ADDENDUM;
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(ast.getComponentType().getSymbol(), usedAddendum).toString()
    );

    String code = getEngine().generateNoA(template, ast, existsHwc).toString();
    Optional<String> formattedCode = Optional.empty();

    try {
      formattedCode = Optional.of(this.getCodeFormatter().formatSource(code));
    } catch (FormatterException e) {
      Log.warn(MA2JavaError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, template, ast.getComponentType().getSymbol().getFullName(), e.getMessage()));
    }

    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(code));

    if (ast.getComponentType().getSymbol().getAllPorts().isEmpty()
      && ast.getComponentType().getSymbol().getParameters().isEmpty()
      && ast.getComponentType().getSymbol().getTypeParameters().isEmpty()) {
      this.generateComponentDeployment(ast.getComponentType().getSymbol());
    }
  }

  /**
   * Generates a component deployment class that regularly executes {@code comp}
   */
  protected void generateComponentDeployment(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);

    final String templateName = "ma2java.component.Deploy.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(comp, "Deploy", "");
    final String usedAddendum = existsHwc ? "TOP" : "";
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(comp, "Deploy", usedAddendum).toString()
    );

    String generatedCode = getEngine().generateNoA(
      templateName, comp, existsHwc).toString();
    Optional<String> formattedCode = Optional.empty();

    try {
      formattedCode = Optional.of(this.getCodeFormatter().formatSource(generatedCode));
    } catch (FormatterException e) {
      Log.warn(MA2JavaError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, templateName, comp.getFullName(), e.getMessage()));
    }

    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(generatedCode));
  }

  protected Path getFileAsPath(@NotNull ComponentTypeSymbol comp, @NotNull String addendum) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(addendum);
    return this.getFileAsPath(comp, "", addendum);
  }

  protected Path getFileAsPath(
    @NotNull ComponentTypeSymbol comp, @NotNull String prefix, @NotNull String addendum) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(addendum);
    return Paths.get(
      comp.getPackageName().replaceAll("\\.", "/")
        + "/" + prefix + comp.getName() + addendum + FILE_EXTENSION);
  }

  protected boolean existsHandWrittenCodeFor(@NotNull ComponentTypeSymbol comp, @NotNull String addendum) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(addendum);
    return GeneratorEngine.existsHandwrittenClass(this.getEngineSetup().getHandcodedPath(),
      comp.getFullName() + addendum);
  }

  protected boolean existsHandWrittenCodeFor(
    @NotNull ComponentTypeSymbol comp, @NotNull String prefix, @NotNull String addendum) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(addendum);
    return GeneratorEngine.existsHandwrittenClass(
      this.getEngineSetup().getHandcodedPath(),
      comp.getPackageName() + "." + prefix + comp.getName() + addendum
    );
  }
}
