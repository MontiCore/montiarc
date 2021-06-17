/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.FieldInitExpressionsOmitPortReferences;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Tests {@link FieldInitExpressionsOmitPortReferencesTest} integrated into MontiArc
 */
public class FieldInitExpressionsOmitPortReferencesTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "fieldInitExpressionsOmitPortReferences";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new FieldInitExpressionsOmitPortReferences(
      new PortReferenceExtractor4CommonExpressions()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"IndependentChild.arc", "IndependentComp.arc", "WithoutPortRef.arc"})
  public void shouldNotFindPortReferenceInFieldInitExpression(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTComponentType ast = this.parseAndLoadAllSymbols(this.getPackage() + "." + model);

    // When
    this.getChecker().checkAll(ast);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @ParameterizedTest
  @MethodSource("provideFaultyModels")
  public void shouldFindPortReferenceInFieldInitExpression(@NotNull String model, ArcError... expectedErrors) {
    Preconditions.checkNotNull(model);

    // Given
    ASTComponentType ast = this.parseAndLoadAllSymbols(this.getPackage() + "." + model);

    // When
    this.getChecker().checkAll(ast);

    // Then
    this.checkOnlyExpectedErrorsPresent(expectedErrors);
  }

  public static Stream<Arguments> provideFaultyModels() {
    return Stream.of(
      Arguments.of("WithExternalModelInheritedPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL}),
      Arguments.of("WithExternalModelPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL}),
      Arguments.of("WithInheritedPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL}),
      Arguments.of("WithOwnPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL}),
      Arguments.of("WithSubCompPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL})
    );
  }

  protected ASTComponentType parseAndLoadAllSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    this.getTool().createSymbolTable(Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, PACKAGE));
    Preconditions.checkState(MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).isPresent());
    return MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).get().getAstNode();
  }
}
