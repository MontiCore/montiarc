/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._symboltable.IArcBasisArtifactScope;
import arcbasis._symboltable.SymbolService;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.CompKindOfGenericComponentTypeDeSer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TypeExprOfGenericComponentDeSerTest extends ArcBasisTestBase {

  public static final String JSON_WITH_PACKAGE =
    "{" +
      "\"kind\":\"de.monticore.types.check.CompKindOfGenericComponentType\"," +
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
    "\"kind\":\"de.monticore.types.check.CompKindOfGenericComponentType\"," +
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
    ASTArcComponentType myCompAST = ArcBasisMill.arcComponentTypeBuilder()
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

    myCompAST.getSpannedScope().add(ArcBasisMill
      .typeVarSymbolBuilder()
      .setName("A")
      .build());
    myCompAST.getSpannedScope().add(ArcBasisMill
      .typeVarSymbolBuilder()
      .setName("B")
      .build());
    myCompAST.getSpannedScope().add(ArcBasisMill
      .typeVarSymbolBuilder()
      .setName("Foo")
      .build());
    myCompAST.getSpannedScope().add(ArcBasisMill
      .typeVarSymbolBuilder()
      .setName("Bar")
      .build());

    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    as.setPackageName("foo.bar");

    SymbolService.link(as, myComp);

    ArcBasisMill.globalScope().addSubScope(as);

    OOTypeSymbol student = ArcBasisMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    IArcBasisArtifactScope as2 = ArcBasisMill.artifactScope();
    as2.setPackageName("noo.boo");

    SymbolService.link(as2, student);

    ArcBasisMill.globalScope().addSubScope(as2);

    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(student);
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive("int");

    CompKindOfGenericComponentType compTypeExpr =
      new CompKindOfGenericComponentType(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    CompKindOfGenericComponentTypeDeSer deser = new CompKindOfGenericComponentTypeDeSer();

    // When
    String compAsJson = deser.serialize(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITH_PACKAGE, compAsJson);
  }

  @Test
  void testSerializeAsJsonWithoutPackage() {
    // Given
    ASTArcComponentType myCompAST = ArcBasisMill.arcComponentTypeBuilder()
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

    myCompAST.getSpannedScope().add(ArcBasisMill
      .typeVarSymbolBuilder()
      .setName("A")
      .build());
    myCompAST.getSpannedScope().add(ArcBasisMill
      .typeVarSymbolBuilder()
      .setName("B")
      .build());
    myCompAST.getSpannedScope().add(ArcBasisMill
      .typeVarSymbolBuilder()
      .setName("Foo")
      .build());
    myCompAST.getSpannedScope().add(ArcBasisMill
      .typeVarSymbolBuilder()
      .setName("Bar")
      .build());

    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();

    SymbolService.link(as, myComp);

    ArcBasisMill.globalScope().addSubScope(as);

    OOTypeSymbol student = ArcBasisMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    IArcBasisArtifactScope as2 = ArcBasisMill.artifactScope();
    as2.setPackageName("noo.boo");

    SymbolService.link(as2, student);

    ArcBasisMill.globalScope().addSubScope(as2);

    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(student);
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive("int");

    CompKindOfGenericComponentType compTypeExpr =
      new CompKindOfGenericComponentType(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    CompKindOfGenericComponentTypeDeSer deser = new CompKindOfGenericComponentTypeDeSer();

    // When
    String compAsJson = deser.serialize(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITHOUT_PACKAGE, compAsJson);
  }

  @Test
  void testDeserializeWithPackageName() {
    // Given
    CompKindOfGenericComponentTypeDeSer deser = new CompKindOfGenericComponentTypeDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(JSON_WITH_PACKAGE);

    OOTypeSymbol student = ArcBasisMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    as.setPackageName("noo.boo");

    SymbolService.link(as, student);

    ArcBasisMill.globalScope().addSubScope(as);

    // When
    CompKindOfGenericComponentType deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertEquals(
      "foo.bar.MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }

  @Test
  void testDeserializeWithoutPackageName() {
    // Given
    CompKindOfGenericComponentTypeDeSer deser = new CompKindOfGenericComponentTypeDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(JSON_WITHOUT_PACKAGE);

    OOTypeSymbol student = ArcBasisMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    as.setPackageName("noo.boo");

    SymbolService.link(as, student);

    ArcBasisMill.globalScope().addSubScope(as);

    // When
    CompKindOfGenericComponentType deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertEquals(
      "MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }
}
