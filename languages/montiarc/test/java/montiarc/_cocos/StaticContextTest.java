/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoInputPortsInInitialOutputDeclaration;
import arcbasis._cocos.ComponentArgumentsOmitPortRef;
import arcbasis._cocos.FieldInitOmitPortReferences;
import arcbasis._cocos.ParameterDefaultValueOmitsPortRef;
import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcAutomataError;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StaticContextTest extends MontiArcAbstractTest {

  protected static final String TEST_MODEL_PATH = "cocos/static_context";

  @ParameterizedTest
  @MethodSource("invalid_models")
  public void shouldReportPortRef(@NotNull String file,
                                  @NotNull Error error) throws IOException {
    Preconditions.checkNotNull(file);
    Preconditions.checkNotNull(error);
    Preconditions.checkArgument(!file.isBlank());

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse(Paths.get(RELATIVE_MODEL_PATH, TEST_MODEL_PATH, file).toString())
      .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));

    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(RELATIVE_MODEL_PATH, TEST_MODEL_PATH)));
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);


    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentArgumentsOmitPortRef());
    checker.addCoCo(new ParameterDefaultValueOmitsPortRef());
    checker.addCoCo(new FieldInitOmitPortReferences());
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount())
      .as("The list of findings " + Log.getFindings() + " should contain exactly one entry.")
      .isEqualTo(1);
    Assertions.assertThat(Log.getFindings().get(0).getMsg().substring(0, 7))
      .as("The expected error code.")
      .isEqualTo(error.getErrorCode());
  }

  protected static Stream<Arguments> invalid_models() {
    return Stream.of(
      Arguments.of("InvArgPort.arc", ArcError.COMP_ARG_PORT_REF),
      Arguments.of("InvArgPortSuper.arc", ArcError.COMP_ARG_PORT_REF),
      Arguments.of("InvDParaPort.arc", ArcError.PORT_REF_DEFAULT_VALUE),
      Arguments.of("InvDParaPortSuper.arc", ArcError.PORT_REF_DEFAULT_VALUE),
      Arguments.of("InvFInitPort.arc", ArcError.PORT_REF_FIELD_INIT),
      Arguments.of("InvExprPort.arc", ArcError.PORT_REF_FIELD_INIT),
      Arguments.of("InvStateInitPort.arc", ArcAutomataError.INPUT_PORT_IN_INITIAL_OUT_DECL)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "VNoPortRef.arc",
    "VDPara.arc",
    "VDPara2.arc",
    //"VExprPort.arc",
    //"VFInit.arc",
    "VFInit2.arc",
    //"VStateInitPort.arc"
  })
  public void shouldNotReport(@NotNull String file) throws IOException {

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse(Paths.get(RELATIVE_MODEL_PATH, TEST_MODEL_PATH, file).toString())
      .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));

    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(RELATIVE_MODEL_PATH, TEST_MODEL_PATH)));
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentArgumentsOmitPortRef());
    checker.addCoCo(new ParameterDefaultValueOmitsPortRef());
    checker.addCoCo(new FieldInitOmitPortReferences());
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount())
      .as("The list of findings " + Log.getFindings() + " should be empty.")
      .isEqualTo(0);
  }

}
