/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import genericarc.AbstractTest;
import genericarc.GenericArcMill;
import genericarc._symboltable.IGenericArcArtifactScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static genericarc.check.TypeExprOfGenericComponentDeSerTest.JSON_WITHOUT_PACKAGE;
import static genericarc.check.TypeExprOfGenericComponentDeSerTest.JSON_WITH_PACKAGE;

public class GenericArcCompTypeExprDeSerTest extends AbstractTest {

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

    CompTypeExpression compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    ComposedCompTypeExprDeSer deser = new GenericArcCompTypeExprDeSer();

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

    CompTypeExpression compTypeExpr =
      new TypeExprOfGenericComponent(myComp, List.of(intExpr, studentExpr, studentExpr, intExpr));
    ComposedCompTypeExprDeSer deser = new GenericArcCompTypeExprDeSer();

    // When
    String compAsJson = deser.serializeAsJson(compTypeExpr);

    // Then
    Assertions.assertEquals(JSON_WITHOUT_PACKAGE, compAsJson);
  }

  @Test
  void testDeserializeWithPackageName() {
    // Given
    ComposedCompTypeExprDeSer deser = new GenericArcCompTypeExprDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(JSON_WITH_PACKAGE);

    OOTypeSymbol studentSym = createOOTypeSymbol("Student");
    IGenericArcArtifactScope studentScope = wrapInArtifactScope("noo.boo", studentSym);
    GenericArcMill.globalScope().addSubScope(studentScope);

    // When
    CompTypeExpression deserializedExpr = deser.deserialize(serialized);

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
    ComposedCompTypeExprDeSer deser = new GenericArcCompTypeExprDeSer();
    JsonObject serialized = JsonParser.parseJsonObject(JSON_WITHOUT_PACKAGE);

    OOTypeSymbol studentSym = createOOTypeSymbol("Student");
    IGenericArcArtifactScope studentScope = wrapInArtifactScope("noo.boo", studentSym);
    GenericArcMill.globalScope().addSubScope(studentScope);

    // When
    CompTypeExpression deserializedExpr = deser.deserialize(serialized);

    // Then
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, deserializedExpr);
    Assertions.assertEquals(
      "MyComp<int,noo.boo.Student,noo.boo.Student,int>",
      deserializedExpr.printFullName()
    );
  }
}
