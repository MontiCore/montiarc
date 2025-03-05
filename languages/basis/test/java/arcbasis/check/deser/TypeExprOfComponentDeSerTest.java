/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisArtifactScope;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeExprOfComponentDeSerTest extends ArcBasisTestBase {

  @Test
  public void testSerializeAsJsonWithPackage() {
    // Given
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder()
      .setName("MyComp")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol sym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(ast.getName())
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ast.setSymbol(sym);
    ast.setSpannedScope(sym.getSpannedScope());
    sym.setAstNode(ast);

    IArcBasisArtifactScope scope = ArcBasisMill.artifactScope();
    scope.setPackageName("foo.bar");

    SymbolService.link(scope, sym);

    ArcBasisMill.globalScope().addSubScope(scope);
    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(sym);
    TypeExprOfComponentDeSer deser = new TypeExprOfComponentDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(
      "{" +
        "\"kind\":\"arcbasis.check.TypeExprOfComponent\"," +
        "\"componentTypeName\":\"foo.bar.MyComp\"" +
        "}",
      compAsJson
    );
  }

  @Test
  public void testSerializeAsJsonWithoutPackage() {
    // Given
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder()
      .setName("MyComp")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol sym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(ast.getName())
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ast.setSymbol(sym);
    ast.setSpannedScope(sym.getSpannedScope());
    sym.setAstNode(ast);

    SymbolService.link(ArcBasisMill.globalScope(), sym);
    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(sym);
    TypeExprOfComponentDeSer deser = new TypeExprOfComponentDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(
      "{" +
        "\"kind\":\"arcbasis.check.TypeExprOfComponent\"," +
        "\"componentTypeName\":\"MyComp\"" +
        "}",
      compAsJson
    );
  }

  @Test
  public void testDeserializeWithPackageName() {
    // Given
    TypeExprOfComponentDeSer deser = new TypeExprOfComponentDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"arcbasis.check.TypeExprOfComponent\"," +
        "\"componentTypeName\":\"foo.bar.MyComp\"" +
        "}"
    );

    // When
    TypeExprOfComponent deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertEquals("foo.bar.MyComp", deserializedExpr.printFullName());
  }

  @Test
  public void testDeserializeWithoutPackageName() {
    // Given
    TypeExprOfComponentDeSer deser = new TypeExprOfComponentDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"arcbasis.check.TypeExprOfComponent\"," +
        "\"componentTypeName\":\"MyComp\"" +
        "}"
    );

    // When
    TypeExprOfComponent deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertEquals("MyComp", deserializedExpr.printFullName());
  }
}
