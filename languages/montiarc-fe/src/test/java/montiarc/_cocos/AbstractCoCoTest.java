/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import montiarc.MontiArcTool;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import arcbasis.util.Error;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public abstract class AbstractCoCoTest extends AbstractTest {

  protected static final String MODEL_PATH = "montiarc/_cocos/";
  protected MontiArcCoCoChecker checker;
  protected MontiArcTool cli;

  /**
   * This method that facilitates stating arguments for parameterized tests. By using an elliptical parameter this
   * method removes the need to explicitly create arrays.
   *
   * @param model  model to test
   * @param errors all expected errors
   */
  protected static Arguments arg(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    return Arguments.of(model, errors);
  }

  protected MontiArcTool getCLI() {
    return this.cli;
  }

  @Override
  @BeforeEach
  public void init() {
    super.init();
    setUpCLI();
    setUpChecker();
  }

  public void setUpCLI() {
    this.cli = new MontiArcTool();
    this.getCLI().init();
    Log.enableFailQuick(false);
  }

  protected MontiArcCoCoChecker getChecker() {
    return this.checker;
  }

  public void setUpChecker() {
    this.checker = new MontiArcCoCoChecker();
    this.registerCoCos(this.checker);
  }

  /**
   * processes the given test model (parse, run scopes genitor, complete symbol table)
   *
   * @param model name of the file (with the file ending)
   * @return generated top ast element
   */
  protected ASTMACompilationUnit parseAndCreateAndCompleteSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    ASTMACompilationUnit ast = this.getCLI().parse(getPathToModel(model)).orElse(null);
    this.getCLI().createSymbolTable(ast);
    this.getCLI().completeSymbolTable(ast);
    return ast;
  }

  /**
   * processes the given test model (parse, run scopes genitor)
   *
   * @param model name of the file (with the file ending)
   * @return generated top ast element
   */
  protected ASTMACompilationUnit parseAndCreateSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    ASTMACompilationUnit ast = this.getCLI().parse(getPathToModel(model)).orElse(null);
    this.getCLI().createSymbolTable(ast);
    return ast;
  }

  /**
   * @param modelFile file that contains the model to test (with file ending)
   * @return {@link Path path} to the model file
   */
  protected Path getPathToModel(@NotNull String modelFile) {
    Preconditions.checkNotNull(modelFile);
    return Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, getPackage(), modelFile);
  }

  /**
   * @return the model path and package in which the test models are located
   */
  abstract protected String getPackage();

  /**
   * Provider for CoCos to execute during tests.
   * @param checker object to add the cocos to
   */
  abstract protected void registerCoCos(@NotNull MontiArcCoCoChecker checker);

  /**
   * Tries to parse a model and coco-checks it.
   * The found errors-codes are then compared with the given one.
   * The {@link org.junit.jupiter.api.Assertions} fail, if the error codes do not match.
   * @param model Filename of the model to test. Relative to {@link #getPackage()} and given with the ".arc" file ending
   * @param errors expected error codes or an empty array if the model is correct
   */
  public void testModel(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(errors, getPathToModel(model).toAbsolutePath());
  }

  protected ASTComponentType parseAndLoadAllSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    Path path = Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, this.getPackage());

    this.getCLI().loadSymbols(MontiArcMill.globalScope().getFileExt(), path);
    Collection<ASTMACompilationUnit> asts = this.getCLI().parse(".arc", path);
    this.getCLI().createSymbolTable(asts);
    this.getCLI().completeSymbolTable(asts);

    Preconditions.checkState(MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).isPresent());
    return MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).get().getAstNode();
  }
}