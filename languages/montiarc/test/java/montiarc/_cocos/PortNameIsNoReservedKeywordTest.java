/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortNameIsNoReservedKeyword;
import montiarc.util.Error;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

class PortNameIsNoReservedKeywordTest extends AbstractCoCoTest {

  protected static String PACKAGE = "portNameIsNoReservedKeyword";

  /**
   * Returns a collection of the keywords that are used in this test and that should not be used for identifier names.
   */
  protected static Collection<String> provideKeywords() {
    Collection<String> keywords = new HashSet<>(22);
    for(int i = 0; i < 11; i++) {
      keywords.add("reserved" + i);
      keywords.add("keyword" + i);
    }

    return keywords;
  }

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new PortNameIsNoReservedKeyword("testLang", provideKeywords()));
  }

  protected static Stream<Arguments> modelAndExpectedErrorProvider() {
    return Stream.of(
      arg("PortNamesAreLegal.arc"),
      arg("PortNamesAreKeywords.arc",  // Expect 9 violations
        ArcError.RESERVED_KEYWORD_USED, ArcError.RESERVED_KEYWORD_USED, ArcError.RESERVED_KEYWORD_USED,
        ArcError.RESERVED_KEYWORD_USED, ArcError.RESERVED_KEYWORD_USED, ArcError.RESERVED_KEYWORD_USED,
        ArcError.RESERVED_KEYWORD_USED, ArcError.RESERVED_KEYWORD_USED, ArcError.RESERVED_KEYWORD_USED)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorProvider")
  void testPortNamesAreExpectedKeywords(@NotNull String modelName, @NotNull Error... expectedErrors) {
    this.testModel(modelName, expectedErrors);
  }
}
