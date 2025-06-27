/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._symboltable.IArcBasisArtifactScope;
import arcbasis._symboltable.SymbolService;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.CompKindOfComponentTypeDeSer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeExprOfComponentDeSerTest extends ArcBasisTestBase {

  @Test
  public void testSerializeAsJsonWithPackage() {
    // Given
    ASTArcComponentType ast = ArcBasisMill.arcComponentTypeBuilder()
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
    CompKindOfComponentType compTypeExpr = new CompKindOfComponentType(sym);
    CompKindOfComponentTypeDeSer deser = new CompKindOfComponentTypeDeSer();

    // When
    String compAsJson = deser.serialize(compTypeExpr);

    // Then
    Assertions.assertEquals(
      "{" +
        "\"kind\":\"de.monticore.types.check.CompKindOfComponentType\"," +
        "\"componentTypeName\":\"foo.bar.MyComp\"" +
        "}",
      compAsJson
    );
  }

  @Test
  public void testSerializeAsJsonWithoutPackage() {
    // Given
    ASTArcComponentType ast = ArcBasisMill.arcComponentTypeBuilder()
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
    CompKindOfComponentType compTypeExpr = new CompKindOfComponentType(sym);
    CompKindOfComponentTypeDeSer deser = new CompKindOfComponentTypeDeSer();

    // When
    String compAsJson = deser.serialize(compTypeExpr);

    // Then
    Assertions.assertEquals(
      "{" +
        "\"kind\":\"de.monticore.types.check.CompKindOfComponentType\"," +
        "\"componentTypeName\":\"MyComp\"" +
        "}",
      compAsJson
    );
  }

  @Test
  public void testDeserializeWithPackageName() {
    // Given
    CompKindOfComponentTypeDeSer deser = new CompKindOfComponentTypeDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"de.monticore.types.check.CompKindOfComponentType\"," +
        "\"componentTypeName\":\"foo.bar.MyComp\"" +
        "}"
    );

    // When
    CompKindOfComponentType deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertEquals("foo.bar.MyComp", deserializedExpr.printFullName());
  }

  @Test
  public void testDeserializeWithoutPackageName() {
    // Given
    CompKindOfComponentTypeDeSer deser = new CompKindOfComponentTypeDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"de.monticore.types.check.CompKindOfComponentType\"," +
        "\"componentTypeName\":\"MyComp\"" +
        "}"
    );

    // When
    CompKindOfComponentType deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertEquals("MyComp", deserializedExpr.printFullName());
  }
}
