/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.ma2kotlin.codegen.KotlinBuilder;
import org.apache.commons.cli.CommandLine;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class ModeArcTool extends MontiArcTool {

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    Log.enableFailQuick(false);
    ModeArcTool tool = new ModeArcTool();
    tool.init();
    tool.run(args);
  }

  @Override
  public void runAdditionalTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine command) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(command);
    super.runAdditionalTasks(asts, command);
    Log.info("Generate kotlin classes from component models", "ModeArcTool");
    new KotlinBuilder(command.getOptionValue("path")).writeComponentsCodes(asts);
  }
}