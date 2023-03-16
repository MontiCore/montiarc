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
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import genericarc.AbstractTest;
import genericarc.GenericArcMill;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TypeExprOfGenericComponentTest extends AbstractTest {

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
    ComponentTypeSymbol parent = createComponentWithTypeParamsInGlobalScope("Parent", "S");
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

  public static Stream<Arguments> symTypeExprProvider() {
    return Stream.of(
      Arguments.of(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT)),
      Arguments.of(SymTypeExpressionFactory.createTypeObject(Mockito.mock(TypeSymbol.class))),
      Arguments.of(SymTypeExpressionFactory.createTypeVariable(Mockito.mock(TypeVarSymbol.class)))
    );
  }

  @ParameterizedTest
  @MethodSource("symTypeExprProvider")
  public void shouldGetParentComponentWithOwnVarReplacement(@NotNull SymTypeExpression typeArgument) {
    // Given
    ComponentTypeSymbol parent = createComponentWithTypeParamsInGlobalScope("Parent", "S");
    TypeVarSymbol parentTypeVar = parent.getTypeParameters().get(0);
    ComponentTypeSymbol comp = createComponentWithTypeParamsInGlobalScope("Comp", "T");
    SymTypeExpression childTypeVar =
      SymTypeExpressionFactory.createTypeVariable(comp.getTypeParameters().get(0));

    // creating a typeExpr representing Parent<T> that is then set to be the parent of Comp<T>
    CompTypeExpression parentTypeExpr = new TypeExprOfGenericComponent(parent, Lists.newArrayList(childTypeVar));
    comp.setParent(parentTypeExpr);

    // Now consider Comp<typeArgument> (see parameter of the test). The parent then should be Parent<typeArgument>.
    CompTypeExpression boundCompTypeExpr = new TypeExprOfGenericComponent(comp, Lists.newArrayList(typeArgument));

    // When
    Optional<CompTypeExpression> parentOfBoundCompTypeExpr = boundCompTypeExpr.getParentTypeExpr();

    // Then
    Assertions.assertTrue(parentOfBoundCompTypeExpr.isPresent());
    Assertions.assertSame(parent, parentOfBoundCompTypeExpr.get().getTypeInfo());
    Assertions.assertTrue(parentOfBoundCompTypeExpr.get() instanceof TypeExprOfGenericComponent);
    Assertions.assertTrue(((TypeExprOfGenericComponent) parentOfBoundCompTypeExpr.get()).getBindingFor(parentTypeVar).isPresent());
    Assertions.assertEquals(typeArgument,
      ((TypeExprOfGenericComponent) parentOfBoundCompTypeExpr.get()).getBindingFor(parentTypeVar).get());
  }

  public static Stream<Arguments> symTypeExpressionPairProvider() {
    SymTypeExpression intSymType = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    SymTypeExpression boolSymType = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    SymTypeExpression ooSymType1 = SymTypeExpressionFactory.createTypeObject(AbstractTest.createTypeSymbol("First"));
    SymTypeExpression ooSymType2 = SymTypeExpressionFactory.createTypeObject(AbstractTest.createTypeSymbol("Second"));
    SymTypeExpression symTypeVar1 = SymTypeExpressionFactory.createTypeVariable(
      GenericArcMill.typeVarSymbolBuilder().setName("A").build());
    SymTypeExpression symTypeVar2 = SymTypeExpressionFactory.createTypeVariable(
      GenericArcMill.typeVarSymbolBuilder().setName("B").build());

    // Setting enclosing scopes (deepEquals throws a null pointer exception else wise)
    ooSymType1.getTypeInfo().setEnclosingScope(GenericArcMill.globalScope());
    ooSymType2.getTypeInfo().setEnclosingScope(GenericArcMill.globalScope());
    symTypeVar1.getTypeInfo().setEnclosingScope(GenericArcMill.globalScope());
    symTypeVar2.getTypeInfo().setEnclosingScope(GenericArcMill.globalScope());

    return Stream.of(
      Arguments.of(intSymType, boolSymType),
      Arguments.of(intSymType, ooSymType1),
      Arguments.of(intSymType, symTypeVar1),
      Arguments.of(ooSymType1, boolSymType),
      Arguments.of(ooSymType1, ooSymType2),
      Arguments.of(ooSymType1, symTypeVar1),
      Arguments.of(symTypeVar1, boolSymType),
      Arguments.of(symTypeVar1, ooSymType1),
      Arguments.of(symTypeVar1, symTypeVar2)
    );
  }

  @ParameterizedTest
  @MethodSource("symTypeExpressionPairProvider")
  public void getParentComponentShouldNotReplaceParentTypeParam(@NotNull SymTypeExpression typeArgForParent,
                                                                @NotNull SymTypeExpression typeArgForChild) {
    Preconditions.checkNotNull(typeArgForParent);
    Preconditions.checkNotNull(typeArgForChild);

    // Given
    ComponentTypeSymbol parent = createComponentWithTypeParamsInGlobalScope("Parent", "S");
    TypeVarSymbol parentTypeVar = parent.getTypeParameters().get(0);

    // Creating the type expression representing the Parent component, binding its type parameter to typeArgForParent
    CompTypeExpression parentTypeExpr = new TypeExprOfGenericComponent(parent, Lists.newArrayList(typeArgForParent));
    ComponentTypeSymbol comp = createComponentWithTypeParamsInGlobalScope("Comp", "T");
    comp.setParent(parentTypeExpr);

    // Instantiating Comp<typeArgForChild>
    CompTypeExpression boundCompTypeExpr = new TypeExprOfGenericComponent(comp, Lists.newArrayList(typeArgForChild));

    // When
    Optional<CompTypeExpression> parentOfBoundCompTypeExpr = boundCompTypeExpr.getParentTypeExpr();

    // Then
    Assertions.assertTrue(parentOfBoundCompTypeExpr.isPresent());
    Assertions.assertSame(parent, parentOfBoundCompTypeExpr.get().getTypeInfo());
    Assertions.assertTrue(parentOfBoundCompTypeExpr.get() instanceof TypeExprOfGenericComponent);
    Assertions.assertTrue(((TypeExprOfGenericComponent) parentOfBoundCompTypeExpr.get()).getBindingFor(parentTypeVar).isPresent());
    Assertions.assertTrue(((TypeExprOfGenericComponent) parentOfBoundCompTypeExpr.get())
      .getBindingFor(parentTypeVar).get().deepEquals(typeArgForParent));
  }

  protected static Stream<Arguments> compWithTypeParamAndOptionallySurrogateProvider() {
    Named<ComponentTypeSymbol> original = Named.of(
      "CompSymbol",
      createComponentWithTypeParamsInGlobalScope("Comp", "T")
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

    ComponentTypeSymbol compDefinition = symbolWithDefinitions;
    TypeVarSymbol typeVar = compDefinition.getTypeParameters().get(0);

    String portName = "porr";
    PortSymbol port = GenericArcMill.portSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
      .setIncoming(true)
      .build();
    compDefinition.getSpannedScope().add(port);

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
    ComponentTypeSymbol parentCompDefinition = createComponentWithTypeParamsInGlobalScope("Parent", "S");
    TypeVarSymbol parentTypeVar = parentCompDefinition.getTypeParameters().get(0);
    String portName = "porr";
    PortSymbol port = GenericArcMill.portSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createTypeVariable(parentTypeVar))
      .setIncoming(true)
      .build();
    parentCompDefinition.getSpannedScope().add(port);

    ComponentTypeSymbol compDefinition = createComponentWithTypeParamsInGlobalScope("Comp", "T");
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

    ComponentTypeSymbol compDefinition = symbolWithDefinitions;
    TypeVarSymbol typeVar = compDefinition.getTypeParameters().get(0);

    String paramName = "parr";
    VariableSymbol param = GenericArcMill.variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
      .build();
    compDefinition.getSpannedScope().add(param);
    compDefinition.addParameter(param);

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
    ComponentTypeSymbol parentCompDefinition = createComponentWithTypeParamsInGlobalScope("Parent", "S");
    TypeVarSymbol parentTypeVar = parentCompDefinition.getTypeParameters().get(0);
    String paramName = "parr";
    VariableSymbol paramOfParent = GenericArcMill.variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createTypeVariable(parentTypeVar))
      .build();
    parentCompDefinition.getSpannedScope().add(paramOfParent);
    parentCompDefinition.addParameter(paramOfParent);

    ComponentTypeSymbol compDefinition = createComponentWithTypeParamsInGlobalScope("Comp", "T");
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
    ComponentTypeSymbol comp = createComponentWithTypeParamsInGlobalScope("Comp", "A", "B", "C");

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
    ComponentTypeSymbol comp = createComponentWithTypeParamsInGlobalScope("Comp", "A", "B", "C");
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
    List<SymTypeExpression> returnedBindings = compTypeExpr.getBindingsAsList();
    Assertions.assertAll(
      () -> Assertions.assertEquals(floatTypeExpr, compTypeExpr.getBindingFor("A").get()),
      () -> Assertions.assertEquals(intTypeExpr, compTypeExpr.getBindingFor("B").get()),
      () -> Assertions.assertEquals(boolTypeExpr, compTypeExpr.getBindingFor("C").get()),
      () -> Assertions.assertEquals(3, compTypeExpr.getTypeVarBindings().size())
    );

  }

  /**
   * Beware that the created symbol is not enclosed by any scope yet.
   */
  protected static ComponentTypeSymbol createComponentWithTypeParamsInGlobalScope(
    @NotNull String compName, @NotNull String... typeVarNames) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(typeVarNames);

    List<TypeVarSymbol> typeVars = new ArrayList<>(typeVarNames.length);
    for(String typeVarName : typeVarNames) {
      TypeVarSymbol typeVar = GenericArcMill.typeVarSymbolBuilder()
        .setName(typeVarName)
        .build();
      typeVars.add(typeVar);
    }

    ComponentTypeSymbol symbol = GenericArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(GenericArcMill.scope())
      .setTypeParameters(typeVars)
      .build();

    return symbol;
  }

  /**
   * Beware that the created surrogate is not enclosed by any scope yet.
   */
  protected static ComponentTypeSymbol createSurrogateInGlobalScopeFor(@NotNull ComponentTypeSymbol original) {
    Preconditions.checkNotNull(original);

    ComponentTypeSymbol surrogate = GenericArcMill
      .componentTypeSymbolSurrogateBuilder()
      .setName(original.getFullName())
      .build();

    return surrogate;
  }
}
