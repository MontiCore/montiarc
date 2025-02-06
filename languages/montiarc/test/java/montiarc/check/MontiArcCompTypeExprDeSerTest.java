/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisArtifactScope;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import arcbasis.check.TypeExprOfGenericComponent;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MontiArcCompTypeExprDeSerTest extends MontiArcTestBase {

  public static final String GENERIC_COMP_JSON =
    "{" +
      "\"kind\":\"arcbasis.check.TypeExprOfGenericComponent\"," +
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
    ASTComponentType cTypeAST = ArcBasisMill.componentTypeBuilder()
      .setName("MyComp")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol myComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(cTypeAST.getName())
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    cTypeAST.setSymbol(myComp);
    cTypeAST.setSpannedScope(myComp.getSpannedScope());
    myComp.setAstNode(cTypeAST);

    IArcBasisArtifactScope scope = ArcBasisMill.artifactScope();
    scope.setPackageName("foo.bar");

    SymbolService.link(scope, myComp);
    
    MontiArcMill.globalScope().addSubScope(scope);
    CompTypeExpression compTypeExpr = new TypeExprOfComponent(myComp);
    FullCompKindExprDeSer deser = new MontiArcCompTypeExprDeSer();

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
    ASTComponentType cTypeAST = ArcBasisMill.componentTypeBuilder()
      .setName("MyComp")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol myComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(cTypeAST.getName())
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    cTypeAST.setSymbol(myComp);
    cTypeAST.setSpannedScope(myComp.getSpannedScope());
    myComp.setAstNode(cTypeAST);
    
    SymbolService.link(MontiArcMill.globalScope(), myComp);
    CompTypeExpression compTypeExpr = new TypeExprOfComponent(myComp);
    FullCompKindExprDeSer deser = new MontiArcCompTypeExprDeSer();

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
    ASTComponentType myCompAST = ArcBasisMill.componentTypeBuilder()
      .setName("MyComp")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol myComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(myCompAST.getName())
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    myCompAST.setSymbol(myComp);
    myCompAST.setSpannedScope(myComp.getSpannedScope());
    myComp.setAstNode(myCompAST);

    myCompAST.getSpannedScope().add(MontiArcMill
      .typeVarSymbolBuilder()
      .setName("A")
      .build());
    myCompAST.getSpannedScope().add(MontiArcMill
      .typeVarSymbolBuilder()
      .setName("B")
      .build());
    myCompAST.getSpannedScope().add(MontiArcMill
      .typeVarSymbolBuilder()
      .setName("Foo")
      .build());
    myCompAST.getSpannedScope().add(MontiArcMill
      .typeVarSymbolBuilder()
      .setName("Bar")
      .build());

    IArcBasisArtifactScope as = MontiArcMill.artifactScope();
    as.setPackageName("foo.bar");

    SymbolService.link(as, myComp);

    MontiArcMill.globalScope().addSubScope(as);

    OOTypeSymbol student = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(MontiArcMill.scope())
      .build();

    IArcBasisArtifactScope as2 = MontiArcMill.artifactScope();
    as2.setPackageName("noo.boo");

    SymbolService.link(as2, student);

    MontiArcMill.globalScope().addSubScope(as2);

    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(student);
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive("int");
    CompTypeExpression compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    FullCompKindExprDeSer deser = new MontiArcCompTypeExprDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(GENERIC_COMP_JSON, compAsJson);
  }
  @Test
  void testDeserializeSimpleComp() {
    // Given
    FullCompKindExprDeSer deser = new MontiArcCompTypeExprDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"arcbasis.check.TypeExprOfComponent\"," +
        "\"componentTypeName\":\"foo.bar.MyComp\"" +
        "}"
    );

    // When
    CompKindExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertEquals("foo.bar.MyComp", deserializedExpr.printFullName());
    Assertions.assertInstanceOf(TypeExprOfComponent.class, deserializedExpr);
  }

  @Test
  void testDeserializeSimpleCompWithoutPackageName() {
    // Given
    FullCompKindExprDeSer deser = new MontiArcCompTypeExprDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"arcbasis.check.TypeExprOfComponent\"," +
        "\"componentTypeName\":\"MyComp\"" +
        "}"
    );

    // When
    CompKindExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertEquals("MyComp", deserializedExpr.printFullName());
    Assertions.assertInstanceOf(TypeExprOfComponent.class, deserializedExpr);
  }

  @Test
  void testDeserializeGenericComp() {
    // Given
    FullCompKindExprDeSer deser = new MontiArcCompTypeExprDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(GENERIC_COMP_JSON);

    OOTypeSymbol student = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(MontiArcMill.scope())
      .build();

    IArcBasisArtifactScope as = MontiArcMill.artifactScope();
    as.setPackageName("noo.boo");

    SymbolService.link(as, student);

    MontiArcMill.globalScope().addSubScope(as);
    
    // When
    CompKindExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, deserializedExpr);
    Assertions.assertEquals(
      "foo.bar.MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }
}
