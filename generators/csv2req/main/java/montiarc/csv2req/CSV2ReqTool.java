/* (c) https://github.com/MontiCore/monticore */
package montiarc.csv2req;

import com.google.common.base.Preconditions;
import de.monticore.symbols.requirementsymbols.RequirementSymbolsMill;
import de.monticore.symbols.requirementsymbols._symboltable.IRequirementSymbolsArtifactScope;
import de.monticore.symbols.requirementsymbols._symboltable.RequirementSymbolsSymbols2Json;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Translates the IDs of requirements given as CSV files into requirement
 * symbol tables (.reqsym) of the MontiCore language RequirementSymbols, such
 * that other projects can trace their artifacts to these requirements.
 * <p>
 * For each CSV file {@code <root>/a/b/Reqs.csv}, the tool stores the symbol
 * table {@code <output>/a/b/Reqs.reqsym}. The artifact scope has the package
 * {@code a.b} and the name {@code Reqs}. It contains a requirement symbol per
 * requirement ID, such that the requirement with the ID {@code REQ-001} is
 * resolvable as {@code a.b.Reqs.REQ_001}. IDs, package segments, and the
 * artifact name are sanitized to match the MontiCore token {@code Name}
 * (see {@link RequirementCsvReader#sanitize(String)}).
 * <p>
 * The root of a CSV file is the input directory containing it. For CSV files
 * given directly as input, the root is the innermost given source root
 * containing the file, or the directory of the file otherwise.
 * <p>
 * Arguments of the form {@code @file} are replaced by the lines of the file,
 * each line being one argument.
 */
public class CSV2ReqTool {

  public static final String CSV_FILE_EXTENSION = "csv";
  public static final String SYMBOL_FILE_EXTENSION = "reqsym";

  public static void main(String[] args) {
    Log.init();
    Log.enableFailQuick(false);
    new CSV2ReqTool().run(args);
    if (Log.getErrorCount() > 0) {
      System.exit(1);
    }
  }

  /**
   * Initializes the mill of the language RequirementSymbols. This resets its
   * global state, e.g., the global scope.
   */
  public void init() {
    RequirementSymbolsMill.init();
  }

  /**
   * Initializes the tool, parses the command line arguments, and runs the
   * tool accordingly.
   */
  public void run(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    Options options = this.initOptions();
    try {
      Optional<String[]> expandedArgs = this.expandArgumentFiles(args);
      if (expandedArgs.isEmpty()) {
        return;
      }
      CommandLine cl = new DefaultParser().parse(options, expandedArgs.get());
      if (cl.hasOption("h") || expandedArgs.get().length == 0) {
        this.printHelp(options);
      } else {
        this.init();
        this.run(cl);
      }
    } catch (ParseException e) {
      Log.error(CSV2ReqError.CLI_PARSE.format(e.getMessage()));
    }
  }

  protected void run(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    if (!cl.hasOption("i")) {
      Log.error(CSV2ReqError.MISSING_INPUT.format());
      return;
    }
    if (!cl.hasOption("s")) {
      Log.error(CSV2ReqError.MISSING_OUTPUT.format());
      return;
    }
    List<String> idColumns = cl.hasOption("c")
      ? Arrays.asList(cl.getOptionValues("c"))
      : List.of(RequirementCsvReader.DEFAULT_ID_COLUMN);
    if (idColumns.stream().anyMatch(String::isBlank)) {
      Log.error(CSV2ReqError.CLI_PARSE.format("The ID column names must not be blank"));
      return;
    }
    Optional<Character> delimiter = cl.hasOption("d")
      ? this.toDelimiter(cl.getOptionValue("d"))
      : Optional.of(RequirementCsvReader.DEFAULT_DELIMITER);
    if (delimiter.isEmpty()) {
      Log.error(CSV2ReqError.CLI_PARSE.format("The delimiter must be a single character other than a quote "
        + "or line break, or '\t' for tabs, but is '" + cl.getOptionValue("d") + "'"));
      return;
    }
    this.run(
      this.toPaths(cl.getOptionValues("i")),
      cl.hasOption("r") ? this.toPaths(cl.getOptionValues("r")) : List.of(),
      Path.of(cl.getOptionValue("s")),
      new RequirementCsvReader(idColumns, delimiter.get())
    );
  }

  /**
   * Translates the given CSV files and all CSV files of the given directories,
   * using the {@link RequirementCsvReader#RequirementCsvReader() default reader}.
   *
   * @see #run(List, List, Path, RequirementCsvReader)
   */
  public void run(@NotNull List<Path> inputs, @NotNull Path output) {
    this.run(inputs, List.of(), output, new RequirementCsvReader());
  }

  /**
   * Translates the given CSV files and all CSV files of the given directories.
   * Directories are walked recursively, the directory structure below an
   * input directory or source root determines the package of the requirements.
   * Callers are responsible to {@link #init() initialize} the tool.
   *
   * @param inputs      CSV files or directories containing CSV files
   * @param sourceRoots directories relative to which the packages of CSV files given as inputs are determined
   * @param output      the directory to store the symbol tables in
   * @param reader      the reader to read the requirement IDs with
   */
  public void run(@NotNull List<Path> inputs, @NotNull List<Path> sourceRoots, @NotNull Path output,
                  @NotNull RequirementCsvReader reader) {
    Preconditions.checkNotNull(inputs);
    Preconditions.checkNotNull(sourceRoots);
    Preconditions.checkNotNull(output);
    Preconditions.checkNotNull(reader);

    List<Path> roots = sourceRoots.stream().map(root -> root.toAbsolutePath().normalize()).toList();
    Map<String, Path> artifacts = new HashMap<>();

    for (Path input : inputs) {
      if (Files.isDirectory(input)) {
        try (Stream<Path> files = Files.walk(input)) {
          files.filter(Files::isRegularFile)
            .filter(this::isCsvFile)
            .sorted()
            .forEach(csv -> this.process(csv, input, output, reader, artifacts));
        } catch (IOException | UncheckedIOException e) {
          Log.error(CSV2ReqError.READ_FAILED.format(input, e.getMessage()));
        }
      } else if (!Files.isRegularFile(input)) {
        Log.error(CSV2ReqError.INPUT_NOT_FOUND.format(input));
      } else if (!this.isCsvFile(input)) {
        Log.error(CSV2ReqError.NOT_A_CSV_FILE.format(input));
      } else {
        this.process(input, this.rootOf(input, roots), output, reader, artifacts);
      }
    }
  }

  /**
   * Reads the requirement IDs of the CSV file and stores them as symbol table.
   * Nothing is stored if the CSV file as a whole is malformed or translates to
   * the same artifact as a previously processed CSV file.
   *
   * @param csv       the CSV file to translate
   * @param root      the directory relative to which the package is determined
   * @param output    the directory to store the symbol table in
   * @param reader    the reader to read the requirement IDs with
   * @param artifacts the processed CSV files by the (case-insensitive) qualified
   *                  names of their artifacts, extended by the given CSV file
   */
  protected void process(@NotNull Path csv, @NotNull Path root, @NotNull Path output,
                         @NotNull RequirementCsvReader reader, @NotNull Map<String, Path> artifacts) {
    Optional<String> artifactName = this.artifactNameOf(csv);
    if (artifactName.isEmpty()) {
      Log.error(CSV2ReqError.INVALID_FILE_NAME.format(csv));
      return;
    }
    String packageName = this.packageNameOf(csv, root);
    String qualifiedName = Names.getQualifiedName(packageName, artifactName.get());

    // Symbol files of artifacts differing only in case collide on case-insensitive file systems
    Path previous = artifacts.putIfAbsent(qualifiedName.toLowerCase(Locale.ROOT), csv);
    if (previous != null) {
      Log.error(CSV2ReqError.DUPLICATE_ARTIFACT.format(previous, csv, qualifiedName));
      return;
    }

    Optional<List<String>> ids = reader.read(csv);
    if (ids.isEmpty()) {
      return;
    }

    IRequirementSymbolsArtifactScope scope = this.createSymbolTable(packageName, artifactName.get(), ids.get());

    Path target = output.resolve(Names.getPathFromPackage(packageName))
      .resolve(artifactName.get() + "." + SYMBOL_FILE_EXTENSION);
    try {
      Files.createDirectories(target.getParent());
      new RequirementSymbolsSymbols2Json().store(scope, target.toString());
    } catch (IOException e) {
      Log.error(CSV2ReqError.WRITE_FAILED.format(target, e.getMessage()));
    }
  }

  /**
   * Creates an artifact scope containing a requirement symbol for each requirement ID.
   */
  public IRequirementSymbolsArtifactScope createSymbolTable(@NotNull String packageName,
                                                            @NotNull String artifactName,
                                                            @NotNull List<String> ids) {
    Preconditions.checkNotNull(packageName);
    Preconditions.checkNotNull(artifactName);
    Preconditions.checkNotNull(ids);

    IRequirementSymbolsArtifactScope scope = RequirementSymbolsMill.artifactScope();
    scope.setPackageName(packageName);
    scope.setName(artifactName);

    for (String id : ids) {
      scope.add(RequirementSymbolsMill.requirementSymbolBuilder()
        .setName(id)
        .setFullName(Names.getQualifiedName(scope.getFullName(), id))
        .setPackageName(packageName)
        .setEnclosingScope(scope)
        .build());
    }
    return scope;
  }

  protected boolean isCsvFile(@NotNull Path file) {
    return file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith("." + CSV_FILE_EXTENSION);
  }

  /**
   * @return the innermost source root containing the CSV file, or the directory of the CSV file
   */
  protected Path rootOf(@NotNull Path csv, @NotNull List<Path> roots) {
    Path file = csv.toAbsolutePath().normalize();
    return roots.stream()
      .filter(file::startsWith)
      .max(Comparator.comparingInt(Path::getNameCount))
      .orElse(file.getParent());
  }

  protected String packageNameOf(@NotNull Path csv, @NotNull Path root) {
    Path parent = csv.toAbsolutePath().normalize().getParent();
    Path relative = root.toAbsolutePath().normalize().relativize(parent);
    return StreamSupport.stream(relative.spliterator(), false)
      .map(Path::toString)
      .filter(segment -> !segment.isEmpty())
      .map(RequirementCsvReader::sanitize)
      .collect(Collectors.joining("."));
  }

  /**
   * @return the sanitized name of the CSV file without extension, or empty if
   * the file name consists of the extension only
   */
  protected Optional<String> artifactNameOf(@NotNull Path csv) {
    String fileName = csv.getFileName().toString();
    String baseName = fileName.substring(0, fileName.length() - CSV_FILE_EXTENSION.length() - 1);
    return baseName.isEmpty() ? Optional.empty() : Optional.of(RequirementCsvReader.sanitize(baseName));
  }

  public Options initOptions() {
    Options options = new Options();
    options.addOption(Option.builder("i")
      .longOpt("input")
      .desc("CSV files or directories containing CSV files to translate. Multiple entries are "
        + "separated by the system's path separator.")
      .hasArgs()
      .argName("paths")
      .get());
    options.addOption(Option.builder("r")
      .longOpt("source-root")
      .desc("Directories relative to which the packages of CSV files given as input are determined. "
        + "Multiple entries are separated by the system's path separator.")
      .hasArgs()
      .argName("dirs")
      .get());
    options.addOption(Option.builder("s")
      .longOpt("symboltable")
      .desc("Stores the requirement symbol tables (.reqsym) in the specified directory")
      .hasArg()
      .argName("dir")
      .get());
    options.addOption(Option.builder("c")
      .longOpt("id-column")
      .desc("The (case-insensitive) header names of the column containing the requirement IDs. "
        + "If multiple names are given, the first one present in a CSV file is used. "
        + "Defaults to '" + RequirementCsvReader.DEFAULT_ID_COLUMN + "'.")
      .hasArgs()
      .argName("names")
      .get());
    options.addOption(Option.builder("d")
      .longOpt("delimiter")
      .desc("The character separating the values, '\\t' for tabs. Defaults to '"
        + RequirementCsvReader.DEFAULT_DELIMITER + "'.")
      .hasArg()
      .argName("char")
      .get());
    options.addOption(Option.builder("h")
      .longOpt("help")
      .desc("Prints this help dialog")
      .get());
    return options;
  }

  protected void printHelp(@NotNull Options options) {
    HelpFormatter formatter = HelpFormatter.builder().get();
    try {
      formatter.printHelp("CSV2ReqTool", "Translates the IDs of requirements given as CSV files "
        + "into requirement symbol tables. Arguments of the form @file are replaced by the lines of "
        + "the file.", options, "", true);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Replaces arguments of the form {@code @file} by the non-empty lines of the file.
   * Lines are taken as they are, such that, e.g., a space can be given as delimiter.
   *
   * @return the expanded arguments, or empty if an argument file could not be read
   */
  protected Optional<String[]> expandArgumentFiles(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    List<String> expanded = new ArrayList<>();
    for (String arg : args) {
      if (arg.startsWith("@") && arg.length() > 1) {
        Path file = Path.of(arg.substring(1));
        try {
          Files.readAllLines(file, StandardCharsets.UTF_8).stream()
            .filter(line -> !line.isEmpty())
            .forEach(expanded::add);
        } catch (IOException e) {
          Log.error(CSV2ReqError.READ_ARGUMENT_FILE_FAILED.format(file, e.getMessage()));
          return Optional.empty();
        }
      } else {
        expanded.add(arg);
      }
    }
    return Optional.of(expanded.toArray(String[]::new));
  }

  /**
   * @return the delimiter denoted by the given value, i.e., the value itself
   * if it is a single valid delimiter character or a tab for {@code \t} and
   * {@code tab}, or empty otherwise
   */
  protected Optional<Character> toDelimiter(@NotNull String value) {
    Preconditions.checkNotNull(value);
    if (value.equals("\\t") || value.equalsIgnoreCase("tab")) {
      return Optional.of('\t');
    }
    if (value.length() != 1 || !RequirementCsvReader.isValidDelimiter(value.charAt(0))) {
      return Optional.empty();
    }
    return Optional.of(value.charAt(0));
  }

  protected List<Path> toPaths(@NotNull String[] paths) {
    Preconditions.checkNotNull(paths);
    return Arrays.stream(paths)
      .map(path -> path.split(Pattern.quote(File.pathSeparator)))
      .flatMap(Arrays::stream)
      .filter(path -> !path.isEmpty())
      .map(Path::of)
      .toList();
  }
}
