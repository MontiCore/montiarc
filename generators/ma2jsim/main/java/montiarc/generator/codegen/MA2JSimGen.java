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
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.Timing;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.util.Helper;
import montiarc.util.MASimError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MA2JSimGen {

  protected static String FILE_EXTENSION = "java";
  protected GeneratorEngine engine;
  protected GeneratorSetup setup;
  protected Formatter formatter;

  public MA2JSimGen(@NotNull GeneratorSetup setup) {
    this.setup = Preconditions.checkNotNull(setup);
    this.engine = new GeneratorEngine(this.setup);
    this.formatter = new Formatter();
  }

  public MA2JSimGen(@NotNull Path targetDir) {
    this(createGeneratorSetup(Preconditions.checkNotNull(targetDir), Collections.emptyList()));
  }

  public MA2JSimGen(@NotNull Path targetDir, @NotNull Path hwcPath) {
    this(Preconditions.checkNotNull(targetDir), Collections.singletonList(Preconditions.checkNotNull(hwcPath)));
  }

  public MA2JSimGen(@NotNull Path targetDir, @NotNull List<Path> hwcPath) {
    this(createGeneratorSetup(Preconditions.checkNotNull(targetDir), Preconditions.checkNotNull(hwcPath)));
  }

  protected static GeneratorSetup createGeneratorSetup(@NotNull Path targetDir, @NotNull List<Path> hwcPath) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir.toFile());
    setup.setHandcodedPath(new MCPath(hwcPath));
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("suffixes", Suffixes.getInstance());
    glex.setGlobalValue("prefixes", Prefixes.getInstance());
    glex.setGlobalValue("helper", new Helper());
    glex.setGlobalValue("prettyPrinter", new MA2JSimJavaPrinter());
    glex.setGlobalValue("timing_untimed", Timing.UNTIMED);
    setup.setGlex(glex);
    return setup;
  }

  protected GeneratorEngine getEngine() {
    return engine;
  }

  protected GeneratorSetup getSetup() {
    return this.setup;
  }

  protected Formatter getFormatter() {
    return this.formatter;
  }

  public void generate(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    generateComponentClass(ast);
    generateComponentInstanceBuilder(ast);
    generateContextInterface(ast);

    if (ast.getComponentType().getSymbol().isAtomic()) {
      generateBehaviorClasses(ast);
    }

    if (ast.getComponentType().getSymbol().getAllPorts().isEmpty()
      && ast.getComponentType().getSymbol().getParameters().isEmpty()
      && ast.getComponentType().getSymbol().getTypeParameters().isEmpty()) {
      this.generateComponentDeployment(ast);
    }
  }

  protected void generateComponentClass(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.CompilationUnitFile.ftl";
    String suffix = Suffixes.COMPONENT;
    final boolean existsHwc = existsHWC(ast.getComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateComponentInstanceBuilder(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.builder.BuilderFile.ftl";
    String suffix = Suffixes.COMPONENT + Suffixes.BUILDER;
    final boolean existsHwc = existsHWC(ast.getComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateContextInterface(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.interface.ContextFile.ftl";
    String suffix = Suffixes.CONTEXT;
    final boolean existsHwc = existsHWC(ast.getComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateBehaviorClasses(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    generateBehaviorImplementation(ast);
    generateBehaviorBuilder(ast);
    generateStatesClass(ast);
  }

  protected void generateBehaviorImplementation(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.behavior.Behavior.ftl";
    String suffix = Suffixes.AUTOMATON;
    final boolean existsHwc = existsHWC(ast.getComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateBehaviorBuilder(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.behavior.BehaviorBuilder.ftl";
    String suffix = Suffixes.AUTOMATON + Suffixes.BUILDER;
    final boolean existsHwc = existsHWC(ast.getComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateStatesClass(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.behavior.automata.StatesFile.ftl";
    String suffix = Suffixes.STATES;
    final boolean existsHwc = existsHWC(ast.getComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  /**
   * Generates a component deployment class that regularly executes {@code comp}
   */
  protected void generateComponentDeployment(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.Deploy.ftl";
    String suffix = "";
    String prefix = Prefixes.DEPLOY;
    final boolean existsHwc = existsHWC(ast.getComponentType().getSymbol(), prefix, suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, prefix, suffix, existsHwc);
  }

  protected void generate(@NotNull String template, @NotNull ASTMACompilationUnit ast,
                          @NotNull String prefix, @NotNull String suffix, boolean existsHwc) {
    final Path outPath = Paths.get(
      this.getSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(ast.getComponentType().getSymbol(), prefix, suffix).toString()
    );

    String code = getEngine().generateNoA(template, ast, existsHwc).toString();
    Optional<String> formattedCode = Optional.empty();

    try {
      formattedCode = Optional.of(this.getFormatter().formatSource(code));
    } catch (FormatterException e) {
      Log.warn(MASimError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, template, ast.getComponentType().getSymbol().getFullName(), e.getMessage()));
    }

    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(code));
  }

  protected Path getFileAsPath(@NotNull ComponentTypeSymbol comp,
                               @NotNull String prefix,
                               @NotNull String suffix) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(suffix);
    final String file = Names.getFileName(prefix + comp.getName() + suffix, FILE_EXTENSION);
    final String dir = Names.getPackageFromPath(comp.getPackageName());
    return Paths.get(dir, file);
  }

  protected boolean existsHWC(@NotNull ComponentTypeSymbol comp, @NotNull String suffix) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(suffix);
    return GeneratorEngine.existsHandwrittenClass(this.getSetup().getHandcodedPath(), comp.getFullName() + suffix);
  }

  protected boolean existsHWC(@NotNull ComponentTypeSymbol comp,
                              @NotNull String prefix,
                              @NotNull String suffix) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(suffix);
    return GeneratorEngine.existsHandwrittenClass(
      this.getSetup().getHandcodedPath(),
      comp.getPackageName() + "." + prefix + comp.getName() + suffix
    );
  }
}