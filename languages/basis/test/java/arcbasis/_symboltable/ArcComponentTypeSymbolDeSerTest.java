/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * Holds tests for {@link ArcComponentTypeSymbolDeSer}.
 */
class ArcComponentTypeSymbolDeSerTest extends ArcBasisTestBase {

  private static final String SIMPLE_JSON =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"" +
      "}";

  private static final String JSON_WITH_PARENT =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"super\":[{\"kind\":\"arcbasis.check.TypeExprOfComponent\",\"componentTypeName\":\"Parent\"}]" +
      "}";

  private static final String JSON_WITH_REFINEMENT =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"refinements\":[{\"kind\":\"arcbasis.check.TypeExprOfComponent\",\"componentTypeName\":\"Parent\"}]" +
      "}";

  private static final String JSON_WITH_TYPE_PARAMS =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"spannedScope\":{\"symbols\":[{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol\"," +
      "\"name\":\"A\"," +
      "\"fullName\":\"Comp.A\"" +
      "},{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol\"," +
      "\"name\":\"B\"," +
      "\"fullName\":\"Comp.B\"" +
      "}]}" +
      "}";

  private static final String JSON_WITH_PARAMS =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"parameters\":[{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\"," +
      "\"name\":\"a\"," +
      "\"fullName\":\"Comp.a\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}" +
      "},{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\"," +
      "\"name\":\"b\"," +
      "\"fullName\":\"Comp.b\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}" +
      "}]," +
      "\"numOptParams\":1" +
      "}";

  private static final String JSON_WITH_PORTS =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"spannedScope\":{\"symbols\":[{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.PortSymbol\"," +
      "\"name\":\"inc\"," +
      "\"fullName\":\"Comp.inc\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}," +
      "\"incoming\":true," +
      "\"timing\":\"timed\"" +
      "},{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.PortSymbol\"," +
      "\"name\":\"outg\"," +
      "\"fullName\":\"Comp.outg\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}," +
      "\"outgoing\":true," +
      "\"timing\":\"timed\"" +
      "}]}" +
      "}";

  private static final String JSON_WITH_SUB =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Parent\"," +
      "\"fullName\":\"Parent\"," +
      "\"spannedScope\":{\"symbols\":[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol\",\"name\":\"inst\",\"fullName\":\"Parent.inst\",\"type\":{\"kind\":\"arcbasis.check.TypeExprOfComponent\",\"componentTypeName\":\"Comp\"}}]}" +
      "}";

  private static final String JSON_WITH_INNER =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"spannedScope\":{\"symbols\":[{\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\",\"name\":\"inst\",\"fullName\":\"Comp.inst\"}]}" +
      "}";

  private static final String JSON_WITH_FIELD =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ArcComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"spannedScope\":{\"symbols\":[{\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\",\"name\":\"inst\",\"fullName\":\"Comp.inst\",\"type\":null}]}" +
      "}";

  @Test
  void shouldSerializeParent() {
    // Given
    ArcComponentTypeSymbol comp = createSimpleComp();
    ArcComponentTypeSymbol parent = createParentComp();
    CompTypeExpression parentType = new TypeExprOfComponent(parent);
    comp.setSuperComponentsList(Collections.singletonList(parentType));

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_PARENT, createdJson);
  }

  @Test
  void shouldSerializeSpec() {
    // Given
    ArcComponentTypeSymbol comp = createSimpleComp();
    ArcComponentTypeSymbol parent = createParentComp();
    CompTypeExpression parentType = new TypeExprOfComponent(parent);
    comp.setRefinementsList(Collections.singletonList(parentType));

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_REFINEMENT, createdJson);
  }

  @Test
  void shouldNotSerializeAbsentParent() {
    // Given
    ArcComponentTypeSymbol comp = createSimpleComp();

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(SIMPLE_JSON, createdJson);
  }

  @Test
  void shouldSerializeTypeParameters() {
    // Given
    ArcComponentTypeSymbol comp = createSimpleComp();
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

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_TYPE_PARAMS, createdJson);
  }

  @Test
  void shouldSerializeParameters() {
    // Given
    ArcComponentTypeSymbol comp = createSimpleComp();
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
    comp.setNumOptParams(1);

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_PARAMS, createdJson);
  }

  @Test
  void shouldSerializePorts() {
    // Given
    ArcComponentTypeSymbol comp = createSimpleComp();
    PortSymbol portIncoming = ArcBasisMill.portSymbolBuilder()
      .setName("inc")
      .setIncoming(true)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .setTiming(Timing.TIMED)
      .setStronglyCausal(false)
      .build();
    PortSymbol portOutgoing = ArcBasisMill.portSymbolBuilder()
      .setName("outg")
      .setOutgoing(true)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .setTiming(Timing.TIMED)
      .setStronglyCausal(false)
      .build();

    comp.getSpannedScope().add(portIncoming);
    comp.getSpannedScope().add(portOutgoing);

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_PORTS, createdJson);
  }

  @Test
  void shouldDeserializeParent() {
    // Given
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(ArcBasisMill.globalScope(), JSON_WITH_PARENT);

    // Then
    Assertions.assertFalse(comp.isEmptySuperComponents(), "Parent not present");
    Assertions.assertEquals("Parent", comp.getSuperComponents(0).printName());
  }

  @Test
  void shouldDeserializeSpec() {
    // Given
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(ArcBasisMill.globalScope(), JSON_WITH_REFINEMENT);

    // Then
    Assertions.assertFalse(comp.isEmptyRefinements(), "Refined component not present");
    Assertions.assertEquals("Parent", comp.getRefinements(0).printName());
  }

  @Test
  void shouldNotDeserializeAbsentParent() {
    // Given
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(ArcBasisMill.globalScope(), SIMPLE_JSON);

    // Then
    Assertions.assertTrue(comp.isEmptySuperComponents(), "Parent is present");
  }

  @Test
  void shouldDeserializeTypeParameters() {
    // Given
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(ArcBasisMill.globalScope(), JSON_WITH_TYPE_PARAMS);

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
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(ArcBasisMill.globalScope(), JSON_WITH_PARAMS);

    // Then
    Assertions.assertEquals(2, comp.getParameterList().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("a", comp.getParameterList().get(0).getName()),
      () -> Assertions.assertEquals("b", comp.getParameterList().get(1).getName()),
      () -> Assertions.assertEquals(1, comp.getNumOptParams())
    );
  }

  @Test
  void shouldDeserializePorts() {
    // Given
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(ArcBasisMill.globalScope(), JSON_WITH_PORTS);

    // Then
    Assertions.assertEquals(2, comp.getPorts().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inc", comp.getPorts().get(0).getName()),
      () -> Assertions.assertEquals("outg", comp.getPorts().get(1).getName())
    );
  }

  @Test
  void shouldSerializeSubComponents() {
    // Given
    ArcComponentTypeSymbol comp = createParentComp();
    comp.getSpannedScope().add(
      ArcBasisMill.subcomponentSymbolBuilder()
        .setName("inst")
        .setType(new TypeExprOfComponent(createSimpleComp()))
        .build()
    );

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_SUB, createdJson);
  }

  @Test
  void shouldDeserializeSubComponents() {
    // Given
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(JSON_WITH_SUB);

    // Then
    Assertions.assertEquals(1, comp.getSubcomponents().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inst", comp.getSubcomponents().get(0).getName())
    );
  }

  @Test
  void shouldSerializeInnerComponents() {
    // Given
    ArcComponentTypeSymbol comp = createSimpleComp();
    comp.getSpannedScope().add(
      ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName("inst")
        .setSpannedScope(ArcBasisMill.scope())
        .build()
    );

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_INNER, createdJson);
  }

  @Test
  void shouldDeserializeInnerComponents() {
    // Given
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(JSON_WITH_INNER);

    // Then
    Assertions.assertEquals(1, comp.getInnerComponents().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inst", comp.getInnerComponents().get(0).getName())
    );
  }

  @Test
  void shouldSerializeFields() {
    // Given
    ArcComponentTypeSymbol comp = createSimpleComp();
    comp.getSpannedScope().add(
      ArcBasisMill.variableSymbolBuilder()
        .setName("inst")
        .setType(Mockito.mock(SymTypeExpression.class))
        .build()
    );

    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();
    ArcBasisSymbols2Json arc2json = new ArcBasisSymbols2Json();

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_FIELD, createdJson);
  }

  @Test
  void shouldDeserializeFields() {
    // Given
    ArcComponentTypeSymbolDeSer deser = new ArcComponentTypeSymbolDeSer();

    // When
    ArcComponentTypeSymbol comp = deser.deserialize(ArcBasisMill.globalScope(), JSON_WITH_FIELD);

    // Then
    Assertions.assertEquals(1, comp.getFields().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inst", comp.getFields().get(0).getName())
    );
  }

  protected static ArcComponentTypeSymbol createSimpleComp() {
    return ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
  }

  protected static ArcComponentTypeSymbol createParentComp() {
    return ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
  }
}
