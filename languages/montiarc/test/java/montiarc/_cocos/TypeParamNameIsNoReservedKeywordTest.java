/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import genericarc._cocos.TypeParamNameIsNoReservedKeyword;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

public class TypeParamNameIsNoReservedKeywordTest extends AbstractCoCoTest {

  protected static String PACKAGE = "typeParamNameIsNoReservedKeyword";

  /**
   * Returns a collection of the keywords that are used in this test and that should not be used for identifier names.
   */
  protected static Collection<String> provideKeywords() {
    Collection<String> keywords = new HashSet<>(22);
    for(int i = 0; i < 11; i++) {
      keywords.add("Reserved" + i);
      keywords.add("Keyword" + i);
    }

    return keywords;
  }

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new TypeParamNameIsNoReservedKeyword("testLang", provideKeywords()));
  }

  protected static Stream<Arguments> modelAndExpectedErrorProvider() {
    return Stream.of(
      arg("TypeParamNamesAreLegal.arc"),
      arg("TypeParamNamesAreKeywords.arc",  // Expect 4 violations
        ArcError.RESTRICTED_IDENTIFIER, ArcError.RESTRICTED_IDENTIFIER, ArcError.RESTRICTED_IDENTIFIER,
        ArcError.RESTRICTED_IDENTIFIER)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorProvider")
  void testPortNamesAreExpectedKeywords(@NotNull String modelName, @NotNull Error... expectedErrors) {
    this.testModel(modelName, expectedErrors);
  }
}
