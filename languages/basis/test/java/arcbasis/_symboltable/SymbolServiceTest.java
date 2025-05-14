/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Holds test for {@link SymbolService}.
 */
public class SymbolServiceTest extends ArcBasisTestBase {

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, VariableSymbol...)}
   */
  @Test
  public void shouldLinkVariablesAndScope() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    VariableSymbol[] variables = new FieldSymbol[]{};

    // When
    SymbolService.link(scope, variables);

    // Then
    Assertions.assertTrue(scope.getLocalVariableSymbols().containsAll(Arrays.asList(variables)),
      "The scope does not contain all expected variables.");
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, VariableSymbol...)}
   */
  @Test
  public void shouldLinkVariablesAndScope2() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    VariableSymbol[] variables = new FieldSymbol[]{
      ArcBasisMill.fieldSymbolBuilder().setName("field1")
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build()
    };

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
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, VariableSymbol...)}
   */
  @Test
  public void shouldLinkVariablesAndScope3() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    VariableSymbol[] variables = new FieldSymbol[]{
      ArcBasisMill.fieldSymbolBuilder().setName("field1")
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build(),
      ArcBasisMill.fieldSymbolBuilder().setName("field2")
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build()
    };

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
  @Test
  public void shouldLinkVariableAndScope() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    VariableSymbol variable = ArcBasisMill.fieldSymbolBuilder()
      .setName("field").setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build();

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
  @Test
  public void shouldLinkFieldsAndScope() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    FieldSymbol[] fields = new FieldSymbol[]{};

    // When
    SymbolService.link(scope, fields);

    // Then
    Assertions.assertTrue(scope.getLocalFieldSymbols().containsAll(Arrays.asList(fields)),
      "The scope does not contain all expected fields.");
  }


  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, FieldSymbol...)}
   */
  @Test
  public void shouldLinkFieldsAndScope2() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    FieldSymbol[] fields = new FieldSymbol[]{
      ArcBasisMill.fieldSymbolBuilder().setName("field1")
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build()
    };

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
   * Method under test {@link SymbolService#link(IOOSymbolsScope, FieldSymbol...)}
   */
  @Test
  public void shouldLinkFieldsAndScope3() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    FieldSymbol[] fields = new FieldSymbol[]{
      ArcBasisMill.fieldSymbolBuilder().setName("field1")
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build(),
      ArcBasisMill.fieldSymbolBuilder().setName("field2")
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build()
    };

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
  @Test
  public void shouldLinkVariableAndScope2() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    FieldSymbol field = ArcBasisMill.fieldSymbolBuilder()
      .setName("field").setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build();

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
  @Test
  public void shouldLinkTypesAndScope() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    TypeSymbol[] types = new OOTypeSymbol[]{};

    // When
    SymbolService.link(scope, types);

    // Then
    Assertions.assertTrue(scope.getLocalTypeSymbols().containsAll(Arrays.asList(types)),
      "The scope does not contain all expected types.");
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, TypeSymbol...)}
   */
  @Test
  public void shouldLinkTypesAndScope2() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    TypeSymbol[] types = new OOTypeSymbol[]{
      ArcBasisMill.oOTypeSymbolBuilder().setName("Type1").build()
    };

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
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, TypeSymbol...)}
   */
  @Test
  public void shouldLinkTypesAndScope3() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    TypeSymbol[] types = new OOTypeSymbol[]{
      ArcBasisMill.oOTypeSymbolBuilder().setName("Type1").build(),
      ArcBasisMill.oOTypeSymbolBuilder().setName("Type2").build()
    };

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
  @Test
  public void shouldLinkTypeAndScope() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    TypeSymbol type = ArcBasisMill.oOTypeSymbolBuilder().setName("Type").build();

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
  @Test
  public void shouldLinkOOTypesAndScope() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    OOTypeSymbol[] ooTypes = new OOTypeSymbol[]{};

    // When
    SymbolService.link(scope, ooTypes);

    // Then
    Assertions.assertTrue(scope.getLocalOOTypeSymbols().containsAll(Arrays.asList(ooTypes)),
      "The scope does not contain all expected oo-types.");
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, OOTypeSymbol...)}
   */
  @Test
  public void shouldLinkOOTypesAndScope2() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    OOTypeSymbol[] ooTypes = new OOTypeSymbol[]{
      ArcBasisMill.oOTypeSymbolBuilder().setName("Type1").build()
    };

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
   * Method under test {@link SymbolService#link(IOOSymbolsScope, OOTypeSymbol...)}
   */
  @Test
  public void shouldLinkOOTypesAndScope3() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    OOTypeSymbol[] ooTypes = new OOTypeSymbol[]{
      ArcBasisMill.oOTypeSymbolBuilder().setName("Type1").build(),
      ArcBasisMill.oOTypeSymbolBuilder().setName("Type2").build()
    };

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
  @Test
  public void shouldLinkOOTypeAndScope() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    OOTypeSymbol ooType = ArcBasisMill.oOTypeSymbolBuilder().setName("Type").build();

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
  @Test
  public void shouldLinkFunctionsAndScope() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    FunctionSymbol[] functions = new MethodSymbol[]{};

    // When
    SymbolService.link(scope, functions);

    // Then
    Assertions.assertTrue(scope.getLocalFunctionSymbols().containsAll(Arrays.asList(functions)),
      "The scope does not contain all expected functions.");
  }

  /**
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, FunctionSymbol...)}
   */
  @Test
  public void shouldLinkFunctionsAndScope2() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    FunctionSymbol[] functions = new MethodSymbol[]{
      ArcBasisMill.methodSymbolBuilder().setName("method1").build()
    };

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
   * Method under test {@link SymbolService#link(IBasicSymbolsScope, FunctionSymbol...)}
   */
  @Test
  public void shouldLinkFunctionsAndScope3() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    FunctionSymbol[] functions = new MethodSymbol[]{
      ArcBasisMill.methodSymbolBuilder().setName("method1").build(),
      ArcBasisMill.methodSymbolBuilder().setName("method2").build()
    };

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
  @Test
  public void shouldLinkFunctionAndScope() {
    // Given
    IBasicSymbolsScope scope = ArcBasisMill.scope();
    FunctionSymbol function = ArcBasisMill.methodSymbolBuilder().setName("method").build();

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
  @Test
  public void shouldLinkMethodsAndScope() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    MethodSymbol[] methods = new MethodSymbol[]{};

    // When
    SymbolService.link(scope, methods);

    // Then
    Assertions.assertTrue(scope.getLocalMethodSymbols().containsAll(Arrays.asList(methods)),
      "The scope does not contain all expected methods.");
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, MethodSymbol...)}
   */
  @Test
  public void shouldLinkMethodsAndScope2() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    MethodSymbol[] methods = new MethodSymbol[]{
      ArcBasisMill.methodSymbolBuilder().setName("method1").build()
    };

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
   * Method under test {@link SymbolService#link(IOOSymbolsScope, MethodSymbol...)}
   */
  @Test
  public void shouldLinkMethodsAndScope3() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    MethodSymbol[] methods = new MethodSymbol[]{
      ArcBasisMill.methodSymbolBuilder().setName("method1").build(),
      ArcBasisMill.methodSymbolBuilder().setName("method2").build()
    };

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
  @Test
  public void shouldLinkMethodAndScope() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    MethodSymbol method = ArcBasisMill.methodSymbolBuilder().setName("method").build();

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
  @Test
  public void shouldLinkTypeVarsAndScope() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    TypeVarSymbol[] typeVariables = new TypeVarSymbol[]{};

    // When
    SymbolService.link(scope, typeVariables);

    // Then
    Assertions.assertTrue(scope.getLocalTypeVarSymbols().containsAll(Arrays.asList(typeVariables)),
      "The scope does not contain all expected type variables.");
  }

  /**
   * Method under test {@link SymbolService#link(IOOSymbolsScope, TypeVarSymbol...)}
   */
  @Test
  public void shouldLinkTypeVarsAndScope2() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    TypeVarSymbol[] typeVariables = new TypeVarSymbol[]{
      ArcBasisMill.typeVarSymbolBuilder().setName("T").build()
    };

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
   * Method under test {@link SymbolService#link(IOOSymbolsScope, TypeVarSymbol...)}
   */
  @Test
  public void shouldLinkTypeVarsAndScope3() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    TypeVarSymbol[] typeVariables = new TypeVarSymbol[]{
      ArcBasisMill.typeVarSymbolBuilder().setName("T").build(),
      ArcBasisMill.typeVarSymbolBuilder().setName("S").build()
    };

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
  @Test
  public void shouldLinkTypeVarAndScope() {
    // Given
    IOOSymbolsScope scope = ArcBasisMill.scope();
    TypeVarSymbol typeVariable = ArcBasisMill.typeVarSymbolBuilder().setName("T").build();

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
  @Test
  public void shouldLinkComponentTypesAndScope() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol[] componentTypes = new ComponentTypeSymbol[]{};

    // When
    SymbolService.link(scope, componentTypes);

    // Then
    Assertions.assertTrue(scope.getLocalComponentTypeSymbols().containsAll(Arrays.asList(componentTypes)),
      "The scope does not contain all expected component types.");
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, ComponentTypeSymbol...)}
   */
  @Test
  public void shouldLinkComponentTypesAndScope2() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol[] componentTypes = new ComponentTypeSymbol[]{
      ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
        .setSpannedScope(ArcBasisMill.scope()).build()
    };

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
   * Method under test {@link SymbolService#link(IArcBasisScope, ComponentTypeSymbol...)}
   */
  @Test
  public void shouldLinkComponentTypesAndScope3() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol[] componentTypes = new ComponentTypeSymbol[]{
      ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
        .setSpannedScope(ArcBasisMill.scope()).build(),
      ArcBasisMill.componentTypeSymbolBuilder().setName("Comp2")
        .setSpannedScope(ArcBasisMill.scope()).build()
    };

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
  @Test
  public void shouldLinkComponentTypeAndScope() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol componentType = ArcBasisMill
      .componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(ArcBasisMill.scope()).build();

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
   * Method under test {@link SymbolService#link(IArcBasisScope, SubcomponentSymbol...)}
   */
  @Test
  public void shouldLinkComponentsAndScope() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    SubcomponentSymbol[] components = new SubcomponentSymbol[]{};

    // When
    SymbolService.link(scope, components);

    // Then
    Assertions.assertTrue(scope.getLocalSubcomponentSymbols().containsAll(Arrays.asList(components)),
      "The scope does not contain all expected components.");
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, SubcomponentSymbol...)}
   */
  @Test
  public void shouldLinkComponentsAndScope2() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    SubcomponentSymbol[] components = new SubcomponentSymbol[]{
      ArcBasisMill.subcomponentSymbolBuilder().setName("component1").build()
    };

    // When
    SymbolService.link(scope, components);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalSubcomponentSymbols().containsAll(Arrays.asList(components)),
        "The scope does not contain all expected components."),
      () -> {
        for (SubcomponentSymbol component : components) {
          Assertions.assertEquals(scope, component.getEnclosingScope(),
            "The component's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, SubcomponentSymbol...)}
   */
  @Test
  public void shouldLinkComponentsAndScope3() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    SubcomponentSymbol[] components = new SubcomponentSymbol[]{
      ArcBasisMill.subcomponentSymbolBuilder().setName("component1").build(),
      ArcBasisMill.subcomponentSymbolBuilder().setName("component2").build()
    };

    // When
    SymbolService.link(scope, components);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalSubcomponentSymbols().containsAll(Arrays.asList(components)),
        "The scope does not contain all expected components."),
      () -> {
        for (SubcomponentSymbol component : components) {
          Assertions.assertEquals(scope, component.getEnclosingScope(),
            "The component's enclosing scope does not match the expected scope.");
        }
      }
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, SubcomponentSymbol)}
   */
  @Test
  public void shouldLinkComponentAndScope() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    SubcomponentSymbol component = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("component").build();

    // When
    SymbolService.link(scope, component);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(scope.getLocalSubcomponentSymbols().contains(component),
        "The scope does not contain the expected component."),
      () -> Assertions.assertEquals(scope, component.getEnclosingScope(),
        "The component's enclosing scope does not match the expected scope.")
    );
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, PortSymbol)}
   */
  @Test
  public void shouldLinkPortAndScope() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    PortSymbol port = ArcBasisMill.portSymbolBuilder().setName("port")
      .setIncoming(true).setType(SymTypeExpressionFactory
        .createPrimitive(BasicSymbolsMill.BOOLEAN)).build();

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

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, PortSymbol...)}
   */
  @Test
  public void shouldLinkPortsAndScope() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    PortSymbol[] ports = new PortSymbol[]{};

    // When
    SymbolService.link(scope, ports);

    // Then
    Assertions.assertTrue(scope.getLocalPortSymbols().containsAll(Arrays.asList(ports)),
      "The scope does not contain all expected ports.");
  }

  /**
   * Method under test {@link SymbolService#link(IArcBasisScope, PortSymbol...)}
   */
  @Test
  public void shouldLinkPortsAndScope2() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    PortSymbol[] ports = new PortSymbol[]{ArcBasisMill
      .portSymbolBuilder().setName("port1").setIncoming(true)
      .setType(SymTypeExpressionFactory
        .createPrimitive(BasicSymbolsMill.BOOLEAN)).build()
    };

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
   * Method under test {@link SymbolService#link(IArcBasisScope, PortSymbol...)}
   */
  @Test
  public void shouldLinkPortsAndScope3() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    PortSymbol[] ports = new PortSymbol[]{
      ArcBasisMill.portSymbolBuilder().setName("port1").setIncoming(true)
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build(),
      ArcBasisMill.portSymbolBuilder().setName("port2").setIncoming(true)
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN)).build()};

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
}
