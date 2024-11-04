/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import montiarc.MontiArcMill;
import montiarc.check.MontiArcTypeCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class MA2JSimApiTest {

  @BeforeEach
  public void setUp() {
    LogStub.init();
    MontiArcMill.init();
    MontiArcTypeCheck.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testValid() throws IOException {
    // Given
    ArrayList<String> validModels = new ArrayList<>();

    validModels.add("component A{}");
    validModels.add("component B{A a;}");
    validModels.add("component B{ port in int a;}");
    validModels.add("component B{ port in int a; compute{}}");

    APIContext context = new APIContext(validModels);

    // When
    MA2JSimApi tool = new MA2JSimApi(context);
    tool.runGeneration();

    // Then
    assertThat(!tool.getGenerated().isEmpty()).isTrue();
  }

  @Test
  public void testInvalid() throws IOException {
    // Given
    ArrayList<String> validModels = new ArrayList<>();

    validModels.add("component {}");
    validModels.add("component B{A a;}");
    validModels.add("component B{ port in a;}");
    validModels.add("component B{ port in int a; compute}");

    APIContext context = new APIContext(validModels);

    // When
    MA2JSimApi tool = new MA2JSimApi(context);

    // Then
    assertThat(!tool.runGeneration().isEmpty()).isTrue();
    assertThat(tool.getGenerated().isEmpty()).isTrue();
  }
}