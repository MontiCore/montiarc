/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import genericarc.check.TypeExprOfGenericComponent;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcArtifactScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class MontiArcCompTypeExprDeSerTest extends AbstractTest {

  public static final String GENERIC_COMP_JSON =
    "{" +
      "\"kind\":\"genericarc.check.TypeExprOfGenericComponent\"," +
      "\"componentTypeName\":\"foo.bar.MyComp\"," +
      "\"typeVarBindings\":[" +
      "{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}," +
      "{\"kind\":\"de.monticore.types.check.SymTypeOfObject\",\"objName\":\"noo.boo.Student\"}," +
      "{\"kind\":\"de.monticore.types.check.SymTypeOfObject\",\"objName\":\"noo.boo.Student\"}," +
      "{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}" +
      "]" +
      "}";

  @Test
  void testSerializeSimpleCompAsJson() {
    // Given
    ComponentTypeSymbol myComp = createComponentTypeWithSymbol("MyComp").getSymbol();
    IMontiArcArtifactScope scope = wrapInArtifactScope("foo.bar", myComp);
    MontiArcMill.globalScope().addSubScope(scope);
    CompTypeExpression compTypeExpr = new TypeExprOfComponent(myComp);
    ComposedCompTypeExprDeSer deser = new MontiArcCompTypeExprDeSer();

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
  void testSerializeSimpleCompAsJsonWithoutPackage() {
    // Given
    ComponentTypeSymbol myComp = createComponentTypeWithSymbol("MyComp").getSymbol();
    SymbolService.link(MontiArcMill.globalScope(), myComp);
    CompTypeExpression compTypeExpr = new TypeExprOfComponent(myComp);
    ComposedCompTypeExprDeSer deser = new MontiArcCompTypeExprDeSer();

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
  void testSerializeGenericCompAsJson() {
    // Given
    ComponentTypeSymbol myComp = createComponentTypeWithSymbol("MyComp").getSymbol();
    addTypeParamsToComp(myComp, "A", "B", "Foo", "Bar");

    IMontiArcArtifactScope scope = wrapInArtifactScope("foo.bar", myComp);
    MontiArcMill.globalScope().addSubScope(scope);

    OOTypeSymbol studentSym = createOOTypeSymbol("Student");
    IMontiArcArtifactScope studentScope = wrapInArtifactScope("noo.boo", studentSym);
    MontiArcMill.globalScope().addSubScope(studentScope);

    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(studentSym);
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive("int");

    CompTypeExpression compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    ComposedCompTypeExprDeSer deser = new MontiArcCompTypeExprDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(GENERIC_COMP_JSON, compAsJson);
  }
  @Test
  void testDeserializeSimpleComp() {
    // Given
    ComposedCompTypeExprDeSer deser = new MontiArcCompTypeExprDeSer();
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
  void testDeserializeSimpleCompWithoutPackageName() {
    // Given
    ComposedCompTypeExprDeSer deser = new MontiArcCompTypeExprDeSer();
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

  @Test
  void testDeserializeGenericComp() {
    // Given
    ComposedCompTypeExprDeSer deser = new MontiArcCompTypeExprDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(GENERIC_COMP_JSON);

    OOTypeSymbol studentSym = createOOTypeSymbol("Student");
    IMontiArcArtifactScope studentScope = wrapInArtifactScope("noo.boo", studentSym);
    MontiArcMill.globalScope().addSubScope(studentScope);

    // When
    CompTypeExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, deserializedExpr);
    Assertions.assertEquals(
      "foo.bar.MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }
}
