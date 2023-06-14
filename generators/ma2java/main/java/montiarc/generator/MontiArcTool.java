/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._cocos.MontiArcCoCos;
import montiarc.cocos.DseSupportedTypes;
import montiarc.generator.codegen.MontiArcGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montiarc.cocos.IdentifiersAreNoJavaKeywords.*;

public class MontiArcTool extends montiarc.MontiArcTool {

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    MontiArcTool tool = new MontiArcTool();
    tool.init();
    tool.run(args);
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    options.addOption(org.apache.commons.cli.Option.builder("o")
      .longOpt("output")
      .hasArgs()
      .desc("Sets the target path for the generated files (optional).")
      .build());
    options.addOption(org.apache.commons.cli.Option.builder("hwc")
      .longOpt("handwritten-code")
      .hasArgs()
      .desc("Sets the artifact path for handwritten code (optional).")
      .build());
    options.addOption(org.apache.commons.cli.Option.builder("dse")
      .longOpt("dynamic-symbolic-execution")
      .desc("Sets the template to symbolic template).")
      .build());
    return super.addStandardOptions(options);
  }

  @Override
  public void runTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cl) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cl);
    super.runTasks(asts, cl);

    if (cl.hasOption("dse")) {
      Log.info("Perform remaining context-condition checks", "MontiArcTool-dse");
      asts.forEach(this::runAdditionalCoCosDse);
    }

    if (cl.hasOption("output")) {
      Log.info("Generate java", "MontiArcTool");
      this.generate(asts, cl.getOptionValue("output"), Optional.ofNullable(cl.getOptionValue("hwc"))
        .orElse(""), cl.hasOption("dse"));
    }
  }

  public void generate(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull String target,
                       @NotNull String hwc, boolean dse) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(!target.isEmpty());

    List<String> componentNames = new ArrayList<>();
    Set<String> imports = new HashSet<>();

    if (dse) {
      asts.forEach(ast -> componentNames.add(ast.getComponentType().getName()));
      asts.forEach(ast -> imports.add(ast.getComponentType().getSymbol().getPackageName()));
      MontiArcGenerator generator = new MontiArcGenerator(Paths.get(target),
        splitPathEntriesToList(hwc));
      asts.forEach(ast -> generator.generateMain(ast, componentNames,
        new ArrayList<>(imports)));
    }
    asts.forEach(ast -> this.generate(ast, target, hwc, dse));
  }

  public void generate(@NotNull ASTMACompilationUnit ast, @NotNull String target,
                       @NotNull String hwc, boolean dse) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(ast.getComponentType().isPresentSymbol());
    Preconditions.checkArgument(!target.isEmpty());
    MontiArcGenerator generator = new MontiArcGenerator(Paths.get(target),
      splitPathEntriesToList(hwc));
    generator.generate(ast, dse);
  }

  @Override
  public void runAdditionalCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    MontiArcCoCoChecker checker = MontiArcCoCos.afterSymTab();
    checker.addCoCo(new PortNoNamesAreNoJavaKeywords());
    checker.addCoCo(new ParameterNamesAreNoJavaKeywords());
    checker.addCoCo(new TypeParameterNamesAreNoJavaKeywords());
    checker.addCoCo(new FieldNamesAreNoJavaKeywords());
    checker.addCoCo(new AutomatonStateNamesAreNoJavaKeywords());
    checker.addCoCo(new ComponentTypeNamesAreNoJavaKeywords());
    checker.addCoCo(new ComponentInstanceNamesAreNoJavaKeywords());

    checker.checkAll(ast);
  }

  public void runAdditionalCoCosDse(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcCoCoChecker checker = MontiArcCoCos.afterSymTab();

    checker.addCoCo(new DseSupportedTypes.DseParameters_VariablesTypes());

    checker.checkAll(ast);
  }

  /**
   * {@link super#splitPathEntries(String)}, but returns a {@code List<Path>} instead.
   */
  private @NotNull List<Path> splitPathEntriesToList(@NotNull String composedPath) {
    return Arrays.stream(splitPathEntries(composedPath))
      .map(Path::of)
      .collect(Collectors.toList());
  }
}