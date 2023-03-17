/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.generator.MontiArcTool;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class IdentifiersAreNoJavaKeywordsTest {

  protected static final String MODEL_PATH = "test/resources/cocos/identifiersAreNoJavaKeywords";

  protected static Stream<Arguments> modelsAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("WithoutJavaKeywords.arc", new Error[]{}),
      Arguments.of("WithAutomatonButNoJavaKeywords.arc", new Error[]{}),
      Arguments.of("PortHasJavaKeywords.arc", repeat(ArcError.RESERVED_KEYWORD_USED, 17).toArray(Error[]::new)),
      Arguments.of("ParameterHasJavaKeywords.arc", repeat(ArcError.RESERVED_KEYWORD_USED, 17).toArray(Error[]::new)),
      Arguments.of("FieldHasJavaKeywords.arc", repeat(ArcError.RESERVED_KEYWORD_USED, 17).toArray(Error[]::new)),
      Arguments.of("TypeParamHasJavaKeywords.arc", repeat(ArcError.RESERVED_KEYWORD_USED, 17).toArray(Error[]::new)),
      Arguments.of("AutomatonHasJavaKeywords.arc", repeat(ArcError.RESERVED_KEYWORD_USED, 17).toArray(Error[]::new)),
      Arguments.of("ComponentTypeHasJavaKeywords.arc", repeat(ArcError.RESERVED_KEYWORD_USED, 17).toArray(Error[]::new)),
      Arguments.of("ComponentInstanceHasJavaKeywords.arc", repeat(ArcError.RESERVED_KEYWORD_USED, 17).toArray(Error[]::new))
    );
  }

  @BeforeEach
  public void init() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
  }

  @ParameterizedTest
  @MethodSource("modelsAndExpectedErrorsProvider")
  public void testIdentifiersWithReservedKeywords(@NotNull String modelName, @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(modelName);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    Path modelLocation = Path.of(MODEL_PATH, modelName);
    MontiArcTool tool = new MontiArcTool();
    tool.init();
    tool.initializeBasicTypes();
    Log.enableFailQuick(false);
    ASTMACompilationUnit asts = tool.parse(modelLocation.toString());
    tool.createSymbolTable(asts);
    tool.completeSymbolTable(asts);

    // When
    tool.runAdditionalCoCos(asts);

    // Then
    String[] actualErrors = Log.getFindings()
      .stream()
      .filter(Finding::isError)
      .map(f -> f.getMsg().split(/*:whitespace*/ ":\\s")[0])
      .toArray(String[]::new);
    String[] expectedErrorsAsList = Arrays.stream(expectedErrors)
      .map(Error::getErrorCode)
      .toArray(String[]::new);

    Assertions.assertArrayEquals(expectedErrorsAsList, actualErrors);
  }

  protected static<T> List<T> repeat(T item, int nTimes) {
    List<T> repeated = new ArrayList<>(nTimes);
    for(int i = 0; i < nTimes; i++) {
      repeated.add(item);
    }
    return repeated;
  }
}
