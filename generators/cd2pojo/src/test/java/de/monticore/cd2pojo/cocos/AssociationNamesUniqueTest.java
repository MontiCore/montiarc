/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo.cocos;

import com.google.common.base.Preconditions;
import de.monticore.cd2pojo.POJOGeneratorTool;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Holds tests for {@link CDAssociationNamesUnique}.
 */
public class AssociationNamesUniqueTest {

  protected static final String TEST_RESOURCE_PATH = "src/test/resources/cocos";

  protected static Stream<Arguments> modelWithAmbiguousAssociationsAndErrorCountProvider() {
    return Stream.of(Arguments.of("AmbiguousAssociations.cd", 1));
  }

  @BeforeAll
  protected static void setUpLog() {
    Log.enableFailQuick(false);
  }

  @BeforeEach
  protected void clearLog() {
    Log.getFindings().clear();
  }

  @ParameterizedTest
  @MethodSource("modelWithAmbiguousAssociationsAndErrorCountProvider")
  public void shouldDetectAmbiguousAssociations(@NotNull String fileName, int expectedErrorCount) {
    Preconditions.checkNotNull(fileName);
    //Given
    Path pathToModel = Paths.get(TEST_RESOURCE_PATH, fileName);

    //WHen
    POJOGeneratorTool.loadCD4CModelsFromPaths(Collections.singleton(pathToModel),
      new CD4CodeCoCos().getCheckerForAllCoCos()
      .addCoCo(new CDAssociationNamesUnique()));

    //Then
    Assertions.assertEquals(expectedErrorCount, Log.getErrorCount());
  }
}