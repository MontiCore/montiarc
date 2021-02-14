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

public class InnerComponentNotExtendsDefiningComponentTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "innerComponentNotExtendsDefiningComponent";

  @Override
  protected String getPackage() {
    return PACKAGE;
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