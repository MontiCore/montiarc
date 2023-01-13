package montiarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

public class MontiArcSerializationTest extends AbstractTest {

  @Test
  public void shouldSerializeArtifactScope() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
      "component Comp { }"
    ).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getSpannedScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertAll(
      () -> assertThat(json.getMember("name").getAsJsonString().getValue()).isEqualTo(ast.getSpannedScope().getName()),
      () -> assertThat(json.getMember("package").getAsJsonString().getValue())
        .isEqualTo(((IMontiArcArtifactScope) ast.getSpannedScope()).getPackageName()),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
        .getMember("name").getAsJsonString().getValue()).isEqualTo(ast.getComponentType().getName()),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
        .getMember("kind").getAsJsonString().getValue()).isEqualTo(ComponentTypeSymbol.class.getCanonicalName())
    );
  }

}
