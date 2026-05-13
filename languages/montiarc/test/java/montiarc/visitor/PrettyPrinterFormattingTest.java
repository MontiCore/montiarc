/* (c) https://github.com/MontiCore/monticore */
package montiarc.visitor;

import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class PrettyPrinterFormattingTest extends MontiArcTestBase {

  private static final String PACKAGE = "parser";

  @Test
  public void shouldProducePrettyPrinting() throws IOException {
    // Given
    String filePath = Paths.get(TEST_RESOURCE, PACKAGE, "RichFormatting.arc").toString();
    MontiArcParser parser = MontiArcMill.parser();
    ASTMACompilationUnit ast = parser.parse(filePath).orElseThrow();
    String expected;
    try (FileInputStream inputStream = new FileInputStream(filePath)) {
      expected = IOUtils.toString(inputStream, Charset.defaultCharset());
    }

    // When
    String actual = MontiArcMill.prettyPrint(ast, true);

    // Then
    Assertions.assertEquals(expected, actual);
  }
}
