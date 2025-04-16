/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypePrimitive;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MontiArcSerializationTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    MontiArcMill.globalScope().add(setUpParentComp());
  }

  protected ComponentTypeSymbol setUpParentComp() {
    ComponentTypeSymbol parentComp = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(MontiArcMill.scope())
      .build();

    parentComp.getSpannedScope().add(
      MontiArcMill.arcPortSymbolBuilder()
        .setIncoming(true)
        .setName("i")
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
        .build()
    );

    parentComp.getSpannedScope().add(
      MontiArcMill.arcPortSymbolBuilder()
        .setOutgoing(true)
        .setName("o")
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
        .build()
    );

    return parentComp;
  }

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

  @Test
  public void shouldSerializeComponentType() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp {" +
        "}").orElseThrow();

    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertAll(
      () -> assertThat(json.getMember("name").getAsJsonString().getValue()).isEqualTo(ast.getEnclosingScope().getName()),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
        .getMember("kind").getAsJsonString().getValue())
        .isEqualTo(ComponentTypeSymbol.class.getCanonicalName())
    );
  }

  @Test
  public void shouldSerializeComponentTypeWithField() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp {" +
        "int i = 0;" +
        "}").orElseThrow();

    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertAll(
      () -> assertThat(json.getMember("name").getAsJsonString().getValue()).isEqualTo(ast.getEnclosingScope().getName()),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
        .getMember("kind").getAsJsonString().getValue()).isEqualTo(ComponentTypeSymbol.class.getCanonicalName())
    );
  }

  @Test
  public void shouldSerializeComponentTypeWithRefinement() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp refines Parent {" +
        "  port in int i;" +
        "  port out int o;" +
        "}").orElseThrow();

    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertAll(
      () -> assertThat(json.hasStringMember("name"))
            .as("name is present as string").isTrue(),
      () -> assertThat(json.hasArrayMember("symbols"))
            .as("symbols is present as array").isTrue()
    );
    assertThat(json.getArrayMember("symbols")).as("symbols").hasSize(1);
    assertThat(json.getArrayMember("symbols").get(0).isJsonObject()).isTrue();

    JsonObject compSym = json.getArrayMember("symbols").get(0).getAsJsonObject();
    assertThat(compSym.hasArrayMember("refinements"))
      .as("refinements is present as array").isTrue();

    final List<JsonElement> refinements = compSym.getArrayMember("refinements");
    assertAll(
      () -> assertThat(json.getStringMember("name"))
            .isEqualTo(ast.getEnclosingScope().getName()),
      () -> assertThat(refinements).hasSize(1),
      () -> assertThat(refinements).allMatch(JsonElement::isJsonObject, "is JsonObject")
    );

    final JsonObject refinement = refinements.get(0).getAsJsonObject();
    assertThat(refinement.hasStringMember("kind"))
      .as("has 'kind' member of type String")
      .isTrue();
    assertThat(refinement.getStringMember("kind"))
      .as("kind")
      .isEqualTo("arcbasis.check.TypeExprOfComponent");
    assertThat(refinement.hasStringMember("componentTypeName"))
      .as("has 'componentTypeName' member of type String")
      .isTrue();
    assertThat(refinement.getStringMember("componentTypeName"))
      .as("componentTypeName")
      .isEqualTo("Parent");
  }

  @Test
  public void shouldSerializeComponentTypeWithInnerComponent() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp {" +
        "component Inner { }" +
        "}").orElseThrow();

    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertAll(
      () -> assertThat(json.getMember("name").getAsJsonString().getValue()).isEqualTo(ast.getEnclosingScope().getName()),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(1),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
        .getMember("spannedScope").getAsJsonObject().getMember("symbols").getAsJsonArray().get(0).getAsJsonObject().getMember("kind").getAsJsonString().getValue())
        .isEqualTo(ComponentTypeSymbol.class.getCanonicalName()),
      () -> assertThat(json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject()
        .getMember("spannedScope").getAsJsonObject().getMember("symbols").getAsJsonArray().get(0).getAsJsonObject().getMember("name").getAsJsonString().getValue())
        .isEqualTo(ast.getComponentType().getInnerComponents().get(0).getName())
    );
  }

  @Test
  public void shouldSerializeComponentWithGenerics() throws IOException {
    // Given
    final ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(
      "package a.b;" +
        "component Comp<A, B extends String> { }"
    ).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // When
    final String s = new MontiArcSymbols2Json().serialize((IMontiArcScope) ast.getComponentType().getSpannedScope());

    // Then
    final JsonObject json = JsonParser.parseJsonObject(s);
    assertThat(json.getMember("symbols").getAsJsonArray().size()).isEqualTo(2);
    final JsonObject jsonTypeVar1 = json.getMember("symbols").getAsJsonArray().get(0).getAsJsonObject();
    assertThat(jsonTypeVar1.getMember("kind").getAsJsonString().getValue()).isEqualTo(TypeVarSymbol.class.getCanonicalName());
    assertThat(jsonTypeVar1.getMember("name").getAsJsonString().getValue()).isEqualTo("A");
    final JsonObject jsonTypeVar2 = json.getMember("symbols").getAsJsonArray().get(1).getAsJsonObject();
    assertThat(jsonTypeVar2.getMember("kind").getAsJsonString().getValue()).isEqualTo(TypeVarSymbol.class.getCanonicalName());
    assertThat(jsonTypeVar2.getMember("name").getAsJsonString().getValue()).isEqualTo("B");
    assertThat(jsonTypeVar2.getMember("superTypes").getAsJsonArray().size()).isEqualTo(1);
    assertThat(jsonTypeVar2.getMember("superTypes").getAsJsonArray().get(0).getAsJsonObject().getMember("kind").getAsJsonString().getValue()).isEqualTo(SymTypeOfObject.class.getCanonicalName());
    assertThat(jsonTypeVar2.getMember("superTypes").getAsJsonArray().get(0).getAsJsonObject().getMember("objName").getAsJsonString().getValue()).isEqualTo("String");
  }
}
