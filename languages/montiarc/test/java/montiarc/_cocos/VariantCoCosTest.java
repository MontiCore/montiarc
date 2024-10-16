/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.AtomicMaxOneBehavior;
import arcbasis._cocos.AtomicNoConnector;
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
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.arcbasis.ConnectorTypesFit;
import variablearc._cocos.arcbasis.UniqueIdentifier;

import java.io.IOException;
import java.util.stream.Stream;

public class VariantCoCosTest extends MontiArcAbstractTest {

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
    setUpEnum();
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
    compile("package a.b; component D { feature ff; varif (ff) { port in int io; } else { port out int io; } }");
    compile("package a.b; component E { port in boolean i; }");
    compile("package a.b; component F { port out boolean o; }");
    compile("package a.b; component G { feature ff; varif (ff) { port in boolean i, out boolean o; } else { port in int i, out int o; } }");
    compile("package a.b; component H { feature ff; varif (ff) { port in int io; } else { port out boolean io; } }");
    compile("package a.b; component I { feature ff; varif (ff) { port <<sync>> in int i; } else { port <<timed>> in int i; } }");
    compile("package a.b; component J { feature ff; varif (ff) { port <<sync>> out int o; } else { port <<timed>> out int o; } }");
    compile("package a.b; component K { port in int i; port out int o1, <<delayed>> out int o2; } ");
    compile("package a.b; component L { port in int i; feature ff; varif (ff) { port out int o; } else { port <<delayed>> out int o; } }");
    compile("package a.b; component M { feature ff; varif (ff) { port in int i; } }");
    compile("package a.b; component N { feature ff; varif (ff) { port out int o; } }");
    compile("package a.b; component O { feature ff; port out int o; a.b.J sub; sub.o -> o; constraint(ff == sub.ff); }");
    compile("package a.b; component P<A,B> { feature ff; varif (ff) { port out A o; } else { port out B o; } }");
  }

  protected static void setUpEnum() {
    OOTypeSymbol onOffEnumType = MontiArcMill.oOTypeSymbolBuilder().setIsEnum(true).setName("OnOff").setIsPublic(true).setSpannedScope(MontiArcMill.scope()).build();
    onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    MontiArcMill.globalScope().add(onOffEnumType);
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
    // in port forward, single variation point
    "component Comp5 { " +
      "port in int i; " +
      "feature f; " +
      "varif (f) { " +
      "a.b.A sub; " +
      "i -> sub.i; " +
      "} " +
      "}",
    // out port forward, single variation point
    "component Comp6 { " +
      "port out int o; " +
      "feature f; " +
      "varif (f) { " +
      "a.b.B sub; " +
      "sub.o -> o; " +
      "} " +
      "}",
    // hidden channel, single variation point
    "component Comp7 { " +
      "feature f; " +
      "varif (f) { " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "} " +
      "}",
    // in port forward, source direction mismatch, excluded variation point
    "component Comp8 { " +
      "varif (false) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "o -> sub.i; " +
      "} " +
      "}",
    // in port forward, target direction mismatch, excluded variation point
    "component Comp9 { " +
      "varif (false) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "i -> sub.o; " +
      "} " +
      "}",
    // out port forward, source direction mismatch, excluded variation point
    "component Comp10 { " +
      "varif (false) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "sub.i -> o; " +
      "} " +
      "}",
    // out port forward, target direction mismatch, excluded variation point
    "component Comp11 { " +
      "varif (false) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "sub.o -> i; " +
      "} " +
      "}",
    // hidden channel, source direction mismatch, excluded variation point
    "component Comp12 { " +
      "varif (false) { " +
      "a.b.A sub1, sub2; " +
      "sub2.i -> sub1.i; " +
      "} " +
      "}",
    // hidden channel, target direction mismatch, excluded variation point
    "component Comp13 { " +
      "varif (false) { " +
      "a.b.B sub1, sub2; " +
      "sub2.o -> sub1.o; " +
      "} " +
      "}",
    // in port forward, source direction mismatch, constrained feature
    "component Comp14 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "o -> sub.i; " +
      "} " +
      "}",
    // in port forward, target direction mismatch, constrained feature
    "component Comp15 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "i -> sub.o; " +
      "} " +
      "}",
    // out port forward, source direction mismatch, constrained feature
    "component Comp16 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "sub.i -> o; " +
      "} " +
      "}",
    // out port forward, target direction mismatch, constrained feature
    "component Comp17 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "sub.o -> i; " +
      "} " +
      "}",
    // hidden channel, source direction mismatch, constrained feature
    "component Comp18 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "a.b.A sub1, sub2; " +
      "sub2.i -> sub1.i; " +
      "} " +
      "}",
    // hidden channel, target direction mismatch, constrained feature
    "component Comp19 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "a.b.B sub1, sub2; " +
      "sub2.o -> sub1.o; " +
      "} " +
      "}",
    // in port forward, subcomponent with variable interface direction
    "component Comp20 { " +
      "port in int i; " +
      "a.b.D sub; " +
      "i -> sub.io; " +
      "constraint (sub.ff); " +
      "}",
    // out port forward, subcomponent with variable interface direction
    "component Comp21 { " +
      "port out int o; " +
      "a.b.D sub; " +
      "sub.io -> o; " +
      "constraint (!sub.ff); " +
      "}",
    // hidden channel, subcomponent with variable interface direction
    "component Comp22 { " +
      "a.b.D sub1; " +
      "a.b.D sub2; " +
      "sub2.io -> sub1.io; " +
      "constraint (sub1.ff && !sub2.ff); " +
      "}",
    // port forward, component & subcomponent with variable interface direction
    "component Comp23 { " +
      "feature f; " +
      "varif (f) { " +
      "port in int io; " +
      "io -> sub.io; " +
      "} " +
      "varif (!f) { " +
      "port out int io; " +
      "sub.io -> io; " +
      "} " +
      "a.b.D sub; " +
      "constraint (sub.ff == f); " +
      "}",
    // in port forward, connector type mismatch, excluded variation point
    "component Comp24 { " +
      "varif (false) { " +
      "port in int i; " +
      "a.b.E sub; " +
      "i -> sub.i; " +
      "} " +
      "}",
    // out port forward, connector type mismatch, excluded variation point
    "component Comp25 { " +
      "varif (false) { " +
      "port out int o; " +
      "a.b.F sub; " +
      "sub.o -> o; " +
      "} " +
      "}",
    // hidden channel, connector type mismatch, excluded variation point
    "component Comp26 { " +
      "varif (false) { " +
      "a.b.E sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "a.b.A sub3; " +
      "a.b.F sub4; " +
      "sub4.o -> sub3.i; " +
      "} " +
      "}",
    // port forward, subcomponent with variable interface types (deselect feature)
    "component Comp27 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.G sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "constraint (!sub.ff); " +
      "}",
    // port forward, subcomponent with variable interface types (select feature)
    "component Comp28 { " +
      "port in boolean i; " +
      "port out boolean o; " +
      "a.b.G sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "constraint (sub.ff); " +
      "}",
    // port forward, component and subcomponent with variable interface types
    "component Comp29 { " +
      "feature f; " +
      "varif (f) { " +
      "port in boolean i; " +
      "port out boolean o; " +
      "} else { " +
      "port in int i; " +
      "port out int o; " +
      "} " +
      "a.b.G sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "constraint (sub.ff == f); " +
      "}",
    // in port forward, subcomponent with variable interface timing (deselect feature)
    "component Comp30 { " +
      "port <<timed>> in int i; " +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "constraint (!sub.ff); " +
      "}",
    // in port forward, subcomponent with variable interface timing (select feature)
    "component Comp31 { " +
      "port <<sync>> in int i; " +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "constraint (sub.ff); " +
      "}",
    // out port forward, subcomponent with variable interface timing (deselect feature)
    "component Comp32 { " +
      "port <<timed>> out int o; " +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "constraint (!sub.ff); " +
      "}",
    // out port forward, subcomponent with variable interface timing (select feature)
    "component Comp33 { " +
      "port <<sync>> out int o; " +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "constraint (sub.ff); " +
      "}",
    // in port forward, component and subcomponent with variable interface timing
    "component Comp34 { " +
      "feature f; " +
      "varif (f) { " +
      "port <<sync>> in int i; " +
      "} else {" +
      "port <<timed>> in int i; " +
      "}" +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "constraint (sub.ff == f); " +
      "}",
    // out port forward, component and subcomponent with variable interface timing
    "component Comp35 { " +
      "feature f; " +
      "varif (f) { " +
      "port <<sync>> out int o; " +
      "} else {" +
      "port <<timed>> out int o; " +
      "}" +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "constraint (sub.ff == f); " +
      "}",
    // feedback loop
    "component Comp36 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.C sub1; " +
      "a.b.K sub2; " +
      "i -> sub1.i1; " +
      "sub1.o -> sub2.i; " +
      "sub2.o1 -> o; " +
      "sub2.o2 -> sub1.i2; " +
      "}",
    // feedback loop, subcomponent with variable interface delay (deselect feature)
    "component Comp37 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.C sub1; " +
      "a.b.L sub2; " +
      "i -> sub1.i1; " +
      "sub1.o -> sub2.i; " +
      "sub2.o -> o; " +
      "sub2.o -> sub1.i2; " +
      "constraint (!sub2.ff); " +
      "}",
    // feedback loop, component with variable configuration and subcomponent with variable interface delay
    "component Comp38 { " +
      "port in int i; " +
      "port out int o; " +
      "feature f; " +
      "a.b.C sub1; " +
      "a.b.L sub2; " +
      "i -> sub1.i1; " +
      "sub1.o -> sub2.i; " +
      "sub2.o -> o; " +
      "varif (!f) { " +
      "sub2.o -> sub1.i2; " +
      "} else { " +
      "i -> sub1.i2; " +
      "}" +
      "constraint (sub2.ff == f); " +
      "}",
    // in port unused, component with variable configuration, excluded variation point
    "component Comp39 { " +
      "varif (false) { " +
      "port in int i; " +
      "} " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // out port unused, component with variable configuration, excluded variation point
    "component Comp40 { " +
      "varif (false) { " +
      "port out int o; " +
      "} " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // in port not connected, component with variable configuration, excluded variation point
    "component Comp41 { " +
      "varif (false) { " +
      "a.b.A sub; " +
      "} " +
      "}",
    // out port not connected, component with variable configuration, excluded variation point
    "component Comp42 { " +
      "varif (false) { " +
      "a.b.B sub; " +
      "} " +
      "}",
    // in port not connected, subcomponent with variable configuration (deselected feature)
    "component Comp43 { " +
      "a.b.M sub; " +
      "constraint (!sub.ff); " +
      "}",
    // out port not connected, subcomponent with variable configuration (deselected feature)
    "component Comp44 { " +
      "a.b.N sub; " +
      "constraint (!sub.ff); " +
      "}",
    // timing of subcomponent is implicitly set by its composition
    "component Comp45 { " +
      "port <<timed>> out int o; " +
      "a.b.O sub; " +
      "sub.o -> o; " +
      "constraint(!sub.ff); " +
      "}",
    // out port forward, subcomponent with variable generic interface type (selected feature)
    "component Comp46<T> { " +
      "port out T o; " +
      "a.b.P<T, java.lang.Integer> sub; " +
      "sub.o -> o; " +
      "constraint(sub.ff); " +
      "}",
    // out port forward, subcomponent with variable generic interface type
    "component Comp47<A, B> { " +
      "feature f; " +
      "varif (f) { port out A o; } " +
      "else { port out B o; } " +
      "a.b.P<A, B> sub; " +
      "sub.o -> o; " +
      "constraint(sub.ff == f); " +
      "}",
    // in port forward with inherited port
    "component Comp48 extends a.b.M { " +
      "varif (ff) { " +
      "a.b.A sub;" +
      "i -> sub.i;" +
      "} " +
      "}",
    // inherited port that switches direction
    "component Comp49 extends a.b.D { " +
      "varif (ff) { " +
      "a.b.A sub;" +
      "io -> sub.i;" +
      "} else { " +
      "a.b.B sub;" +
      "sub.o -> io;" +
      "} " +
      "}",
    // inherited generic port that switches between int and boolean
    "component Comp50 extends a.b.P<int, boolean> { " +
      "varif (ff) { " +
      "a.b.B sub; " +
      "} else {" +
      "a.b.F sub; " +
      "} " +
      "sub.o -> o; " +
      "}",
    // atomic component with port that switches types
    "component Comp51 { " +
      "feature ff; " +
      "varif (ff) { " +
      "port out int p; " +
      "} else { " +
      "port out double p; " +
      "} " +
      "compute {" +
      "p = 5; " +
      "}" +
      "}",
    // Switches between behaviors
    "component Comp52 { " +
      "feature f; " +
      "varif (f) { " +
      "compute { } " +
      "} else { " +
      "compute { } " +
      "} " +
      "}",
    // Switches between field initial values
    "component Comp53 { " +
      "feature f; " +
      "port out int o; " +
      "varif (f) { " +
      "int i = 0; " +
      "} else { " +
      "int i = 5; " +
      "} " +
      "compute { " +
      "  o = i; " +
      "} " +
      "}",
    // Switches between automaton behaviors with preconditions
    "component Comp54 { " +
      "feature f;" +
      "varif (f) {" +
      "port in int i;" +
      "<<sync>> automaton {" +
      "initial state A;" +
      "A -> A [i > 1];" +
      "}" +
      "} else {" +
      "port in boolean i;" +
      "<<sync>> automaton {" +
      "initial state A;" +
      "A -> A [i];" +
      "}" +
      "}" +
      "}",
    // Switches between field types
    "component Comp55 { " +
      "feature f; " +
      "port out double o; " +
      "varif (f) { " +
      "  double i = 2.5; " +
      "} else {" +
      "  int i = 2; " +
      "} " +
      "compute { " +
      "  o = i; " +
      "} " +
      "}",
    // Enum constants map to different values
    "component Comp56(OnOff onOff) { " +
      "varif(onOff == OnOff.OFF) {" +
      "  automaton {" +
      "    initial state S;" +
      "  }" +
      "} " +
      "varif(onOff == OnOff.ON) {" +
      "  automaton {" +
      "    initial state S;" +
      "  }" +
      "} " +
      "}",
    // in port forward, timing match, subcomponent with variable interface timing (deselect feature)
    "component Comp57 { " +
      "port <<sync>> in int i; " +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "constraint (!sub.ff); " +
      "}",
    // out port forward, timing mismatch, subcomponent with variable interface timing (select feature)
    "component Comp58 { " +
      "port <<timed>> out int o; " +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "constraint (sub.ff); " +
      "}",
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
    checker.addVariantCoCo(ConnectorTypesFit.class);
    checker.addVariantCoCo(ConnectorDirectionsFit.class);
    checker.addVariantCoCo(ConnectorTimingsFit.class);
    checker.addVariantCoCo(AtomicNoConnector.class);
    checker.addVariantCoCo(AtomicMaxOneBehavior.class);
    checker.addVariantCoCo(FeedbackStrongCausality.class);
    checker.addVariantCoCo(PortHeritageTypeFits.class);
    checker.addVariantCoCo(UniqueIdentifier.class);

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
    checkOnlyExpectedErrorsPresent();
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
    checker.addVariantCoCo(ConnectorTypesFit.class);
    checker.addVariantCoCo(ConnectorDirectionsFit.class);
    checker.addVariantCoCo(ConnectorTimingsFit.class);
    checker.addVariantCoCo(AtomicNoConnector.class);
    checker.addVariantCoCo(AtomicMaxOneBehavior.class);
    checker.addVariantCoCo(FeedbackStrongCausality.class);
    checker.addVariantCoCo(PortHeritageTypeFits.class);
    checker.addVariantCoCo(UniqueIdentifier.class);

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
    checkOnlyExpectedErrorsPresent(errors);
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
      // in port forward, source direction mismatch, single variation point
      arg("component Comp7 { " +
          "port out int o; " +
          "feature f; " +
          "varif (f) { " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, single variation point
      arg("component Comp8 { " +
          "port in int i; " +
          "feature f; " +
          "varif (f) { " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, single variation point
      arg("component Comp9 { " +
          "port out int o; " +
          "feature f; " +
          "varif (f) { " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, single variation point
      arg("component Comp10 { " +
          "port in int i; " +
          "feature f; " +
          "varif (f) { " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, single variation point, source direction mismatch
      arg("component Comp11 { " +
          "feature f; " +
          "varif (f) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, single variation point, target direction mismatch
      arg("component Comp12 { " +
          "feature f; " +
          "varif (f) { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, included variation point
      arg("component Comp13 { " +
          "varif (true) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, included variation point
      arg("component Comp14 { " +
          "varif (true) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, included variation point
      arg("component Comp15 { " +
          "varif (true) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, included variation point
      arg("component Comp16 { " +
          "varif (true) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, included variation point
      arg("component Comp17 { " +
          "varif (true) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, included variation point
      arg("component Comp18 { " +
          "varif (true) { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, unconstrained feature
      arg("component Comp19 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, unconstrained feature
      arg("component Comp20 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, unconstrained feature
      arg("component Comp21 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, unconstrained feature
      arg("component Comp22 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, unconstrained feature
      arg("component Comp23 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, unconstrained feature
      arg("component Comp24 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, subcomponent with variable interface direction
      arg("component Comp25 { " +
          "port in int i; " +
          "a.b.D sub; " +
          "i -> sub.io; " +
          "constraint (!sub.ff); " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, subcomponent with variable interface direction
      arg("component Comp26 { " +
          "port out int o; " +
          "a.b.D sub; " +
          "sub.io -> o; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, subcomponent with variable interface direction
      arg("component Comp27 { " +
          "a.b.D sub1; " +
          "a.b.D sub2; " +
          "sub2.io -> sub1.io; " +
          "constraint (sub1.ff && sub2.ff); " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, subcomponent with variable interface direction
      arg("component Comp28 { " +
          "a.b.D sub1; " +
          "a.b.D sub2; " +
          "sub2.io -> sub1.io; " +
          "constraint (!sub1.ff && !sub2.ff); " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source and target direction mismatch, subcomponent with variable interface direction
      arg("component Comp29 { " +
          "a.b.D sub1; " +
          "a.b.D sub2; " +
          "sub2.io -> sub1.io; " +
          "constraint (!sub1.ff && sub2.ff); " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, connector type mismatch, included variation point
      arg("component Comp30 { " +
          "varif (true) { " +
          "port in int i; " +
          "a.b.E sub; " +
          "i -> sub.i; " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // out port forward, connector type mismatch, included variation point
      arg("component Comp31 { " +
          "varif (true) { " +
          "port out int o; " +
          "a.b.F sub; " +
          "sub.o -> o; " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // hidden channel, connector type mismatch, included variation point
      arg("component Comp32 { " +
          "varif (true) { " +
          "a.b.E sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "a.b.A sub3; " +
          "a.b.F sub4; " +
          "sub4.o -> sub3.i; " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // port forward, connector type mismatch, component and subcomponent with variable interface types
      arg("component Comp33 { " +
          "feature f; " +
          "varif (f) { " +
          "port in boolean i; " +
          "port out boolean o; " +
          "} else { " +
          "port in int i; " +
          "port out int o; " +
          "} " +
          "a.b.G sub; " +
          "i -> sub.i; " +
          "sub.o -> o; " +
          "constraint (sub.ff == !f); " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // port forward, connector direction and type mismatch, component and subcomponent with variable interface types
      arg("component Comp34 { " +
          "feature f; " +
          "a.b.H sub; " +
          "varif (f) { " +
          "port out boolean o; " +
          "sub.io -> o; " +
          "} else { " +
          "port in int i; " +
          "i -> sub.io; " +
          "} " +
          "constraint (sub.ff == f); " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // in port forward, timing mismatch, subcomponent with variable interface timing (select feature)
      arg("component Comp35 { " +
          "port <<timed>> in int i; " +
          "a.b.I sub; " +
          "i -> sub.i; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // out port forward, timing mismatch, subcomponent with variable interface timing (deselect feature)
      arg("component Comp36 { " +
          "port <<sync>> out int o; " +
          "a.b.J sub; " +
          "sub.o -> o; " +
          "constraint (!sub.ff); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // in port forward, timing mismatch, component and subcomponent with variable interface timing
      arg("component Comp37 { " +
          "feature f; " +
          "varif (f) { " +
          "port <<sync>> in int i; " +
          "} else {" +
          "port <<timed>> in int i; " +
          "}" +
          "a.b.I sub; " +
          "i -> sub.i; " +
          "constraint (sub.ff == !f); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // out port forward, timing mismatch, component and subcomponent with variable interface timing
      arg("component Comp38 { " +
          "feature f; " +
          "varif (f) { " +
          "port <<sync>> out int o; " +
          "} else {" +
          "port <<timed>> out int o; " +
          "}" +
          "a.b.J sub; " +
          "sub.o -> o; " +
          "constraint (sub.ff == !f); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // feedback loop, weakly-causal feedback
      arg("component Comp39 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.C sub1; " +
          "a.b.K sub2; " +
          "i -> sub1.i1; " +
          "sub1.o -> sub2.i; " +
          "sub2.o1 -> sub1.i2; " +
          "sub2.o2 -> o; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY),
      // feedback loop, weakly-causal feedback, subcomponent with variable interface delay (select feature)
      arg("component Comp40 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.C sub1; " +
          "a.b.L sub2; " +
          "i -> sub1.i1; " +
          "sub1.o -> sub2.i; " +
          "sub2.o -> o; " +
          "sub2.o -> sub1.i2; " +
          "constraint (sub2.ff); " +
          "}",
        ArcError.FEEDBACK_CAUSALITY),
      // feedback loop, weakly-causal feedback, component with variable configuration and subcomponent with variable interface delay
      arg("component Comp41 { " +
          "port in int i; " +
          "port out int o; " +
          "feature f; " +
          "a.b.C sub1; " +
          "a.b.L sub2; " +
          "i -> sub1.i1; " +
          "sub1.o -> sub2.i; " +
          "sub2.o -> o; " +
          "varif (f) { " +
          "sub2.o -> sub1.i2; " +
          "} else { " +
          "i -> sub1.i2; " +
          "}" +
          "constraint (sub2.ff == f); " +
          "}",
        ArcError.FEEDBACK_CAUSALITY),
      // in port unused
      arg("component Comp42 { " +
          "port in int i; " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.IN_PORT_UNUSED),
      // out port unused
      arg("component Comp43 { " +
          "port out int o; " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.OUT_PORT_UNUSED),
      // ports unused
      arg("component Comp44 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.IN_PORT_UNUSED,
        ArcError.OUT_PORT_UNUSED),
      // in port unused, component with variable configuration, included variation point
      arg("component Comp45 { " +
          "varif (true) { " +
          "port in int i; " +
          "} " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.IN_PORT_UNUSED),
      // out port unused, component with variable configuration, included variation point
      arg("component Comp46 { " +
          "varif (true) { " +
          "port out int o; " +
          "} " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.OUT_PORT_UNUSED),
      // in port unused, component with variable configuration
      arg("component Comp47 { " +
          "feature f; " +
          "port in int i; " +
          "varif (f) { " +
          "a.b.A sub; " +
          "i -> sub.i; " +
          "} " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.IN_PORT_UNUSED),
      // out port unused, component with variable configuration
      arg("component Comp48 { " +
          "feature f; " +
          "port out int o; " +
          "varif (f) { " +
          "a.b.B sub; " +
          "sub.o -> o; " +
          "} " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.OUT_PORT_UNUSED),
      // in port not connected
      arg("component Comp49 { " +
          "a.b.A sub; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port not connected
      arg("component Comp50 { " +
          "a.b.B sub; " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      // ports not connected
      arg("component Comp51 { " +
          "a.b.C sub; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      // in port not connected, component with variable configuration, included variation point
      arg("component Comp52 { " +
          "varif (true) { " +
          "a.b.A sub; " +
          "} " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port not connected, component with variable configuration, included variation point
      arg("component Comp53 { " +
          "varif (true) { " +
          "a.b.B sub; " +
          "} " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      // in port not connected, component with variable configuration
      arg("component Comp54 { " +
          "feature f; " +
          "a.b.A sub; " +
          "varif (f) { " +
          "port in int i; " +
          "i -> sub.i; " +
          "} " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port not connected, component with variable configuration
      arg("component Comp55 { " +
          "feature f; " +
          "a.b.B sub; " +
          "varif (f) { " +
          "port out int o; " +
          "sub.o -> o; " +
          "} " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      // in port unused, in port not connected, component with variable configuration
      arg("component Comp56 { " +
          "feature f; " +
          "a.b.A sub; " +
          "port in int i; " +
          "varif (f) { " +
          "i -> sub.i; " +
          "} " +
          "}",
        ArcError.IN_PORT_UNUSED,
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port unused, out port not connected, component with variable configuration
      arg("component Comp57 { " +
          "feature f; " +
          "a.b.B sub; " +
          "port out int o; " +
          "varif (f) { " +
          "sub.o -> o; " +
          "} " +
          "}",
        ArcError.OUT_PORT_UNUSED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      // in port not connected, subcomponent with variable configuration (selected feature)
      arg("component Comp58 { " +
          "a.b.M sub; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port not connected, subcomponent with variable configuration (selected feature)
      arg("component Comp59 { " +
          "a.b.N sub; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      // Multiple behaviors if feature is selected
      arg("component Comp60 { " +
          "feature f; " +
          "varif (!f) { " +
          "a.b.N sub; " +
          "} " +
          "constraint (!sub.ff); " +
          "compute { } " +
          "compute { } " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR),
      // timing mismatch with implicitly set timing
      arg("component Comp61 { " +
          "feature ff; " +
          "port <<sync>> out int o; " +
          "a.b.O sub; " +
          "sub.o -> o; " +
          "constraint(ff == sub.ff); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // out port forward, subcomponent with variable generic interface type (deselected feature)
      arg("component Comp62<T> { " +
          "port out T o; " +
          "a.b.P<T, java.lang.Integer> sub; " +
          "sub.o -> o; " +
          "constraint(!sub.ff); " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // in port forward with inherited port
      arg("component Comp63 extends a.b.M { " +
          "a.b.A sub;" +
          "i -> sub.i;" +
          "}",
        ArcError.MISSING_PORT
      ),
      // inherited port that switches direction
      arg("component Comp64 extends a.b.D { " +
          "a.b.A sub;" +
          "io -> sub.i;" +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH
      ),/*
      // atomic component with port that switches existence
      arg("component Comp65 { " +
          "feature ff; " +
          "varif (ff) { " +
          "port out int p; " +
          "} " +
          "compute {" +
          "p = 5; " +
          "} " +
          "}",
        new InternalError("0xFD118")
      ),*/
      // Multiple behaviors if feature is selected
      arg("component Comp66 { " +
          "feature f; " +
          "varif (f) { " +
          "compute { } " +
          "} " +
          "compute { } " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR),
      // Multiple behaviors if both features are selected
      arg("component Comp67 { " +
          "feature f1, f2; " +
          "varif (f1) { " +
          "compute { } " +
          "} " +
          "varif (f2) { " +
          "compute { } " +
          "} " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR),
      // Multiple fields if both features are selected
      arg("component Comp68 { " +
          "feature f1, f2; " +
          "varif (f1) { " +
          "int i = 0; " +
          "} " +
          "varif (f2) { " +
          "int i = 1; " +
          "} " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // component that switches between atomic and decomposed with connector
      arg("component Comp69 { " +
          "feature f;" +
          "port in int i;" +
          "port out int o;" +
          "varif (f) {" +
          "port in int i2;" +
          "a.b.C sub;" +
          "i -> sub.i1;" +
          "i2 -> sub.i2;" +
          "sub.o -> o;" +
          "} else {" +
          "i -> o;" +
          "}" +
          "}",
        ArcError.CONNECTORS_IN_ATOMIC
      ),
      // Switches between automaton behaviors with preconditions
      arg(
        "component Comp70 { " +
          "feature f;" +
          "varif(f){" +
          "port in int i;" +
          "<<sync>> automaton {" +
          "initial state B;" +
          "B -> B [i];" +
          "}" +
          "} else {" +
          "port in boolean i;" +
          "<<sync>> automaton {" +
          "initial state A;" +
          "A -> A [i == \"a\"];" +
          "}" +
          "}" +
          "}",
        new InternalError("0xB0166"), // equal not applicable
        new InternalError("0xCC111") // int not boolean
      ),
      // Enum constants map to same value
      arg("component Comp71(OnOff onOff) { " +
          "varif(onOff == OnOff.OFF) {" +
          "  automaton {" +
          "    initial state S;" +
          "  }" +
          "} " +
          "varif(onOff == OnOff.OFF) {" +
          "  automaton {" +
          "    initial state S;" +
          "  }" +
          "} " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR),
      // Multiple identifier if feature is selected
      arg("component Comp72(boolean i) { " +
          "feature f1; " +
          "varif (f1) { " +
          "int i = 0; " +
          "} " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // Multiple identifier if feature is selected
      arg("component Comp73(boolean i) { " +
          "feature f1; " +
          "varif (f1) { " +
          "port in int i; " +
          "} " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // Multiple identifier feature should not throw exception
      arg("component Comp74 { " +
          "feature f; " +
          "feature f; " +
          "varif (f) { } " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES,
        ArcError.UNIQUE_IDENTIFIER_NAMES)
    );
  }

  static class InternalError implements Error {

    protected String errorCode;

    public InternalError(@NotNull String errorCode) {
      Preconditions.checkNotNull(errorCode);
      this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
      return errorCode;
    }

    @Override
    public String printErrorMessage() {
      return "";
    }
  }
}
