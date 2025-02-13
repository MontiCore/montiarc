/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.AtomicMaxOneBehavior;
import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorPortsExist;
import arcbasis._cocos.ConnectorTimingsFit;
import arcbasis._cocos.FeedbackStrongCausality;
import arcbasis._cocos.PortHeritageTypeFits;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortsConnected;
import arcbasis._cocos.SubPortsConnected;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ModeCoCosTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { port in int i; }");
    compile("package a.b; component B { port out int o; }");
    compile("package a.b; component C { port in int i1, i2; port out int o; }");
    compile("package a.b; component D { port out boolean o; }");
    compile("package a.b; component E { port out int o, in int i; compute {o = i;}}");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic component, no variability
    "component Comp1 { }",
    // in port forward
    "component Comp2 { " +
      "port in int i; " +
      "a.b.A sub; " +
      "i -> sub.i; " +
      "}",
    // out port forward
    "component Comp3 { " +
      "port out int o; " +
      "a.b.B sub; " +
      "sub.o -> o; " +
      "}",
    // hidden channel
    "component Comp4 { " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // in port forward
    "component Comp5 { " +
      "port in int i; " +
      "mode automaton { " +
      "mode m1 { " +
      "a.b.A sub; " +
      "i -> sub.i; " +
      "} " +
      "} " +
      "}",
    // in port forward and complex forwards and hidden channel
    "component Comp5 { " +
      "port in int i; " +
      "mode automaton { " +
      "mode m1 { " +
      "a.b.A sub; " +
      "i -> sub.i; " +
      "} " +
      "mode m2 { " +
      "a.b.B sub1; " +
      "a.b.C sub2; " +
      "a.b.A sub3; " +
      "i -> sub2.i1; " +
      "sub1.o -> sub2.i2; " +
      "sub2.o -> sub3.i; " +
      "} " +
      "} " +
      "}",
    // calculate port timing for each mode variant
    "component Comp6 {" +
      "port in int i, out int o;" +
      "a.b.E always1;" +
      "a.b.E always2;" +
      "i -> always1.i;" +
      "always1.o -> always2.i;" +
      "mode automaton {" +
      "initial mode Normal {" +
      "a.b.E modeComp;" +
      "always2.o -> modeComp.i;" +
      "modeComp.o -> o;" +
      "}" +
      "}" +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcVariantCoCoChecker checker2 = new MontiArcVariantCoCoChecker();
    checker2.get4Variant().addCoCo(new PortsConnected());
    checker2.get4Variant().addCoCo(new PortUniqueSender());
    checker2.get4Variant().addCoCo(new SubPortsConnected());
    checker2.get4Variant().addCoCo(new ConnectorPortsExist());
    checker2.get4Variant().addCoCo(new variablearc._cocos.arcbasis.ConnectorTypesFit());
    checker2.get4Variant().addCoCo(new ConnectorDirectionsFit());
    checker2.get4Variant().addCoCo(new ConnectorTimingsFit());
    checker2.get4Variant().addCoCo(new AtomicMaxOneBehavior());
    checker2.get4Variant().addCoCo(new FeedbackStrongCausality());
    checker2.get4Variant().addCoCo(new PortHeritageTypeFits());
    checker2.get4Variant().addCoCo(new variablearc._cocos.arcbasis.UniqueIdentifier());
    checker2.get4Variant().addCoCo(new TransitionPreconditionsAreBoolean());
    checker2.get4Variant().addCoCo(new ExpressionStatementIsValid());
    checker2.get4Variant().addCoCo(new VarDeclarationInitializationHasCorrectType());
    checker2.get4Variant().addCoCo(new ForConditionHasBooleanType());
    checker2.get4Variant().addCoCo(new ForEachIsValid());
    checker2.get4Variant().addCoCo(new IfConditionHasBooleanType());
    checker2.get4Variant().addCoCo(new SwitchStatementValid());

    // When
    checker2.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcVariantCoCoChecker checker = new MontiArcVariantCoCoChecker();
    checker.get4Variant().addCoCo(new PortsConnected());
    checker.get4Variant().addCoCo(new PortUniqueSender());
    checker.get4Variant().addCoCo(new SubPortsConnected());
    checker.get4Variant().addCoCo(new ConnectorPortsExist());
    checker.get4Variant().addCoCo(new variablearc._cocos.arcbasis.ConnectorTypesFit());
    checker.get4Variant().addCoCo(new ConnectorDirectionsFit());
    checker.get4Variant().addCoCo(new ConnectorTimingsFit());
    checker.get4Variant().addCoCo(new AtomicMaxOneBehavior());
    checker.get4Variant().addCoCo(new FeedbackStrongCausality());
    checker.get4Variant().addCoCo(new PortHeritageTypeFits());
    checker.get4Variant().addCoCo(new variablearc._cocos.arcbasis.UniqueIdentifier());
    checker.get4Variant().addCoCo(new TransitionPreconditionsAreBoolean());
    checker.get4Variant().addCoCo(new ExpressionStatementIsValid());
    checker.get4Variant().addCoCo(new VarDeclarationInitializationHasCorrectType());
    checker.get4Variant().addCoCo(new ForConditionHasBooleanType());
    checker.get4Variant().addCoCo(new ForEachIsValid());
    checker.get4Variant().addCoCo(new IfConditionHasBooleanType());
    checker.get4Variant().addCoCo(new SwitchStatementValid());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // in port forward, source direction mismatch
      arg("component Comp1 { " +
          "port out int o; " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch
      arg("component Comp2 { " +
          "port in int i; " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch
      arg("component Comp3 { " +
          "port out int o; " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch
      arg("component Comp4 { " +
          "port in int i; " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch
      arg("component Comp5 { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch
      arg("component Comp6 { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, connector type mismatch, in mode
      arg("component Comp7 { " +
          "port out int o; " +
          "mode automaton { " +
          "mode m1 { " +
          "a.b.D sub; " +
          "sub.o -> o; " +
          "} " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // out port forward, connector source direction mismatch, in mode
      arg("component Comp8 { " +
          "port out int o; " +
          "mode automaton { " +
          "mode m1 { " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, connector missing subcomponent & port not connected
      arg("component Comp9 { " +
          "port out int o; " +
          "mode automaton { " +
          "mode m1 { " +
          "sub.i -> o; " +
          "} " +
          "mode m2 { " +
          "a.b.A sub; " +
          "} " +
          "} " +
          "}",
        ArcError.MISSING_SUBCOMPONENT, ArcError.OUT_PORT_UNUSED, ArcError.IN_PORT_NOT_CONNECTED)
    );
  }
}
