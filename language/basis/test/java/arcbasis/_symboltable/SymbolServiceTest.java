/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Holds test for {@link SymbolService}.
 */
class SymbolServiceTest extends AbstractTest {

  @BeforeAll
  public static void initClass() {
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    addBasicTypes2Scope();
  }

  protected static Stream<Arguments> scopeAndFieldSymbolsProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), new FieldSymbol[]{}),
      Arguments.of(ArcBasisMill.scope(), new FieldSymbol[]{
        ArcBasisMill.fieldSymbolBuilder().setName("field1")
          .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN)).build(),
      }),
      Arguments.of(ArcBasisMill.scope(), new FieldSymbol[]{
        ArcBasisMill.fieldSymbolBuilder().setName("field1")
          .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN)).build(),
        ArcBasisMill.fieldSymbolBuilder().setName("field2")
          .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN)).build()
      })
    );
  }

  protected static Stream<Arguments> scopeAndFieldSymbolProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), ArcBasisMill.fieldSymbolBuilder()
        .setName("field").setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN)).build())
    );
  }

  protected static Stream<Arguments> scopeAndOOTypeSymbolsProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), new OOTypeSymbol[]{}),
      Arguments.of(ArcBasisMill.scope(), new OOTypeSymbol[]{
        ArcBasisMill.oOTypeSymbolBuilder().setName("Type1").build(),
      }),
      Arguments.of(ArcBasisMill.scope(), new OOTypeSymbol[]{
        ArcBasisMill.oOTypeSymbolBuilder().setName("Type1").build(),
        ArcBasisMill.oOTypeSymbolBuilder().setName("Type2").build(),
      })
    );
  }

  protected static Stream<Arguments> scopeAndOOTypeSymbolProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), ArcBasisMill.oOTypeSymbolBuilder().setName("Type").build())
    );
  }

  protected static Stream<Arguments> scopeAndMethodSymbolsProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), new MethodSymbol[]{}),
      Arguments.of(ArcBasisMill.scope(), new MethodSymbol[]{
        ArcBasisMill.methodSymbolBuilder().setName("method1").build()
      }),
      Arguments.of(ArcBasisMill.scope(), new MethodSymbol[]{
        ArcBasisMill.methodSymbolBuilder().setName("method1").build(),
        ArcBasisMill.methodSymbolBuilder().setName("method2").build()
      })
    );
  }

  protected static Stream<Arguments> scopeAndMethodSymbolProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), ArcBasisMill.methodSymbolBuilder()
        .setName("method").build())
    );
  }

  protected static Stream<Arguments> scopeAndTypeVarSymbolsProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), new TypeVarSymbol[]{}),
      Arguments.of(ArcBasisMill.scope(), new TypeVarSymbol[]{
        ArcBasisMill.typeVarSymbolBuilder().setName("T").build(),
      }),
      Arguments.of(ArcBasisMill.scope(), new TypeVarSymbol[]{
        ArcBasisMill.typeVarSymbolBuilder().setName("T").build(),
        ArcBasisMill.typeVarSymbolBuilder().setName("S").build()
      })
    );
  }

  protected static Stream<Arguments> scopeAndTypeVarSymbolProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), ArcBasisMill.typeVarSymbolBuilder().setName("T").build())
    );
  }

  protected static Stream<Arguments> scopeAndComponentTypeSymbolsProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), new ComponentTypeSymbol[]{}),
      Arguments.of(ArcBasisMill.scope(), new ComponentTypeSymbol[]{
        ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
          .setSpannedScope(ArcBasisMill.scope()).build()
      }),
      Arguments.of(ArcBasisMill.scope(), new ComponentTypeSymbol[]{
        ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
          .setSpannedScope(ArcBasisMill.scope()).build(),
        ArcBasisMill.componentTypeSymbolBuilder().setName("Comp2")
          .setSpannedScope(ArcBasisMill.scope()).build()
      })
    );
  }

  protected static Stream<Arguments> scopeAndComponentTypeSymbolProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
        .setSpannedScope(ArcBasisMill.scope()).build())
    );
  }

  protected static Stream<Arguments> scopeAndComponentInstanceSymbolsProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), new ComponentInstanceSymbol[]{}),
      Arguments.of(ArcBasisMill.scope(), new ComponentInstanceSymbol[]{
        ArcBasisMill.componentInstanceSymbolBuilder().setName("component1").build()
      }),
      Arguments.of(ArcBasisMill.scope(),new ComponentInstanceSymbol[]{
        ArcBasisMill.componentInstanceSymbolBuilder().setName("component1").build(),
        ArcBasisMill.componentInstanceSymbolBuilder().setName("component2").build()
      })
    );
  }

  protected static Stream<Arguments> scopeAndComponentInstanceSymbolProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), ArcBasisMill.componentInstanceSymbolBuilder().setName("component").build())
    );
  }

  protected static Stream<Arguments> scopeAndPortSymbolsProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), new PortSymbol[]{}),
      Arguments.of(ArcBasisMill.scope(), new PortSymbol[]{
        ArcBasisMill.portSymbolBuilder().setName("port1").setIncoming(true)
          .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN)).build()
      }),
      Arguments.of(ArcBasisMill.scope(), new PortSymbol[]{
        ArcBasisMill.portSymbolBuilder().setName("port1").setIncoming(true)
          .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN)).build(),
        ArcBasisMill.portSymbolBuilder().setName("port2").setIncoming(true)
          .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN)).build()
      })
    );
  }

  protected static Stream<Arguments> scopeAndPortSymbolProvider() {
    return Stream.of(
      Arguments.of(ArcBasisMill.scope(), ArcBasisMill.portSymbolBuilder().setName("port")
        .setIncoming(true).setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN)).build())
    );
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, VariableSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndFieldSymbolsProvider")
  public void shouldLink(@NotNull IBasicSymbolsScope scope, @NotNull VariableSymbol... variables) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(variables);

    // When
    SymbolService.link(scope, variables);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalVariableSymbols().containsAll(Arrays.asList(variables)),
        "The scope does not contain all expected variables."),
      () -> {
        for (VariableSymbol variable : variables) {
          Assertions.assertEquals(scope, variable.getEnclosingScope(),
            "The variable's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, VariableSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndFieldSymbolProvider")
  public void shouldLink(@NotNull IBasicSymbolsScope scope, @NotNull VariableSymbol variable) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(variable);

    // When
    SymbolService.link(scope, variable);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalVariableSymbols().contains(variable),
        "The scope does not contain the expected variable."),
      () -> Assertions.assertEquals(scope, variable.getEnclosingScope(),
        "The variable's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, FieldSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndFieldSymbolsProvider")
  public void shouldLink(@NotNull IOOSymbolsScope scope, @NotNull FieldSymbol... fields) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(fields);

    // When
    SymbolService.link(scope, fields);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalFieldSymbols().containsAll(Arrays.asList(fields)),
        "The scope does not contain all expected fields."),
      () -> {
        for (FieldSymbol field : fields) {
          Assertions.assertEquals(scope, field.getEnclosingScope(),
            "The field's enclosing scope do not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, FieldSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndFieldSymbolProvider")
  public void shouldLink(@NotNull IOOSymbolsScope scope, @NotNull FieldSymbol field) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(field);

    // When
    SymbolService.link(scope, field);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalFieldSymbols().contains(field),
        "The scope does not contain the expected field."),
      () -> Assertions.assertEquals(scope, field.getEnclosingScope(),
        "The field's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, TypeSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndOOTypeSymbolsProvider")
  public void shouldLink(@NotNull IBasicSymbolsScope scope, @NotNull TypeSymbol... types) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(types);

    // When
    SymbolService.link(scope, types);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalTypeSymbols().containsAll(Arrays.asList(types)),
        "The scope does not contain all expected types."),
      () -> {
        for (TypeSymbol type : types) {
          Assertions.assertEquals(scope, type.getEnclosingScope(),
            "The type's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, TypeSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndOOTypeSymbolProvider")
  public void shouldLink(@NotNull IBasicSymbolsScope scope, @NotNull TypeSymbol type) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(type);

    // When
    SymbolService.link(scope, type);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalTypeSymbols().contains(type),
        "The scope does not contain the expected type."),
      () -> Assertions.assertEquals(scope, type.getEnclosingScope(),
        "The type's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, OOTypeSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndOOTypeSymbolsProvider")
  public void shouldLink(@NotNull IOOSymbolsScope scope, @NotNull OOTypeSymbol... ooTypes) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(ooTypes);

    // When
    SymbolService.link(scope, ooTypes);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalOOTypeSymbols().containsAll(Arrays.asList(ooTypes)),
        "The scope does not contain all expected oo-types."),
      () -> {
        for (OOTypeSymbol ooType : ooTypes) {
          Assertions.assertEquals(scope, ooType.getEnclosingScope(),
            "The oo-type's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, OOTypeSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndOOTypeSymbolProvider")
  public void shouldLink(@NotNull IOOSymbolsScope scope, @NotNull OOTypeSymbol ooType) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(ooType);

    // When
    SymbolService.link(scope, ooType);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalOOTypeSymbols().contains(ooType),
        "The scope does not contain the expected oo-type."),
      () -> Assertions.assertEquals(scope, ooType.getEnclosingScope(),
        "The oo-type's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, FunctionSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndMethodSymbolsProvider")
  public void shouldLink(@NotNull IBasicSymbolsScope scope, @NotNull FunctionSymbol... functions) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(functions);

    // When
    SymbolService.link(scope, functions);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalFunctionSymbols().containsAll(Arrays.asList(functions)),
        "The scope does not contain all expected functions."),
      () -> {
        for (FunctionSymbol function : functions) {
          Assertions.assertEquals(scope, function.getEnclosingScope(),
            "The function's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, FunctionSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndMethodSymbolProvider")
  public void shouldLink(@NotNull IBasicSymbolsScope scope, @NotNull FunctionSymbol function) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(function);

    // When
    SymbolService.link(scope, function);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalFunctionSymbols().contains(function),
        "The scope does not contain the expected function."),
      () -> Assertions.assertEquals(scope, function.getEnclosingScope(),
        "The function's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, MethodSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndMethodSymbolsProvider")
  public void shouldLink(@NotNull IOOSymbolsScope scope, @NotNull MethodSymbol... methods) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(methods);

    // When
    SymbolService.link(scope, methods);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalMethodSymbols().containsAll(Arrays.asList(methods)),
        "The scope does not contain all expected methods."),
      () -> {
        for (MethodSymbol method : methods) {
          Assertions.assertEquals(scope, method.getEnclosingScope(),
            "The method's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, MethodSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndMethodSymbolProvider")
  public void shouldLink(@NotNull IOOSymbolsScope scope, @NotNull MethodSymbol method) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(method);

    // When
    SymbolService.link(scope, method);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalMethodSymbols().contains(method),
        "The scope does not contain the expected method."),
      () -> Assertions.assertEquals(scope, method.getEnclosingScope(),
        "The method's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, TypeVarSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndTypeVarSymbolsProvider")
  public void shouldLink(@NotNull IOOSymbolsScope scope, @NotNull TypeVarSymbol... typeVariables) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(typeVariables);

    // When
    SymbolService.link(scope, typeVariables);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalTypeVarSymbols().containsAll(Arrays.asList(typeVariables)),
        "The scope does not contain all expected type variables."),
      () -> {
        for (TypeVarSymbol typeVariable : typeVariables) {
          Assertions.assertEquals(scope, typeVariable.getEnclosingScope(),
            "The type variable's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, TypeVarSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndTypeVarSymbolProvider")
  public void shouldLink(@NotNull IOOSymbolsScope scope, @NotNull TypeVarSymbol typeVariable) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(typeVariable);

    // When
    SymbolService.link(scope, typeVariable);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalTypeVarSymbols().contains(typeVariable),
        "The scope does not contain the expected type variable."),
      () -> Assertions.assertEquals(scope, typeVariable.getEnclosingScope(),
        "The type variable's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, ComponentTypeSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndComponentTypeSymbolsProvider")
  public void shouldLink(@NotNull IArcBasisScope scope, @NotNull ComponentTypeSymbol... componentTypes) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(componentTypes);

    // When
    SymbolService.link(scope, componentTypes);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalComponentTypeSymbols().containsAll(Arrays.asList(componentTypes)),
        "The scope does not contain all expected component types."),
      () -> {
        for (ComponentTypeSymbol componentType : componentTypes) {
          Assertions.assertEquals(scope, componentType.getEnclosingScope(),
            "The component type's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, ComponentTypeSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndComponentTypeSymbolProvider")
  public void shouldLink(@NotNull IArcBasisScope scope, @NotNull ComponentTypeSymbol componentType) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(componentType);

    // When
    SymbolService.link(scope, componentType);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalComponentTypeSymbols().contains(componentType),
        "The scope does not contain the expected component type."),
      () -> Assertions.assertEquals(scope, componentType.getEnclosingScope(),
        "The component type's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, ComponentInstanceSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndComponentInstanceSymbolsProvider")
  public void shouldLink(@NotNull IArcBasisScope scope, @NotNull ComponentInstanceSymbol... components) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(components);

    // When
    SymbolService.link(scope, components);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalComponentInstanceSymbols().containsAll(Arrays.asList(components)),
        "The scope does not contain all expected components."),
      () -> {
        for (ComponentInstanceSymbol component : components) {
          Assertions.assertEquals(scope, component.getEnclosingScope(),
            "The component's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, ComponentInstanceSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndComponentInstanceSymbolProvider")
  public void shouldLink(@NotNull IArcBasisScope scope, @NotNull ComponentInstanceSymbol component) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(component);

    // When
    SymbolService.link(scope, component);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalComponentInstanceSymbols().contains(component),
        "The scope does not contain the expected component."),
      () -> Assertions.assertEquals(scope, component.getEnclosingScope(),
        "The component's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, PortSymbol...)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndPortSymbolsProvider")
  public void shouldLink(@NotNull IArcBasisScope scope, @NotNull PortSymbol... ports) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(ports);

    // When
    SymbolService.link(scope, ports);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalPortSymbols().containsAll(Arrays.asList(ports)),
        "The scope does not contain all expected ports."),
      () -> {
        for (PortSymbol port : ports) {
          Assertions.assertEquals(scope, port.getEnclosingScope(),
            "The port's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, PortSymbol)}
   */
  @ParameterizedTest
  @MethodSource("scopeAndPortSymbolProvider")
  public void shouldLink(@NotNull IArcBasisScope scope, @NotNull PortSymbol port) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(port);

    // When
    SymbolService.link(scope, port);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalPortSymbols().contains(port),
        "The scope does not contain the expected port."),
      () -> Assertions.assertEquals(scope, port.getEnclosingScope(),
        "The port's enclosing scope does not match the expected scope.")
    );
  }
}
