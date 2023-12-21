/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;
import arcbasis._cocos.ComponentInstantiationNamedTick;
import arcbasis._cocos.ComponentNamedTick;
import arcbasis._cocos.ParameterNamedTick;
import arcbasis._cocos.PortNamedTick;
import arcbasis._cocos.FieldNamedTick;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import genericarc._cocos.TypeParameterNamedTick;
import montiarc.MontiArcAbstractTest;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.FeatureNamedTick;
import java.util.stream.Stream;

public class NameTickTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @MethodSource("validModels")
  void noErrorsExpected(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentInstantiationNamedTick());
    checker.addCoCo(new ComponentNamedTick());
    checker.addCoCo(new PortNamedTick());
    checker.addCoCo(new ParameterNamedTick());
    checker.addCoCo(new FieldNamedTick());
    checker.addCoCo(new TypeParameterNamedTick());
    checker.addCoCo(new FeatureNamedTick());

    //When
    checker.checkAll(ast);

    //Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void errorsExpected(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentInstantiationNamedTick());
    checker.addCoCo(new ComponentNamedTick());
    checker.addCoCo(new PortNamedTick());
    checker.addCoCo(new ParameterNamedTick());
    checker.addCoCo(new FieldNamedTick());
    checker.addCoCo(new TypeParameterNamedTick());
    checker.addCoCo(new FeatureNamedTick());

    //When
    checker.checkAll(ast);

    //Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
        .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels(){
    return Stream.of(
        arg("component Tick{component Tick{}}",
            ArcError.COMPONENT_NAMED_TICK,
            ArcError.COMPONENT_NAMED_TICK),
        arg("component Tick{}",
            ArcError.COMPONENT_NAMED_TICK),
        arg("component Tick{component Tick{} Tick Tick;}",
            ArcError.COMPONENT_NAMED_TICK,
            ArcError.COMPONENT_NAMED_TICK,
            ArcError.COMPONENTINSTANCE_NAMED_TICK),
        arg("component A{port in int Tick;}",
            ArcError.PORT_NAMED_TICK),
        arg("component A(int Tick){}",
            ArcError.PARAMETER_NAMED_TICK),
        arg("component A {int Tick = 0; }",
            ArcError.FIELD_NAMED_TICK),
        arg("component A <Tick>{ }",
            ArcError.TYPEPARAMETERS_NAMED_TICK),
        arg("component A {feature Tick;}",
            ArcError.FEATURE_NAMED_TICK),
        arg("component Tick<Tick>{component Tick{port in int Tick; int Tick = 0; }Tick Tick;}",
            ArcError.COMPONENT_NAMED_TICK,
            ArcError.COMPONENT_NAMED_TICK,
            ArcError.COMPONENTINSTANCE_NAMED_TICK,
            ArcError.PORT_NAMED_TICK,
            ArcError.FIELD_NAMED_TICK,
            ArcError.TYPEPARAMETERS_NAMED_TICK));
  }

  protected static Stream<Arguments> validModels(){
    return Stream.of(
        arg("component A{component B{}}"),
        arg("component A{component B{} B c;}"),
        arg("component A{port in int a;}"),
        arg("component A(int a){}"),
        arg("component A {int b = 0; }"),
        arg("component A <t>{ }"),
        arg("component A {feature b;}"));
  }
}
