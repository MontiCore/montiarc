/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.stream.Stream;

public class RootComponentTypesNoInstanceNameTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "rootComponentTypesNoInstanceName";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new RootComponentTypesNoInstanceName());
  }

  protected static Stream<Arguments> provideModelsAndErrors() {
    return Stream.of(
      arg("RootWithInstanceName.arc", ArcError.ROOT_COMPONENT_TYPES_MISS_INSTANCE_NAMES)
    );
  }

  @ParameterizedTest
  @MethodSource("provideModelsAndErrors")
  public void shouldFindRootCompWithInstanceName(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    Optional<ASTMACompilationUnit> ast = this.getTool().parse(getPathToModel(model));
    Preconditions.checkState(ast.isPresent());

    //When
    this.getChecker().checkAll(ast.get());

    //Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }

  @ParameterizedTest
  @ValueSource(strings = {"RootWithoutInstanceName.arc"})
  public void shouldFindNoErrors(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    Optional<ASTMACompilationUnit> ast = this.getTool().parse(getPathToModel(model));
    Preconditions.checkState(ast.isPresent());

    //When
    this.getChecker().checkAll(ast.get());

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }
}
