/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.UniqueIdentifierNames;
import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class UniqueIdentifierNamesTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "uniqueIdentifierNames";
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new UniqueIdentifierNames());
  }

  @ParameterizedTest
  @ValueSource(strings = {"FineNames.arc"})
  public void shouldNotFindErrorInValidModel(@NotNull String model) {
    Preconditions.checkNotNull(model);

    testModel(model);
  }

  protected static Stream<Arguments> provideFaultyModels() {
    return Stream.of(
      arg("DuplicatedCompTypeNames.arc", repeatError(ArcError.UNIQUE_IDENTIFIER_NAMES, 6)),
      arg("DuplicatedConfigParamNames.arc", repeatError(ArcError.UNIQUE_IDENTIFIER_NAMES, 6)),
      arg("DuplicatedFieldNames.arc", repeatError(ArcError.UNIQUE_IDENTIFIER_NAMES, 6)),
      arg("DuplicatedPortNames.arc", repeatError(ArcError.UNIQUE_IDENTIFIER_NAMES, 6)),
      arg("DuplicatedSubCompNames.arc", repeatError(ArcError.UNIQUE_IDENTIFIER_NAMES, 6)),
      arg("DuplicatedTypeParamNames.arc", repeatError(ArcError.UNIQUE_IDENTIFIER_NAMES, 6)),
      arg("TripleFieldNames.arc", repeatError(ArcError.UNIQUE_IDENTIFIER_NAMES, 1))
    );
  }

  @ParameterizedTest
  @MethodSource("provideFaultyModels")
  public void shouldFindDuplicatedNames(@NotNull String model, Error... expectedErrors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrors);

    testModel(model, expectedErrors);
  }

  protected static ArcError[] repeatError(ArcError error, int repeats) {
    return Stream.generate(() -> error).limit(repeats).toArray(ArcError[]::new);
  }
}
