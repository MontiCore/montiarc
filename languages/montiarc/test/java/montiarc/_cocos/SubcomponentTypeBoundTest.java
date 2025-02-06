/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
import arcbasis._cocos.SubcomponentTypeBound;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SubcomponentTypeBoundTest extends MontiArcTestBase {

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
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentTypeBound());

    // When
    checker.checkAll(ast);

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

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentTypeBound());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
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
}
