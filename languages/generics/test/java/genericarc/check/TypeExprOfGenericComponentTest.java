/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis._symboltable.PortSymbol;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import genericarc.GenericArcAbstractTest;
import genericarc.GenericArcMill;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TypeExprOfGenericComponentTest extends GenericArcAbstractTest {

  @BeforeEach
  @Override
  public void init() {
    GenericArcMill.globalScope().clear();
    GenericArcMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void shouldGetParentComponent() {
    // Given
    ComponentTypeSymbol parent = createComponentWithTypeVar("Parent", "S");
    ComponentTypeSymbol component = GenericArcMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(GenericArcMill.scope())
      .build();

    // Creating a typeExpr representing Parent<int> that is then set to be the parent of comp
    CompTypeExpression parentTypeExpr = new TypeExprOfGenericComponent(parent,
      Lists.newArrayList(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT)));
    component.setParent(parentTypeExpr);

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When && Then
    Assertions.assertTrue(compTypeExpr.getParentTypeExpr().isPresent());
    Assertions.assertEquals(parentTypeExpr, compTypeExpr.getParentTypeExpr().get());
  }

  @Test
  public void shouldGetParentWithTypeVarPrimitive() {
    // Given
    ComponentTypeSymbol parent =  createComponentWithTypeVar("Parent", "S");
    ComponentTypeSymbol child = createComponentWithTypeVar("Child", "T");

    SymTypeVariable typeVar = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(0));
    child.setParent(new TypeExprOfGenericComponent(parent, Lists.newArrayList(typeVar)));

    SymTypeExpression typeArg = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    CompTypeExpression bChild = new TypeExprOfGenericComponent(child, Lists.newArrayList(typeArg));

    // When
    TypeExprOfGenericComponent bParent = ((TypeExprOfGenericComponent) bChild.getParentTypeExpr().orElseThrow());

    // Then
    Assertions.assertSame(parent, bParent.getTypeInfo());
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, bParent);
    Assertions.assertTrue(bParent.getBindingFor(parent.getTypeParameters().get(0)).isPresent());
    Assertions.assertEquals(typeArg, bParent.getBindingFor(parent.getTypeParameters().get(0)).get());
  }

  @Test
  public void shouldGetParentWithTypeVarObject() {
    // Given
    ComponentTypeSymbol parent =  createComponentWithTypeVar("Parent", "S");
    ComponentTypeSymbol child = createComponentWithTypeVar("Child", "T");

    SymTypeVariable typeVar = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(0));
    child.setParent(new TypeExprOfGenericComponent(parent, Lists.newArrayList(typeVar)));

    SymTypeExpression typeArg = SymTypeExpressionFactory.createTypeObject(GenericArcAbstractTest.createTypeSymbol("First"));
    CompTypeExpression bChild = new TypeExprOfGenericComponent(child, Lists.newArrayList(typeArg));

    // When
    TypeExprOfGenericComponent bParent = ((TypeExprOfGenericComponent) bChild.getParentTypeExpr().orElseThrow());

    // Then
    Assertions.assertSame(parent, bParent.getTypeInfo());
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, bParent);
    Assertions.assertTrue(bParent.getBindingFor(parent.getTypeParameters().get(0)).isPresent());
    Assertions.assertEquals(typeArg, bParent.getBindingFor(parent.getTypeParameters().get(0)).get());
  }

  @Test
  public void shouldGetParentWithTypeVarObjects() {
    // Given
    ComponentTypeSymbol parent =  createComponentWithTypeVar("Parent", "S", "T");
    ComponentTypeSymbol child = createComponentWithTypeVar("Child", "U", "V");

    SymTypeVariable typeVar1 = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(0));
    SymTypeVariable typeVar2 = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(1));
    child.setParent(new TypeExprOfGenericComponent(parent, Lists.newArrayList(typeVar1, typeVar2)));

    SymTypeExpression typeArg1 = SymTypeExpressionFactory.createTypeObject(GenericArcAbstractTest.createTypeSymbol("First"));
    SymTypeExpression typeArg2 = SymTypeExpressionFactory.createTypeObject(GenericArcAbstractTest.createTypeSymbol("Second"));
    CompTypeExpression bChild = new TypeExprOfGenericComponent(child, Lists.newArrayList(typeArg1, typeArg2));

    // When
    TypeExprOfGenericComponent bParent = ((TypeExprOfGenericComponent) bChild.getParentTypeExpr().orElseThrow());

    // Then
    Assertions.assertSame(parent, bParent.getTypeInfo());
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, bParent);
    Assertions.assertTrue(bParent.getBindingFor(parent.getTypeParameters().get(0)).isPresent());
    Assertions.assertEquals(typeArg1, bParent.getBindingFor(parent.getTypeParameters().get(0)).get());
    Assertions.assertTrue(bParent.getBindingFor(parent.getTypeParameters().get(1)).isPresent());
    Assertions.assertEquals(typeArg2, bParent.getBindingFor(parent.getTypeParameters().get(1)).get());
  }

  @Test
  public void shouldGetParentWithTypeVar() {
    // Given
    ComponentTypeSymbol parent =  createComponentWithTypeVar("Parent", "S");
    ComponentTypeSymbol child = createComponentWithTypeVar("Child", "T");

    SymTypeVariable typeVar = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(0));
    child.setParent(new TypeExprOfGenericComponent(parent, Lists.newArrayList(typeVar)));

    TypeVarSymbol symbol = GenericArcMill.typeVarSymbolBuilder().setName("A").build();
    SymTypeExpression typeArg = SymTypeExpressionFactory.createTypeVariable(symbol);
    CompTypeExpression bChild = new TypeExprOfGenericComponent(child, Lists.newArrayList(typeArg));

    // When
    TypeExprOfGenericComponent bParent = ((TypeExprOfGenericComponent) bChild.getParentTypeExpr().orElseThrow());

    // Then
    Assertions.assertSame(parent, bParent.getTypeInfo());
    Assertions.assertInstanceOf(TypeExprOfGenericComponent.class, bParent);
    Assertions.assertTrue(bParent.getBindingFor(parent.getTypeParameters().get(0)).isPresent());
    Assertions.assertEquals(typeArg, bParent.getBindingFor(parent.getTypeParameters().get(0)).get());
  }

  protected static Stream<Arguments> compWithTypeParamAndOptionallySurrogateProvider() {
    Named<ComponentTypeSymbol> original = Named.of(
      "CompSymbol",
      createComponentWithTypeVar("Comp", "T")
    );
    Named<ComponentTypeSymbol> surrogate = Named.of(
      "CompSurrogate",
      createSurrogateInGlobalScopeFor(original.getPayload())
    );

    return Stream.of(
      Arguments.of(original, original),
      Arguments.of(original, surrogate)
    );
  }

  /**
   * @param symbolWithDefinitions Provide a component type symbol in which ports will be added in this test.
   * @param symbolVersionForTypeExpr Set this to {@code symbolWithDefinitions}, or to a surrogate pointing to that
   *                                 symbol. This object will be used to create The ComponentTypeExpression.
   */
  @ParameterizedTest
  @MethodSource("compWithTypeParamAndOptionallySurrogateProvider")
  public void shouldGetTypeExprOfPortWithOwnTypeVarReplaced(@NotNull ComponentTypeSymbol symbolWithDefinitions,
                                                            @NotNull ComponentTypeSymbol symbolVersionForTypeExpr) {
    Preconditions.checkNotNull(symbolWithDefinitions);
    Preconditions.checkNotNull(symbolVersionForTypeExpr);

    // Given
    SymbolService.link(ArcBasisMill.globalScope(), symbolWithDefinitions);
    symbolVersionForTypeExpr.setEnclosingScope(ArcBasisMill.globalScope());

    TypeVarSymbol typeVar = symbolWithDefinitions.getTypeParameters().get(0);

    String portName = "port";
    PortSymbol port = GenericArcMill.portSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
      .setIncoming(true)
      .build();
    symbolWithDefinitions.getSpannedScope().add(port);

    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    TypeExprOfGenericComponent boundCompTypeExpr =
      new TypeExprOfGenericComponent(symbolVersionForTypeExpr, Lists.newArrayList(intTypeExpr));

    // When
    Optional<SymTypeExpression> portsType = boundCompTypeExpr.getTypeExprOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent(), "Port missing");
    Assertions.assertInstanceOf(SymTypePrimitive.class, portsType.get());
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfPortWithParentTypeVarReplaced() {
    // Given
    ComponentTypeSymbol parentCompDefinition = createComponentWithTypeVar("Parent", "S");
    TypeVarSymbol parentTypeVar = parentCompDefinition.getTypeParameters().get(0);
    String portName = "porr";
    PortSymbol port = GenericArcMill.portSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createTypeVariable(parentTypeVar))
      .setIncoming(true)
      .build();
    parentCompDefinition.getSpannedScope().add(port);

    ComponentTypeSymbol compDefinition = createComponentWithTypeVar("Comp", "T");
    // bind parent's S with child's T to declare: Comp<T> extends Parent<T>
    TypeVarSymbol childTypeVar = compDefinition.getTypeParameters().get(0);
    SymTypeExpression childTypeVarExpr = SymTypeExpressionFactory.createTypeVariable(childTypeVar);
    CompTypeExpression boundParentTypeExpr =
      new TypeExprOfGenericComponent(parentCompDefinition, Lists.newArrayList(childTypeVarExpr));
    compDefinition.setParent(boundParentTypeExpr);

    // create CompTypeExpr representing Comp<int>
    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    TypeExprOfGenericComponent boundCompTypeExpr =
      new TypeExprOfGenericComponent(compDefinition, Lists.newArrayList(intTypeExpr));

    // When
    Optional<SymTypeExpression> portsType = boundCompTypeExpr.getTypeExprOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent());
    Assertions.assertTrue(portsType.get() instanceof SymTypePrimitive);
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  /**
   * @param symbolWithDefinitions Provide a component type symbol in which parameters will be added in this test.
   * @param symbolVersionForTypeExpr Set this to {@code symbolWithDefinitions}, or to a surrogate pointing to that
   *                                 symbol. This object will be used to create The ComponentTypeExpression.
   */
  @ParameterizedTest
  @MethodSource("compWithTypeParamAndOptionallySurrogateProvider")
  public void shouldGetTypeExprOfParameterWithOwnTypeVarReplaced(@NotNull ComponentTypeSymbol symbolWithDefinitions,
                                                                 @NotNull ComponentTypeSymbol symbolVersionForTypeExpr) {
    Preconditions.checkNotNull(symbolWithDefinitions);
    Preconditions.checkNotNull(symbolVersionForTypeExpr);

    // Given
    SymbolService.link(ArcBasisMill.globalScope(), symbolWithDefinitions);
    symbolVersionForTypeExpr.setEnclosingScope(ArcBasisMill.globalScope());

    TypeVarSymbol typeVar = symbolWithDefinitions.getTypeParameters().get(0);

    String paramName = "parr";
    VariableSymbol param = GenericArcMill.variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
      .build();
    symbolWithDefinitions.getSpannedScope().add(param);
    symbolWithDefinitions.addParameter(param);

    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    TypeExprOfGenericComponent boundCompTypeExpr =
      new TypeExprOfGenericComponent(symbolVersionForTypeExpr, Lists.newArrayList(intTypeExpr));

    // When
    Optional<SymTypeExpression> paramTypeExpr = boundCompTypeExpr.getTypeExprOfParameter(paramName);

    // Then
    Assertions.assertTrue(paramTypeExpr.isPresent(), "param missing");
    Assertions.assertInstanceOf(SymTypePrimitive.class, paramTypeExpr.get());
    Assertions.assertEquals(BasicSymbolsMill.INT, paramTypeExpr.get().print());
  }

  @Test
  public void shouldGetTypeExprOfParameterWithParentTypeVarReplaced() {
    // Given
    ComponentTypeSymbol parentCompDefinition = createComponentWithTypeVar("Parent", "S");
    TypeVarSymbol parentTypeVar = parentCompDefinition.getTypeParameters().get(0);
    String paramName = "parr";
    VariableSymbol paramOfParent = GenericArcMill.variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createTypeVariable(parentTypeVar))
      .build();
    parentCompDefinition.getSpannedScope().add(paramOfParent);
    parentCompDefinition.addParameter(paramOfParent);

    ComponentTypeSymbol compDefinition = createComponentWithTypeVar("Comp", "T");
    TypeVarSymbol childTypeVar = compDefinition.getTypeParameters().get(0);
    VariableSymbol paramOfComp = GenericArcMill.variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createTypeVariable(childTypeVar))
      .build();
    compDefinition.getSpannedScope().add(paramOfComp);
    compDefinition.addParameter(paramOfComp);
    // bind parent's S with child's T to declare: Comp<T> extends Parent<T>
    SymTypeExpression childTypeVarExpr = SymTypeExpressionFactory.createTypeVariable(childTypeVar);
    CompTypeExpression boundParentTypeExpr =
      new TypeExprOfGenericComponent(parentCompDefinition, Lists.newArrayList(childTypeVarExpr));
    compDefinition.setParent(boundParentTypeExpr);

    // create CompTypeExpr representing Comp<int>
    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    TypeExprOfGenericComponent boundCompTypeExpr =
      new TypeExprOfGenericComponent(compDefinition, Lists.newArrayList(intTypeExpr));

    // When
    Optional<SymTypeExpression> paramTypeExpr = boundCompTypeExpr.getTypeExprOfParameter(paramName);

    // Then
    Assertions.assertTrue(paramTypeExpr.isPresent());
    Assertions.assertTrue(paramTypeExpr.get() instanceof SymTypePrimitive);
    Assertions.assertEquals(BasicSymbolsMill.INT, paramTypeExpr.get().print());
  }

  @Test
  public void shouldGetBindingsAsListInCorrectOrder() {
    // Given
    ComponentTypeSymbol comp = createComponentWithTypeVar("Comp", "A", "B", "C");

    SymTypeExpression floatTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT);
    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    SymTypeExpression boolTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    List<SymTypeExpression> typeExprList = Lists.newArrayList(floatTypeExpr, intTypeExpr, boolTypeExpr);

    // When
    TypeExprOfGenericComponent compTypeExpr = new TypeExprOfGenericComponent(comp, typeExprList);

    // Then
    List<SymTypeExpression> returnedBindings = compTypeExpr.getBindingsAsList();
    Assertions.assertEquals(typeExprList, returnedBindings);
  }

  @Test
  public void shouldGetTypeParamBindingsSkippingSurrogate() {
    // Given
    ComponentTypeSymbol comp = createComponentWithTypeVar("Comp", "A", "B", "C");
    ComponentTypeSymbolSurrogate compSurrogate = ArcBasisMill
      .componentTypeSymbolSurrogateBuilder()
      .setName("Comp")
      .setEnclosingScope(ArcBasisMill.globalScope()).build();
    SymbolService.link(ArcBasisMill.globalScope(), comp);



    SymTypeExpression floatTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT);
    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    SymTypeExpression boolTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    List<SymTypeExpression> typeExprList = Lists.newArrayList(floatTypeExpr, intTypeExpr, boolTypeExpr);

    // When
    TypeExprOfGenericComponent compTypeExpr = new TypeExprOfGenericComponent(compSurrogate, typeExprList);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(floatTypeExpr, compTypeExpr.getBindingFor("A").orElseThrow()),
      () -> Assertions.assertEquals(intTypeExpr, compTypeExpr.getBindingFor("B").orElseThrow()),
      () -> Assertions.assertEquals(boolTypeExpr, compTypeExpr.getBindingFor("C").orElseThrow()),
      () -> Assertions.assertEquals(3, compTypeExpr.getTypeVarBindings().size())
    );

  }

  /**
   * Beware that the created symbol is not enclosed by any scope yet.
   */
  protected static ComponentTypeSymbol createComponentWithTypeVar(@NotNull String compName,
                                                                  @NotNull String... typeVarNames) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(typeVarNames);

    List<TypeVarSymbol> typeVars = new ArrayList<>(typeVarNames.length);
    for(String typeVarName : typeVarNames) {
      TypeVarSymbol typeVar = GenericArcMill.typeVarSymbolBuilder()
        .setName(typeVarName)
        .build();
      typeVars.add(typeVar);
    }

    return GenericArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(GenericArcMill.scope())
      .setTypeParameters(typeVars)
      .build();
  }

  /**
   * Beware that the created surrogate is not enclosed by any scope yet.
   */
  protected static ComponentTypeSymbol createSurrogateInGlobalScopeFor(@NotNull ComponentTypeSymbol original) {
    Preconditions.checkNotNull(original);

    return GenericArcMill
      .componentTypeSymbolSurrogateBuilder()
      .setName(original.getFullName())
      .build();
  }
}
