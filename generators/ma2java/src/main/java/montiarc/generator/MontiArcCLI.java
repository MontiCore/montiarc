/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.codegen.xtend.MAAGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

public class MontiArcCLI extends montiarc.MontiArcCLI {

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    MontiArcCLI cli = new MontiArcCLI();
    cli.init();
    cli.run(args);
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);

    options.addOption(org.apache.commons.cli.Option.builder("t")
      .longOpt("target")
      .hasArgs()
      .desc("Sets the target path for generated files.")
      .build());
    options.addOption(org.apache.commons.cli.Option.builder("hwc")
      .hasArgs()
      .desc("Sets the artifact path for handwritten code.")
      .build());

    return super.addStandardOptions(options);
  }

  @Override
  public void runAdditionalTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cli) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cli);
    super.runAdditionalTasks(asts, cli);
    Log.info("Generate java classes from component models", "MontiArcCLITool");
    this.generate(asts, cli.getOptionValue("target"), Optional.ofNullable(cli.getOptionValue("hwc")).orElse(""));
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
    MAAGenerator generator = new MAAGenerator();
    generator.generateAll(Paths.get(target, Names.getPathFromPackage(symbol.getPackageName())).toFile(), Paths.get(hwc).toFile(), symbol);
  }
}
