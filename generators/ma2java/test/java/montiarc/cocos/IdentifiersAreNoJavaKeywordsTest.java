/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class IdentifiersAreNoJavaKeywordsTest {
  
  @BeforeEach
  public void setUp() {
    LogStub.init();
    MontiArcMill.reset();
    MontiArcMill.init();
    BasicSymbolsMill.initializePrimitives();
  }
  
  @AfterEach
  public void clean() {
    MontiArcMill.globalScope().clear();
    Log.getFindings().clear();
  }
  
  protected static final String MODEL_PATH = "test/resources/cocos/identifiersAreNoJavaKeywords";
  
  protected static Stream<Arguments> modelsAndExpectedErrorsProvider() {
    return Stream.of(
        Arguments.of("WithoutJavaKeywords.arc", new Error[]{}),
        Arguments.of("WithAutomatonButNoJavaKeywords.arc", new Error[]{}),
        Arguments.of("PortHasJavaKeywords.arc", repeat(ArcError.RESTRICTED_IDENTIFIER, 17).toArray(Error[]::new)),
        Arguments.of("ParameterHasJavaKeywords.arc", repeat(ArcError.RESTRICTED_IDENTIFIER, 17).toArray(Error[]::new)),
        Arguments.of("FieldHasJavaKeywords.arc", repeat(ArcError.RESTRICTED_IDENTIFIER, 17).toArray(Error[]::new)),
        Arguments.of("TypeParamHasJavaKeywords.arc", repeat(ArcError.RESTRICTED_IDENTIFIER, 17).toArray(Error[]::new)),
        Arguments.of("AutomatonHasJavaKeywords.arc", repeat(ArcError.RESTRICTED_IDENTIFIER, 17).toArray(Error[]::new)),
        Arguments.of("ComponentTypeHasJavaKeywords.arc", repeat(ArcError.RESTRICTED_IDENTIFIER, 17).toArray(Error[]::new)),
        Arguments.of("ComponentInstanceHasJavaKeywords.arc", repeat(ArcError.RESTRICTED_IDENTIFIER, 17).toArray(Error[]::new))
    );
  }
  
  @ParameterizedTest
  @MethodSource("modelsAndExpectedErrorsProvider")
  public void testIdentifiersWithReservedKeywords(@NotNull String modelName,
                                                  @NotNull Error... expectedErrors) throws IOException {
    Preconditions.checkNotNull(modelName);
    Preconditions.checkNotNull(expectedErrors);
    
    // Given
    Path modelLocation = Path.of(MODEL_PATH, modelName);
    ASTMACompilationUnit ast = MontiArcMill.parser().parse(modelLocation.toString()).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);
    
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.AutomatonStateNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.ComponentTypeNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.ComponentInstanceNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.FieldNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.PortNoNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.ParameterNamesAreNoJavaKeywords());
    checker.addCoCo(new IdentifiersAreNoJavaKeywords.TypeParameterNamesAreNoJavaKeywords());
    
    // When
    checker.checkAll(ast);
    
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
  
  protected static <T> List<T> repeat(T item, int nTimes) {
    List<T> repeated = new ArrayList<>(nTimes);
    for (int i = 0; i < nTimes; i++) {
      repeated.add(item);
    }
    return repeated;
  }
}

