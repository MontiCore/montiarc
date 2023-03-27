/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisArtifactScope;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArcBasisCompTypeExprDeSerTest extends ArcBasisAbstractTest {
  @Test
  public void testSerializeAsJsonWithPackage() {
    // Given
    ComponentTypeSymbol myComp = createComponentTypeWithSymbol("MyComp").getSymbol();
    IArcBasisArtifactScope scope = wrapInArtifactScope("foo.bar", myComp);
    ArcBasisMill.globalScope().addSubScope(scope);
    CompTypeExpression compTypeExpr = new TypeExprOfComponent(myComp);
    ComposedCompTypeExprDeSer deser = new ArcBasisCompTypeExprDeSer();

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
    ComponentTypeSymbol myComp = createComponentTypeWithSymbol("MyComp").getSymbol();
    SymbolService.link(ArcBasisMill.globalScope(), myComp);
    CompTypeExpression compTypeExpr = new TypeExprOfComponent(myComp);
    ComposedCompTypeExprDeSer deser = new ArcBasisCompTypeExprDeSer();

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
    ComposedCompTypeExprDeSer deser = new ArcBasisCompTypeExprDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"arcbasis.check.TypeExprOfComponent\"," +
        "\"componentTypeName\":\"foo.bar.MyComp\"" +
        "}"
    );

    // When
    CompTypeExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertEquals("foo.bar.MyComp", deserializedExpr.printFullName());
    Assertions.assertInstanceOf(TypeExprOfComponent.class, deserializedExpr);
  }

  @Test
  public void testDeserializeWithoutPackageName() {
    // Given
    ComposedCompTypeExprDeSer deser = new ArcBasisCompTypeExprDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"arcbasis.check.TypeExprOfComponent\"," +
        "\"componentTypeName\":\"MyComp\"" +
        "}"
    );

    // When
    CompTypeExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertEquals("MyComp", deserializedExpr.printFullName());
    Assertions.assertInstanceOf(TypeExprOfComponent.class, deserializedExpr);
  }
}
