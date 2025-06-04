/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._cocos.TypeBound;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
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

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeBoundTest extends MontiArcTestBase {

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

  // Heritage TypeBound tests
  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 extends a.b.A { }",
    "component Comp2 extends a.b.B<int> { }",
    "component Comp3 extends a.b.B<java.lang.Integer> { }",
    "component Comp5 extends a.b.C<int, int> { }",
    "component Comp5 extends a.b.C<java.lang.Integer, java.lang.String> { }",
    "component Comp6 extends a.b.D<int> { }",
    "component Comp7 extends a.b.E<java.lang.Integer> { }",
    "component Comp8 extends a.b.F<java.lang.String, java.lang.Integer> { }",
    //"component Comp9 extends a.b.G<java.lang.Integer, java.lang.Integer> { }",
    //"component Comp10 extends a.b.H<java.lang.Integer, java.lang.Integer> { }",
    "component Comp11<T> extends a.b.A { }",
    "component Comp12<T> extends a.b.B<T> { }",
    "component Comp13<T> extends a.b.C<T, T> { }",
    "component comp14<T, U> extends a.b.C<T, U> { }",
    //"component comp15<T extends int> extends a.b.D<T> { }",
    "component comp16<T extends java.lang.Integer> extends a.b.E<T> { }",
    "component Comp17 extends a.b.B { }",
    "component Comp18 extends a.b.C { }",
    "component Comp19 extends a.b.B, a.b.C { }",
    "component Comp20 extends a.b.E<java.lang.Integer>, a.b.F<java.lang.String, java.lang.Integer> { }",
    "component comp21<T, U> extends a.b.B<T>, a.b.C<T, U> { }"
  })
  public void shouldNotReportErrorForHeritage(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidHeritageModels")
  public void shouldReportErrorForHeritage(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());

    // When
    checker.checkAll(ast);

    // Then
    //assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidHeritageModels() {
    return Stream.of(
      arg("component Comp1 extends a.b.D<boolean> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp2 extends a.b.E<java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp3 extends a.b.F<java.lang.Integer, java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp4 extends a.b.F<java.lang.Integer, java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp5 extends a.b.G<java.lang.Integer, java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp6 extends a.b.H<java.lang.Integer, java.lang.String> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp7<T extends java.lang.String> extends a.b.E<T> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp8 extends a.b.C<java.lang.Integer> { }",
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp8 extends a.b.F<java.lang.Integer> { }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp9 extends a.b.F<java.lang.String> { }",
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp10 extends a.b.A<java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp11 extends a.b.B<java.lang.Integer, java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp12 extends a.b.E<java.lang.String, java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp13 extends a.b.E<java.lang.Integer, java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp14 extends a.b.A<java.lang.Integer>, a.b.E<java.lang.String, java.lang.Integer> { }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS,
        ArcError.TOO_MANY_TYPE_ARGUMENTS,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND)
    );
  }

  // Subcomponent TypeBound tests
  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { a.b.A sub; }",
    "component Comp2 { a.b.B<java.lang.Integer> sub; }",
    "component Comp3 { a.b.B<java.lang.String> sub; }",
    "component Comp4 { a.b.C<java.lang.Integer, java.lang.Integer> sub; }",
    "component Comp5 { a.b.D<int> sub; }",
    "component Comp6 { a.b.E<java.lang.Integer> sub; }",
    "component Comp7 { a.b.F<java.lang.String, java.lang.Integer> sub; }",
    //"component Comp8 { a.b.G<java.lang.Integer, java.lang.Integer> sub; }",
    //"component Comp9 { a.b.H<java.lang.Integer, java.lang.Integer> sub; }"
    "component Comp10<T> { a.b.A sub; }",
    "component Comp11<T> { a.b.B<T> sub; }",
    "component Comp12<T> { a.b.C<T, T> sub; }",
    "component comp13<T, U> { a.b.C<T, U> sub; }",
    "component comp14<T extends java.lang.Integer> { a.b.E<T> sub; }",
    "component Comp17 { a.b.B sub; }",
    "component Comp18 { a.b.C sub; }"
  })
  public void shouldNotReportErrorForSubcomponent(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidSubcomponentModels")
  public void shouldReportErrorForSubcomponent(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidSubcomponentModels() {
    return Stream.of(
      arg("component Comp1 { a.b.D<boolean> sub; }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp2 { a.b.E<java.lang.String> sub; }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp3 { a.b.F<java.lang.Integer, java.lang.String> sub; }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp4 { a.b.F<java.lang.Integer, java.lang.String> sub; }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp5 { a.b.G<java.lang.Integer, java.lang.String> sub; }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp6 { a.b.H<java.lang.Integer, java.lang.String> sub; }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp7<T extends java.lang.String> { a.b.E<T> sub; }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp8 { a.b.C<java.lang.Integer> sub; }",
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp8 { a.b.F<java.lang.Integer> sub; }",
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp9 { a.b.F<java.lang.String> sub; }",
        ArcError.TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp10 { a.b.A<java.lang.Integer> sub; }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp11 { a.b.B<java.lang.Integer, java.lang.Integer> sub; }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp12 { a.b.E<java.lang.String, java.lang.Integer> sub; }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS,
        ArcError.TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp13 { a.b.E<java.lang.Integer, java.lang.Integer> sub; }",
        ArcError.TOO_MANY_TYPE_ARGUMENTS)
    );
  }

  // Refinement TypeBound tests
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
  void shouldNotReportErrorForRefinement(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidRefinementModels")
  void shouldReportErrorForRefinement(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidRefinementModels() {
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
