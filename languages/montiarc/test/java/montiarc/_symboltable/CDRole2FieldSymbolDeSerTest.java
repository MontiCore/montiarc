/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static montiarc.MontiArcMill.scope;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link CDRole2FieldSymbolDeSer}.
 */
class CDRole2FieldSymbolDeSerTest extends MontiArcTestBase {

  @BeforeEach
  protected void init() {
    super.init();
    JsonElementFactory.setInstance(new JsonElementFactory());
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  @AfterEach
  protected void clearJsonElementFactory() {
    JsonElementFactory.setInstance(null);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "false, false, false, true, true",
    "false, false, true, true, false",
    "false, true, false, true, false",
    "true, false, false, true, true",
    "false, false, false, false, false",
    "false, false, true, false, false",
    "false, true, false, false, false",
    "true, false, false, false, false",
  })
  void shouldDeserializePublic(boolean isPublic,
                               boolean isProtected,
                               boolean isPrivate,
                               boolean isNavigable,
                               boolean isPublicExpected) {
    // Given
    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("isPublic", JsonElementFactory.createJsonBoolean(isPublic));
    members.put("isProtected", JsonElementFactory.createJsonBoolean(isProtected));
    members.put("isPrivate", JsonElementFactory.createJsonBoolean(isPrivate));
    members.put("isDefinitiveNavigable", JsonElementFactory.createJsonBoolean(isNavigable));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    boolean isPublicActual = deSer.deserializeIsPublic(scope(), json);

    // Then
    assertThat(isPublicActual).isEqualTo(isPublicExpected);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "false, false, false, true, false",
    "false, false, true, true, false",
    "false, true, false, true, true",
    "true, false, false, true, false",
    "false, false, false, false, false",
    "false, false, true, false, false",
    "false, true, false, false, false",
    "true, false, false, false, false",
  })
  void shouldDeserializeProtected(boolean isPublic,
                                  boolean isProtected,
                                  boolean isPrivate,
                                  boolean isNavigable,
                                  boolean isProtectedExpected) {
    // Given
    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("isPublic", JsonElementFactory.createJsonBoolean(isPublic));
    members.put("isProtected", JsonElementFactory.createJsonBoolean(isProtected));
    members.put("isPrivate", JsonElementFactory.createJsonBoolean(isPrivate));
    members.put("isDefinitiveNavigable", JsonElementFactory.createJsonBoolean(isNavigable));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    boolean isProtectedActual = deSer.deserializeIsProtected(scope(), json);

    // Then
    assertThat(isProtectedActual).isEqualTo(isProtectedExpected);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "false, false, false, true, false",
    "false, false, true, true, true",
    "false, true, false, true, false",
    "true, false, false, true, false",
    "false, false, false, false, true",
    "false, false, true, false, true",
    "false, true, false, false, true",
    "true, false, false, false, true",
  })
  void shouldDeserializePrivate(boolean isPublic,
                                boolean isProtected,
                                boolean isPrivate,
                                boolean isNavigable,
                                boolean isPrivateExpected) {
    // Given
    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("isPublic", JsonElementFactory.createJsonBoolean(isPublic));
    members.put("isProtected", JsonElementFactory.createJsonBoolean(isProtected));
    members.put("isPrivate", JsonElementFactory.createJsonBoolean(isPrivate));
    members.put("isDefinitiveNavigable", JsonElementFactory.createJsonBoolean(isNavigable));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    boolean isPrivateActual = deSer.deserializeIsPrivate(scope(), json);

    // Then
    assertThat(isPrivateActual).isEqualTo(isPrivateExpected);
  }

  @Test
  void shouldDeserializeTypeDefault() {
    // Given
    JsonObject jsonOfType = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> membersOfType = new HashMap<>();
    membersOfType.put("kind", JsonElementFactory.createJsonString("de.monticore.types.check.SymTypePrimitive"));
    membersOfType.put("primitiveName", JsonElementFactory.createJsonString(BasicSymbolsMill.BOOLEAN));

    jsonOfType.setMembers(membersOfType);

    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("type", jsonOfType);

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    SymTypeExpression actualType = deSer.deserializeType(scope(), json);

    // Then
    assertThat(actualType.isPrimitive()).isTrue();
    assertThat(actualType.asPrimitive().getPrimitiveName()).isEqualTo(BasicSymbolsMill.BOOLEAN);
  }

  @Test
  void shouldDeserializeTypeOne() {
    // Given
    JsonObject jsonOfType = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> membersOfType = new HashMap<>();
    membersOfType.put("kind", JsonElementFactory.createJsonString("de.monticore.types.check.SymTypePrimitive"));
    membersOfType.put("primitiveName", JsonElementFactory.createJsonString(BasicSymbolsMill.BOOLEAN));

    jsonOfType.setMembers(membersOfType);

    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("type", jsonOfType);
    members.put("cardinality", JsonElementFactory.createJsonString("[1]"));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    SymTypeExpression actualType = deSer.deserializeType(scope(), json);

    // Then
    assertThat(actualType.isPrimitive()).isTrue();
    assertThat(actualType.asPrimitive().getPrimitiveName()).isEqualTo(BasicSymbolsMill.BOOLEAN);
  }

  @Test
  void shouldDeserializeTypeZeroToOne() {
    // Given
    JsonObject jsonOfType = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> membersOfType = new HashMap<>();
    membersOfType.put("kind", JsonElementFactory.createJsonString("de.monticore.types.check.SymTypePrimitive"));
    membersOfType.put("primitiveName", JsonElementFactory.createJsonString(BasicSymbolsMill.BOOLEAN));

    jsonOfType.setMembers(membersOfType);

    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("type", jsonOfType);
    members.put("cardinality", JsonElementFactory.createJsonString("[0..1]"));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    SymTypeExpression actualType = deSer.deserializeType(scope(), json);

    // Then
    assertThat(actualType.isGenericType()).isTrue();
    assertThat(actualType.asGenericType().getTypeConstructorFullName()).isEqualTo("java.util.Optional");
  }

  @Test
  void shouldDeserializeTypeZeroToMany() {
    // Given
    JsonObject jsonOfType = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> membersOfType = new HashMap<>();
    membersOfType.put("kind", JsonElementFactory.createJsonString("de.monticore.types.check.SymTypePrimitive"));
    membersOfType.put("primitiveName", JsonElementFactory.createJsonString(BasicSymbolsMill.BOOLEAN));

    jsonOfType.setMembers(membersOfType);

    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("type", jsonOfType);
    members.put("cardinality", JsonElementFactory.createJsonString("[0..*]"));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    SymTypeExpression actualType = deSer.deserializeType(scope(), json);

    // Then
    assertThat(actualType.isGenericType()).isTrue();
    assertThat(actualType.asGenericType().getTypeConstructorFullName()).isEqualTo("java.util.Set");
  }

  @Test
  void shouldDeserializeTypeOneToMany() {
    // Given
    JsonObject jsonOfType = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> membersOfType = new HashMap<>();
    membersOfType.put("kind", JsonElementFactory.createJsonString("de.monticore.types.check.SymTypePrimitive"));
    membersOfType.put("primitiveName", JsonElementFactory.createJsonString(BasicSymbolsMill.BOOLEAN));

    jsonOfType.setMembers(membersOfType);

    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("type", jsonOfType);
    members.put("cardinality", JsonElementFactory.createJsonString("[1..*]"));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    SymTypeExpression actualType = deSer.deserializeType(scope(), json);

    // Then
    assertThat(actualType.isGenericType()).isTrue();
    assertThat(actualType.asGenericType().getTypeConstructorFullName()).isEqualTo("java.util.Set");
  }

  @Test
  void shouldDeserializeTypeZeroToManyOrdered() {
    // Given
    JsonObject jsonOfType = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> membersOfType = new HashMap<>();
    membersOfType.put("kind", JsonElementFactory.createJsonString("de.monticore.types.check.SymTypePrimitive"));
    membersOfType.put("primitiveName", JsonElementFactory.createJsonString(BasicSymbolsMill.BOOLEAN));

    jsonOfType.setMembers(membersOfType);

    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("type", jsonOfType);
    members.put("cardinality", JsonElementFactory.createJsonString("[0..*]"));
    members.put("isOrdered", JsonElementFactory.createJsonBoolean(true));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    SymTypeExpression actualType = deSer.deserializeType(scope(), json);

    // Then
    assertThat(actualType.isGenericType()).isTrue();
    assertThat(actualType.asGenericType().getTypeConstructorFullName()).isEqualTo("java.util.List");
  }

  @Test
  void shouldDeserializeTypeOneToManyOrdered() {
    // Given
    JsonObject jsonOfType = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> membersOfType = new HashMap<>();
    membersOfType.put("kind", JsonElementFactory.createJsonString("de.monticore.types.check.SymTypePrimitive"));
    membersOfType.put("primitiveName", JsonElementFactory.createJsonString(BasicSymbolsMill.BOOLEAN));

    jsonOfType.setMembers(membersOfType);

    JsonObject json = JsonElementFactory.createJsonObject();

    Map<String, JsonElement> members = new HashMap<>();
    members.put("type", jsonOfType);
    members.put("cardinality", JsonElementFactory.createJsonString("[1..*]"));
    members.put("isOrdered", JsonElementFactory.createJsonBoolean(true));

    json.setMembers(members);

    CDRole2FieldSymbolDeSer deSer = new CDRole2FieldSymbolDeSer();

    // When
    SymTypeExpression actualType = deSer.deserializeType(scope(), json);

    // Then
    assertThat(actualType.isGenericType()).isTrue();
    assertThat(actualType.asGenericType().getTypeConstructorFullName()).isEqualTo("java.util.List");
  }
}
