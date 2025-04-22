/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.RefinementTypeBound;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import montiarc.MontiArcMill;
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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link RefinementTypeBound}.
 */
class RefinementTypeBoundTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B<T> { }");
    compile("package a.b; component C<T, U> { }");
    compile("package a.b; component D<T extends int> { }");
    compile("package a.b; component E<T extends java.lang.Comparable<java.lang.Integer>> { }");
    compile("package a.b; component F<T extends java.lang.Comparable<java.lang.String>, " +
                                   "U extends java.lang.Comparable<java.lang.Integer>> { }");
    compile("package a.b; component G<T, U extends java.lang.Comparable<T>> { }");
    compile("package a.b; component H<U extends java.lang.Comparable<T>, T> { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 refines a.b.A { }",
    "component Comp2 refines a.b.B<int> { }",
    "component Comp3 refines a.b.B<java.lang.Integer> { }",
    "component Comp5 refines a.b.C<int, int> { }",
    "component Comp5 refines a.b.C<java.lang.Integer, java.lang.String> { }",
    "component Comp6 refines a.b.D<int> { }",
    "component Comp7 refines a.b.E<java.lang.Integer> { }",
    "component Comp8 refines a.b.F<java.lang.String, java.lang.Integer> { }",
    //"component Comp9 refines a.b.G<java.lang.Integer, java.lang.Integer> { }",
    //"component Comp10 refines a.b.H<java.lang.Integer, java.lang.Integer> { }",
    "component Comp11<T> refines a.b.A { }",
    "component Comp12<T> refines a.b.B<T> { }",
    "component Comp13<T> refines a.b.C<T, T> { }",
    "component comp14<T, U> refines a.b.C<T, U> { }",
    //"component comp15<T extends int> refines a.b.D<T> { }",
    "component comp16<T extends java.lang.Integer> refines a.b.E<T> { }",
    "component Comp17 refines a.b.B { }",
    "component Comp18 refines a.b.C { }",
    "component Comp19 refines a.b.B, a.b.C { }",
    "component Comp20 refines a.b.E<java.lang.Integer>, a.b.F<java.lang.String, java.lang.Integer> { }",
    "component comp21<T, U> refines a.b.B<T>, a.b.C<T, U> { }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementTypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementTypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 refines a.b.D<boolean> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp2 refines a.b.E<java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp3 refines a.b.F<java.lang.Integer, java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp4 refines a.b.F<java.lang.Integer, java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp5 refines a.b.G<java.lang.Integer, java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp6 refines a.b.H<java.lang.Integer, java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp7<T extends java.lang.String> refines a.b.E<T> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp8 refines a.b.C<java.lang.Integer> { }",
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp8 refines a.b.F<java.lang.Integer> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp9 refines a.b.F<java.lang.String> { }",
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp10 refines a.b.A<java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp11 refines a.b.B<java.lang.Integer, java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp12 refines a.b.E<java.lang.String, java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp13 refines a.b.E<java.lang.Integer, java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp14 refines a.b.A<java.lang.Integer>, a.b.E<java.lang.String, java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS,
        ArcError.TOO_MANY_TYPE_ARGUMENTS,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component comp15 refines a.b.X<java.lang.String> { }",
        ArcError.MISSING_COMPONENT)
    );
  }
}