/* (c) https://github.com/MontiCore/monticore */
package montiarc.cli;

import com.google.common.base.Preconditions;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._cocos.MontiArcCoCos;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.MontiArcScopesGenitorDelegator;
import montiarc._symboltable.MontiArcSymbols2Json;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.*;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class MontiArcCLI {

  protected MontiArcParser parser;
  protected MontiArcFullPrettyPrinter prettyPrinter;
  protected MontiArcScopesGenitorDelegator symbolTableCreator;
  protected MontiArcCoCoChecker coCoChecker;
  protected MontiArcSymbols2Json symbols2Json;

  public static void main(@NotNull String[] args) throws IOException, ParseException {
    Preconditions.checkNotNull(args);

    Log.initWARN();

    MontiArcCLI cli = new MontiArcCLI();
    CLISetting cliSetting = new CLISetting();
    CommandLine cmd = cliSetting.handleArgs(args);
    try {
      if (cli.handleArgs(cmd)) {
        Log.enableFailQuick(cli.failQuick(cmd));
        cli.run(cmd);
      }
    } catch (AmbiguousOptionException e) {
      Log.error(String.format(MontiArcError.CLI_OPTION_AMBIGUOUS.toString(), e.getOption()));
    } catch (UnrecognizedOptionException e) {
      Log.error(String.format(MontiArcError.CLI_OPTION_UNRECOGNIZED.toString(), e.getOption()));
    } catch (MissingOptionException e) {
      Log.error(String.format(MontiArcError.CLI_OPTION_MISSING.toString(), Joiners.COMMA.join(e.getMissingOptions())));
    } catch (MissingArgumentException e) {
      Log.error(String.format(MontiArcError.CLI_ARGUMENT_MISSING.toString(), e.getOption()));
    }
  }

  protected MontiArcCLI() {
    this(new MontiArcParser(),
      new MontiArcFullPrettyPrinter(),
      new MontiArcScopesGenitorDelegator(),
      MontiArcCoCos.createChecker(),
      new MontiArcSymbols2Json());
  }

  protected MontiArcCLI(@NotNull MontiArcParser parser, @NotNull MontiArcFullPrettyPrinter prettyPrinter,
                        @NotNull MontiArcScopesGenitorDelegator symbolTableCreator, @NotNull MontiArcCoCoChecker coCoChecker,
                        @NotNull MontiArcSymbols2Json symbols2Json) {
    Preconditions.checkNotNull(parser);
    Preconditions.checkNotNull(prettyPrinter);
    Preconditions.checkNotNull(symbolTableCreator);
    Preconditions.checkNotNull(coCoChecker);
    Preconditions.checkNotNull(symbols2Json);
    this.parser = parser;
    this.prettyPrinter = prettyPrinter;
    this.symbolTableCreator = symbolTableCreator;
    this.coCoChecker = coCoChecker;
    this.symbols2Json = symbols2Json;
  }

  protected MontiArcParser getParser() {
    return this.parser;
  }

  protected MontiArcFullPrettyPrinter getPrettyPrinter() {
    return this.prettyPrinter;
  }

  protected MontiArcScopesGenitorDelegator getSymbolTableCreator() {
    return this.symbolTableCreator;
  }

  protected MontiArcCoCoChecker getCoCoChecker() {
    return this.coCoChecker;
  }

  protected MontiArcSymbols2Json getSymbols2Json() {
    return this.symbols2Json;
  }

  protected boolean failQuick(@NotNull CommandLine cmd) {
    Preconditions.checkNotNull(cmd);
    return cmd.hasOption(CLIOption.FAIL_QUICK.getName())
      && Boolean.parseBoolean(cmd.getOptionValue(CLIOption.FAIL_QUICK.getName(), "true"));
  }

  protected boolean handleArgs(@NotNull CommandLine cmd) throws IOException, ParseException {
    Preconditions.checkNotNull(cmd);

    if (cmd.hasOption(CLIOption.HELP.getName())) {
      this.printHelp();
      return false;
    } else if (!cmd.hasOption(CLIOption.INPUT.getName()) && !cmd.hasOption(CLIOption.STDIN.getName())) {
      this.printHelp();
      Log.error(String.format(MontiArcError.CLI_INPUT_OPTION_MISSING.toString(),
        "[" + CLIOption.INPUT.getName() + ", " + CLIOption.STDIN.getName() + "]"));
      return false;
    } else {
      return true;
    }
  }

  protected void run(@NotNull CommandLine cmd) throws IOException {
    Preconditions.checkNotNull(cmd);
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
    MontiArcMill.init();

    final Optional<ASTMACompilationUnit> ast = this.parseComponentModel(cmd);
    if (!ast.isPresent()) {
      System.out.print("Could not create the abstract syntax tree for the provided input model.");
      return;
    } else {
      System.out.print("Successfully parsed '" + ast.get().getComponentType().getName() + "'.");
    }

    this.handlePrettyPrint(cmd, ast.get());

    this.handleBuildInTypes(cmd);

    this.createSymbolTable(cmd, ast.get());

    this.checkCoCos(ast.get());

    this.handleSymbolTableExport(cmd, ast.get());
  }

  protected boolean modelFileExists(@NotNull String modelFile) {
    Path filePath = Paths.get(modelFile);
    return Files.exists(filePath);
  }

  protected void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(110);
    formatter.printHelp("Examples in case the CLI file is called MontiArcCLI.jar: "
        + System.lineSeparator()
        + "java -jar MontiArcCLI.jar -" + CLIOption.INPUT.getName() + " Motor.arc "
        + "--" + CLIOption.PATH.getLongName() + " target:src/models "
        + "-" + CLIOption.BUILD_IN_TYPES.getName() + " true "
        + "-" + CLIOption.SYMBOL_TABLE.getName()
        + System.lineSeparator()
        + "java -jar MontiArcCLI.jar "
        + "-" + CLIOption.INPUT.getName() + " src/Motor.arc "
        + "-" + CLIOption.PRETTY_PRINT.getName() + " target/Motor.arc",
      new CLISetting().getOptions());
  }

  protected void handleBuildInTypes(@NotNull CommandLine cmd) {
    Preconditions.checkNotNull(cmd);
    if (!cmd.hasOption(CLIOption.BUILD_IN_TYPES.getName())
      || Boolean.parseBoolean(cmd.getOptionValue(CLIOption.BUILD_IN_TYPES.getName(), "false"))) {
      BasicSymbolsMill.initializePrimitives();
    }
  }

  protected Optional<ASTMACompilationUnit> parseComponentModel(@NotNull CommandLine cmd) throws IOException {
    Preconditions.checkNotNull(cmd);
    if (cmd.hasOption(CLIOption.INPUT.getName())) {
      String modelFile = cmd.getOptionValue(CLIOption.INPUT.getName());
      if (!this.modelFileExists(modelFile)) {
        System.out.printf(MontiArcError.CLI_INPUT_FILE_NOT_EXIST.toString(), modelFile);
        return Optional.empty();
      } else {
        return this.getParser().parse(modelFile);
      }
    } else if (cmd.hasOption(CLIOption.STDIN.getName())){
      return this.getParser().parse(new BufferedReader(new InputStreamReader(System.in)));
    } else {
      return Optional.empty();
    }
  }

  protected void handlePrettyPrint(@NotNull CommandLine cmd, @NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(cmd);
    Preconditions.checkNotNull(ast);
    if (cmd.hasOption(CLIOption.PRETTY_PRINT.getName())) {
      System.out.print(this.getPrettyPrinter().prettyprint(ast));
    }
  }

  protected void createSymbolTable(@NotNull CommandLine cmd, @NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(cmd);
    Preconditions.checkNotNull(ast);
    if (cmd.hasOption(CLIOption.PATH.getName())) {
      String[] modelPath = cmd.getOptionValues(CLIOption.PATH.getName());
      MontiArcMill.globalScope()
        .setModelPath(new ModelPath(Arrays.stream(modelPath).map(Paths::get).collect(Collectors.toSet())));
    }
    this.getSymbolTableCreator().createFromAST(ast);
  }

  protected void checkCoCos(@NotNull ASTMACompilationUnit ast) {
    this.getCoCoChecker().checkAll(ast);
  }

  protected void handleSymbolTableExport(@NotNull CommandLine cmd, @NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(cmd);
    Preconditions.checkNotNull(ast);
    Preconditions.checkArgument(ast.getEnclosingScope() instanceof IMontiArcArtifactScope);
    if (cmd.hasOption(CLIOption.SYMBOL_TABLE.getName())) {
      String targetFile = cmd.getOptionValue(CLIOption.SYMBOL_TABLE.getName());
      Path symbolPath;
      if (targetFile == null) {
        symbolPath = Paths.get(Names.getPathFromPackage(ast.getPackage().getQName()),
          ast.getComponentType().getName() + ".sym");
      } else {
        symbolPath = Paths.get(targetFile);
      }
      symbols2Json.store((IMontiArcArtifactScope) ast.getEnclosingScope(), symbolPath.toString());
    }
  }
}