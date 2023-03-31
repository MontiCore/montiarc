/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._cocos.MontiArcCoCos;
import montiarc.generator.codegen.MontiArcGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montiarc.cocos.IdentifiersAreNoJavaKeywords.AutomatonStateNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.ComponentInstanceNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.ComponentTypeNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.FieldNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.ParameterNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.PortNoNamesAreNoJavaKeywords;
import static montiarc.cocos.IdentifiersAreNoJavaKeywords.TypeParameterNamesAreNoJavaKeywords;

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

    return super.addStandardOptions(options);
  }

  @Override
  public void runTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cl) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cl);
    super.runTasks(asts, cl);

    if(cl.hasOption("output")) {
      Log.info("Generate java", "MontiArcTool");
      this.generate(asts, cl.getOptionValue("output"), Optional.ofNullable(cl.getOptionValue("hwc")).orElse(""));
    }
  }

  public void generate(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull String target, @NotNull String hwc) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(!target.isEmpty());
    asts.forEach(ast -> this.generate(ast, target, hwc));
  }

  public void generate(@NotNull ASTMACompilationUnit ast, @NotNull String target, @NotNull String hwc) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(ast.getComponentType().isPresentSymbol());
    Preconditions.checkArgument(!target.isEmpty());
    MontiArcGenerator generator = new MontiArcGenerator(Paths.get(target), splitPathEntriesToList(hwc));
    generator.generate(ast);
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

  /**
   * {@link super#splitPathEntries(String)}, but returns a {@code List<Path>} instead.
   */
  private @NotNull List<Path> splitPathEntriesToList(@NotNull String composedPath) {
    return Arrays.stream(splitPathEntries(composedPath))
      .map(Path::of)
      .collect(Collectors.toList());
  }
}
