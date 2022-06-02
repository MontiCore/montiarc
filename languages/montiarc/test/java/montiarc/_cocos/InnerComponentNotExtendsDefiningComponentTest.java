/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.InnerComponentNotExtendsDefiningComponent;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
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
    ASTComponentType ast = this.parseAndLoadAllSymbols(PACKAGE + "." + model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(new ArcError[]{}, getPathToModel(model).toAbsolutePath());
  }

  @ParameterizedTest
  @ValueSource(strings = {"InnerComponentExtendsDefiningComponent.arc"})
  public void innerComponentExtendsDefiningComponent(@NotNull String model) {
    Preconditions.checkNotNull(model);

    testModel(model, ArcError.INNER_COMPONENT_EXTENDS_OUTER);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
  }
}