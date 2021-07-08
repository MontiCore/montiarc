/* (c) https://github.com/MontiCore/monticore */
package montiarc.cli;

import com.google.common.base.Preconditions;
import org.apache.commons.cli.*;
import org.codehaus.commons.nullanalysis.NotNull;

public class CLISetting {

  protected Options options;
  protected DefaultParser parser;

  public CLISetting() {
    this(new Options(), new DefaultParser());
  }

  protected CLISetting(@NotNull Options options, @NotNull DefaultParser parser) {
    Preconditions.checkNotNull(options);
    Preconditions.checkNotNull(parser);
    this.options = options;
    this.parser = parser;
    this.init();
  }

  protected Options getOptions() {
    return this.options;
  }

  public CommandLine handleArgs(@NotNull String[] args) throws ParseException {
    Preconditions.checkNotNull(args);
    return parser.parse(this.options, args, true);
  }

  protected void init() {
    this.initOptions();
  }

  protected void initOptions() {
    this.initHelp();
    this.initFailQuick();
    this.initInput();
    this.initStdin();
    this.initBuiltInTypes();
    this.initSymbolTableExport();
    this.initPrettyPrinter();
    this.initPath();
  }

  protected void initHelp() {
    options.addOption(Option
      .builder(CLIOption.HELP.getName()).longOpt(CLIOption.HELP.getLongName())
      .desc("Prints help information.")
      .build());
  }

  protected void initFailQuick() {
    options.addOption(Option
      .builder(CLIOption.FAIL_QUICK.getName()).longOpt(CLIOption.FAIL_QUICK.getLongName())
      .hasArg().type(Boolean.class)
      .argName("value").optionalArg(true).numberOfArgs(1)
      .desc("Configures if the application should fail quickly on errors. "
        + "`-" + CLIOption.FAIL_QUICK.getName()
        + "` equals to `--" + CLIOption.FAIL_QUICK.getLongName() + " true`. Default is `false`. "
        + "If `true` the check stops at the first error, otherwise tries to find all errors before it stops.")
      .build());
  }

  protected void initInput() {
    options.addOption(Option
      .builder(CLIOption.INPUT.getName()).longOpt(CLIOption.INPUT.getLongName())
      .hasArg().type(String.class)
      .argName("file").numberOfArgs(1)
      .desc("Reads the source file (mandatory) and parses the contents as MontiArc component model.")
      .build());
  }

  protected void initStdin() {
    options.addOption(Option
      .builder(CLIOption.STDIN.getName()).longOpt(CLIOption.STDIN.getLongName())
      .desc("Reads the input MontiArc component model from stdin "
        + "instead of argument `-" + CLIOption.INPUT.getName() + "`.")
      .build());
  }

  protected void initBuiltInTypes() {
    options.addOption(Option
      .builder(CLIOption.BUILD_IN_TYPES.getName()).longOpt(CLIOption.BUILD_IN_TYPES.getLongName())
      .hasArg().type(Boolean.class)
      .argName("useBuiltInTypes").optionalArg(true).numberOfArgs(1)
      .desc("Configures if built-in-types should be considered. "
        + "Default: `true`. `" + CLIOption.BUILD_IN_TYPES.printOption()
        + "` toggles it to `" + CLIOption.BUILD_IN_TYPES.printLongOption() + " false`.")
      .build());
  }

  protected void initSymbolTableExport() {
    options.addOption(Option
      .builder(CLIOption.SYMBOL_TABLE.getName()).longOpt(CLIOption.SYMBOL_TABLE.getLongName())
      .hasArg().type(String.class)
      .argName("file").optionalArg(true).numberOfArgs(1)
      .desc("Stores the symbol table of the MontiArc component model. The default value is `{ComponentName}.sym`.")
      .build());
  }

  protected void initPrettyPrinter() {
    options.addOption(Option
      .builder(CLIOption.PRETTY_PRINT.getName()).longOpt(CLIOption.PRETTY_PRINT.getLongName())
      .hasArg().type(String.class)
      .argName("file").optionalArg(true).numberOfArgs(1)
      .argName("prettyPrint")
      .desc("Prints the input MontiArc component model to stdout or to the specified file (optional).")
      .build());
  }

  protected void initPath() {
    options.addOption(Option
      .builder(CLIOption.PATH.getName()).longOpt(CLIOption.PATH.longName)
      .hasArgs()
      .desc("Artifact path for importable symbols, space separated.")
      .build());
  }
}