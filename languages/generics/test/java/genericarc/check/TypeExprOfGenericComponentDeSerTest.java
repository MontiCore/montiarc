/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.SymbolService;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import genericarc.GenericArcMill;
import genericarc.GenericArcTestBase;
import genericarc._symboltable.IGenericArcArtifactScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TypeExprOfGenericComponentDeSerTest extends GenericArcTestBase {

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

    myCompAST.getSpannedScope().add(GenericArcMill
      .typeVarSymbolBuilder()
      .setName("A")
      .build());
    myCompAST.getSpannedScope().add(GenericArcMill
      .typeVarSymbolBuilder()
      .setName("B")
      .build());
    myCompAST.getSpannedScope().add(GenericArcMill
      .typeVarSymbolBuilder()
      .setName("Foo")
      .build());
    myCompAST.getSpannedScope().add(GenericArcMill
      .typeVarSymbolBuilder()
      .setName("Bar")
      .build());

    IGenericArcArtifactScope as = GenericArcMill.artifactScope();
    as.setPackageName("foo.bar");

    SymbolService.link(as, myComp);

    GenericArcMill.globalScope().addSubScope(as);

    OOTypeSymbol student = GenericArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(GenericArcMill.scope())
      .build();

    IGenericArcArtifactScope as2 = GenericArcMill.artifactScope();
    as2.setPackageName("noo.boo");

    SymbolService.link(as2, student);

    GenericArcMill.globalScope().addSubScope(as2);

    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(student);
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

    myCompAST.getSpannedScope().add(GenericArcMill
      .typeVarSymbolBuilder()
      .setName("A")
      .build());
    myCompAST.getSpannedScope().add(GenericArcMill
      .typeVarSymbolBuilder()
      .setName("B")
      .build());
    myCompAST.getSpannedScope().add(GenericArcMill
      .typeVarSymbolBuilder()
      .setName("Foo")
      .build());
    myCompAST.getSpannedScope().add(GenericArcMill
      .typeVarSymbolBuilder()
      .setName("Bar")
      .build());

    IGenericArcArtifactScope as = GenericArcMill.artifactScope();

    SymbolService.link(as, myComp);

    GenericArcMill.globalScope().addSubScope(as);

    OOTypeSymbol student = GenericArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(GenericArcMill.scope())
      .build();

    IGenericArcArtifactScope as2 = GenericArcMill.artifactScope();
    as2.setPackageName("noo.boo");

    SymbolService.link(as2, student);

    GenericArcMill.globalScope().addSubScope(as2);

    SymTypeExpression studentExpr = SymTypeExpressionFactory.createTypeObject(student);
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive("int");

    TypeExprOfGenericComponent compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    TypeExprOfGenericComponentDeSer deser = new TypeExprOfGenericComponentDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITHOUT_PACKAGE, compAsJson);
  }

  @Test
  void testDeserializeWithPackageName() {
    // Given
    TypeExprOfGenericComponentDeSer deser = new TypeExprOfGenericComponentDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(JSON_WITH_PACKAGE);

    OOTypeSymbol student = GenericArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(GenericArcMill.scope())
      .build();

    IGenericArcArtifactScope as = GenericArcMill.artifactScope();
    as.setPackageName("noo.boo");

    SymbolService.link(as, student);

    GenericArcMill.globalScope().addSubScope(as);

    // When
    TypeExprOfGenericComponent deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertEquals(
      "foo.bar.MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }

  @Test
  void testDeserializeWithoutPackageName() {
    // Given
    TypeExprOfGenericComponentDeSer deser = new TypeExprOfGenericComponentDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(JSON_WITHOUT_PACKAGE);

    OOTypeSymbol student = GenericArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(GenericArcMill.scope())
      .build();

    IGenericArcArtifactScope as = GenericArcMill.artifactScope();
    as.setPackageName("noo.boo");

    SymbolService.link(as, student);

    GenericArcMill.globalScope().addSubScope(as);

    // When
    TypeExprOfGenericComponent deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertEquals(
      "MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }
}
