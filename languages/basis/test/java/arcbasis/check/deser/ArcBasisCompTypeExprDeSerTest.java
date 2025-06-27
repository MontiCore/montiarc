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
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindExpressionDeSer;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static arcbasis.check.deser.TypeExprOfGenericComponentDeSerTest.JSON_WITHOUT_PACKAGE;
import static arcbasis.check.deser.TypeExprOfGenericComponentDeSerTest.JSON_WITH_PACKAGE;

public class ArcBasisCompTypeExprDeSerTest extends ArcBasisTestBase {

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
    CompKindExpression compTypeExpr = new CompKindOfComponentType(sym);
    CompKindExpressionDeSer deser = new CompKindExpressionDeSer();

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
    CompKindExpression compTypeExpr = new CompKindOfComponentType(sym);
    CompKindExpressionDeSer deser = new CompKindExpressionDeSer();

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
    CompKindExpressionDeSer deser = new CompKindExpressionDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"de.monticore.types.check.CompKindOfComponentType\"," +
        "\"componentTypeName\":\"foo.bar.MyComp\"" +
        "}"
    );

    // When
    CompKindExpression deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertEquals("foo.bar.MyComp", deserializedExpr.printFullName());
    Assertions.assertInstanceOf(CompKindOfComponentType.class, deserializedExpr);
  }

  @Test
  public void testDeserializeWithoutPackageName() {
    // Given
    CompKindExpressionDeSer deser = new CompKindExpressionDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(
      "{" +
        "\"kind\":\"de.monticore.types.check.CompKindOfComponentType\"," +
        "\"componentTypeName\":\"MyComp\"" +
        "}"
    );

    // When
    CompKindExpression deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertEquals("MyComp", deserializedExpr.printFullName());
    Assertions.assertInstanceOf(CompKindOfComponentType.class, deserializedExpr);
  }

  @Test
  void testGenericSerializeAsJsonWithPackage() {
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

    CompKindExpression compTypeExpr =
      new CompKindOfGenericComponentType(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    CompKindExpressionDeSer deser = new CompKindExpressionDeSer();

    // When
    String compAsJson = deser.serialize(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITH_PACKAGE, compAsJson);
  }

  @Test
  void testGenericSerializeAsJsonWithoutPackage() {
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

    SymbolService.link(ArcBasisMill.globalScope(), myComp);

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

    CompKindExpression compTypeExpr =
      new CompKindOfGenericComponentType(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    CompKindExpressionDeSer deser = new CompKindExpressionDeSer();

    // When
    String compAsJson = deser.serialize(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITHOUT_PACKAGE, compAsJson);
  }

  @Test
  void testGenericDeserializeWithPackageName() {
    // Given
    CompKindExpressionDeSer deser = new CompKindExpressionDeSer();
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
    CompKindExpression deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, deserializedExpr);
    Assertions.assertEquals(
      "foo.bar.MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }

  @Test
  void testGenericDeserializeWithoutPackageName() {
    // Given
    CompKindExpressionDeSer deser = new CompKindExpressionDeSer();
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
    CompKindExpression deserializedExpr = deser.deserialize(ArcBasisMill.globalScope(), serialized);

    // Then
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, deserializedExpr);
    Assertions.assertEquals(
      "MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }
}
