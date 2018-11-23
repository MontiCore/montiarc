/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.monticore.ModelingLanguageFamily;
import de.monticore.ast.Comment;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.symboltable.GlobalScope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc.helper.JavaDefaultTypesManager;
import org.junit.Before;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
 */
public class AbstractGeneratorTest {

  public static final String[] fileSuffixes = new String[]{
    "Result", "Input", ""
  };

  private static final boolean REGENERATE = true;

  public static final String IMPLEMENTATION_SUFFIX = "Impl";

  public static final String GENERATED_TEST_SOURCES = "generated-test-sources";
  public static final Path TARGET_GENERATED_TEST_SOURCES_DIR
      = Paths.get(GENERATED_TEST_SOURCES + "/");
  public static final Path TEST_MODEL_PATH
      = Paths.get("target/test-models/");

  protected MontiArcGeneratorTool generatorTool;
  protected static final ArrayList<Path> EXCLUDED_MODELS = new ArrayList<>();

  /*
   * Add excluded models to list.
   * These are models that are valid, but due to different problems/bugs to not
   * generate correctly.
   */
  static {
    /*
     * Requires different symbol table setup that includes a new model path.
     */
    EXCLUDED_MODELS.add(TEST_MODEL_PATH
                            .resolve("components/ComponentFromJar.arc"));

    /*
     * Reason: NullPointerException in TypeCompatibilityChecker
     */
    EXCLUDED_MODELS.add(TEST_MODEL_PATH
                            .resolve("components/head/generics/" +
                                         "UsingComplexGenericParams.arc"));
    /*
     * Associations in CD files will not be generated correctly
     */
    EXCLUDED_MODELS.add(TEST_MODEL_PATH.resolve("types/Units.cd"));

    EXCLUDED_MODELS.add(TEST_MODEL_PATH.resolve("types/Simulation.arc"));

    // Reason: Assignment Matching Trafo is missing
    EXCLUDED_MODELS.add(TEST_MODEL_PATH
                            .resolve("components/body/automaton/" +
                                         "transition/assignments/" +
                                         "ValidAssignmentMatching.arc"));

    /*
     * Reason: Autoconnection is probably not working as intended. For the
     * source/targets of connectors where only the component is specified,
     * the entries in the Connector Symbol are wrong.
     */
    EXCLUDED_MODELS.add(TEST_MODEL_PATH.resolve("components/body/ports/ReferenceConnectorCompletion.arc"));

    /*
     * Reason: It is not clear how the name space hiding of components is suppoesed
     * to be transferred to Java generation
     */
    EXCLUDED_MODELS.add(TEST_MODEL_PATH.resolve("components/NameSpaceHiding.arc"));

    /**
     * Reason: There is a problem with mismatching generic types in the generated files
     * expected Port<Number>, actual Port<T>
     */
    EXCLUDED_MODELS.add(TEST_MODEL_PATH.resolve("components/body/connectors/GenericSourceTypeIsSubtypeOfTargetType.arc"));
  }

  /**
   * Invokes the Java compiler on the given files.
   * Only checks java files. Paths from the argument corresponding to directories
   * will be disregarded.
   *
   * @param paths Files to compile
   * @return true, if there are no compiler errors
   */
  public static boolean isCompiling(Set<Path> paths){
    // Remove directories, non java files and convert to File objects
    List<File> files = paths.stream()
                           .filter(path -> !Files.isDirectory(path))
                           .filter(path -> path.getFileName().toString().endsWith(".java"))
                           .map(Path::toFile)
                           .collect(Collectors.toList());

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager
        = compiler.getStandardFileManager(null, null, null);
    DiagnosticCollector<JavaFileObject> diagnostics
        = new DiagnosticCollector<JavaFileObject>();

    Iterable<? extends JavaFileObject> compilationUnits1 =
        fileManager.getJavaFileObjectsFromFiles(files);
    compiler.getTask(null, fileManager, diagnostics,
        null, null, compilationUnits1).call();

    try {
      fileManager.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
      if(diagnostic.getSource() != null)
      System.out.format("Error on line %d in %s%n %s%n",
          diagnostic.getLineNumber(),
          diagnostic.getSource().toUri(), diagnostic.getMessage(Locale.ENGLISH));
    }
    return diagnostics.getDiagnostics()
               .stream()
               .filter(d -> d.getKind().equals(Diagnostic.Kind.ERROR))
               .collect(Collectors.toList())
               .size() <= 0;
  }

  @Before
  public void setUp() throws Exception {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    generatorTool = new MontiArcGeneratorTool();

    // Clear output folder
    if (REGENERATE && TARGET_GENERATED_TEST_SOURCES_DIR.toFile().exists()) {
      delete(TARGET_GENERATED_TEST_SOURCES_DIR);
    }

    // Test models are assumed to be unpacked by Maven
    assertTrue(Files.exists(TEST_MODEL_PATH));
    assertTrue(Files.exists(Paths.get("target/test-models/components")));
    assertTrue("components dir does exists1", Files.isDirectory(TEST_MODEL_PATH));
    System.out.println("components dir exists1");

    // Remove directories which are not whitelisted as folders with test
    // models and files
    removeNonWhitelistedDirs();

    assertTrue("components dir does exists2", Files.isDirectory(TEST_MODEL_PATH));
    System.out.println("components dir exists2");
    // Remove invalid or unspecified models
    InvalidFileDeleter deleter = new InvalidFileDeleter(".arc");
    Files.walkFileTree(TEST_MODEL_PATH, deleter);
    assertTrue("components dir does exists3", Files.isDirectory(TEST_MODEL_PATH));
    System.out.println("components dir exists3");

    // Remove files which are declared as valid but which still generate
    // errors in the generation process
    for (Path resolvedPath : EXCLUDED_MODELS) {
      Files.deleteIfExists(resolvedPath);
    }
    assertTrue("components dir does exists4", Files.isDirectory(TEST_MODEL_PATH));
    System.out.println("components dir exists4");
    // Generate models (at specified location)
    if (REGENERATE) {
      generatorTool.generate(
          TEST_MODEL_PATH.toFile(),
          TARGET_GENERATED_TEST_SOURCES_DIR.toFile(),
          Paths.get("src/main/java").toFile());
    }

    copyJavaTypesToOutput();
    assertTrue("components dir does exists5", Files.isDirectory(TEST_MODEL_PATH));
    System.out.println("components dir exists5");
  }

  /**
   * Removes directories from the unpacked test models which are not whitelisted.
   * This eliminates unparseable files and unintended files passed to the generator.
   *
   * @throws IOException
   */
  private void removeNonWhitelistedDirs() throws IOException {
    List<String> allowedDirectories = new ArrayList<>();
    allowedDirectories.add("components");
    allowedDirectories.add("types");

    final List<Path> paths
        = Files.walk(TEST_MODEL_PATH, 1, FileVisitOption.FOLLOW_LINKS)
              .collect(Collectors.toList());
    for (Path path : paths) {
      final String pathString = path.toString();
      if(Files.isSameFile(TEST_MODEL_PATH, path)){
        continue;
      }
      final String[] split = pathString.split("\\\\");
      if(!allowedDirectories.contains(split[split.length-1])) {
        delete(path);
      }
    }
  }

  /**
   * Copies java types to the output directory.
   * They are required to compile the generated files of the components.
   *
   * @throws IOException
   */
  private void copyJavaTypesToOutput() throws IOException {
    Files.walkFileTree(TEST_MODEL_PATH, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(dir.equals(TEST_MODEL_PATH)){
          return FileVisitResult.CONTINUE;
        }
        try {
          final Path relativize = TEST_MODEL_PATH.relativize(dir);
          final Path target = TARGET_GENERATED_TEST_SOURCES_DIR.resolve(relativize);
          if(!Files.exists(target)){
            Files.copy(dir, target);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try {
          final Path relativize = TEST_MODEL_PATH.relativize(file);
          final Path target = TARGET_GENERATED_TEST_SOURCES_DIR.resolve(relativize);
          if(relativize.getFileName().toString().endsWith(".java")) {
            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        return FileVisitResult.CONTINUE;
      }
    });
  }

  /**
   * This InvalidFileDeleter is used to delete files from the test-models
   * directory which are either declared as "invalid" or have no such
   * declaration.
   */
  class InvalidFileDeleter extends SimpleFileVisitor<Path> {
    private final String fileEnding;

    InvalidFileDeleter(String fileEnding) {
      this.fileEnding = fileEnding;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {

      if(Files.exists(file) && file.toString().toLowerCase().endsWith(fileEnding)){

        MontiArcParser parser = new MontiArcParser();
        final Optional<ASTMACompilationUnit> astmaCompilationUnit
            = parser.parse(file.toString());
        if(!astmaCompilationUnit.isPresent()){
          // Model is not parseable -> Delete the model
          Files.delete(file);
          Log.debug(
              String.format("Deleted file %s as it is not parseable.",
                  file.toString()),
              "AbstractGeneratorTest");
          return FileVisitResult.CONTINUE;
        }

        final ASTMACompilationUnit model = astmaCompilationUnit.get();
        final List<Comment> preComments
            = model.getComponent().get_PreCommentList();
        if(preComments.size() < 1){
          // Delete file
          Files.delete(file);
          Log.debug(
              String.format("Deleted file %s as it is neither declared " +
                                "as valid or invalid.", file.toString()),
              "AbstractGeneratorTest");
          return FileVisitResult.CONTINUE;
        }

        final Comment comment = preComments.get(preComments.size() - 1);
        if(comment.getText().toLowerCase().contains("valid")){
          if(comment.getText().toLowerCase().contains("invalid")){
            // Delete file
            Files.delete(file);
            Log.debug(
                String.format("Deleted file %s as it is declared " +
                                  "as invalid.", file.toString()),
                "AbstractGeneratorTest");
          }
        } else {
          Files.delete(file);
          Log.debug(
              String.format("Deleted file %s as it is neither declared " +
                                "as valid or invalid.", file.toString()),
              "AbstractGeneratorTest");
        }
      }
      return FileVisitResult.CONTINUE;
    }
  }

  /**
   * Recousively deletes the files/directories for the given path
   * @param path File/Directory to delete
   */
  private void delete(Path path){
    try {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initialize the Symbol table of the generated java files.
   * @return
   */
  protected GlobalScope initJavaDSLSymbolTable() {
    ModelingLanguageFamily family = new ModelingLanguageFamily();
    family.addModelingLanguage(new JavaDSLLanguage());

    Set<Path> paths = new HashSet<>();
    paths.add(Paths.get(GENERATED_TEST_SOURCES));

    ModelPath modelPath = new ModelPath(paths);

    GlobalScope gs = new GlobalScope(modelPath, family);
    JavaDefaultTypesManager.addJavaPrimitiveTypes(gs);
    return gs;
  }
}
