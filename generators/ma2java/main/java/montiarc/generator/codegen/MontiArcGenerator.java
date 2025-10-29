/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.helper.ArcAutomatonHelper;
import montiarc.generator.helper.ComponentHelper;
import montiarc.generator.helper.dse.ComponentHelperDse;
import montiarc.generator.helper.dse.ComponentHelperDseValue;
import montiarc.generator.util.Identifier;
import montiarc.util.MA2JavaError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.eclipse.jdt.core.formatter.CodeFormatter.K_COMPILATION_UNIT;

public class MontiArcGenerator {

  /**
   * This addendum, appended to a component name, makes the name of the java code that represents the component.
   */
  protected static final String COMPONENT_ADDENDUM = "";
  protected static String FILE_EXTENSION = ".java";
  protected GeneratorEngine engine;
  protected GeneratorSetup engineSetup;
  protected CodeFormatter codeFormatter;

  public MontiArcGenerator(@NotNull GeneratorSetup setup) {
    this.engineSetup = Preconditions.checkNotNull(setup);
    this.engine = new GeneratorEngine(this.engineSetup);
    this.codeFormatter = new DefaultCodeFormatter();
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
    glex.setGlobalValue("compHelperDse", new ComponentHelperDse());
    glex.setGlobalValue("compHelperDseValue", new ComponentHelperDseValue());
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

  protected CodeFormatter getCodeFormatter() {
    return this.codeFormatter;
  }

  public void generate(@NotNull ASTMACompilationUnit ast, boolean dse) {
    Preconditions.checkNotNull(ast);

    if (dse) {
      engineSetup.getGlex()
        .replaceTemplate("ma2java.Import.ftl", new TemplateHookPoint("ma2java.dse.Import-dse.ftl"));
      engineSetup.getGlex()
        .replaceTemplate("ma2java.component.Component.ftl", new TemplateHookPoint("ma2java.dse" +
          ".Component-dse.ftl"));
      engineSetup.getGlex()
        .replaceTemplate("ma2java.component.Atomic.ftl", new TemplateHookPoint("ma2java.dse" +
          ".Atomic-dse.ftl"));
      engineSetup.getGlex()
        .replaceTemplate("ma2java.component.Automaton.ftl", new TemplateHookPoint("ma2java.dse" +
          ".Automaton-dse.ftl"));
      engineSetup.getGlex()
        .replaceTemplate("ma2java.component.Port.ftl", new TemplateHookPoint("ma2java.dse" +
          ".Port-dse.ftl"));
      engineSetup.getGlex()
        .replaceTemplate("ma2java.component.Transition.ftl", new TemplateHookPoint("ma2java.dse.Transition-dse.ftl"));

      this.generateComponentDse(ast.getArcComponentType());
    }

    final String template = "ma2java.component.CompilationUnit.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(ast.getArcComponentType().getSymbol(), COMPONENT_ADDENDUM);
    final String usedAddendum = existsHwc ? COMPONENT_ADDENDUM + "TOP" : COMPONENT_ADDENDUM;
    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(ast.getArcComponentType().getSymbol(), usedAddendum).toString()
    );

    String code = getEngine().generateNoA(template, ast, existsHwc).toString();

    formatFile(code, outPath, template, ast.getArcComponentType());

    if (ast.getArcComponentType().getSymbol().getAllPorts().isEmpty()
      && ast.getArcComponentType().getSymbol().getParameterList().isEmpty()
      && ast.getArcComponentType().getSymbol().getTypeParameters().isEmpty()) {
      this.generateComponentDeployment(ast.getArcComponentType());
    }
  }

  /**
   * Generates a component deployment class that regularly executes {@code comp}
   */
  protected void generateComponentDeployment(@NotNull ASTArcComponentType comp) {
    final String templateName = "ma2java.component.Deploy.ftl";
    final boolean existsHwc = existsHandWrittenCodeFor(comp.getSymbol(), "Deploy", "");
    final String addendum = existsHwc ? "TOP" : "";

    final Path outPath = getCodePath(comp, "Deploy", addendum);

    String generatedCode = getEngine().generateNoA(
      templateName, comp, existsHwc).toString();

    formatFile(generatedCode, outPath, templateName, comp);
  }

  /**
   * Generates multiple classes that are needed for the dse application
   *
   * @param comp
   */
  protected void generateComponentDse(@NotNull ASTArcComponentType comp) {

    String[] templateNames = new String[]{"ma2java.dse.tool.DSEMain.ftl", "ma2java.dse.tool.DSE" +
      ".ftl"};
    String[] prefixNames = new String[]{"DSEMain", "DSE"};

    int i = 0;
    for (String templateName : templateNames) {
      final String currentPrefixName = prefixNames[i];
      generateDseFile(comp, templateName, currentPrefixName, "");
      i++;
    }

    String[] ListerNames = new String[]{"ListerIn", "ListerOut", "ListerParameter",
      "ListerExpression", "ListerExprOut"};

    for (String listerName : ListerNames) {
      generateDseFile(comp, "ma2java.dse.tool.Lister.ftl", listerName, listerName);
    }
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

    return existsHandWrittenCodeFor(comp, "", addendum);
  }

  protected boolean existsHandWrittenCodeFor(
    @NotNull ComponentTypeSymbol comp, @NotNull String prefix, @NotNull String addendum) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(addendum);

    String simpleHwcName = prefix + comp.getName() + addendum;
    String fullHwcName = comp.getPackageName().equals("") ?
      simpleHwcName
      : comp.getPackageName() + "." + simpleHwcName;

    return GeneratorEngine.existsHandwrittenClass(this.getEngineSetup().getHandcodedPath(), fullHwcName);
  }

  /**
   * Generates the main entry point and abstract classes for the dse application
   */
  public void generateMain(@NotNull ASTMACompilationUnit ast, List<String> names,
                           List<String> imports) {

    ASTArcComponentType comp = ast.getArcComponentType();
    final String templateName = "ma2java.dse.tool.Main.ftl";

    final Path outPath = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(), "main/MainDse" + FILE_EXTENSION
    );

    String generatedCode = getEngine().generateNoA(
      templateName, ast, names, imports).toString();

    formatFile(generatedCode, outPath, templateName, comp);

    final String templateNames = "ma2java.dse.tool.DSEMainAbstract.ftl";

    final Path outPaths = Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(), "main/DSEMain" + FILE_EXTENSION
    );

    String generatedCodes = getEngine().generateNoA(templateNames).toString();

    formatFile(generatedCodes, outPaths, templateNames, comp);
  }

  /**
   * Helper function to generate extra classes
   */
  public void generateDseFile(@NotNull ASTArcComponentType comp, String templateName,
                              String prefixName, String listerType) {

    final boolean existsHwc = existsHandWrittenCodeFor(comp.getSymbol(), prefixName, "");
    final String addendum = existsHwc ? "TOP" : "";

    final Path outPath = getCodePath(comp, prefixName, addendum);

    String generatedCode = getEngine().generateNoA(
      templateName, comp, existsHwc, listerType).toString();

    formatFile(generatedCode, outPath, templateName, comp);
  }

  protected Path getCodePath(@NotNull ASTArcComponentType comp, String prefixName, String addendum) {
    return Paths.get(
      this.getEngineSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(comp.getSymbol(), prefixName, addendum).toString()
    );
  }

  /**
   * Helper function to format the generated Code
   */
  protected void formatFile(String generatedCode, Path outPath, String templateName,
                            @NotNull ASTArcComponentType comp) {
    Optional<String> formattedCode = Optional.empty();

    try {
      Document document = new Document(generatedCode);
      codeFormatter.format(K_COMPILATION_UNIT, generatedCode, 0, generatedCode.length(), 0, null).apply(document);
      formattedCode = Optional.of(document.get());
    }
    catch (BadLocationException | java.lang.Error e) {
      Log.warn(MA2JavaError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, templateName, comp.getSymbol().getFullName(), e.getMessage()));
    }
    FileReaderWriter.storeInFile(outPath, formattedCode.orElse(generatedCode));
  }
}
