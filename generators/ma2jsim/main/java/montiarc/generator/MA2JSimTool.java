/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.codegen.MA2JSimGen;
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

public class MA2JSimTool extends MontiArcTool {

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    MA2JSimTool tool = new MA2JSimTool();
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
    options.addOption(org.apache.commons.cli.Option.builder("symbolic")
        .longOpt("symbolic-execution")
        .desc("Sets the template to symbolic template).")
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
      this.generate(asts, cl.getOptionValue("output"), Optional.ofNullable(cl.getOptionValue("hwc")).orElse(""), cl.hasOption("symbolic"));
    }
  }

  public void generate(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull String target, @NotNull String hwc, boolean symbolic) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(!target.isEmpty());
    asts.forEach(ast -> this.generate(ast, target, hwc, symbolic));
  }

  public void generate(@NotNull ASTMACompilationUnit ast, @NotNull String target, @NotNull String hwc, boolean symbolic) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(ast.getComponentType().isPresentSymbol());
    Preconditions.checkArgument(!target.isEmpty());
    MA2JSimGen generator = new MA2JSimGen(Paths.get(target), splitPathEntriesToList(hwc));
    generator.generate(ast);
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
