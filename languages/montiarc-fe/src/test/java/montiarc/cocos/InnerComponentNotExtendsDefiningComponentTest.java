/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis._cocos.InnerComponentNotExtendsDefiningComponent;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class InnerComponentNotExtendsDefiningComponentTest extends AbstractCoCoTest {

  protected static String MODEL_PATH = "montiarc/cocos/";

  protected static String PACKAGE = "innerComponentNotExtendsDefiningComponent";

  protected ASTMACompilationUnit parseAndLoadSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    Path pathToModel = Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, PACKAGE, model);
    ASTMACompilationUnit ast = this.getTool().parse(pathToModel).orElse(null);
    this.getTool().createSymbolTable(ast);
    return ast;
  }

  @ParameterizedTest
  @ValueSource(strings = {"NoInnerComponent.arc", "InnerComponentExtendsNothing.arc",
    "InnerComponentExtendsNotDefiningComponent.arc"})
  public void shouldNotFindInnerExtendingDefiningComponent(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  @ParameterizedTest
  @ValueSource(strings = {"InnerComponentExtendsDefiningComponent.arc"})
  public void innerComponentExtendsDefiningComponent(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit compilationUnit = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(compilationUnit);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), new ArcError[]{ArcError.INNER_COMPONENT_EXTENDS_OUTER});
  }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new InnerComponentNotExtendsDefiningComponent());
  }
}