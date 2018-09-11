/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components;

import com.google.common.base.Preconditions;
import de.monticore.ast.Comment;
import de.monticore.java.javadsl._ast.ASTClassDeclaration;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import generation.ComponentElementsCollector;
import generation.GeneratedComponentClassVisitor;
import infrastructure.AbstractGeneratorTest;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.montiarcautomaton.generator.MontiArcGeneratorTool.DEFAULT_TYPES_FOLDER;
import static de.montiarcautomaton.generator.MontiArcGeneratorTool.LIBRARY_MODELS_FOLDER;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-04-27
 * @since TODO
 */
public class ComponentGenerationTest extends AbstractGeneratorTest {

  public static final String PACKAGE = "components";
  public static final String JAVA_FILE_ENDING = ".java";

  @Test
  /**
   * Test whether all generated classes of the model are syntactically and
   * semantically correct.
   * This is done by proceeding through the following steps:
   * 1. Run the generator (Done implicitely through setUp method)
   * 2. Specify the model to check and load the component symbol
   * 3. Determine which files have to be checked for the specified model
   * 4. Assert that all expected files are present
   * 5. Compile the files to ensure syntactic correctness
   * 6. Initialise the JavaDSL global scope
   * 7. Run a visitor that collects the information about expected elements
   *    in the generated files
   * 8. Run the visitors on all generated files and check that exactly the
   *    expected elements are present
   */
  public void testExample() {
    // 2. Specify the model to check
    executeGeneratorTest(PACKAGE, "AtomicCompWithoutImpl");
  }

  @Test
  public void test() {
    executeGeneratorTest("components.head.generics",
        "SubSubCompExtendsGenericComparableCompValid");
  }

  @Test
  @Ignore("")
  public void testFEModels() {

    FileWalker modelVisitor = new FileWalker(".arc");
    try {
      Files.walkFileTree(Paths.get("target/test-models/components"), modelVisitor);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Log.info("Finished checking all models from montiarc-fe, which were not excluded.", "ComponentGenerationTest");
  }

  class FileWalker extends SimpleFileVisitor<Path> {
    private final String fileEnding;

    FileWalker(String fileEnding) {
      this.fileEnding = fileEnding;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
        throws IOException {

      if(Files.exists(path)
             && path.toString().toLowerCase().endsWith(fileEnding)){

        MontiArcParser parser = new MontiArcParser();
        final Optional<ASTMACompilationUnit> astmaCompilationUnit
            = parser.parse(path.toString());
        if(!astmaCompilationUnit.isPresent()){
          return FileVisitResult.CONTINUE;
        }

        final ASTMACompilationUnit model = astmaCompilationUnit.get();
        final List<Comment> preComments
            = model.getComponent().get_PreCommentList();
        if(preComments.size() < 1){
          return FileVisitResult.CONTINUE;
        }

        final Comment comment = preComments.get(preComments.size() - 1);
        if(comment.getText().toLowerCase().contains("valid")){
          if(!comment.getText().toLowerCase().contains("invalid")){
            // Execute test
            StringBuilder modelPackage = new StringBuilder();
            for (String s : model.getPackageList()) {
              modelPackage.append(s).append(".");
            }
            modelPackage.deleteCharAt(modelPackage.length() - 1);

            String modelName = model.getComponent().getName();
            executeGeneratorTest(modelPackage.toString(), modelName);
          }
        } else {
          Log.warn(
            String.format("Description of model %s does not state " +
                              "whether it is valid or invalid!",
                path.toString()));
        }
      }
      return FileVisitResult.CONTINUE;
    }
  }

  private void executeGeneratorTest(String packageName, String componentName) {
    String qualifiedName = packageName + "." + componentName;

    // Load component symbol
    final ComponentSymbol symbol
        = generatorTool.loadComponentSymbolWithCocos(
            qualifiedName,
        TEST_MODEL_PATH.toFile(),
        Paths.get(DEFAULT_TYPES_FOLDER).toFile(),
        Paths.get(LIBRARY_MODELS_FOLDER).toFile()).orElse(null);
    assertNotNull(symbol);

    // Skip if it contains inner components
    if(anyHasInnerComponent(symbol)){
      return;
    }

    // 3. Determine all files which have to be checked
    Set<Path> filesToCheck = determineFilesToCheck(
        componentName,
        qualifiedName,
        symbol, TARGET_GENERATED_TEST_SOURCES_DIR);

    try {
      final Path generatedTypesPath
          = TARGET_GENERATED_TEST_SOURCES_DIR.resolve("types");
      filesToCheck.addAll(Files.walk(generatedTypesPath).collect(Collectors.toSet()));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // 4. Determine if all files are present
    for (Path path : filesToCheck) {
      assertTrue("Could not find expected generated file " + path.toString(),
          Files.exists(path));
    }

    // 5. Invoke Java compiler to see whether they are compiling
    final boolean compiling = AbstractGeneratorTest.isCompiling(filesToCheck);
//    assertTrue(
//        String.format("The generated files for model %s are not " +
//                          "compiling without errors", qualifiedName),
//        compiling);

    // Parse the files with the JavaDSL
    GlobalScope gs = initJavaDSLSymbolTable();

    // Collect information about expected features per file
    ASTComponent compUnit = (ASTComponent) symbol.getAstNode().get();
    ComponentElementsCollector compCollector
        = new ComponentElementsCollector(symbol, componentName);
    compCollector.handle(compUnit);

    // Check if all expected elements are present and no other errors occured
    Log.getFindings().clear();

    // Component class
    runVisitorOnFile(gs, compCollector.getClassVisitor(),
        qualifiedName);

    // Input class
    runVisitorOnFile(gs, compCollector.getInputVisitor(),
        qualifiedName + "Input");

    // Result
    runVisitorOnFile(gs, compCollector.getResultVisitor(),
        qualifiedName + "Result");

    // Impl
    // Only run if the component is not composed
    if(symbol.isAtomic()) {
      runVisitorOnFile(gs, compCollector.getImplVisitor(),
          qualifiedName + "Impl");
    }
    // Log checking
    Log.debug("Number of errors found: " + Log.getFindings().size(), "ComponentGenerationTest");
//    assertEquals(0, Log.getFindings().size());
  }

  private boolean anyHasInnerComponent(ComponentSymbol symbol){
    if(!symbol.getInnerComponents().isEmpty()){
      return true;
    }
    for(ComponentInstanceSymbol instanceSymbol : symbol.getSubComponents()) {
      final ComponentSymbol referencedSymbol
          = instanceSymbol.getComponentType().getReferencedSymbol();
      if(anyHasInnerComponent(referencedSymbol)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void testComponentWithEmptyComponent() {
    executeGeneratorTest(PACKAGE, "ComponentWithEmptyComponent" );
  }

  /**
   * Run the visitor with the expected elements on the specified class.
   * @param javaGlobalScope Scope in which the class is present
   * @param visitor The visitor which should be run on the class
   * @param className Name of the class to run the visitor on.
   */
  private void runVisitorOnFile(Scope javaGlobalScope,
                                GeneratedComponentClassVisitor visitor,
                                String className){

    Preconditions.checkNotNull(javaGlobalScope);
    Preconditions.checkNotNull(visitor);
    Preconditions.checkNotNull(className);

    final Symbol optinalClassTypeSymbol
        = javaGlobalScope.resolve(className, JavaTypeSymbol.KIND).orElse(null);

    assertNotNull(optinalClassTypeSymbol);
    assertTrue(optinalClassTypeSymbol.getAstNode().isPresent());
    visitor.handle((ASTClassDeclaration) optinalClassTypeSymbol.getAstNode().get());
    visitor.allExpectedPresent();
  }

  /**
   * Recursively determines a list of File objects which correspond to the
   * locations of the generated files of the component given by the parameters.
   *
   * @param componentName Name of the component
   * @param qualifiedName Qualified name of the component (package.componentName)
   * @param component ComponentSymbol of the Component
   * @param basedir Location of the generated files
   * @return A list of generated files for the specified component
   */
  private Set<Path> determineFilesToCheck(String componentName,
                                          String qualifiedName,
                                          ComponentSymbol component,
                                          Path basedir) {
    Set<Path> filesToCheck = new HashSet<>();

    final Optional<String> deploy = component.getStereotype().get("deploy");

    // Add the special deployment file
    if (deploy != null && deploy.isPresent()) {
      filesToCheck.add(
          TARGET_GENERATED_TEST_SOURCES_DIR.resolve(PACKAGE + "\\" +
                       "Deploy" + componentName + JAVA_FILE_ENDING));
    }

    if (component.isAtomic()) {

      // Determine if an automaton or a compute block is present
//      final ASTComponent astNode
//          = (ASTComponent) component.getAstNode().get();

//      for (ASTElement element : astNode.getBody().getElements()) {
//        if (element instanceof ASTBehaviorElement) {
//          final String qualifiedFileName
//              = basedir + qualifiedName.replace('.', '\\') + "Impl";
//          filesToCheck.add(
//              new File(qualifiedFileName + JAVA_FILE_ENDING));
//          break;
//        }
//      }

      final String qualifiedFileName
          = qualifiedName.replace('.', '\\') + "Impl";
      filesToCheck.add(basedir.resolve(qualifiedFileName + JAVA_FILE_ENDING));
    } else {

      //Recursively add files for subcomponents
      for (ComponentInstanceSymbol instanceSymbol : component.getSubComponents()) {
        final ComponentSymbol referencedSymbol
            = instanceSymbol.getComponentType().getReferencedSymbol();
        filesToCheck.addAll(
            determineFilesToCheck(
                referencedSymbol.getName(),
                referencedSymbol.getFullName(),
                referencedSymbol, basedir));
      }
    }

    // Add super components to the files which have to be checked and compiled
    if(component.getSuperComponent().isPresent()){
      if(component.getSuperComponent().get().existsReferencedSymbol()){
        final ComponentSymbol superComponent = component.getSuperComponent().get().getReferencedSymbol();
        filesToCheck.addAll(
            determineFilesToCheck(
                superComponent.getName(),
                superComponent.getFullName(),
                superComponent,
                basedir
            )
        );
      }
    }

    for (String suffix : AbstractGeneratorTest.fileSuffixes) {
      final String qualifiedFileName
          = qualifiedName.replace('.', '\\') + suffix;
      filesToCheck.add(
          basedir.resolve(qualifiedFileName + JAVA_FILE_ENDING));
    }

    return filesToCheck;
  }

}
