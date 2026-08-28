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
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.TOO_FEW_TYPE_ARGUMENTS;
import static montiarc.util.ArcError.TOO_MANY_TYPE_ARGUMENTS;
import static montiarc.util.ArcError.TYPE_ARG_IGNORES_UPPER_BOUND;
import static montiarc.util.MCError.MISSING_COMPONENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link TypeBound}.
 */
class TypeBoundTest extends MontiArcTestBase {

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
    // extends a non-generic supertype
    "component ValidComp1 extends a.b.A { }",
    // extends a generic supertype with a primitive type argument
    "component ValidComp2 extends a.b.B<int> { }",
    // extends a generic supertype with a boxed type argument
    "component ValidComp3 extends a.b.B<java.lang.Integer> { }",
    // extends a two-parameter generic supertype with matching primitive type arguments
    "component ValidComp4 extends a.b.C<int, int> { }",
    // extends a two-parameter generic supertype with different type arguments
    "component ValidComp5 extends a.b.C<java.lang.Integer, java.lang.String> { }",
    // extends a supertype with a primitive upper bound, with a matching argument
    "component ValidComp6 extends a.b.D<int> { }",
    // extends a supertype with a generic upper bound, with a satisfying argument
    "component ValidComp7 extends a.b.E<java.lang.Integer> { }",
    // extends a supertype with two independently-bounded type parameters, both satisfied
    "component ValidComp8 extends a.b.F<java.lang.String, java.lang.Integer> { }",
    //"component ValidComp9 extends a.b.G<java.lang.Integer, java.lang.Integer> { }",
    //"component ValidComp10 extends a.b.H<java.lang.Integer, java.lang.Integer> { }",
    // generic subtype extends a non-generic supertype
    "component ValidComp11<T> extends a.b.A { }",
    // generic subtype extends a supertype using its own type parameter
    "component ValidComp12<T> extends a.b.B<T> { }",
    // generic subtype extends a two-parameter supertype reusing its own type parameter for both
    "component ValidComp13<T> extends a.b.C<T, T> { }",
    // generic subtype (two type parameters) extends a two-parameter supertype with matching parameters
    "component ValidComp14<T, U> extends a.b.C<T, U> { }",
    //"component ValidComp15<T extends int> extends a.b.D<T> { }",
    // generic subtype whose own type parameter bound is consistent with the supertype's bound
    "component ValidComp16<T extends java.lang.Integer> extends a.b.E<T> { }",
    // raw use of a generic supertype (no type argument to bound-check)
    "component ValidComp17 extends a.b.B { }",
    // raw use of a two-parameter generic supertype
    "component ValidComp18 extends a.b.C { }",
    // multi-inheritance with two raw generic supertypes
    "component ValidComp19 extends a.b.B, a.b.C { }",
    // multi-inheritance from two independently-bounded generic supertypes, both satisfied
    "component ValidComp20 extends a.b.E<java.lang.Integer>, a.b.F<java.lang.String, java.lang.Integer> { }",
    // generic subtype (two type parameters) with multi-inheritance reusing its parameters across both supertypes
    "component ValidComp21<T, U> extends a.b.B<T>, a.b.C<T, U> { }"
  })
  void shouldNotReportErrorForHeritage(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidHeritageModels")
  void shouldReportErrorForHeritage(@NotNull String model, @NotNull Error... errors) {
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

  protected static Stream<Arguments> invalidHeritageModels() {
    return Stream.of(
      // type argument violates a primitive upper bound
      arg("component InvalidComp1 extends a.b.D<boolean> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates a generic upper bound
      arg("component InvalidComp2 extends a.b.E<java.lang.String> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // both type arguments violate their respective (swapped) upper bounds
      arg("component InvalidComp3 extends a.b.F<java.lang.Integer, java.lang.String> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND,
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates an upper bound that depends on another type parameter
      arg("component InvalidComp4 extends a.b.G<java.lang.Integer, java.lang.String> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates an upper bound declared before the parameter it depends on
      arg("component InvalidComp5 extends a.b.H<java.lang.Integer, java.lang.String> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // own type parameter's bound doesn't satisfy the supertype's required bound
      arg("component InvalidComp6<T extends java.lang.String> extends a.b.E<T> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // missing one type argument for a two-parameter supertype
      arg("component InvalidComp7 extends a.b.C<java.lang.Integer> { }",
        TOO_FEW_TYPE_ARGUMENTS),
      // missing one type argument, and the remaining one violates its upper bound
      arg("component InvalidComp8 extends a.b.F<java.lang.Integer> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND,
        TOO_FEW_TYPE_ARGUMENTS),
      // missing one type argument, the remaining one satisfies its upper bound
      arg("component InvalidComp9 extends a.b.F<java.lang.String> { }",
        TOO_FEW_TYPE_ARGUMENTS),
      // type argument given for a non-generic supertype
      arg("component InvalidComp10 extends a.b.A<java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS),
      // one extra type argument for a single-parameter supertype
      arg("component InvalidComp11 extends a.b.B<java.lang.Integer, java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS),
      // one extra type argument, and the first argument violates its upper bound
      arg("component InvalidComp12 extends a.b.E<java.lang.String, java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS,
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // one extra type argument, the first argument satisfies its upper bound
      arg("component InvalidComp13 extends a.b.E<java.lang.Integer, java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS),
      // multi-inheritance where both supertypes have too many type arguments, one also violating its bound
      arg("component InvalidComp14 extends a.b.A<java.lang.Integer>, a.b.E<java.lang.String, java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS,
        TOO_MANY_TYPE_ARGUMENTS,
        TYPE_ARG_IGNORES_UPPER_BOUND)
    );
  }

  // Subcomponent TypeBound tests
  @ParameterizedTest
  @ValueSource(strings = {
    // subcomponent of a non-generic type
    "component ValidComp1 { a.b.A sub; }",
    // subcomponent of a generic type with a boxed type argument
    "component ValidComp2 { a.b.B<java.lang.Integer> sub; }",
    // subcomponent of a generic type with a different boxed type argument
    "component ValidComp3 { a.b.B<java.lang.String> sub; }",
    // subcomponent of a two-parameter generic type with matching arguments
    "component ValidComp4 { a.b.C<java.lang.Integer, java.lang.Integer> sub; }",
    // subcomponent of a type with a primitive upper bound, with a matching argument
    "component ValidComp5 { a.b.D<int> sub; }",
    // subcomponent of a type with a generic upper bound, with a satisfying argument
    "component ValidComp6 { a.b.E<java.lang.Integer> sub; }",
    // subcomponent of a type with two independently-bounded type parameters, both satisfied
    "component ValidComp7 { a.b.F<java.lang.String, java.lang.Integer> sub; }",
    //"component ValidComp8 { a.b.G<java.lang.Integer, java.lang.Integer> sub; }",
    //"component ValidComp9 { a.b.H<java.lang.Integer, java.lang.Integer> sub; }"
    // generic component with a subcomponent of a non-generic type
    "component ValidComp10<T> { a.b.A sub; }",
    // generic component with a subcomponent using its own type parameter
    "component ValidComp11<T> { a.b.B<T> sub; }",
    // generic component with a subcomponent reusing its own type parameter for both arguments
    "component ValidComp12<T> { a.b.C<T, T> sub; }",
    // generic component (two type parameters) with a subcomponent using matching parameters
    "component ValidComp13<T, U> { a.b.C<T, U> sub; }",
    // generic component whose own type parameter bound is consistent with the subcomponent type's bound
    "component ValidComp14<T extends java.lang.Integer> { a.b.E<T> sub; }",
    // subcomponent that is a raw use of a generic type
    "component ValidComp15 { a.b.B sub; }",
    // subcomponent that is a raw use of a two-parameter generic type
    "component ValidComp16 { a.b.C sub; }"
  })
  void shouldNotReportErrorForSubcomponent(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidSubcomponentModels")
  void shouldReportErrorForSubcomponent(@NotNull String model, @NotNull Error... errors) {
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
      // type argument violates a primitive upper bound
      arg("component InvalidComp1 { a.b.D<boolean> sub; }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates a generic upper bound
      arg("component InvalidComp2 { a.b.E<java.lang.String> sub; }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // both type arguments violate their respective (swapped) upper bounds
      arg("component InvalidComp3 { a.b.F<java.lang.Integer, java.lang.String> sub; }",
        TYPE_ARG_IGNORES_UPPER_BOUND,
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates an upper bound that depends on another type parameter
      arg("component InvalidComp4 { a.b.G<java.lang.Integer, java.lang.String> sub; }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates an upper bound declared before the parameter it depends on
      arg("component InvalidComp5 { a.b.H<java.lang.Integer, java.lang.String> sub; }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // own type parameter's bound doesn't satisfy the subcomponent type's required bound
      arg("component InvalidComp6<T extends java.lang.String> { a.b.E<T> sub; }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // missing one type argument for a two-parameter type
      arg("component InvalidComp7 { a.b.C<java.lang.Integer> sub; }",
        TOO_FEW_TYPE_ARGUMENTS),
      // missing one type argument, and the remaining one violates its upper bound
      arg("component InvalidComp8 { a.b.F<java.lang.Integer> sub; }",
        TYPE_ARG_IGNORES_UPPER_BOUND,
        TOO_FEW_TYPE_ARGUMENTS),
      // missing one type argument, the remaining one satisfies its upper bound
      arg("component InvalidComp9 { a.b.F<java.lang.String> sub; }",
        TOO_FEW_TYPE_ARGUMENTS),
      // type argument given for a non-generic type
      arg("component InvalidComp10 { a.b.A<java.lang.Integer> sub; }",
        TOO_MANY_TYPE_ARGUMENTS),
      // one extra type argument for a single-parameter type
      arg("component InvalidComp11 { a.b.B<java.lang.Integer, java.lang.Integer> sub; }",
        TOO_MANY_TYPE_ARGUMENTS),
      // one extra type argument, and the first argument violates its upper bound
      arg("component InvalidComp12 { a.b.E<java.lang.String, java.lang.Integer> sub; }",
        TOO_MANY_TYPE_ARGUMENTS,
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // one extra type argument, the first argument satisfies its upper bound
      arg("component InvalidComp13 { a.b.E<java.lang.Integer, java.lang.Integer> sub; }",
        TOO_MANY_TYPE_ARGUMENTS)
    );
  }

  // Refinement TypeBound tests
  @ParameterizedTest
  @ValueSource(strings = {
    // refines a non-generic supertype
    "component ValidComp1 refines a.b.A { }",
    // refines a generic supertype with a primitive type argument
    "component ValidComp2 refines a.b.B<int> { }",
    // refines a generic supertype with a boxed type argument
    "component ValidComp3 refines a.b.B<java.lang.Integer> { }",
    // refines a two-parameter generic supertype with matching primitive type arguments
    "component ValidComp4 refines a.b.C<int, int> { }",
    // refines a two-parameter generic supertype with different type arguments
    "component ValidComp5 refines a.b.C<java.lang.Integer, java.lang.String> { }",
    // refines a supertype with a primitive upper bound, with a matching argument
    "component ValidComp6 refines a.b.D<int> { }",
    // refines a supertype with a generic upper bound, with a satisfying argument
    "component ValidComp7 refines a.b.E<java.lang.Integer> { }",
    // refines a supertype with two independently-bounded type parameters, both satisfied
    "component ValidComp8 refines a.b.F<java.lang.String, java.lang.Integer> { }",
    //"component ValidComp9 refines a.b.G<java.lang.Integer, java.lang.Integer> { }",
    //"component ValidComp10 refines a.b.H<java.lang.Integer, java.lang.Integer> { }",
    // generic subtype refines a non-generic supertype
    "component ValidComp11<T> refines a.b.A { }",
    // generic subtype refines a supertype using its own type parameter
    "component ValidComp12<T> refines a.b.B<T> { }",
    // generic subtype refines a two-parameter supertype reusing its own type parameter for both
    "component ValidComp13<T> refines a.b.C<T, T> { }",
    // generic subtype (two type parameters) refines a two-parameter supertype with matching parameters
    "component ValidComp14<T, U> refines a.b.C<T, U> { }",
    //"component ValidComp15<T extends int> refines a.b.D<T> { }",
    // generic subtype whose own type parameter bound is consistent with the supertype's bound
    "component ValidComp16<T extends java.lang.Integer> refines a.b.E<T> { }",
    // raw use of a generic supertype (no type argument to bound-check)
    "component ValidComp17 refines a.b.B { }",
    // raw use of a two-parameter generic supertype
    "component ValidComp18 refines a.b.C { }",
    // multi-refinement with two raw generic supertypes
    "component ValidComp19 refines a.b.B, a.b.C { }",
    // multi-refinement from two independently-bounded generic supertypes, both satisfied
    "component ValidComp20 refines a.b.E<java.lang.Integer>, a.b.F<java.lang.String, java.lang.Integer> { }",
    // generic subtype (two type parameters) with multi-refinement reusing its parameters across both supertypes
    "component ValidComp21<T, U> refines a.b.B<T>, a.b.C<T, U> { }",
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
      // type argument violates a primitive upper bound
      arg("component InvalidComp1 refines a.b.D<boolean> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates a generic upper bound
      arg("component InvalidComp2 refines a.b.E<java.lang.String> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // both type arguments violate their respective (swapped) upper bounds
      arg("component InvalidComp3 refines a.b.F<java.lang.Integer, java.lang.String> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND,
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates an upper bound that depends on another type parameter
      arg("component InvalidComp4 refines a.b.G<java.lang.Integer, java.lang.String> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // type argument violates an upper bound declared before the parameter it depends on
      arg("component InvalidComp5 refines a.b.H<java.lang.Integer, java.lang.String> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // own type parameter's bound doesn't satisfy the supertype's required bound
      arg("component InvalidComp6<T extends java.lang.String> refines a.b.E<T> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // missing one type argument for a two-parameter supertype
      arg("component InvalidComp7 refines a.b.C<java.lang.Integer> { }",
        TOO_FEW_TYPE_ARGUMENTS),
      // missing one type argument, and the remaining one violates its upper bound
      arg("component InvalidComp8 refines a.b.F<java.lang.Integer> { }",
        TYPE_ARG_IGNORES_UPPER_BOUND,
        TOO_FEW_TYPE_ARGUMENTS),
      // missing one type argument, the remaining one satisfies its upper bound
      arg("component InvalidComp9 refines a.b.F<java.lang.String> { }",
        TOO_FEW_TYPE_ARGUMENTS),
      // type argument given for a non-generic supertype
      arg("component InvalidComp10 refines a.b.A<java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS),
      // one extra type argument for a single-parameter supertype
      arg("component InvalidComp11 refines a.b.B<java.lang.Integer, java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS),
      // one extra type argument, and the first argument violates its upper bound
      arg("component InvalidComp12 refines a.b.E<java.lang.String, java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS,
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // one extra type argument, the first argument satisfies its upper bound
      arg("component InvalidComp13 refines a.b.E<java.lang.Integer, java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS),
      // multi-refinement where both supertypes have too many type arguments, one also violating its bound
      arg("component InvalidComp14 refines a.b.A<java.lang.Integer>, a.b.E<java.lang.String, java.lang.Integer> { }",
        TOO_MANY_TYPE_ARGUMENTS,
        TOO_MANY_TYPE_ARGUMENTS,
        TYPE_ARG_IGNORES_UPPER_BOUND),
      // refines an undeclared component type (robustness: no cascading error beyond the missing type)
      arg("component InvalidComp15 refines a.b.X<java.lang.String> { }",
        MISSING_COMPONENT)
    );
  }

}
