/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * Holds tests for {@link ComponentTypeSymbolDeSer}.
 */
public class ComponentTypeSymbolDeSerTest extends ArcBasisAbstractTest {

  private static final String SIMPLE_JSON =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"" +
      "}";

  private static final String JSON_WITH_PARENT =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"parents\":[{\"kind\":\"arcbasis.check.TypeExprOfComponent\",\"componentTypeName\":\"Parent\"}]" +
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

  private static final String JSON_WITH_PORTS =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"ports\":[{" +
      "\"kind\":\"arcbasis._symboltable.ArcPortSymbol\"," +
      "\"name\":\"inc\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}," +
      "\"incoming\":true," +
      "\"timing\":\"untimed\"" +
      "},{" +
      "\"kind\":\"arcbasis._symboltable.ArcPortSymbol\"," +
      "\"name\":\"outg\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}," +
      "\"outgoing\":true," +
      "\"timing\":\"untimed\"" +
      "}]" +
      "}";

  private static final String JSON_WITH_SUB =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Parent\"," +
      "\"subcomponents\":[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol\",\"name\":\"inst\",\"type\":{\"kind\":\"arcbasis.check.TypeExprOfComponent\",\"componentTypeName\":\"Comp\"}}]" +
      "}";

  private static final String JSON_WITH_INNER =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"innerComponents\":[{\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\",\"name\":\"inst\"}]" +
      "}";

  private static final String JSON_WITH_FIELD =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fields\":[{\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\",\"name\":\"inst\",\"type\":null}]" +
      "}";

  @Test
  void shouldSerializeParent() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    ComponentTypeSymbol parent = createParentComp();
    CompTypeExpression parentType = new TypeExprOfComponent(parent);
    comp.setSuperComponentsList(Collections.singletonList(parentType));

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
  void shouldSerializePorts() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    ArcPortSymbol portIncoming = ArcBasisMill.arcPortSymbolBuilder()
      .setName("inc")
      .setIncoming(true)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();
    ArcPortSymbol portOutgoing = ArcBasisMill.arcPortSymbolBuilder()
      .setName("outg")
      .setOutgoing(true)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();

    comp.getSpannedScope().add(portIncoming);
    comp.getSpannedScope().add(portOutgoing);

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_PORTS, createdJson);
  }

  @Test
  void shouldDeserializeParent() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_PARENT);

    // Then
    Assertions.assertFalse(comp.isEmptySuperComponents(), "Parent not present");
    Assertions.assertEquals("Parent", comp.getSuperComponents(0).printName());
  }

  @Test
  void shouldNotDeserializeAbsentParent() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(SIMPLE_JSON);

    // Then
    Assertions.assertTrue(comp.isEmptySuperComponents(), "Parent is present");
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
  void shouldDeserializePorts() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_PORTS);

    // Then
    Assertions.assertEquals(2, comp.getArcPorts().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inc", comp.getArcPorts().get(0).getName()),
      () -> Assertions.assertEquals("outg", comp.getArcPorts().get(1).getName())
    );
  }

  @Test
  void shouldSerializeSubComponents() {
    // Given
    ComponentTypeSymbol comp = createParentComp();
    comp.getSpannedScope().add(
      ArcBasisMill.subcomponentSymbolBuilder()
        .setName("inst")
        .setType(new TypeExprOfComponent(createSimpleComp()))
        .build()
    );

    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_SUB, createdJson);
  }

  @Test
  void shouldDeserializeSubComponents() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_SUB);

    // Then
    Assertions.assertEquals(1, comp.getSubcomponents().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inst", comp.getSubcomponents().get(0).getName())
    );
  }

  @Test
  void shouldSerializeInnerComponents() {
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
    Assertions.assertEquals(JSON_WITH_INNER, createdJson);
  }

  @Test
  void shouldDeserializeInnerComponents() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_INNER);

    // Then
    Assertions.assertEquals(1, comp.getInnerComponents().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inst", comp.getInnerComponents().get(0).getName())
    );
  }

  @Test
  void shouldSerializeFields() {
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
    Assertions.assertEquals(JSON_WITH_FIELD, createdJson);
  }

  @Test
  void shouldDeserializeFields() {
    // Given
    ComponentTypeSymbolDeSer deser = new ComponentTypeSymbolDeSer();

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_FIELD);

    // Then
    Assertions.assertEquals(1, comp.getFields().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inst", comp.getFields().get(0).getName())
    );
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
