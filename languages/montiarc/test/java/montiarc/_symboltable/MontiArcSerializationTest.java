/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
    assertThat(json.getMember("name").getAsJsonString().getValue()).isEqualTo(ast.getSpannedScope().getName());
    assertThat(json.getMember("package").getAsJsonString().getValue())
      .isEqualTo(((IMontiArcArtifactScope) ast.getSpannedScope()).getPackageName());
    assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1);
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(ComponentTypeSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo(ast.getComponentType().getName());
  }

  @Test
  public void shouldSerializeComponentWithInPort() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp { " +
        "port in int i;" +
      "}"
    ).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcScope) ast.getComponentType().getSpannedScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1);
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(PortSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo("i");
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("incoming").getAsJsonBoolean().getValue()).isTrue();
  }

  @Test
  public void shouldSerializeComponentWithOutPort() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp { " +
        "port out int o;" +
      "}"
    ).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcScope) ast.getComponentType().getSpannedScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1);
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(PortSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo("o");
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("outgoing").getAsJsonBoolean().getValue()).isTrue();
  }

  @Test
  public void shouldSerializeComponentWithPorts() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp { " +
        "port in int i;" +
        "port out int o;" +
      "}"
    ).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcScope) ast.getComponentType().getSpannedScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(2);
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(PortSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo("i");
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("incoming").getAsJsonBoolean().getValue()).isTrue();
    assertThat(json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(PortSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo("o");
    assertThat(json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject()
      .getMember("outgoing").getAsJsonBoolean().getValue()).isTrue();
  }

}
