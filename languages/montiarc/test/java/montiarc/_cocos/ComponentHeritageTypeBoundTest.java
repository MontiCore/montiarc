/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.TypeRelations;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import genericarc._cocos.ComponentHeritageTypeBound;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.GenericArcError;
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

public class ComponentHeritageTypeBoundTest extends MontiArcAbstractTest {

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
    "component Comp18 extends a.b.C { }"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentHeritageTypeBound(new TypeRelations()));

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
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentHeritageTypeBound(new TypeRelations()));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 extends a.b.D<boolean> { }",
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp2 extends a.b.E<java.lang.String> { }",
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp3 extends a.b.F<java.lang.Integer, java.lang.String> { }",
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND,
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp4 extends a.b.F<java.lang.Integer, java.lang.String> { }",
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND,
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp5 extends a.b.G<java.lang.Integer, java.lang.String> { }",
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp6 extends a.b.H<java.lang.Integer, java.lang.String> { }",
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp7<T extends java.lang.String> extends a.b.E<T> { }",
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp8 extends a.b.C<java.lang.Integer> { }",
        GenericArcError.HERITAGE_TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp8 extends a.b.F<java.lang.Integer> { }",
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND,
        GenericArcError.HERITAGE_TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp9 extends a.b.F<java.lang.String> { }",
        GenericArcError.HERITAGE_TOO_FEW_TYPE_ARGUMENTS),
      arg("component Comp10 extends a.b.A<java.lang.Integer> { }",
        GenericArcError.HERITAGE_TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp11 extends a.b.B<java.lang.Integer, java.lang.Integer> { }",
        GenericArcError.HERITAGE_TOO_MANY_TYPE_ARGUMENTS),
      arg("component Comp12 extends a.b.E<java.lang.String, java.lang.Integer> { }",
        GenericArcError.HERITAGE_TOO_MANY_TYPE_ARGUMENTS,
        GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND),
      arg("component Comp13 extends a.b.E<java.lang.Integer, java.lang.Integer> { }",
        GenericArcError.HERITAGE_TOO_MANY_TYPE_ARGUMENTS)
    );
  }
}