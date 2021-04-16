/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.UniqueFieldNames;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UniqueFieldNamesTest extends AbstractCoCoTest {

  @Override
  protected String getPackage() {
    return "uniqueFieldNames";
  }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new UniqueFieldNames());
  }

  @Test
  public void shouldNotFindErrorInValidModel() {
    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols("FineNames.arc");

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  @ParameterizedTest
  @ValueSource(strings = {"DuplicatedNames.arc", "TripleNames.arc"})
  public void shouldFindDuplicatedNames(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), new ArcError[]{ArcError.UNIQUE_FIELD_NAME, ArcError.UNIQUE_FIELD_NAME});
  }
}