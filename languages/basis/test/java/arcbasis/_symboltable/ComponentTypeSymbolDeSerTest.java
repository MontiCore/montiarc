/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ComponentTypeSymbolDeSerTest extends AbstractTest {

  private static final String SIMPLE_JSON =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"" +
      "}";

  private static final String JSON_WITH_PARENT =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"parent\":{\"kind\":\"arcbasis.check.TypeExprOfComponent\",\"componentTypeName\":\"Parent\"}" +
      "}";

  private static final String JSON_WITH_TYPE_PARAMS =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"typeParameters\":[{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol\"," +
      "\"name\":\"A\"" +
      "},{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol\"," +
      "\"name\":\"B\"" +
      "}]" +
      "}";

  private static final String JSON_WITH_PARAMS =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"parameters\":[{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\"," +
      "\"name\":\"a\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}" +
      "},{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\"," +
      "\"name\":\"b\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}" +
      "}]" +
      "}";


  @Test
  void shouldSerializeParent() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    ComponentTypeSymbol parent = createParentComp();
    CompTypeExpression parentType = new TypeExprOfComponent(parent);
    comp.setParent(parentType);

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_PARENT, createdJson);
  }

  @Test
  void shouldNotSerializeAbsentParent() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(SIMPLE_JSON, createdJson);
  }

  @Test
  void shouldSerializeTypeParameters() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    comp.getSpannedScope().add(
      ArcBasisMill.typeVarSymbolBuilder()
        .setName("A")
        .setSpannedScope(ArcBasisMill.scope())
        .build()
    );
    comp.getSpannedScope().add(
      ArcBasisMill.typeVarSymbolBuilder()
        .setName("B")
        .setSpannedScope(ArcBasisMill.scope())
        .build()
    );

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_TYPE_PARAMS, createdJson);
  }

  @Test
  void shouldSerializeParameters() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    VariableSymbol paramA = ArcBasisMill.variableSymbolBuilder()
      .setName("a")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();
    VariableSymbol paramB = ArcBasisMill.variableSymbolBuilder()
      .setName("b")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();

    comp.getSpannedScope().add(paramA);
    comp.getSpannedScope().add(paramB);
    comp.addParameter(paramA);
    comp.addParameter(paramB);

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_PARAMS, createdJson);
  }

  @Test
  void shouldDeserializeParent() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_PARENT);

    // Then
    Assertions.assertTrue(comp.isPresentParent(), "Parent not present");
    Assertions.assertEquals("Parent", comp.getParent().printName());
  }

  @Test
  void shouldNotDeserializeAbsentParent() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(SIMPLE_JSON);

    // Then
    Assertions.assertFalse(comp.isPresentParent(), "Parent is present");
  }

  @Test
  void shouldDeserializeTypeParameters() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_TYPE_PARAMS);

    // Then
    Assertions.assertEquals(2, comp.getTypeParameters().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("A", comp.getTypeParameters().get(0).getName()),
      () -> Assertions.assertEquals("B", comp.getTypeParameters().get(1).getName())
    );
  }

  @Test
  void shouldDeserializeParameters() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_PARAMS);

    // Then
    Assertions.assertEquals(2, comp.getParameters().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("a", comp.getParameters().get(0).getName()),
      () -> Assertions.assertEquals("b", comp.getParameters().get(1).getName())
    );
  }

  @Test
  void shouldNotSerializeSubComponents() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    comp.getSpannedScope().add(
      ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("inst")
        .setType(Mockito.mock(CompTypeExpression.class))
        .build()
    );

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(SIMPLE_JSON, createdJson);
  }

  @Test
  void shouldNotSerializeInnerComponents() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    comp.getSpannedScope().add(
      ArcBasisMill.componentTypeSymbolBuilder()
        .setName("inst")
        .setSpannedScope(ArcBasisMill.scope())
        .build()
    );

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(SIMPLE_JSON, createdJson);
  }

  @Test
  void shouldNotSerializeFieldsComponents() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    comp.getSpannedScope().add(
      ArcBasisMill.variableSymbolBuilder()
        .setName("inst")
        .setType(Mockito.mock(SymTypeExpression.class))
        .build()
    );

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(SIMPLE_JSON, createdJson);
  }

  protected static ComponentTypeSymbol createSimpleComp() {
    return ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
  }

  protected static ComponentTypeSymbol createParentComp() {
    return ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
  }
}
