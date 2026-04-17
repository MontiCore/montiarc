/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.CompArgNoAssignmentExpr;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.trafo.MontiArcTrafos;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static montiarc.util.ArcError.INVALID_CONTEXT_ASSIGNMENT;
import static montiarc.util.ArcError.INVALID_CONTEXT_DEC_PREFIX;
import static montiarc.util.ArcError.INVALID_CONTEXT_DEC_SUFFIX;
import static montiarc.util.ArcError.INVALID_CONTEXT_INC_PREFIX;
import static montiarc.util.ArcError.INVALID_CONTEXT_INC_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link CompArgNoAssignmentExpr}.
 */
class CompArgNoAssignmentExprTest extends MontiArcTestBase {

  public static ASTMACompilationUnit compile(@NotNull String model) {
    Preconditions.checkNotNull(model);
    try {
      ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model)
        .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));
      MontiArcTrafos.afterParsing().applyAll(ast);
      return ast;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B(int p) { }");
    compile("package a.b; component C(int p1, int p2) { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // subcomponent instantiation without arguments
    "component ValidComp1 { a.b.A a(); }",
    // single positional argument
    "component ValidComp2 { a.b.B b(1); }",
    // single keyed argument
    "component ValidComp3 { a.b.B b(p = 1); }",
    // two positional arguments
    "component ValidComp4 { a.b.C c(1, 2); }",
    // one positional and one keyed argument
    "component ValidComp5 { a.b.C c(1, p2 = 2); }",
    // two keyed arguments
    "component ValidComp6 { a.b.C c(p1 = 1, p2 = 2); }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CompArgNoAssignmentExpr());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CompArgNoAssignmentExpr());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // nested assignment (=) as a component argument value
      arg("""
          component InvalidComp1 {
            a.b.B b(p = p = 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested addition-assignment (+=) as a component argument value
      arg("""
          component InvalidComp2 {
            a.b.B b(p = p += 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested subtraction-assignment (-=) as a component argument value
      arg("""
          component InvalidComp3 {
            a.b.B b(p = p -= 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested multiplication-assignment (*=) as a component argument value
      arg("""
          component InvalidComp4 {
            a.b.B b(p = p *= 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested division-assignment (/=) as a component argument value
      arg("""
          component InvalidComp5 {
            a.b.B b(p = p /= 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested unsigned-right-shift-assignment (>>>=) as a component argument value
      arg("""
          component InvalidComp6 {
            a.b.B b(p = p >>>= 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested and-assignment (&=) as a component argument value
      arg("""
          component InvalidComp7 {
            a.b.B b(p = p &= 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested or-assignment (|=) as a component argument value
      arg("""
          component InvalidComp8 {
            a.b.B b(p = p |= 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested xor-assignment (^=) as a component argument value
      arg("""
          component InvalidComp9 {
            a.b.B b(p = p ^= 1);
          }""",
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested right-shift-assignment (>>=) as a component argument value
      arg("""
          component InvalidComp10 {
            a.b.B b(p = p >>= 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested left-shift-assignment (<<=) as a component argument value
      arg("""
          component InvalidComp11 {
            a.b.B b(p = p <<= 1);
          }""",
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // nested modulo-assignment (%=) as a component argument value
      arg("""
          component InvalidComp12 {
            a.b.B b(p = p %= 1);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // one plain keyed argument, one keyed argument with a nested assignment
      arg("""
          component InvalidComp13 {
            a.b.C c(p1 = 1, p2 = p2 = 2);
          }
          """,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // one keyed argument with a nested assignment, one plain keyed argument
      arg("""
          component InvalidComp14 {
            a.b.C c(p1 = p1 = 1, p2 = 2);
          }""",
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // two keyed arguments, each with a nested assignment to the same parameter
      arg("""
          component InvalidComp15 {
            a.b.C c(p1 = p1 = 1, p2 = p2 = 2);
          }""",
        INVALID_CONTEXT_ASSIGNMENT,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // two keyed arguments, each with a nested assignment to a different parameter
      arg("""
          component InvalidComp16 {
            a.b.C c(p1 = p2 = 1, p1 = p2 = 2);
          }""",
        INVALID_CONTEXT_ASSIGNMENT,
        INVALID_CONTEXT_ASSIGNMENT
      ),
      // prefix increment expression as a positional component argument
      arg("""
          component InvalidComp17 {
            a.b.B b(++p);
          }""",
        INVALID_CONTEXT_INC_PREFIX
      ),
      // prefix decrement expression as a positional component argument
      arg("""
          component InvalidComp18 {
            a.b.B b(--p);
          }""",
        INVALID_CONTEXT_DEC_PREFIX
      ),
      // suffix increment expression as a positional component argument
      arg("""
          component InvalidComp19 {
            a.b.B b(p++);
          }""",
        INVALID_CONTEXT_INC_SUFFIX
      ),
      // suffix decrement expression as a positional component argument
      arg("""
          component InvalidComp20 {
            a.b.B b(p--);
          }""",
        INVALID_CONTEXT_DEC_SUFFIX
      ),
      // keyed argument with a nested prefix increment expression
      arg("""
          component InvalidComp21 {
            a.b.B b(p = ++p);
          }""",
        INVALID_CONTEXT_INC_PREFIX
      ),
      // keyed argument with a nested prefix decrement expression
      arg("""
          component InvalidComp22 {
            a.b.B b(p = --p);
          }""",
        INVALID_CONTEXT_DEC_PREFIX
      ),
      // keyed argument with a nested suffix increment expression
      arg("""
          component InvalidComp23 {
            a.b.B b(p = p++);
          }""",
        INVALID_CONTEXT_INC_SUFFIX
      ),
      // keyed argument with a nested suffix decrement expression
      arg("""
          component InvalidComp24 {
            a.b.B b(p = p--);
          }""",
        INVALID_CONTEXT_DEC_SUFFIX
      )
    );
  }
}
