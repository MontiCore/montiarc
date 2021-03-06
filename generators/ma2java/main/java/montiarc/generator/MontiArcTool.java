/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.codegen.MontiArcGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

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
      .hasArgs()
      .desc("Sets the artifact path for handwritten code (optional).")
      .build());

    return super.addStandardOptions(options);
  }

  @Override
  public void runAdditionalTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cl) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cl);
    super.runAdditionalTasks(asts, cl);
    Log.info("Generate java classes from component models", "MontiArcTool");
    this.generate(asts, cl.getOptionValue("output"), Optional.ofNullable(cl.getOptionValue("hwc")).orElse(""));
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
    this.generate(ast.getComponentType().getSymbol(), target, hwc);
  }

  public void generate(@NotNull ComponentTypeSymbol symbol, @NotNull String target, @NotNull String hwc) {
    Preconditions.checkNotNull(symbol);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(hwc);
    Preconditions.checkArgument(!target.isEmpty());
    MontiArcGenerator generator = new MontiArcGenerator(Paths.get(target), Paths.get(hwc));
    generator.generate(symbol);
  }
}
