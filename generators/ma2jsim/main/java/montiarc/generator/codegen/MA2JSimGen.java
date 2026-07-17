/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;
import modes._ast.ASTModeAutomaton;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcTypeCheck;
import montiarc.generator.util.Helper;
import montiarc.generator.util.MaUnitHelper;
import montiarc.util.LogAspects;
import montiarc.util.MASimError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MA2JSimGen {

  protected static String FILE_EXTENSION = "java";
  protected GeneratorEngine engine;
  protected GeneratorSetup setup;
  protected MA2JSimCodeFormatter formatter;
  protected Helper helper;

  public MA2JSimGen(@NotNull GeneratorSetup setup) {
    this.setup = Preconditions.checkNotNull(setup);
    this.engine = new GeneratorEngine(this.setup);
    this.formatter = new MA2JSimCodeFormatter();
    this.helper = (Helper) setup.getGlex().getGlobalVar("helper");
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
    setup.setGlex(glex());
    String name = SymTypeRelations.class.getCanonicalName();
    try {
      setup.getGlex().setGlobalValue("SymTypeRelations",
        ((BeansWrapper) setup.getConfig().getObjectWrapper())
          .getStaticModels().get(name));
    } catch (TemplateModelException e) {
      Log.errorInternal("Internal error: could not retrieve the static model for " + name);
    }
    return setup;
  }

  protected static GlobalExtensionManagement glex() {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("suffixes", Suffixes.getInstance());
    glex.setGlobalValue("prefixes", Prefixes.getInstance());
    glex.setGlobalValue("log_aspects", LogAspects.getInstance());
    glex.setGlobalValue("helper", new Helper());
    glex.setGlobalValue("javaPrinter", new MA2JSimJavaGenerator());
    glex.setGlobalValue("timing_untimed", Timing.UNTIMED);
    glex.setGlobalValue("MaUnitHelper", new MaUnitHelper());
    glex.setGlobalValue("mill", MontiArcMill.getMill());
    glex.bindTemplateHookPoint("<Component>Body", "montiarc.generator.ma2jsim.unit.Component.ftl");
    glex.bindTemplateHookPoint("<Component>Header", "montiarc.generator.ma2jsim.unit.Header.ftl");
    return glex;
  }

  protected GeneratorEngine getEngine() {
    return engine;
  }

  protected GeneratorSetup getSetup() {
    return this.setup;
  }

  protected MA2JSimCodeFormatter getFormatter() {
    return this.formatter;
  }

  public void generate(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    generateComponentClass(ast);
    generateComponentInstanceBuilder(ast);
    generatePublicApiClass(ast);
    generateContextInterface(ast);

    generateSyncedInputsClass(ast);
    generateBehaviorInterface(ast);
    generateBehaviorClasses(ast);

    ASTMCQualifiedName innerComponentPackage = ast.isPresentPackage() ? ast.getPackage().deepClone() : MontiArcMill.mCQualifiedNameBuilder().build();
    innerComponentPackage.addParts(ast.getArcComponentType().getName());
    for (ComponentTypeSymbol innerComp : ast.getArcComponentType().getSymbol().getSpannedScope().getLocalComponentTypeSymbols()) {
      innerComp.setPackageName(ast.getArcComponentType().getSymbol().getFullName());
      generate(MontiArcMill.mACompilationUnitBuilder().setArcComponentType((ASTArcComponentType) innerComp.getAstNode()).setPackage(innerComponentPackage).build());
    }

    if (ast.getArcComponentType().getBody().streamArcElementsOfType(ASTModeAutomaton.class).findAny().isPresent()) {
      generateContextInterfaceForModeAutomaton(ast);
      generateModeAutomaton(ast);
    }

    if (helper.getVariantHelper().getVariants(ast.getArcComponentType()).size() <= 1
      && ast.getArcComponentType().getSymbol().getTypeParameters().isEmpty()) {
      this.generateComponentDeployment(ast);
      if (!ast.getArcComponentType().getSymbol().getAllPorts().isEmpty()) {
        this.generateComponentMqttDeployment(ast);
        this.generateComponentRestDeployment(ast);
      }
    }
  }

  protected void generateComponentClass(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.CompilationUnitFile.ftl";
    String suffix = Suffixes.COMP_IMPL;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateComponentInstanceBuilder(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.builder.BuilderFile.ftl";
    String suffix = Suffixes.COMP + Suffixes.BUILDER;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generatePublicApiClass(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    final String template = "montiarc.generator.ma2jsim.component.interface.PublicApiFile.ftl";
    String suffix = Suffixes.COMP;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateContextInterface(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.interface.ContextFile.ftl";
    String suffix = Suffixes.CONTEXT;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateSyncedInputsClass(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.behavior.sync.SyncedInputsClass.ftl";
    String suffix = Suffixes.SYNC_MSG;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateBehaviorInterface(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.behavior.interface.EventBehaviorInterfaceFile.ftl";
    String suffix = Suffixes.EVENTS;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateBehaviorClasses(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    List<VariableArcVariantComponentTypeSymbol> variants = helper.getVariantHelper().getVariants(ast.getArcComponentType());
    for (VariableArcVariantComponentTypeSymbol variant : variants) {
      if (variants.size() > 1) MontiArcTypeCheck.enterContext(variant);

      // set variant pretty printer
      this.setup.getGlex().setGlobalValue("javaPrinter", new MA2JSimJavaGenerator(variant));
      final String variantSuffix = helper.getVariantHelper().variantSuffix(variant);

      if (variant.isAtomic()) {
        if (helper.getBehaviorHelper().getAutomatonBehavior((ASTArcComponentType) variant.getAstNode()).isPresent()) {
          generateAutomatonImplementation(ast, variantSuffix, variant);
          generateAutomatonBuilder(ast, variantSuffix, variant);
          generateStatesClass(ast, variantSuffix, variant);
        } else if (helper.getBehaviorHelper().getComputeBehavior((ASTArcComponentType) variant.getAstNode()).isPresent()) {
          generateComputeImplementation(ast, variantSuffix, variant);
        }
      }
      // Reset TypeCheckContext
      if (variants.size() > 1) MontiArcTypeCheck.leaveContext();
    }
    // reset prettyPrinter
    this.setup.getGlex().setGlobalValue("javaPrinter", new MA2JSimJavaGenerator());
  }

  protected void generateAutomatonImplementation(@NotNull ASTMACompilationUnit ast, @NotNull String suffix, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(suffix);
    Preconditions.checkNotNull(variant);

    final String template = "montiarc.generator.ma2jsim.behavior.automata.AutomatonFile.ftl";
    suffix = Suffixes.AUTOMATON + suffix;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc, variant);
  }

  protected void generateComputeImplementation(@NotNull ASTMACompilationUnit ast, @NotNull String suffix, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(suffix);
    Preconditions.checkNotNull(variant);

    final String template = "montiarc.generator.ma2jsim.behavior.compute.ComputeFile.ftl";
    suffix = Suffixes.COMPUTE + suffix;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc, variant);
  }

  protected void generateAutomatonBuilder(@NotNull ASTMACompilationUnit ast, @NotNull String suffix, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(suffix);
    Preconditions.checkNotNull(variant);

    final String template = "montiarc.generator.ma2jsim.behavior.automata.AutomatonBuilderFile.ftl";
    suffix = Suffixes.AUTOMATON + suffix + Suffixes.BUILDER;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc, variant);
  }

  protected void generateStatesClass(@NotNull ASTMACompilationUnit ast, @NotNull String suffix, @NotNull ComponentTypeSymbol variant) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(suffix);
    Preconditions.checkNotNull(variant);

    final String template = "montiarc.generator.ma2jsim.behavior.automata.StatesFile.ftl";
    suffix = Suffixes.STATES + suffix;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc, variant);
  }

  protected void generateModeAutomaton(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.modes.ModeAutomatonFile.ftl";
    String suffix = Suffixes.MODE_AUTOMATON;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, "", suffix, existsHwc);
  }

  protected void generateContextInterfaceForModeAutomaton(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.interface.ContextForModesFile.ftl";
    String suffix = Suffixes.CONTEXT_FOR_MODES;
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), suffix);
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
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), prefix, suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, prefix, suffix, existsHwc, helper.getVariantHelper().getVariants(ast.getArcComponentType()).stream().findFirst().orElse(null));
  }

  /**
   * Generates a component deployment class that regularly executes {@code comp}
   */
  protected void generateComponentMqttDeployment(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.DeployMqtt.ftl";
    String suffix = "";
    String prefix = Prefixes.DEPLOY + "Mqtt";
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), prefix, suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, prefix, suffix, existsHwc, helper.getVariantHelper().getVariants(ast.getArcComponentType()).stream().findFirst().orElse(null));
  }

  /**
   * Generates a component deployment class that regularly executes {@code comp}
   */
  protected void generateComponentRestDeployment(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    final String template = "montiarc.generator.ma2jsim.component.DeployRest.ftl";
    String suffix = "";
    String prefix = Prefixes.DEPLOY + "Rest";
    final boolean existsHwc = existsHWC(ast.getArcComponentType().getSymbol(), prefix, suffix);
    if (existsHwc) suffix += Suffixes.TOP;

    generate(template, ast, prefix, suffix, existsHwc, helper.getVariantHelper().getVariants(ast.getArcComponentType()).stream().findFirst().orElse(null));
  }

  protected void generate(@NotNull String template, @NotNull ASTMACompilationUnit ast,
                          @NotNull String prefix, @NotNull String suffix, boolean existsHwc, Object... templateArguments) {
    final Path outPath = Paths.get(
      this.getSetup().getOutputDirectory().getAbsolutePath(),
      getFileAsPath(ast.getArcComponentType().getSymbol(), prefix, suffix).toString()
    );

    this.setup.getGlex().setGlobalValue("isTop", existsHwc);
    String code = getEngine().generate(template, ast, templateArguments).toString();
    this.setup.getGlex().setGlobalValue("isTop", null);  // Reset

    Optional<String> formattedCode = formatter.format(code);
    if (formattedCode.isEmpty()) {
      Log.warn(MASimError.POST_GENERATION_FORMATTING_FAIL.format(
        outPath, template, ast.getArcComponentType().getSymbol().getFullName()));
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
    final String dir = Names.getPathFromPackage(comp.getPackageName());
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
