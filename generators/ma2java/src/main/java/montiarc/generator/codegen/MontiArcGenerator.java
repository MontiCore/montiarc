/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.generator.util.Identifier;
import montiarc.generator.util.MA2JavaError;
import montiarc.generator.helper.ComponentHelper;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MontiArcGenerator {

  protected static String FILE_EXTENSION = ".java";

  /**
   * This addendum, appended to a component name, makes the name of the java code that represents the component.
   */
  protected final static String COMPONENT_ADDENDUM = "";
  /**
   * This addendum, appended to a component name, makes the name of the java code that represents a component's result.
   */
  protected final static String RESULT_ADDENDUM = "Result";
  /**
   * This addendum, appended to a component name, makes the name of the java code that represents a component's input.
   */
  protected final static String INPUT_ADDENDUM = "Input";
  /**
   * This addendum, appended to a component name, makes the name of the java code that represents a component's
   * implementation.
   */
  protected final static String IMPL_ADDENDUM = "Impl";

  protected GeneratorEngine engine;
  protected GeneratorSetup engineSetup;
  protected Formatter codeFormatter;

  public MontiArcGenerator(@NotNull GeneratorSetup setup) {
    this.engineSetup = Preconditions.checkNotNull(setup);
    this.engine = new GeneratorEngine(this.engineSetup);
    this.codeFormatter = new Formatter();
  }

  public MontiArcGenerator(@NotNull Path targetDir, @NotNull Path hwcPath) {
    this(Preconditions.checkNotNull(targetDir), Preconditions.checkNotNull(Collections.singletonList(hwcPath)));
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

  public void generate(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    this.generateComponentInput(comp);
    this.generateComponentResult(comp);
    this.generateComponentBaseClass(comp);
    this.generateComponentImpl(comp);

    if (comp.getAllPorts().isEmpty() && comp.getParameters().isEmpty() && comp.getTypeParameters().isEmpty()) {
      this.generateComponentDeployment(comp);
    }
  }

  /**
   * Generates java code that represents the input of the component {@code comp}.
   */
  protected void generateComponentInput(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);

    final String templateName = "templates.input.InputClass.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(comp, INPUT_ADDENDUM);
    final String usedAddendum = existsHwc ? INPUT_ADDENDUM + "TOP" : INPUT_ADDENDUM;
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(comp, usedAddendum).toString()
    );

    String generatedCode = getEngine().generateNoA(
      templateName, comp, new ComponentHelper(comp), existsHwc).toString();
    Optional<String> formattedCode = Optional.empty();

    try {
      formattedCode = Optional.of(this.getCodeFormatter().formatSource(generatedCode));
    } catch (FormatterException e) {
      Log.warn(MA2JavaError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, templateName, comp.getFullName(), e.getMessage()));
    }

    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(generatedCode));
  }

  /**
   * Generates java code that represents the result of the component {@code comp}.
   */
  protected void generateComponentResult(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);

    final String templateName = "templates.result.ResultClass.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(comp, RESULT_ADDENDUM);
    final String usedAddendum = existsHwc ? RESULT_ADDENDUM + "TOP" : RESULT_ADDENDUM;
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(comp, usedAddendum).toString()
    );

    String generatedCode = getEngine().generateNoA(
      templateName, comp, new ComponentHelper(comp), existsHwc).toString();
    Optional<String> formattedCode = Optional.empty();

    try {
      formattedCode = Optional.of(this.getCodeFormatter().formatSource(generatedCode));
    } catch (FormatterException e) {
      Log.warn(MA2JavaError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, templateName, comp.getFullName(), e.getMessage()));
    }

    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(generatedCode));
  }

  /**
   * Generates the java base class for representing the component {@code comp}.
   */
  protected void generateComponentBaseClass(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);

    final String templateName = "templates.component.ComponentClass.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(comp, COMPONENT_ADDENDUM);
    final String usedAddendum = existsHwc ? COMPONENT_ADDENDUM + "TOP" : COMPONENT_ADDENDUM;
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(comp, usedAddendum).toString()
    );

    String generatedCode = getEngine().generateNoA(
      templateName, comp, new ComponentHelper(comp), new Identifier(comp), existsHwc).toString();
    Optional<String> formattedCode = Optional.empty();

    try {
      formattedCode = Optional.of(this.getCodeFormatter().formatSource(generatedCode));
    } catch (FormatterException e) {
      Log.warn(MA2JavaError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, templateName, comp.getFullName(), e.getMessage()));
    }

    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(generatedCode));
  }

  /**
   * Generates the java class that represents the implementation part of the component {@code comp}.
   */
  protected void generateComponentImpl(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);

    final String templateName = "templates.implementation.ImplementationClass.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(comp, IMPL_ADDENDUM);
    final String usedAddendum = existsHwc ? IMPL_ADDENDUM + "TOP" : IMPL_ADDENDUM;
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(comp, usedAddendum).toString()
    );

    String generatedCode = getEngine().generateNoA(
      templateName, comp, new ComponentHelper(comp), new Identifier(comp), existsHwc).toString();
    Optional<String> formattedCode = Optional.empty();

    try {
      formattedCode = Optional.of(this.getCodeFormatter().formatSource(generatedCode));
    } catch (FormatterException e) {
      Log.warn(MA2JavaError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, templateName, comp.getFullName(), e.getMessage()));
    }

    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(generatedCode));
  }

  /**
   * Generates a component deployment class that regularly executes {@code comp}
   */
  protected void generateComponentDeployment(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);

    final String templateName = "templates.Deploy.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(comp, "Deploy", "");
    final String usedAddendum = existsHwc ? "TOP" : "";
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(comp, "Deploy", usedAddendum).toString()
    );

    String generatedCode = getEngine().generateNoA(
      templateName, comp, new ComponentHelper(comp), existsHwc).toString();
    Optional<String> formattedCode = Optional.empty();

    try {
      formattedCode = Optional.of(this.getCodeFormatter().formatSource(generatedCode));
    } catch (FormatterException e) {
      Log.warn(MA2JavaError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, templateName, comp.getFullName(), e.getMessage()));
    }

    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(generatedCode));
  }

  protected Path getPackageAsPath(@NotNull ComponentTypeSymbol comp) {
    return Paths.get(Names.getPackageFromPath(comp.getPackageName()));
  }

  protected Path getFileAsPath(@NotNull ComponentTypeSymbol comp, @NotNull String addendum) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(addendum);
    return Paths.get(comp.getFullName().replaceAll("\\.", "/") + addendum + FILE_EXTENSION);
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
