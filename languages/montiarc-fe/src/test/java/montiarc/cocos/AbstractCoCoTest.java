/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractCoCoTest extends AbstractTest {

  protected static final String MODEL_PATH = "montiarc/cocos/";
  protected MontiArcCoCoChecker checker;
  protected MontiArcTool tool;

  @BeforeAll
  public static void init() {
    MontiArcMill.init();
    Log.enableFailQuick(false);
  }

  /**
   * method that facilitates stating arguments for parameterized tests. removes the need to explicitly created arrays by
   * using elliptical parameter
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

  @BeforeEach
  public void setUpTool() {
    this.tool = new MontiArcTool();
  }

  protected MontiArcCoCoChecker getChecker() {
    return this.checker;
  }

  @BeforeEach
  public void setUpChecker() {
    this.checker = new MontiArcCoCoChecker();
    this.registerCoCos();
  }

  /**
   * processes the given test model
   *
   * @param model name of the file (with the file ending)
   * @return generated top ast element
   */
  protected ASTMACompilationUnit parseAndLoadSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    Path pathToModel = Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, getPackage(), model);
    ASTMACompilationUnit ast = this.getTool().parse(pathToModel).orElse(null);
    this.getTool().createSymbolTable(ast);
    return ast;
  }

  /**
   * @return the model path and package in which the test models are located
   */
  abstract protected String getPackage();

  /**
   * Provider for CoCos to execute during tests.
   */
  abstract protected void registerCoCos();

  @BeforeEach
  public void setUpLog() {
    Log.getFindings().clear();
  }

  @BeforeEach
  public void clearGlobalScope() {
    MontiArcMill.globalScope().clear();
  }
}