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
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

public class ModeCoCosTest extends MontiArcAbstractTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MontiArcMill.reset();
    MontiArcMill.init();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  @Override
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
    Log.clearFindings();
  }

  protected static void setUpComponents() {
    compile("package a.b; component A { port in int i; }");
    compile("package a.b; component B { port out int o; }");
    compile("package a.b; component C { port in int i1, i2; port out int o; }");
    compile("package a.b; component D { port out boolean o; }");
    compile("package a.b; component E { port out int o, in int i; <<sync>> compute {o = i;}}");
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
      "<<sync>> mode automaton {" +
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

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addVariantCoCo(PortsConnected.class);
    checker.addVariantCoCo(PortUniqueSender.class);
    checker.addVariantCoCo(SubPortsConnected.class);
    checker.addVariantCoCo(ConnectorPortsExist.class);
    checker.addVariantCoCo(variablearc._cocos.arcbasis.ConnectorTypesFit.class);
    checker.addVariantCoCo(ConnectorDirectionsFit.class);
    checker.addVariantCoCo(ConnectorTimingsFit.class);
    checker.addVariantCoCo(AtomicMaxOneBehavior.class);
    checker.addVariantCoCo(FeedbackStrongCausality.class);
    checker.addVariantCoCo(PortHeritageTypeFits.class);
    checker.addVariantCoCo(variablearc._cocos.arcbasis.UniqueIdentifier.class);

    checker.addVariantCoCo(TransitionPreconditionsAreBoolean.class);

    checker.addVariantCoCo(ExpressionStatementIsValid.class);
    checker.addVariantCoCo(VarDeclarationInitializationHasCorrectType.class);
    checker.addVariantCoCo(ForConditionHasBooleanType.class);
    checker.addVariantCoCo(ForEachIsValid.class);
    checker.addVariantCoCo(IfConditionHasBooleanType.class);
    checker.addVariantCoCo(SwitchStatementValid.class);

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addVariantCoCo(PortsConnected.class);
    checker.addVariantCoCo(PortUniqueSender.class);
    checker.addVariantCoCo(SubPortsConnected.class);
    checker.addVariantCoCo(ConnectorPortsExist.class);
    checker.addVariantCoCo(variablearc._cocos.arcbasis.ConnectorTypesFit.class);
    checker.addVariantCoCo(ConnectorDirectionsFit.class);
    checker.addVariantCoCo(ConnectorTimingsFit.class);
    checker.addVariantCoCo(AtomicMaxOneBehavior.class);
    checker.addVariantCoCo(FeedbackStrongCausality.class);
    checker.addVariantCoCo(PortHeritageTypeFits.class);
    checker.addVariantCoCo(variablearc._cocos.arcbasis.UniqueIdentifier.class);

    checker.addVariantCoCo(TransitionPreconditionsAreBoolean.class);

    checker.addVariantCoCo(ExpressionStatementIsValid.class);
    checker.addVariantCoCo(VarDeclarationInitializationHasCorrectType.class);
    checker.addVariantCoCo(ForConditionHasBooleanType.class);
    checker.addVariantCoCo(ForEachIsValid.class);
    checker.addVariantCoCo(IfConditionHasBooleanType.class);
    checker.addVariantCoCo(SwitchStatementValid.class);

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
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
