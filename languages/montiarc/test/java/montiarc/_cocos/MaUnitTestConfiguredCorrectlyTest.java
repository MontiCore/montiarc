/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentBody;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.MCError;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MaUnitTestConfiguredCorrectly}
 */
class MaUnitTestConfiguredCorrectlyTest extends MontiArcTestBase {

  @BeforeEach
  public void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no test
    "component Comp1 { }",
    // no test with other stereotypes
    "<<a, b=\"as\", c=[]>> component Comp2 { }",
    // test
    "<<test>> component Comp2 { }",
    // test, custom tick count
    "<<test, ticks=5>> component Comp3 { }",
    // test, custom tick count, default parameter value
    "<<test, ticks=5>> component Comp4(int i = 0) { }",
    // test, custom tick count, custom parameter value
    "<<test, ticks=5, i=5>> component Comp5(int i = 0) { }",
    // test, custom tick count, custom feature value
    "<<test, f=true>> component Comp6() { feature f; }",
    // 2 tests, custom ticks count
    "<<test, ticks=[1, 2]>> component Comp7() { }",
    // 2 tests, custom ticks count, custom parameter value
    "<<test, ticks=[1, 2], b=true>> component Comp8(boolean b) { }",
    // 2 tests, custom ticks count, custom parameter values
    "<<test, ticks=[1, 2], b=[false, true]>> component Comp9(boolean b) { }",
    // 2 tests, custom parameter values
    "<<test, b=[false, true]>> component Comp10(boolean b) { }",
    // 2 tests, custom tick count, custom parameter values
    "<<test, ticks=3, b=[false, true]>> component Comp11(boolean b) { }",
    // 2 tests, test source, custom tick count, custom parameter values
    "<<test={[false],[true]}, ticks=[1,3]>> component Comp12(boolean b) { }",
    // 2 tests, test source, custom tick count, custom parameter values
    "<<test={[false, 1],[true, 2]}>> component Comp13(boolean b, int i) { }",
    // 2 tests, test source, custom tick count, custom parameter values, default omitted
    "<<test={[false, 1],[true]}>> component Comp14(boolean b, int i = 0) { }",
  })
  void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaUnitTestConfiguredCorrectly());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaUnitTestConfiguredCorrectly());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // test, 1 missing parameter assignment
      arg("<<test>> component Comp1(int i) { }",
        MontiArcError.UNIT_MISSING_ARGUMENT),
      // test, 1 missing parameter assignment
      arg("<<test>> component Comp2(int i, int j = 2) { }",
        MontiArcError.UNIT_MISSING_ARGUMENT),
      // test, 2 missing parameter assignments
      arg("<<test>> component Comp3(int i, int j) { }",
        MontiArcError.UNIT_MISSING_ARGUMENT,
        MontiArcError.UNIT_MISSING_ARGUMENT),
      // test, missing parameter assignment
      arg("<<test, i = 1>> component Comp4(int i, int j) { }",
        MontiArcError.UNIT_MISSING_ARGUMENT),
      // test, multiple parameter assignment
      arg("<<test, i = 1, i = 5>> component Comp5(int i) { }",
        MontiArcError.UNIT_DUPLICATE_ARGUMENTS),
      // test, multiple parameter assignment
      arg("<<test, i = 1, i=[1,2]>> component Comp6(int i) { }",
        MontiArcError.UNIT_DUPLICATE_ARGUMENTS),
      // test, multiple tick assignment
      arg("<<test, ticks = 1, ticks=[1,2]>> component Comp7() { }",
        MontiArcError.UNIT_DUPLICATE_ARGUMENTS),
      // 3 tests, missing values for tests
      arg("<<test, ticks=[1,2,3], i = [1,2]>> component Comp8(int i = 0, int j = 0) { }",
        MontiArcError.UNIT_TEST_COUNT_MISMATCH),
      // 3 tests, missing values for tests (k is good since it's not using lists)
      arg("<<test, ticks=[1,2], i = [1,2,3], j=[1], k=0>> component Comp9(int i = 0, int j = 0, int k) { }",
        MontiArcError.UNIT_TEST_COUNT_MISMATCH,
        MontiArcError.UNIT_TEST_COUNT_MISMATCH),
      // test, ticks type mismatch
      arg("<<test, ticks=true>> component Comp10() { }",
        MontiArcError.UNIT_TYPE_MISMATCH),
      // 2 tests, parameter type mismatch in list
      arg("<<test, ticks=[1,2], i=true>> component Comp11(int i) { }",
        MontiArcError.UNIT_TYPE_MISMATCH),
      // 2 tests, ticks type mismatch in list
      arg("<<test, ticks=[1,true]>> component Comp12() { }",
        MCError.TARGET_TYPE_MISMATCH),
      // 2 tests, parameter type mismatch in list
      arg("<<test, ticks=[1,2], i=[1,true]>> component Comp13(int i) { }",
        MCError.TARGET_TYPE_MISMATCH),
      // test, but not deployable
      arg("<<test>> component Comp14 { port out int p; }",
        MontiArcError.UNIT_CANNOT_HAVE_PORTS),
      // 2 tests, test source, missing argument
      arg("<<test={[0], [1]}>> component Comp15(int i, int j) { }",
        MontiArcError.UNIT_MISSING_ARGUMENTS,
        MontiArcError.UNIT_MISSING_ARGUMENTS),
      // 2 tests, test source, to many arguments
      arg("<<test={[0, 1, 2], [1]}>> component Comp16(int i, int j = 0) { }",
        MontiArcError.UNIT_TOO_MANY_ARGUMENTS),
      // No test, test source misconfigured
      arg("<<test=5>> component Comp17() { }",
        MontiArcError.UNIT_TEST_SOURCE_MISCONFIGURED),
      // 2 tests, test source, individual test misconfigured
      arg("<<test={[], 5}>> component Comp18() { }",
        MontiArcError.UNIT_TEST_CASE_MISCONFIGURED),
      // 2 tests, test source, individual test misconfigured
      arg("<<test={[], {}}>> component Comp19() { }",
        MontiArcError.UNIT_TEST_CASE_MISCONFIGURED),
      // 1 test, test source, unsupported list feature
      arg("<<test={[1..2]}>> component Comp20(int i, int j) { }",
        MontiArcError.UNIT_TEST_CASE_PARAMETER_MISCONFIGURED,
        MontiArcError.UNIT_MISSING_ARGUMENTS),
      // 1 test, test source and value source combined
      arg("<<test={[1,2]}, j=2>> component Comp21(int i, int j) { }",
        MontiArcError.UNIT_TEST_SOURCE_AND_VALUE_SOURCE),
      // 2 tests, test source, type mismatch
      arg("<<test={[true]}>> component Comp22(int i) { }",
        MontiArcError.UNIT_TYPE_MISMATCH),
      // 3 tests, missing values for tests
      arg("<<test={[], [0]}, ticks=[1,2,3]>> component Comp23(int i = 0, int j = 0) { }",
        MontiArcError.UNIT_TEST_COUNT_MISMATCH)
    );
  }

  @Test
  void shouldNotFindAnythingWithNoTestStereotypes() {
    // Given
    ASTArcComponentType comp = MontiArcMill.arcComponentTypeBuilder()
      .setName("A")
      .setStereotype(MontiArcMill.stereotypeBuilder()
        .addValues(MontiArcMill.stereoValueBuilder()
          .setName("s")
          
          .build())
        .build())
      .setHead(MontiArcMill.componentHeadBuilder()
        .addArcParameter(MontiArcMill.arcParameterBuilder()
          .setName("p")
          .setMCType(Mockito.mock(ASTMCType.class))
          .build()).
        build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    comp.setSymbol(Mockito.mock(ComponentTypeSymbol.class));

    // When
    new MaUnitTestConfiguredCorrectly().check(comp);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void missingParameterStereotypeWithDefaultValue() {
    // Given
    ASTArcComponentType comp = MontiArcMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(MontiArcMill.stereotypeBuilder().setValuesList(List.of(
          MontiArcMill.stereoValueBuilder().setName("test").build(),
          MontiArcMill.stereoValueBuilder().setName("s").build()))
        .build())
      .setHead(MontiArcMill.componentHeadBuilder()
        .addArcParameter(MontiArcMill.arcParameterBuilder()
          .setName("p").setDefault(Mockito.mock(ASTExpression.class))
          .setMCType(Mockito.mock(ASTMCType.class))
          .build())
        .build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    comp.setSymbol(Mockito.mock(ComponentTypeSymbol.class));
    comp.getHead().getArcParameter(0)
      .setSymbol(MontiArcMill.variableSymbolBuilder()
        .setName("p")
        .setType(SymTypeExpressionFactory.createPrimitive("int")).build());

    // When
    new MaUnitTestConfiguredCorrectly().check(comp);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void correctAssignment() {
    // Given
    ASTArcComponentType comp = MontiArcMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(MontiArcMill.stereotypeBuilder().setValuesList(List.of(
        MontiArcMill.stereoValueBuilder().setName("test").build(),
        MontiArcMill.stereoValueBuilder().setName("p").setExpression(getIntLiteral(1)).build(),
        MontiArcMill.stereoValueBuilder().setName("s").build())).build())
      .setHead(MontiArcMill.componentHeadBuilder()
        .addArcParameter(MontiArcMill.arcParameterBuilder()
          .setName("p").setMCType(Mockito.mock(ASTMCType.class))
          .build())
        .build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    comp.setSymbol(Mockito.mock(ComponentTypeSymbol.class));
    comp.getHead().getArcParameter(0)
      .setSymbol(MontiArcMill.variableSymbolBuilder()
        .setName("p")
        .setType(SymTypeExpressionFactory.createPrimitive("int")).build());

    // When
    new MaUnitTestConfiguredCorrectly().check(comp);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void combineListAndSingleValueAssignment() {
    // Given
    ASTArcComponentType comp = MontiArcMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(MontiArcMill.stereotypeBuilder().setValuesList(List.of(
          MontiArcMill.stereoValueBuilder().setName("test").build(),
          MontiArcMill.stereoValueBuilder()
            .setName("p1").setExpression(getListExpression(List.of(getIntLiteral(2), getIntLiteral(3)))).build(),
          MontiArcMill.stereoValueBuilder()
            .setName("p2").setExpression(getIntLiteral(5)).build()))
        .build())
      .setHead(MontiArcMill.componentHeadBuilder().setArcParametersList(List.of(
          MontiArcMill.arcParameterBuilder()
            .setName("p1").setMCType(Mockito.mock(ASTMCType.class)).build(),
          MontiArcMill.arcParameterBuilder()
            .setName("p2").setMCType(Mockito.mock(ASTMCType.class)).build()))
        .build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    comp.setSymbol(Mockito.mock(ComponentTypeSymbol.class));
    comp.getHead().getArcParameter(0)
      .setSymbol(MontiArcMill.variableSymbolBuilder()
        .setName("p1")
        .setType(SymTypeExpressionFactory.createPrimitive("int")).build());
    comp.getHead().getArcParameter(1)
      .setSymbol(MontiArcMill.variableSymbolBuilder()
        .setName("p2")
        .setType(SymTypeExpressionFactory.createPrimitive("int")).build());

    // When
    new MaUnitTestConfiguredCorrectly().check(comp);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void missingParameterStereotype() {
    // Given
    ASTArcComponentType comp = MontiArcMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(MontiArcMill.stereotypeBuilder().setValuesList(List.of(
          MontiArcMill.stereoValueBuilder().setName("test").build(),
          MontiArcMill.stereoValueBuilder().setName("s").build()))
        .build())
      .setHead(MontiArcMill.componentHeadBuilder()
        .setArcParametersList(List.of(MontiArcMill.arcParameterBuilder()
          .setName("p")
          .setMCType(Mockito.mock(ASTMCType.class))
          .build()))
        .build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    comp.setSymbol(Mockito.mock(ComponentTypeSymbol.class));
    comp.getHead().getArcParameter(0)
      .setSymbol(MontiArcMill.variableSymbolBuilder()
        .setName("p")
        .setType(SymTypeExpressionFactory.createPrimitive("int")).build());

    // When
    new MaUnitTestConfiguredCorrectly().check(comp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(MontiArcError.UNIT_MISSING_ARGUMENT));
  }

  @Test
  void tooManyStereotypesForParameter() {
    // Given
    ASTArcComponentType comp = MontiArcMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(MontiArcMill.stereotypeBuilder().setValuesList(List.of(
        MontiArcMill.stereoValueBuilder().setName("test").build(),
        MontiArcMill.stereoValueBuilder().setName("p").setExpression(getIntLiteral(5)).build(),
        MontiArcMill.stereoValueBuilder().setName("p").setExpression(getIntLiteral(1)).build(),
        MontiArcMill.stereoValueBuilder().setName("s").build())).build())
      .setHead(MontiArcMill.componentHeadBuilder()
        .setArcParametersList(List.of(MontiArcMill.arcParameterBuilder()
          .setName("p").setMCType(Mockito.mock(ASTMCType.class))
          .build()))
        .build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    comp.setSymbol(Mockito.mock(ComponentTypeSymbol.class));
    comp.getHead().getArcParameter(0)
      .setSymbol(MontiArcMill.variableSymbolBuilder()
        .setName("p")
        .setType(SymTypeExpressionFactory.createPrimitive("int")).build());

    // When
    new MaUnitTestConfiguredCorrectly().check(comp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(MontiArcError.UNIT_DUPLICATE_ARGUMENTS));
  }

  @Test
  void wrongType() {
    // Given
    ASTArcComponentType comp = MontiArcMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(MontiArcMill.stereotypeBuilder().setValuesList(List.of(
        MontiArcMill.stereoValueBuilder().setName("test").build(),
        MontiArcMill.stereoValueBuilder().setName("p").setExpression(getIntLiteral(5)).build(),
        MontiArcMill.stereoValueBuilder().setName("s").build())).build())
      .setHead(MontiArcMill.componentHeadBuilder()
        .setArcParametersList(List.of(MontiArcMill.arcParameterBuilder()
          .setName("p").setMCType(Mockito.mock(ASTMCType.class))
          .build()))
        .build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    comp.setSymbol(Mockito.mock(ComponentTypeSymbol.class));
    comp.getHead().getArcParameter(0)
      .setSymbol(MontiArcMill.variableSymbolBuilder()
        .setName("p")
        .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build());

    // When
    new MaUnitTestConfiguredCorrectly().check(comp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(MontiArcError.UNIT_TYPE_MISMATCH));
  }

  @Test
  void mismatchedListValueCount() {
    // Given
    ASTArcComponentType comp = MontiArcMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(MontiArcMill.stereotypeBuilder().setValuesList(List.of(
          MontiArcMill.stereoValueBuilder().setName("test").build(),
          MontiArcMill.stereoValueBuilder().setName("p1")
            .setExpression(getListExpression(List.of(getIntLiteral(2), getIntLiteral(3)))).build(),
          MontiArcMill.stereoValueBuilder().setName("p2")
            .setExpression(getListExpression(List.of(getIntLiteral(2)))).build()))
        .build())
      .setHead(MontiArcMill.componentHeadBuilder().setArcParametersList(List.of(
        MontiArcMill.arcParameterBuilder()
          .setName("p1").setMCType(Mockito.mock(ASTMCType.class)).build(),
        MontiArcMill.arcParameterBuilder()
          .setName("p2").setMCType(Mockito.mock(ASTMCType.class)).build()
      )).build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    comp.setSymbol(Mockito.mock(ComponentTypeSymbol.class));
    comp.getHead().getArcParameter(0)
      .setSymbol(MontiArcMill.variableSymbolBuilder().setName("p1")
        .setType(SymTypeExpressionFactory.createPrimitive("int")).build());
    comp.getHead().getArcParameter(1)
      .setSymbol(MontiArcMill.variableSymbolBuilder().setName("p2")
        .setType(SymTypeExpressionFactory.createPrimitive("int")).build());

    // When
    new MaUnitTestConfiguredCorrectly().check(comp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(MontiArcError.UNIT_TEST_COUNT_MISMATCH));
  }

  protected static ASTExpression getIntLiteral(int value) {
    ASTNatLiteral literal = MontiArcMill.natLiteralBuilder().setDigits(String.valueOf(value)).build();
    literal.setEnclosingScope(MontiArcMill.globalScope());
    ASTExpression expression = MontiArcMill.literalExpressionBuilder().setLiteral(literal).build();
    expression.setEnclosingScope(MontiArcMill.globalScope());
    return expression;
  }

  protected static ASTExpression getListExpression(List<ASTExpression> elements) {
    return MontiArcMill.setEnumerationBuilder()
      .setSetAbsent()
      .setOpeningBracket("[")
      .setSetCollectionItemsList(elements.stream()
        .map(expr -> MontiArcMill.setValueItemBuilder()
          .setExpression(expr)
          .build())
        .collect(Collectors.toList()))
      .build();
  }
}
