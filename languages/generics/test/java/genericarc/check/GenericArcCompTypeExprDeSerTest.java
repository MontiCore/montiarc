/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.SymbolService;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import genericarc.GenericArcMill;
import genericarc.GenericArcTestBase;
import genericarc._symboltable.IGenericArcArtifactScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static genericarc.check.TypeExprOfGenericComponentDeSerTest.JSON_WITHOUT_PACKAGE;
import static genericarc.check.TypeExprOfGenericComponentDeSerTest.JSON_WITH_PACKAGE;

public class GenericArcCompTypeExprDeSerTest extends GenericArcTestBase {

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

    CompKindExpression compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    FullCompKindExprDeSer deser = new GenericArcCompTypeExprDeSer();

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

    SymbolService.link(GenericArcMill.globalScope(), myComp);

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

    CompKindExpression compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    FullCompKindExprDeSer deser = new GenericArcCompTypeExprDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITHOUT_PACKAGE, compAsJson);
  }

  @Test
  void testDeserializeWithPackageName() {
    // Given
    FullCompKindExprDeSer deser = new GenericArcCompTypeExprDeSer();
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
    CompKindExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, deserializedExpr);
    Assertions.assertEquals(
      "foo.bar.MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }

  @Test
  void testDeserializeWithoutPackageName() {
    // Given
    FullCompKindExprDeSer deser = new GenericArcCompTypeExprDeSer();
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
    CompKindExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, deserializedExpr);
    Assertions.assertEquals(
      "MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }
}
