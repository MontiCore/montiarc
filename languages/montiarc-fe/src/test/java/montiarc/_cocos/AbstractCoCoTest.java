/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc.AbstractTest;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractCoCoTest extends AbstractTest {

  protected static final String MODEL_PATH = "montiarc/_cocos/";
  protected MontiArcCoCoChecker checker;
  protected MontiArcTool tool;

  /**
   * This method that facilitates stating arguments for parameterized tests. By using an elliptical parameter this
   * method removes the need to explicitly create arrays.
   *
   * @param model  model to test
   * @param errors all expected errors
   */
  protected static Arguments arg(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    return Arguments.of(model, errors);
  }

  protected MontiArcTool getTool() {
    return this.tool;
  }

  @Override
  @BeforeEach
  public void init() {
    super.init();
    setUpTool();
    setUpChecker();
  }

  public void setUpTool() {
    this.tool = new MontiArcTool();
  }

  protected MontiArcCoCoChecker getChecker() {
    return this.checker;
  }

  public void setUpChecker() {
    this.checker = new MontiArcCoCoChecker();
    this.registerCoCos(this.checker);
  }

  /**
   * processes the given test model
   *
   * @param model name of the file (with the file ending)
   * @return generated top ast element
   */
  protected ASTMACompilationUnit parseAndLoadSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    ASTMACompilationUnit ast = this.getTool().parse(getPathToModel(model)).orElse(null);
    this.getTool().createSymbolTable(ast);
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
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(errors, getPathToModel(model).toAbsolutePath());
  }
}