/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
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

import static montiarc.util.MCError.FOR_EACH_EXPR_NOT_ITERABLE;
import static montiarc.util.MCError.FOR_EACH_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ForEachIsValid4MA}.
 */
class ForEachIsValidTest extends MontiArcTestBase {

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpMultiInheritanceTypes();
  }

  protected void setUpMultiInheritanceTypes() {
    OOTypeSymbol i1 = MontiArcMill.oOTypeSymbolBuilder()
      .setName("I1")
      .setIsInterface(true)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    SymbolService.link(MontiArcMill.globalScope(), i1);

    OOTypeSymbol i2 = MontiArcMill.oOTypeSymbolBuilder()
      .setName("I2")
      .setIsInterface(true)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    SymbolService.link(MontiArcMill.globalScope(), i2);

    OOTypeSymbol mi1 = MontiArcMill.oOTypeSymbolBuilder()
      .setName("MI1")
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(i1))
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(i2))
      .build();
    SymbolService.link(MontiArcMill.globalScope(), mi1);

    OOTypeSymbol mi2 = MontiArcMill.oOTypeSymbolBuilder()
      .setName("MI2")
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(i1))
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(i2))
      .build();
    SymbolService.link(MontiArcMill.globalScope(), mi2);

    OOTypeSymbol unrelated = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Unrelated")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    SymbolService.link(MontiArcMill.globalScope(), unrelated);
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ForEachIsValid4MA());

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
    checker.addCoCo(new ForEachIsValid4MA());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // component no behavior
      arg("""
        component ValidComp1 { }
        """
      ),
      // for-each over set
      arg("""
        import java.lang.Boolean;
        import java.util.Set;
        component ValidComp2 {
          Set<Boolean> v = { };
          automaton {
            initial state S;
            S -> S / {
              for (Boolean i : v) { }
            }
          }
        }
        """
      ),
      // for-each over list
      arg("""
        import java.lang.Boolean;
        import java.util.List;
        component ValidComp3 {
          List<Boolean> v = [ ];
          automaton {
            initial state S;
            S -> S / {
              for (Boolean i : v) { }
            }
          }
        }
        """
      ),
      // for-each over subtype of set
      arg("""
        import java.lang.Boolean;
        import java.util.HashSet;
        component ValidComp4 {
          HashSet<Boolean> v = HashSet.HashSet();
          automaton {
            initial state S;
            S -> S / {
              for (Boolean i : v) { }
            }
          }
        }
        """
      ),
      // for-each over subtype of list
      arg("""
        import java.lang.Boolean;
        import java.util.ArrayList;
        component ValidComp5 {
          ArrayList<Boolean> v = ArrayList.ArrayList();
          automaton {
            initial state S;
            S -> S / {
              for (Boolean i : v) { }
            }
          }
        }
        """
      ),
      // for-each over collection
      arg("""
        import java.lang.Boolean;
        import java.util.Collection;
        component ValidComp6 {
          Collection<Boolean> v = [ ];
          automaton {
            initial state S;
            S -> S / {
              for (Boolean i : v) { }
            }
          }
        }
        """
      ),
      // for-each with subtype
      arg("""
        import java.lang.Integer;
        import java.lang.Number;
        import java.util.Collection;
        component ValidComp7 {
          Collection<Integer> v = [ ];
          automaton {
            initial state S;
            S -> S / {
              for (Number i : v) { }
            }
          }
        }
        """
      ),
      // for-each over String iterates characters
      arg("""
        import java.lang.String;
        component ValidComp8 {
          String v = "";
          automaton {
            initial state S;
            S -> S / {
              for (char i : v) { }
            }
          }
        }
        """
      ),
      // for-each over a list expression whose elements form a union type
      arg("""
        component ValidComp9 {
          port in MI1 mi1;
          port in MI2 mi2;
          automaton {
            initial state S;
            S -> S / {
              for (I1 e : [mi1, mi2]) { }
              for (I2 e : [mi1, mi2]) { }
            }
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // for-each with expression of non-iterable, non-generic type
      arg("""
          import java.lang.Boolean;
          component InvalidComp1 {
            Boolean v = true;
            automaton {
              initial state S;
              S -> S / {
                for (Boolean i : v) { }
              }
            }
          }
          """,
        FOR_EACH_EXPR_NOT_ITERABLE
      ),
      // for-each with expression of non-iterable, generic type
      arg("""
          import java.lang.Boolean;
          import java.util.Optional;
          component InvalidComp2 {
            Optional<Boolean> v = Optional.of(true);
            automaton {
              initial state S;
              S -> S / {
                for (Boolean i : v) { }
              }
            }
          }
          """,
        FOR_EACH_EXPR_NOT_ITERABLE
      ),
      // for-each with mismatching types
      arg("""
          import java.lang.Boolean;
          import java.lang.Integer;
          import java.util.Collection;
          import java.util.Collections;
          component InvalidComp3 {
            Collection<Boolean> v = [ ];
            automaton {
              initial state S;
              S -> S / {
                for (Integer i : v) { }
              }
            }
          }
          """,
        FOR_EACH_TYPE_MISMATCH
      ),
      // for-each with mismatching super- and subtype
      arg("""
          import java.lang.Integer;
          import java.lang.Number;
          import java.util.Collection;
          import java.util.Collections;
          component InvalidComp4 {
            Collection<Number> v = [ ];
            automaton {
              initial state S;
              S -> S / {
                for (Integer i : v) { }
              }
            }
          }
          """,
        FOR_EACH_TYPE_MISMATCH
      ),
      // for-each over String with non-char loop variable
      arg("""
          import java.lang.String;
          import java.lang.Boolean;
          component InvalidComp5 {
            String v = "";
            automaton {
              initial state S;
              S -> S / {
                for (Boolean i : v) { }
              }
            }
          }
          """,
        FOR_EACH_TYPE_MISMATCH
      ),
      // for-each over union-typed list where not every alternative fits the loop variable
      arg("""
          component InvalidComp6 {
            port in MI1 mi1;
            port in Unrelated unrelated;
            automaton {
              initial state S;
              S -> S / {
                for (I1 i : [mi1, unrelated]) { }
              }
            }
          }
          """,
        FOR_EACH_TYPE_MISMATCH
      )
    );
  }
}
