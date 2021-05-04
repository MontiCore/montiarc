/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

public class MontiArcDeSerTest extends AbstractTest {

  protected static final String MODEL_PATH = "montiarc/_symboltable/deserialization/";

  protected MontiArcSymbols2Json deSer = new MontiArcSymbols2Json();

  protected MontiArcSymbols2Json getDeSer() {
    return this.deSer;
  }

  @BeforeEach
  public void setUpDeSer() {
    this.deSer = new MontiArcSymbols2Json();
  }

  @ParameterizedTest
  @ValueSource(strings = {"Builder.json", "SingleTypeSymbol.json", "SymbolWithPackage.json", "OOTypeWithField.json", "MultipleTypeSymbols.json"})
  public void shouldDeserializeWithoutErrors(@NotNull String filename) {
    Preconditions.checkNotNull(filename);
    //Given && When
    IMontiArcScope scope = this.loadTestModel(filename);

    //Then
    Assertions.assertNotNull(scope);
    Assertions.assertEquals(0, Log.getErrorCount(), ()->Log.getFindings().toString());
  }

  @Test
  public void shouldDeserializeSingleTypeSymbol() {
    //Given && When
    IMontiArcScope scope = this.loadTestModel("SingleTypeSymbol.json");

    //Then
    Assertions.assertFalse(scope.getLocalTypeSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalTypeSymbols().size());
    Assertions.assertEquals("A", scope.getLocalTypeSymbols().get(0).getName());
    Assertions.assertEquals("A", scope.getLocalTypeSymbols().get(0).getFullName());
  }

  @Test
  public void shouldDeserializeWithPackage() {
    //Given && When
    IMontiArcScope scope = this.loadTestModel("SymbolWithPackage.json");

    //Then
    Assertions.assertFalse(scope.getLocalTypeSymbols().isEmpty());
    Assertions.assertEquals(1, scope.getLocalTypeSymbols().size());
    Assertions.assertEquals("B", scope.getLocalTypeSymbols().get(0).getName());
    Assertions.assertEquals("some.package.B", scope.getLocalTypeSymbols().get(0).getFullName());
  }

  @Test
  public void shouldDeserializeMultipleTypeSymbols() {
    //Given && When
    IMontiArcScope scope = this.loadTestModel("MultipleTypeSymbols.json");

    //Then
    Assertions.assertFalse(scope.getLocalTypeSymbols().isEmpty());
    Assertions.assertEquals(2, scope.getLocalTypeSymbols().size());
    Assertions.assertEquals("A", scope.getLocalTypeSymbols().get(0).getName());
    Assertions.assertEquals("B", scope.getLocalTypeSymbols().get(1).getName());
    Assertions.assertEquals("some.package.A", scope.getLocalTypeSymbols().get(0).getFullName());
    Assertions.assertEquals("some.package.B", scope.getLocalTypeSymbols().get(1).getFullName());
  }

  protected IMontiArcScope loadTestModel(@NotNull String filename) {
    Preconditions.checkNotNull(filename);
    return this.getDeSer().load(Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, filename).toString());
  }
}