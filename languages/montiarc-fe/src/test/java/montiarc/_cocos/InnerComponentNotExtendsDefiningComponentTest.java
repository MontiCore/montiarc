/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.InnerComponentNotExtendsDefiningComponent;
import arcbasis.util.ArcError;
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

    testModel(model);
  }

  @ParameterizedTest
  @ValueSource(strings = {"InnerComponentExtendsDefiningComponent.arc"})
  public void innerComponentExtendsDefiningComponent(@NotNull String model) {
    Preconditions.checkNotNull(model);

    testModel(model, new ArcError[]{ArcError.INNER_COMPONENT_EXTENDS_OUTER});
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
  }
}