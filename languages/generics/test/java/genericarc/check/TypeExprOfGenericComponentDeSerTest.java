/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.SymbolService;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import genericarc.AbstractTest;
import genericarc.GenericArcMill;
import genericarc._symboltable.IGenericArcArtifactScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class TypeExprOfGenericComponentDeSerTest extends AbstractTest {

  public static final String JSON_WITH_PACKAGE =
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

  public static final String JSON_WITHOUT_PACKAGE =
    "{" +
    "\"kind\":\"genericarc.check.TypeExprOfGenericComponent\"," +
    "\"componentTypeName\":\"MyComp\"," +
    "\"typeVarBindings\":[" +
      "{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}," +
      "{\"kind\":\"de.monticore.types.check.SymTypeOfObject\",\"objName\":\"noo.boo.Student\"}," +
      "{\"kind\":\"de.monticore.types.check.SymTypeOfObject\",\"objName\":\"noo.boo.Student\"}," +
      "{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}" +
    "]" +
    "}";
  @Test
  void testSerializeAsJsonWithPackage() {
    // Given
    ComponentTypeSymbol myComp = createComponentTypeWithSymbol("MyComp").getSymbol();
    addTypeParamsToComp(myComp, "A", "B", "Foo", "Bar");

    IGenericArcArtifactScope scope = wrapInArtifactScope("foo.bar", myComp);
    GenericArcMill.globalScope().addSubScope(scope);

    OOTypeSymbol studentSym = createOOTypeSymbol("Student");
    IGenericArcArtifactScope studentScope = wrapInArtifactScope("noo.boo", studentSym);
    GenericArcMill.globalScope().addSubScope(studentScope);

    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(studentSym);
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive("int");

    TypeExprOfGenericComponent compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    TypeExprOfGenericComponentDeSer deser = new TypeExprOfGenericComponentDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITH_PACKAGE, compAsJson);
  }

  @Test
  void testSerializeAsJsonWithoutPackage() {
    // Given
    ComponentTypeSymbol myComp = createComponentTypeWithSymbol("MyComp").getSymbol();
    addTypeParamsToComp(myComp, "A", "B", "Foo", "Bar");
    SymbolService.link(GenericArcMill.globalScope(), myComp);

    OOTypeSymbol studentSym = createOOTypeSymbol("Student");
    IGenericArcArtifactScope studentScope = wrapInArtifactScope("noo.boo", studentSym);
    GenericArcMill.globalScope().addSubScope(studentScope);

    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(studentSym);
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive("int");

    TypeExprOfGenericComponent compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    TypeExprOfGenericComponentDeSer deser = new TypeExprOfGenericComponentDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITHOUT_PACKAGE, compAsJson);
  }

  @Disabled("'Cause while constructing TypeExprOfGenericComps, missing TypeVarSymbols lead to missing TypeVarBindings.")
  @Test
  void testDeserializeWithPackageName() {
    // Given
    TypeExprOfGenericComponentDeSer deser = new TypeExprOfGenericComponentDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(JSON_WITH_PACKAGE);

    OOTypeSymbol studentSym = createOOTypeSymbol("Student");
    IGenericArcArtifactScope studentScope = wrapInArtifactScope("noo.boo", studentSym);
    GenericArcMill.globalScope().addSubScope(studentScope);

    // When
    // TODO: Problem is that the constructor of TypeExprOfGenericComponent only fills generic type arg bindings up to
    //    the number of TypeVars the type has. Therefore, the type has to be deserialized first. Is this handled by
    //    our desers? I hope so. But I guess resolving that type will trigger it's deserialization so that should be fine.
    TypeExprOfGenericComponent deserializedExpr = deser.deserialize(serialized);

    // Then
    Map<String, SymTypeExpression> typeVarBindings = getTypeVarBindingsByName(deserializedExpr.getTypeVarBindings());

    Assertions.assertAll(
      () -> Assertions.assertEquals("foo.bar.MyComp", deserializedExpr.printFullName()),
      () -> Assertions.assertEquals("int", typeVarBindings.get("A").printFullName()),
      () -> Assertions.assertEquals("noo.boo.Student", typeVarBindings.get("B").printFullName()),
      () -> Assertions.assertEquals("noo.boo.Student", typeVarBindings.get("Foo").printFullName()),
      () -> Assertions.assertEquals("int", typeVarBindings.get("Bar").printFullName())
    );
  }

  @Disabled("'Cause while constructing TypeExprOfGenericComps, missing TypeVarSymbols lead to missing TypeVarBindings.")
  @Test
  void testDeserializeWithoutPackageName() {
    // Given
    TypeExprOfGenericComponentDeSer deser = new TypeExprOfGenericComponentDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(JSON_WITHOUT_PACKAGE);

    OOTypeSymbol studentSym = createOOTypeSymbol("Student");
    IGenericArcArtifactScope studentScope = wrapInArtifactScope("noo.boo", studentSym);
    GenericArcMill.globalScope().addSubScope(studentScope);

    // When
    TypeExprOfGenericComponent deserializedExpr = deser.deserialize(serialized);

    // Then
    Map<String, SymTypeExpression> typeVarBindings = getTypeVarBindingsByName(deserializedExpr.getTypeVarBindings());

    Assertions.assertAll(
      () -> Assertions.assertEquals("MyComp", deserializedExpr.printFullName()),
      () -> Assertions.assertEquals("int", typeVarBindings.get("A").printFullName()),
      () -> Assertions.assertEquals("noo.boo.Student", typeVarBindings.get("B").printFullName()),
      () -> Assertions.assertEquals("noo.boo.Student", typeVarBindings.get("Foo").printFullName()),
      () -> Assertions.assertEquals("int", typeVarBindings.get("Bar").printFullName())
    );
  }
}
