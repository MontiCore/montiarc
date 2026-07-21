/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.FeedbackStrongCausality;
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

import java.util.stream.Stream;

import static montiarc.util.ArcError.FEEDBACK_CAUSALITY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link FeedbackStrongCausality}.
 */
class FeedbackStrongCausalityTest extends MontiArcTestBase {

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    this.setUpComponents();
  }

  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i; } ");
    compile("package a.b; component C { port out int o; } ");
    compile("package a.b; component D { port in int i; port out int o; <<delayed>> compute {}}");
    compile("package a.b; component E { port in int i; port out int o; }");
    compile("package a.b; component F { port in int i1, i2; port out int o; <<delayed>> compute {}}");
    compile("package a.b; component G { port in int i1, i2; port out int o; }");
    compile("package a.b; component H { port in int i; port out int o; D sub; i -> sub.i; sub.o -> o; }");
    compile("package a.b; component I { port in int i; port out int o; E sub; i -> sub.i; sub.o -> o; }");
    compile("package a.b; component J { port in int i; port out int o; D sub1; E sub2; i -> sub1.i; sub1.o -> sub2.i; sub2.o -> o; } ");
    compile("package a.b; component K { port in int i; port out int o; B sub1; C sub2; i -> sub1.i; sub2.o -> o; } ");
    compile("package a.b; component L { port in int i1, i2; port out int o1, o2; a.b.E fwd1, fwd2; i1 -> fwd1.i; fwd1.o -> o1; i2 -> fwd2.i; fwd2.o -> o2; } ");
    compile("package a.b; component M { port in int i1, i2; port out int o; automaton {}} ");
    compile("package a.b; component N { port in int i1, i2; port out int o; a.b.D d; a.b.M c; i1 -> c.i1; i2 -> d.i; d.o -> c.i2; c.o -> o; } ");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithEffectChains")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackStrongCausality());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithEffectChains")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackStrongCausality());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // component without subcomponents
      arg("""
        component ValidComp1 { }
        """
      ),
      // subcomponent without ports
      arg("""
        component ValidComp2 {
          a.b.A sub;
        }
        """
      ),
      // input forward to sink
      arg("""
        component ValidComp3 {
          port in int i;
          a.b.B sub;
          i -> sub.i;
        }
        """
      ),
      // output forward from source
      arg("""
        component ValidComp4 {
          port out int o;
          a.b.E sub;
          sub.o -> o;
        }
        """
      ),
      // direct strongly causal feedback loop
      arg("""
        component ValidComp5 {
          a.b.D sub;
          sub.o -> sub.i;
        }
        """
      ),
      // direct strongly causal feedback loop with input and output forward
      arg("""
        component ValidComp6 {
          port in int i;
          port out int o;
          a.b.F sub;
          i -> sub.i1;
          sub.o -> sub.i2;
          sub.o -> o;
        }
        """
      ),
      // indirect strongly causal feedback loop
      arg("""
        component ValidComp7 {
          a.b.D sub1;
          a.b.E sub2;
          sub1.o -> sub2.i;
          sub2.o -> sub1.i;
        }
        """
      ),
      // strongly causal feedback loop with input and output forward
      arg("""
        component ValidComp8 {
          port in int i;
          port out int o;
          a.b.G sub1;
          a.b.D sub2;
          i -> sub1.i1;
          sub1.o -> sub2.i;
          sub2.o -> sub1.i2;
          sub2.o -> o;
        }
        """
      ),
      // direct strongly causal feedback loop & nested subcomponent
      arg("""
        component ValidComp9 {
          a.b.H sub;
          sub.o -> sub.i;
        }
        """
      ),
      // indirect strongly causal feedback loop & nested subcomponent
      arg("""
        component ValidComp10 {
          a.b.J sub;
          sub.o -> sub.i;
        }
        """
      ),
      // multiple strongly causal feedback loops & port forward
      arg("""
        component ValidComp11 {
          port in int i;
          port out int o;
          a.b.G sub1;
          a.b.F sub2;
          i -> sub1.i1;
          sub1.o -> sub2.i1;
          sub1.o -> sub2.i2;
          sub2.o -> sub1.i2;
          sub2.o -> o;
        }
        """
      ),
      // multiple strongly causal feedback loops & port forward
      arg("""
        component ValidComp12 {
          port in int i;
          port out int o;
          a.b.F sub1;
          a.b.G sub2;
          i -> sub1.i1;
          sub1.o -> sub2.i1;
          sub1.o -> sub2.i2;
          sub2.o -> sub1.i2;
          sub2.o -> o;
        }
        """
      ),
      // multiple strongly causal feedback loops & port forward (connector with multiple targets)
      arg("""
        component ValidComp13 {
          port in int i;
          port out int o;
          a.b.G sub1;
          a.b.F sub2;
          i -> sub1.i1;
          sub1.o -> sub2.i1, sub2.i2;
          sub2.o -> sub1.i2, o;
        }
        """
      ),
      // multiple strongly causal feedback loops & port forward (connector with multiple targets)
      arg("""
        component ValidComp14 {
          port in int i;
          port out int o;
          a.b.F sub1;
          a.b.G sub2;
          i -> sub1.i1;
          sub1.o -> sub2.i1, sub2.i2;
          sub2.o -> sub1.i2, o;
        }
        """
      ),
      // Strongly causal feedback loops with nested sink and source
      arg("""
        component ValidComp15 {
          a.b.K sub;
          sub.o -> sub.i;
        }
        """
      ),
      // directly strongly causal with behavior declaring the delay
      arg("""
        component ValidComp16 {
          port in int i;
          port out int o;
          component Inner inner {
            port in int i;
            port out int o;
            <<delayed>> automaton {}
          }
          i -> inner.i; inner.o -> o;
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // direct non strongly causal feedback loop
      arg("""
          component InvalidComp1 {
            a.b.E sub;
            sub.o -> sub.i;
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // direct non strongly causal feedback loop & port forward
      arg("""
          component InvalidComp2 {
            port in int i;
            port out int o;
            a.b.G sub;
            i -> sub.i1;
            sub.o -> sub.i2;
            sub.o -> o;
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // non strongly causal feedback loop
      arg("""
          component InvalidComp3 {
            a.b.E sub1;
            a.b.E sub2;
            sub1.o -> sub2.i;
            sub2.o -> sub1.i;
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // non strongly causal feedback loop & port forward
      arg("""
          component InvalidComp4 {
            port in int i;
            port out int o;
            a.b.G sub1;
            a.b.E sub2;
            i -> sub1.i1;
            sub1.o -> sub2.i;
            sub2.o -> sub1.i2;
            sub2.o -> o;
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // direct non strongly causal feedback loop & nested subcomponent
      arg("""
          component InvalidComp5 {
            a.b.I sub;
            sub.o -> sub.i;
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // multiple direct non strongly causal feedback loops
      arg("""
          component InvalidComp6 {
            a.b.G sub;
            sub.o -> sub.i1;
            sub.o -> sub.i2;
          }
          """,
        FEEDBACK_CAUSALITY,
        FEEDBACK_CAUSALITY
      ),
      // multiple non strongly causal feedback loops
      arg("""
          component InvalidComp7 {
            a.b.G sub1;
            a.b.E sub2;
            a.b.E sub3;
            sub1.o -> sub2.i;
            sub1.o -> sub3.i;
            sub2.o -> sub1.i1;
            sub3.o -> sub1.i2;
          }
          """,
        FEEDBACK_CAUSALITY,
        FEEDBACK_CAUSALITY
      ),
      // multiple non strongly causal feedback loops & port forward
      arg("""
          component InvalidComp8 {
            port in int i;
            port out int o;
            a.b.G sub1;
            a.b.G sub2;
            i -> sub1.i1;
            sub1.o -> sub2.i1;
            sub1.o -> sub2.i2;
            sub2.o -> sub1.i2;
            sub2.o -> o;
          }
          """,
        FEEDBACK_CAUSALITY,
        FEEDBACK_CAUSALITY
      ),
      // multiple non strongly causal feedback loops & port forward (connector with multiple targets)
      arg("""
          component InvalidComp9 {
            port in int i;
            port out int o;
            a.b.G sub1;
            a.b.G sub2;
            i -> sub1.i1;
            sub1.o -> sub2.i1, sub2.i2;
            sub2.o -> sub1.i2, o;
          }
          """,
        FEEDBACK_CAUSALITY,
        FEEDBACK_CAUSALITY

      )
    );
  }

  static Stream<Arguments> validModelsWithEffectChains() {
    return Stream.of(
      // Independent effect chains in subcomponent
      arg("""
        component ValidCompWithEffectChains1 {
          port in int i;
          port out int o;
          a.b.L sub;
          i -> sub.i1;
          sub.o1 -> sub.i2;
          sub.o2 -> o;
        }
        """
      ),
      // Independent effect chains through delay
      arg("""
        component ValidCompWithEffectChains2{
          a.b.N sub;
          sub.o -> sub.i2;
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithEffectChains() {
    return Stream.of(
      // compare to ValidComp18, this does not use the delay
      arg("""
          component InvalidCompWithEffectChains1 {
            a.b.N sub;
            sub.o -> sub.i1;
          }
          """,
        FEEDBACK_CAUSALITY
      )
    );
  }
}
