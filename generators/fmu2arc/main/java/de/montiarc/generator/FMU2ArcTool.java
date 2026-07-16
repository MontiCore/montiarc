/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.generator;

import com.google.common.base.Preconditions;
import de.montiarc.generator.codegen.FMU2ArcGen;
import de.monticore.io.FileReaderWriter;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool.*;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc.util.MontiArcError;
import no.ntnu.ihb.fmi4j.importer.fmi2.*;
import no.ntnu.ihb.fmi4j.*;
import no.ntnu.ihb.fmi4j.importer.fmi2.Fmu;
import no.ntnu.ihb.fmi4j.modeldescription.variables.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;

import montiarc._symboltable.MontiArcSymbols2Json;

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED;
import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;

public class FMU2ArcTool {

  public static void main(String[] args) {
    FMU2ArcTool tool = new FMU2ArcTool();
    tool.run(args);
  }

  public void run(String[] args) {
    try {
      CommandLineParser cliParser = new DefaultParser();
      if (args.length > 0) {
        if (args.length < 3) {
          printHelp();
        }
        Options options = this.initRunOptions();
        addStandardOptions(options);
        CommandLine cl = cliParser.parse(options, args);
        //possibility for other options
        run(cl);
      }
    } catch (ParseException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), e.getMessage()));
    }
  }

  protected void run(@NotNull CommandLine cl) {
    String[] i = cl.hasOption("i") ? splitPathEntries(cl.getOptionValues("i")) : new String[0];

    String s = cl.getOptionValue("s");

    String o = cl.getOptionValue("o");

    this.run(i, s, o);
  }

  /**
   * Processes input paths (files or directories) to generate Java code.
   * Directories are walked recursively, collecting all .fmu and .jar files.
   * Each file is dispatched to the appropriate processor based on its extension.
   *
   * @param i array of input file/directory paths to process
   * @param s output directory path for the generated symbols
   * @param o output directory path for generated Java code
   */
  protected void run(@NotNull String[] i, String s, @NotNull String o) {
    for (String inputPathStr : i) {
      Path inputPath = Paths.get(inputPathStr);

      if (Files.isDirectory(inputPath)) {
        try (Stream<Path> stream = Files.walk(inputPath)) {
          stream
            .filter(Files::isRegularFile)
            .filter(p -> {
              String lower = p.toString().toLowerCase();
              return lower.endsWith(".fmu") || lower.endsWith(".jar");
            })
            .forEach(f -> {
              if (f.toString().toLowerCase().endsWith(".jar")) {
                processJar(f, o, s);
              } else {
                processFmu(f, inputPath, o, s);
              }
            });
        } catch (IOException e) {
          Log.error("Couldn't read directory: " + e.getMessage());
        }
      } else if (Files.exists(inputPath)) {
        String lower = inputPath.toString().toLowerCase();
        if (lower.endsWith(".jar")) {
          processJar(inputPath, o, s);
        } else {
          processFmu(inputPath, inputPath, o, s);
        }
      } else {
        Log.error("Path doesn't exist: " + inputPathStr);
      }
    }
  }

  public void generate(Fmu myFmu, File fmuFile, Path inputRoot, @NotNull String o) {
    Path relative = inputRoot.relativize(fmuFile.toPath().getParent());
    String packageName = relative.toString().replace(File.separator, ".");

    Path targetDir = Path.of(o + "/" + packageName);

    FMU2ArcGen generator = new FMU2ArcGen(targetDir, packageName);
    generator.generateCompImpl(myFmu, fmuFile);
    generator.generateCompute(myFmu, fmuFile);
    generator.generateSyncMsg(myFmu);
    generator.generateEvents(myFmu);
    generator.generateContext(myFmu);
    generator.generateCompBuilder(myFmu);
    generator.generateComp(myFmu);
    generator.generateDeploy(myFmu);
  }

  public Options initRunOptions() {
    Options options = new Options();
    options = addStandardOptions(options);
    return options;
  }

  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    options.addOption(Option.builder("i")
      .longOpt("input")
      .desc("Generates java code from the specified directory")
      .hasArg()
      .argName("dir")
      .get());
    options.addOption(Option.builder("s")
      .longOpt("symboltable")
      .desc("Serializes the symbol table of the given artifacts to the specified directory")
      .hasArg()
      .argName("dir")
      .get());
    options.addOption(Option.builder("o")
      .longOpt("output")
      .desc("Generates java code to the specified directory")
      .hasArg()
      .argName("dir")
      .get());
    return options;
  }

  protected final @NotNull String[] splitPathEntries(@NotNull String paths) {
    Preconditions.checkNotNull(paths);

    return paths.split(Pattern.quote(File.pathSeparator));
  }

  protected final @NotNull String[] splitPathEntries(@NotNull String[] paths) {
    Preconditions.checkNotNull(paths);
    return Arrays.stream(paths)
      .map(this::splitPathEntries)
      .flatMap(Arrays::stream)
      .toArray(String[]::new);
  }

  protected void processFmu(Path fmuPath, Path inputRoot, String output, String symOutput) {
    File fmuFile = fmuPath.toFile();
    try {
      Fmu myFmu = Fmu.from(fmuFile);
      generate(myFmu, fmuFile, inputRoot, output);

      Path relative = inputRoot.relativize(fmuPath.getParent());
      String packageName = relative.toString().replace(File.separator, ".");

      storeSymTable(myFmu, packageName, symOutput);
    } catch (IOException e) {
      Log.error("Failed to initialize FMU: " + e.getMessage());
    }
  }

  /**
   * Builds and serializes a MontiArc symbol table for the given FMU component.
   * Partitions the FMU's model variables into inputs (including tunable parameters),
   * outputs, and fixed/constant parameters, then constructs the corresponding
   * ComponentTypeSymbol with typed ports and parameters.
   * The resulting artifact scope is serialized to a .arcsym file under symOutput.
   *
   * @param myFmu       the FMU whose model description is used to extract variables
   * @param packageName package name for the generated component symbol
   * @param symOutput   output directory where the .arcsym file will be stored
   */
  public void storeSymTable(Fmu myFmu, String packageName, String symOutput) {
    MontiArcMill.init();
    IMontiArcArtifactScope scope = MontiArcMill.artifactScope();
    IMontiArcScope subscope = MontiArcMill.scope();

    List<TypedScalarVariable<?>> allVariables = myFmu.getModelDescription().getModelVariables().getVariables();

    //Get all Inputs
    List<TypedScalarVariable<?>> syncinputs = allVariables.stream()
      .filter(v -> v.getCausality() == Causality.INPUT)
      .toList();

    //Get Tunable Params (here handled like input ports)
    List<TypedScalarVariable<?>> eventinputs = allVariables.stream()
      .filter(v -> v.getCausality() == Causality.PARAMETER && (v.getVariability() == Variability.TUNABLE))
      .toList();

    // Get Outputs
    List<TypedScalarVariable<?>> outputs = allVariables.stream()
      .filter(v -> v.getCausality() == Causality.OUTPUT)
      .toList();

    // Get never-changing Parameters
    List<TypedScalarVariable<?>> fixedParams = allVariables.stream()
      .filter(v -> v.getCausality() == Causality.PARAMETER && (v.getVariability() == Variability.FIXED || v.getVariability() == Variability.CONSTANT))
      .toList();

    scope.setPackageName(packageName);

    ComponentTypeSymbol comp = MontiArcMill.componentTypeSymbolBuilder()
      .setName(myFmu.getName()).setFullName(packageName + "." + myFmu.getName())
      .setSpannedScope(subscope).setEnclosingScope(scope)
      .setPackageName(packageName)
      .build();

    //build Params
    for (TypedScalarVariable<?> v : fixedParams) {
      VariableSymbol param = MontiArcMill.variableSymbolBuilder()
        .setName(sanitizeName(v.getName())).setFullName(packageName + "." + myFmu.getName() + "." + sanitizeName(v.getName()))
        .setType(getType(v))
        .setEnclosingScope(subscope)
        .build();
      subscope.add(param);
      comp.addParameter(param);
    }
    // add main component to scope
    scope.add(comp);

    //build syncInputs
    for (TypedScalarVariable<?> i : syncinputs) {
      subscope.add(MontiArcMill.portSymbolBuilder()
        .setName(sanitizeName(i.getName())).setFullName(packageName + "." + myFmu.getName() + "." + sanitizeName(i.getName()))
        .setType(getType(i))
        .setIncoming(true).setTiming(TIMED_SYNC).setStronglyCausal(false)
        .setEnclosingScope(subscope)
        .build());
    }

    //build eventInputs
    for (TypedScalarVariable<?> i : eventinputs) {
      subscope.add(MontiArcMill.portSymbolBuilder()
        .setName(sanitizeName(i.getName())).setFullName(packageName + "." + myFmu.getName() + "." + sanitizeName(i.getName()))
        .setType(getType(i))
        .setIncoming(true).setTiming(TIMED).setStronglyCausal(false)
        .setEnclosingScope(subscope)
        .build());
    }

    //build Outputs
    for (TypedScalarVariable<?> o : outputs) {
      subscope.add(MontiArcMill.portSymbolBuilder()
        .setName(sanitizeName(o.getName())).setFullName(packageName + "." + myFmu.getName() + "." + sanitizeName(o.getName()))
        .setType(getType(o))
        .setOutgoing(true).setTiming(TIMED_SYNC).setStronglyCausal(false)
        .setEnclosingScope(subscope)
        .build());
    }
    subscope.setShadowing(true);
    subscope.setEnclosingScope(scope);

    String syms = new MontiArcSymbols2Json().serialize(scope);
    String packagePath = packageName.replace('.', '/');
    Path filepath = Path.of(symOutput, packagePath, myFmu.getName() + ".arcsym");
    try {
      Files.createDirectories(filepath.getParent());
      FileReaderWriter.storeInFile(filepath, syms);
    } catch (IOException e) {
      Log.error("Failed to store Symbols: " + e.getMessage());
    }
  }

  protected void printHelp() {
    org.apache.commons.cli.help.HelpFormatter formatter = org.apache.commons.cli.help.HelpFormatter.builder().get();
    try {
      formatter.printHelp("FMU2ArcTool", "The Options to run the FMU2ArcTool.", initRunOptions(), "", true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void processJar(Path jarPath, String output, String symOutput) {
    try (JarFile jar = new JarFile(jarPath.toFile())) {
      // Use a fixed, neutral prefix so the temp root never leaks into package names
      Path tempRoot = Files.createTempDirectory("fmu_jar_");
      try {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          if (!entry.getName().endsWith(".fmu")) continue;

          // Sanitize the entry path: replace any char illegal in a Java identifier
          // segment with '_', so the generated class names stay valid
          String safeName = sanitizeEntryName(entry.getName());

          // Mirror the sanitized structure under tempRoot so relativize() works
          Path tempFmu = tempRoot.resolve(safeName);
          Files.createDirectories(tempFmu.getParent());

          try (InputStream in = jar.getInputStream(entry)) {
            Files.copy(in, tempFmu, StandardCopyOption.REPLACE_EXISTING);
          }

          processFmu(tempFmu, tempRoot, output, symOutput);
        }
      } finally {
        try (Stream<Path> walk = Files.walk(tempRoot)) {
          walk.sorted(Comparator.reverseOrder())
            .forEach(p -> {
              try { Files.deleteIfExists(p); } catch (IOException e) { Log.warn("Failed to delete temp file: " + p); }
            });
        }
      }
    } catch (IOException e) {
      Log.error("Failed to open JAR: " + jarPath + " — " + e.getMessage());
    }
  }

  protected String sanitizeEntryName(String entryName) {
    String[] segments = entryName.split("/");
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < segments.length; i++) {
      if (i > 0) result.append(File.separator);
      String seg = segments[i];
      //Strip the extension for the last segment, sanitize, then re-add it
      if (i == segments.length - 1 && seg.endsWith(".fmu")) {
        String base = seg.substring(0, seg.length() - 4);
        result.append(sanitizeIdentifier(base)).append(".fmu");
      } else {
        result.append(sanitizeIdentifier(seg));
      }
    }
    return result.toString();
  }

  protected String sanitizeIdentifier(String s) {
    if (s.isEmpty()) return "_";
    StringBuilder sb = new StringBuilder();
    //prefix with '_' if the segment starts with a digit
    if (Character.isDigit(s.charAt(0))) sb.append('_');
    for (char c : s.toCharArray()) {
      sb.append(Character.isJavaIdentifierPart(c) ? c : '_');
    }
    return sb.toString();
  }

  protected SymTypeExpression getType(TypedScalarVariable<?> fmuVar) {
    String fmiType = fmuVar.getType().toString();
    return switch (fmiType) {
      case "INTEGER", "ENUMERATION" -> SymTypeExpressionFactory.createPrimitive(new TypeVarSymbol("int"));
      case "REAL" -> SymTypeExpressionFactory.createPrimitive(new TypeVarSymbol("double"));
      case "BOOLEAN" -> SymTypeExpressionFactory.createPrimitive(new TypeVarSymbol("boolean"));
      case "STRING" -> SymTypeExpressionFactory.createStringType();

      default -> null;
    };
  }

  /**
   * Helper function to deal with weirdly named fmu-variables
   */
  public static String sanitizeName(String varName) {
    if (varName == null) {
      return null;
    }

    return varName.replaceAll("[^a-zA-Z0-9_]", "_");
  }
}
