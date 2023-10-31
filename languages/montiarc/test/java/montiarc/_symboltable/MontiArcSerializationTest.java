/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypePrimitive;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class MontiArcSerializationTest extends MontiArcAbstractTest {

  @Test
  public void shouldSerializeArtifactScope() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp { }"

    ).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertThat(json.getMember("name").getAsJsonString().getValue()).isEqualTo(ast.getEnclosingScope().getName());
    assertThat(json.getMember("package").getAsJsonString().getValue())
      .isEqualTo(((IMontiArcArtifactScope) ast.getEnclosingScope()).getPackageName());
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
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcScope) ast.getComponentType().getSpannedScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1);
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(ArcPortSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo("i");
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("incoming").getAsJsonBoolean().getValue()).isTrue();
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject().getMember("type").getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(SymTypePrimitive.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject().getMember("type").getAsJsonObject()
      .getMember("primitiveName").getAsJsonString().getValue()).isEqualTo("int");
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
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcScope) ast.getComponentType().getSpannedScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1);
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(ArcPortSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo("o");
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("outgoing").getAsJsonBoolean().getValue()).isTrue();
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject().getMember("type").getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(SymTypePrimitive.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject().getMember("type").getAsJsonObject()
      .getMember("primitiveName").getAsJsonString().getValue()).isEqualTo("int");
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
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcScope) ast.getComponentType().getSpannedScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(2);
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(ArcPortSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo("i");
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
      .getMember("incoming").getAsJsonBoolean().getValue()).isTrue();
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject().getMember("type").getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(SymTypePrimitive.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject().getMember("type").getAsJsonObject()
      .getMember("primitiveName").getAsJsonString().getValue()).isEqualTo("int");
    assertThat(json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(ArcPortSymbol.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject()
      .getMember("name").getAsJsonString().getValue()).isEqualTo("o");
    assertThat(json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject()
      .getMember("outgoing").getAsJsonBoolean().getValue()).isTrue();
    assertThat(json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject().getMember("type").getAsJsonObject()
      .getMember("kind").getAsJsonString().getValue()).isEqualTo(SymTypePrimitive.class.getCanonicalName());
    assertThat(json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject().getMember("type").getAsJsonObject()
      .getMember("primitiveName").getAsJsonString().getValue()).isEqualTo("int");
  }

}
