/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static montiarc.util.ArcError.RESTRICTED_IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Holds tests for {@link MA2JavaTool}.
 */
class MA2JavaToolTest extends MA2JavaTestBase {

  @Test
  void shouldRejectJavaKeywords(@TempDir Path models, @TempDir Path target) throws IOException {
    // Given
    Files.writeString(models.resolve("Comp.arc"), "component Comp { port in int assert; }");

    MA2JavaTool tool = new MA2JavaTool();

    // When
    tool.run(new String[]{"-i", models.toString(), "-o", target.toString()});

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(RESTRICTED_IDENTIFIER));
  }
}
