/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import com.google.common.base.Preconditions;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Derives feature diagrams from the features and constraints of MontiArc
 * components. Extends the MontiArc CLI by the options <code>--output</code>
 * and <code>--include-subcomponents</code>.
 */
public class Arc2FDTool extends MontiArcTool {

  public static final String OPT_OUTPUT = "o";
  public static final String OPT_INCLUDE_SUBCOMPONENTS = "include-subcomponents";

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    Arc2FDTool tool = new Arc2FDTool();
    tool.init();
    tool.run(args);
  }

  @Override
  public void init() {
    super.init();
    FeatureDiagramMill.init();
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    super.addStandardOptions(options);
    options.addOption(Option.builder(OPT_OUTPUT)
      .longOpt("output")
      .desc("Writes the feature diagrams to the specified directory")
      .hasArg()
      .argName("dir")
      .get());
    options.addOption(Option.builder()
      .longOpt(OPT_INCLUDE_SUBCOMPONENTS)
      .desc("Flattens the features of subcomponents into the feature diagram of their parent")
      .get());
    return options;
  }

  @Override
  protected void printHelp() {
    HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();
    try {
      formatter.printHelp("Arc2FD", " Derives feature diagrams from MontiArc components.", initOptions(), "", true);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  protected void doRun(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    Preconditions.checkArgument(cl.hasOption("i"));

    if (!cl.hasOption(OPT_OUTPUT)) {
      Log.error(MontiArcError.CLI_OPTION_MISSING.format(OPT_OUTPUT));
      return;
    }

    String[] i = splitPathEntries(cl.getOptionValues("i"));

    String[] p = cl.hasOption("path") ? splitPathEntries(cl.getOptionValues("path")) : new String[0];

    Path o = Path.of(cl.getOptionValue(OPT_OUTPUT));

    String pp = cl.hasOption("pp") ? Optional.ofNullable(cl.getOptionValue("pp")).orElse("") : null;

    String s = cl.getOptionValue("s");

    String r = cl.getOptionValue("r");

    boolean c2mc = cl.hasOption("c2mc");

    boolean novar = cl.hasOption("novar");

    boolean recursive = cl.hasOption(OPT_INCLUDE_SUBCOMPONENTS);

    this.doRun(i, p, o, pp, s, r, c2mc, novar, recursive);
  }

  /**
   * Compiles the models (see {@link #compile}), derives their feature diagrams, and writes them to
   * <code>o</code>. Terminates with an error if any error occurred.
   *
   * @param o         Output directory of the feature diagrams
   * @param recursive Whether to include the features of subcomponents
   */
  protected void doRun(@NotNull String[] i,
                       @NotNull String[] p,
                       @NotNull Path o,
                       @Nullable String pp,
                       @Nullable String s,
                       @Nullable String r,
                       boolean c2mc,
                       boolean novar,
                       boolean recursive) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(p);
    Preconditions.checkNotNull(o);
    Preconditions.checkArgument(i.length > 0);

    this.prepareGlobalScope(p, c2mc);
    Set<ASTMACompilationUnit> asts = this.compile(i, pp, s, r, c2mc, novar);
    this.writeFeatureDiagrams(o, this.toFeatureDiagrams(asts, recursive));

    // terminates if errors were reported
    Log.enableFailQuick(true);
  }

  /**
   * Compiles the models at <code>modelPath</code> and derives their feature diagrams, without
   * subcomponent features.
   */
  public Set<ASTFDCompilationUnit> arc2fd(@NotNull Path modelPath) {
    return arc2fd(modelPath, false);
  }

  /**
   * Compiles the models at <code>modelPath</code> and derives their feature diagrams.
   *
   * @param modelPath File or directory containing the models
   * @param recursive Whether to include the features of subcomponents
   */
  public Set<ASTFDCompilationUnit> arc2fd(@NotNull Path modelPath, boolean recursive) {
    Preconditions.checkNotNull(modelPath);

    this.prepareGlobalScope(new String[0], false);
    Set<ASTMACompilationUnit> asts = this.compile(new String[]{modelPath.toString()}, null, null, null, false, false);
    return this.toFeatureDiagrams(asts, recursive);
  }

  /**
   * @param asts      Compiled models
   * @param recursive Whether to include the features of subcomponents
   * @return The feature diagrams of all components that have features and could be transformed
   */
  public Set<ASTFDCompilationUnit> toFeatureDiagrams(@NotNull Collection<ASTMACompilationUnit> asts,
                                                     boolean recursive) {
    Preconditions.checkNotNull(asts);

    Arc2FDTransformer transformer = new Arc2FDTransformer(recursive);
    Set<ASTFDCompilationUnit> fds = new LinkedHashSet<>();
    asts.forEach(ast -> transformer.transform(ast).ifPresent(fds::add));
    return fds;
  }

  protected void prepareGlobalScope(@NotNull String[] symbolPath, boolean c2mc) {
    Preconditions.checkNotNull(symbolPath);

    MontiArcMill.globalScope().clear();
    MontiArcMill.globalScope().init();
    this.initGlobalScope(symbolPath);
    this.initBuiltInSymbols(c2mc);
  }

  @Override
  public Set<ASTMACompilationUnit> compile(@NotNull String[] i,
                                           @Nullable String pp,
                                           @Nullable String s,
                                           @Nullable String r,
                                           boolean c2mc,
                                           boolean novar) {
    Set<ASTMACompilationUnit> asts = super.compile(i, pp, s, r, c2mc, novar);
    // super.compile re-enables fail quick, but we want to report all arc2fd errors
    Log.enableFailQuick(false);
    return asts;
  }

  protected void writeFeatureDiagrams(@NotNull Path outputDir, @NotNull Set<ASTFDCompilationUnit> fds) {
    Preconditions.checkNotNull(outputDir);
    Preconditions.checkNotNull(fds);

    if (!createDirectories(outputDir))
      return;

    for (ASTFDCompilationUnit fd : fds) {
      Path targetDir = fd.isPresentPackage()
        ? outputDir.resolve(Names.getPathFromPackage(fd.getPackage().getQName()))
        : outputDir;

      if (!createDirectories(targetDir))
        continue;

      Path targetFile = targetDir.resolve(fd.getFeatureDiagram().getName() + ".fd");

      try {
        Files.writeString(targetFile, FeatureDiagramMill.prettyPrint(fd, false));
      } catch (IOException e) {
        Log.error(Arc2FDError.IO_EXCEPTION.format(targetFile, e.getMessage()));
      }
    }
  }

  /**
   * @param dir Directory to create, including missing parents
   * @return <code>true</code>, iff the directory exists afterward
   */
  private static boolean createDirectories(@NotNull Path dir) {
    try {
      Files.createDirectories(dir);
      return true;
    } catch (IOException e) {
      Log.error(Arc2FDError.OUTPUT_DIRECTORY.format(dir, e.getMessage()));
      return false;
    }
  }
}
