/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
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
import variablearc._cocos.NoFieldInConstraint;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

class NoFieldInConstraintTest extends MontiArcTestBase {

  private static final String SYMBOLS_DIR = "symbols";

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR)));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // component without a constraint
    "component ValidComp1 { }",
    // constraint referencing a parameter
    "component ValidComp2(int p) { constraint(p > 1); }",
    // field declared, but not referenced by the constraint
    "component ValidComp3 { int x = 1; constraint(true); }",
    // constraint referencing fields of a parameter (not of the component itself)
    "import montiarc.test.OOTypeWithFieldIO; component ValidComp4(OOTypeWithFieldIO p) { constraint(p.i > 1); constraint(p.o > 1); }",
    // constraint referencing static fields of an external type
    "import montiarc.test.OOTypeWithStaticFieldIO; component ValidComp5 { constraint(OOTypeWithStaticFieldIO.i > 1); constraint(OOTypeWithStaticFieldIO.o > 1); }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoFieldInConstraint());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoFieldInConstraint());

    checker.checkAll(ast);

    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // constraint directly referencing the component's own field
      arg("component InvalidComp1 { int x = 1; constraint(x > 0); }",
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // constraint referencing the component's own field via an increment expression
      arg("component InvalidComp2 { int x = 1; constraint(++x > 0); }",
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // constraint referencing two of the component's own fields
      arg("component InvalidComp3 { int x = 1; int y = 2; constraint(x > y); }",
        FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // constraint referencing the component's own field inside a conditional expression
      arg("component InvalidComp4 { int x = 1; constraint(x == 1 ? true : false); }",
        FIELD_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
