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
    "component Comp1 { }",
    "component Comp2(int p) { constraint(p > 1); }",
    "component Comp3 { int x = 1; constraint(true); }",
    "import montiarc.test.OOTypeWithFieldIO; component Comp4(OOTypeWithFieldIO p) { constraint(p.i > 1); constraint(p.o > 1); }",
    "import montiarc.test.OOTypeWithStaticFieldIO; component Comp5 { constraint(OOTypeWithStaticFieldIO.i > 1); constraint(OOTypeWithStaticFieldIO.o > 1); }"
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
      arg("component Comp1 { int x = 1; constraint(x > 0); }",
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      arg("component Comp2 { int x = 1; constraint(++x > 0); }",
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      arg("component Comp3 { int x = 1; int y = 2; constraint(x > y); }",
        FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      arg("component Comp4 { int x = 1; constraint(x == 1 ? true : false); }",
        FIELD_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
