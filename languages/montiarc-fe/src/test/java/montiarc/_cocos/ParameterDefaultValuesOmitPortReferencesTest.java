/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ParameterDefaultValuesOmitPortReferences;
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

public class ParameterDefaultValuesOmitPortReferencesTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "parameterDefaultValuesOmitPortReferences";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ParameterDefaultValuesOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"IndependentChild.arc", "IndependentComp.arc", "WithoutPortRef.arc"})
  public void shouldNotFindPortReferenceInFieldInitExpression(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTComponentType ast = this.parseAndLoadWithPackageSymbols(this.getPackage() + "." + model);

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
    ASTComponentType ast = this.parseAndLoadWithPackageSymbols(this.getPackage() + "." + model);

    // When
    this.getChecker().checkAll(ast);

    // Then
    this.checkOnlyExpectedErrorsPresent(expectedErrors);
  }

  public static Stream<Arguments> provideFaultyModels() {
    return Stream.of(
      Arguments.of("WithExternalModelInheritedPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL}),
      Arguments.of("WithExternalModelPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL}),
      Arguments.of("WithInheritedPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL}),
      Arguments.of("WithOwnPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL}),
      Arguments.of("WithSubCompPortRef.arc", new ArcError[] {
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL,
        ArcError.PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL})
    );
  }

  protected ASTComponentType parseAndLoadWithPackageSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    this.getTool().createSymbolTable(Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, PACKAGE));
    Preconditions.checkState(MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).isPresent());
    return MontiArcMill.globalScope().resolveComponentType(FilenameUtils.removeExtension(model)).get().getAstNode();
  }
}