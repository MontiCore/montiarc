/* (c) https://github.com/MontiCore/monticore */
package arcbasis.context;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.IArcBasisArtifactScope;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentType2TypeSymbolAdapter;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Subcomponent2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Holds the tests for {@link WithinComponentResolver}.
 */
public class WithinComponentResolverTest extends ArcBasisTestBase {

  protected final WithinComponentResolver resolver = new WithinComponentResolver();

  @Test
  public void shouldCreateKindOfNonGenericComponentType() {
    // Given
    ComponentTypeSymbol comp = component("C");

    // When
    CompKindExpression kind = resolver.kindOf(comp);

    // Then
    assertThat(kind).isInstanceOf(CompKindOfComponentType.class);
    assertThat(kind.getTypeInfo()).isSameAs(comp);
  }

  @Test
  public void shouldCreateKindOfGenericComponentTypeWithFreeTypeParameters() {
    // Given
    ComponentTypeSymbol comp = component("K", "T");

    // When
    CompKindExpression kind = resolver.kindOf(comp);

    // Then
    assertThat(kind).isInstanceOf(CompKindOfGenericComponentType.class);
    assertThat(kind.printName()).isEqualTo("K<T>");
  }

  @Test
  public void shouldResolveAllDeclaredMembersOfNameAndKind() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i1 = port(comp, "i", intType());
    PortSymbol i2 = port(comp, "i", boolType());
    port(comp, "j", intType());
    subcomponent(comp, "i", new CompKindOfComponentType(component("K")));

    // When
    List<PortSymbol> resolved = resolver.resolveMany(comp, "i", PortSymbol.class);

    // Then
    assertThat(resolved).containsExactly(i1, i2);
  }

  @Test
  public void shouldResolveInheritedMembersUnlessHiddenByDeclaredMember() {
    // Given
    ComponentTypeSymbol parent = component("P");
    PortSymbol a = port(parent, "a", intType());
    port(parent, "b", intType());
    ComponentTypeSymbol child = component("C");
    PortSymbol b = port(child, "b", intType());
    extend(child, new CompKindOfComponentType(parent));

    // When
    List<PortSymbol> resolvedA = resolver.resolveMany(child, "a", PortSymbol.class);
    List<PortSymbol> resolvedB = resolver.resolveMany(child, "b", PortSymbol.class);

    // Then
    assertThat(resolvedA).containsExactly(a);
    assertThat(resolvedB).containsExactly(b);
  }

  @Test
  public void shouldResolveInheritedMembersOfAllSuperComponents() {
    // Given
    ComponentTypeSymbol first = component("P1");
    PortSymbol x1 = port(first, "x", intType());
    ComponentTypeSymbol second = component("P2");
    PortSymbol x2 = port(second, "x", intType());
    ComponentTypeSymbol child = component("C");
    extend(child, new CompKindOfComponentType(first), new CompKindOfComponentType(second));

    // When
    List<PortSymbol> resolved = resolver.resolveMany(child, "x", PortSymbol.class);

    // Then
    assertThat(resolved).containsExactly(x1, x2);
  }

  @Test
  public void shouldResolveOnlySymbolsOfKindInScope() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = port(comp, "i", intType());
    IArcBasisScope block = block(comp.getSpannedScope());
    VariableSymbol local = variable(block, "i");

    // When
    List<VariableSymbol> variables = resolver.resolveMany(block, "i", VariableSymbol.class);
    List<PortSymbol> ports = resolver.resolveMany(block, "i", PortSymbol.class);

    // Then
    assertThat(variables).containsExactly(local);
    assertThat(ports).containsExactly(i);
  }

  @Test
  public void shouldResolveMatchingSymbolsOfInnermostDeclaringScope() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = port(comp, "i", intType());
    IArcBasisScope block = block(comp.getSpannedScope());
    VariableSymbol local = variable(block, "i");
    IArcBasisScope innerBlock = block(block);
    IArcBasisScope otherBlock = block(comp.getSpannedScope());

    // When
    List<ISymbol> inBlock = resolver.resolveMany(block, "i", resolver::isValue);
    List<ISymbol> inInnerBlock = resolver.resolveMany(innerBlock, "i", resolver::isValue);
    List<ISymbol> inOtherBlock = resolver.resolveMany(otherBlock, "i", resolver::isValue);
    List<ISymbol> inComponent = resolver.resolveMany(comp.getSpannedScope(), "i", resolver::isValue);
    List<ISymbol> unknown = resolver.resolveMany(block, "j", resolver::isValue);

    // Then
    assertThat(inBlock).containsExactly(local);
    assertThat(inInnerBlock).containsExactly(local);
    assertThat(inOtherBlock).containsExactly(i);
    assertThat(inComponent).containsExactly(i);
    assertThat(unknown).isEmpty();
  }

  @Test
  public void shouldResolveThroughScopesSpannedByOtherSymbols() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = port(comp, "i", intType());
    IArcBasisScope typeScope = typeScope(comp.getSpannedScope());

    // When
    List<ISymbol> resolved = resolver.resolveMany(typeScope, "i", resolver::isValue);

    // Then
    assertThat(resolved).containsExactly(i);
  }

  @Test
  public void shouldResolveLocalSymbolsButNothingBeyondComponents() {
    // Given
    IArcBasisArtifactScope artifact = ArcBasisMill.artifactScope();
    IArcBasisScope inArtifact = block(artifact);
    variable(artifact, "i");
    IArcBasisScope detached = ArcBasisMill.scope();
    VariableSymbol local = variable(detached, "i");

    // When
    List<ISymbol> fromArtifact = resolver.resolveMany(artifact, "i", resolver::isValue);
    List<ISymbol> fromScopeInArtifact = resolver.resolveMany(inArtifact, "i", resolver::isValue);
    List<ISymbol> fromGlobal = resolver.resolveMany(ArcBasisMill.globalScope(), "i", resolver::isValue);
    List<ISymbol> fromDetached = resolver.resolveMany(detached, "i", resolver::isValue);
    List<ISymbol> unknownInDetached = resolver.resolveMany(detached, "j", resolver::isValue);

    // Then
    assertThat(fromArtifact).isEmpty();
    assertThat(fromScopeInArtifact).isEmpty();
    assertThat(fromGlobal).isEmpty();
    assertThat(fromDetached).containsExactly(local);
    assertThat(unknownInDetached).isEmpty();
  }

  @Test
  public void shouldResolveValuesButNoOtherSymbols() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = port(comp, "i", intType());
    IArcBasisScope block = block(comp.getSpannedScope());
    VariableSymbol local = variable(block, "v");
    SymbolService.link(block, ArcBasisMill.typeSymbolBuilder().setName("i").setSpannedScope(ArcBasisMill.scope()).build());

    // When
    List<ISymbol> port = resolver.resolveValues(block, "i");
    List<ISymbol> variable = resolver.resolveValues(block, "v");

    // Then
    assertThat(port).containsExactly(i);
    assertThat(variable).containsExactly(local);
  }

  @Test
  public void shouldResolvePortsUnlessHiddenByVariable() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = port(comp, "i", intType());
    IArcBasisScope block = block(comp.getSpannedScope());
    variable(block, "i");
    IArcBasisScope otherBlock = block(comp.getSpannedScope());

    // When
    List<PortSymbol> hidden = resolver.resolvePorts(block, "i");
    List<PortSymbol> visible = resolver.resolvePorts(otherBlock, "i");
    List<PortSymbol> unknown = resolver.resolvePorts(otherBlock, "j");

    // Then
    assertThat(hidden).isEmpty();
    assertThat(visible).containsExactly(i);
    assertThat(unknown).isEmpty();
  }

  @Test
  public void shouldGetDeclaredAndInheritedMembersGroupedByName() {
    // Given
    ComponentTypeSymbol first = component("P1");
    PortSymbol x1 = port(first, "x", intType());
    port(first, "b", intType());
    ComponentTypeSymbol second = component("P2");
    PortSymbol x2 = port(second, "x", intType());
    ComponentTypeSymbol child = component("C");
    PortSymbol b1 = port(child, "b", intType());
    PortSymbol a = port(child, "a", intType());
    PortSymbol b2 = port(child, "b", boolType());
    extend(child, new CompKindOfComponentType(first), new CompKindOfComponentType(second));

    // When
    List<PortSymbol> all = resolver.getAll(child, PortSymbol.class);

    // Then
    assertThat(all).containsExactly(b1, b2, a, x1, x2);
  }

  @Test
  public void shouldGetMembersWithoutAdaptersAndSymbolsOfNestedScopes() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = port(comp, "i", intType());
    SubcomponentSymbol s = subcomponent(comp, "s", new CompKindOfComponentType(component("K")));
    VariableSymbol p = variable(comp.getSpannedScope(), "p");
    SymbolService.link(comp.getSpannedScope(), new Port2VariableAdapter(i));
    SymbolService.link(comp.getSpannedScope(), new Subcomponent2VariableAdapter(s));
    ComponentTypeSymbol inner = component("Inner");
    comp.getSpannedScope().add(inner);
    inner.setEnclosingScope(comp.getSpannedScope());
    SymbolService.link(comp.getSpannedScope(), new ComponentType2TypeSymbolAdapter(inner));
    comp.getSpannedScope().add(new ComponentType2TypeSymbolAdapter(component("Foreign")));
    variable(block(comp.getSpannedScope()), "local");

    // When
    List<ISymbol> all = resolver.getAll(comp, ISymbol.class);
    List<VariableSymbol> variables = resolver.getAll(comp, VariableSymbol.class);

    // Then
    assertThat(all).containsExactlyInAnyOrder(i, s, p, inner);
    assertThat(variables).containsExactly(p);
  }

  @Test
  public void shouldGetSymbolsOfScopeChainAndComponent() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = port(comp, "i", intType());
    IArcBasisScope block = block(comp.getSpannedScope());
    VariableSymbol x = variable(block, "x");
    IArcBasisScope typeScope = typeScope(block);
    VariableSymbol shadowing = variable(typeScope, "i");

    // When
    List<ISymbol> visible = resolver.getAll(typeScope, ISymbol.class);

    // Then
    assertThat(visible).containsExactly(shadowing, x, i);
  }

  @Test
  public void shouldGetLocalSymbolsButNothingBeyondComponents() {
    // Given
    IArcBasisArtifactScope artifact = ArcBasisMill.artifactScope();
    IArcBasisScope inArtifact = block(artifact);
    VariableSymbol inBlock = variable(inArtifact, "v");
    variable(artifact, "w");
    IArcBasisScope detached = ArcBasisMill.scope();
    VariableSymbol local = variable(detached, "v");

    // When
    List<ISymbol> fromScopeInArtifact = resolver.getAll(inArtifact, ISymbol.class);
    List<ISymbol> fromDetached = resolver.getAll(detached, ISymbol.class);

    // Then
    assertThat(fromScopeInArtifact).containsExactly(inBlock);
    assertThat(fromDetached).containsExactly(local);
  }

  @Test
  public void shouldFindSuperPathOfInheritedMembers() {
    // Given
    ComponentTypeSymbol grandparent = component("G");
    PortSymbol g = port(grandparent, "g", intType());
    ComponentTypeSymbol parent = component("P");
    CompKindExpression toGrandparent = new CompKindOfComponentType(grandparent);
    extend(parent, toGrandparent);
    ComponentTypeSymbol other = component("O");
    ComponentTypeSymbol child = component("C");
    PortSymbol c = port(child, "c", intType());
    VariableSymbol nested = variable(typeScope(child.getSpannedScope()), "n");
    CompKindExpression toOther = new CompKindOfComponentType(other);
    CompKindExpression toParent = new CompKindOfComponentType(parent);
    extend(child, toOther, toParent);

    // When
    List<CompKindExpression> toDeclared = resolver.superPathTo(child, c).orElseThrow();
    List<CompKindExpression> toNested = resolver.superPathTo(child, nested).orElseThrow();
    List<CompKindExpression> toInherited = resolver.superPathTo(child, g).orElseThrow();
    boolean foundInParent = resolver.superPathTo(parent, c).isPresent();

    // Then
    assertThat(toDeclared).isEmpty();
    assertThat(toNested).isEmpty();
    assertThat(toInherited).containsExactly(toParent, toGrandparent);
    assertThat(foundInParent).isFalse();
  }

  @Test
  public void shouldNotTreatMembersOfInnerComponentTypesAsMembers() {
    // Given
    ComponentTypeSymbol outer = component("Outer");
    ComponentTypeSymbol inner = component("Inner");
    outer.getSpannedScope().addSubScope(inner.getSpannedScope());
    PortSymbol innerPort = port(inner, "i", intType());
    VariableSymbol unlinked = ArcBasisMill.variableSymbolBuilder().setName("v").setType(intType()).build();

    // When
    boolean innerPortFound = resolver.superPathTo(outer, innerPort).isPresent();
    boolean unlinkedFound = resolver.superPathTo(outer, unlinked).isPresent();

    // Then
    assertThat(innerPortFound).isFalse();
    assertThat(unlinkedFound).isFalse();
  }

  @Test
  public void shouldKeepTypesOfMembersOfNonGenericOwner() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = port(comp, "i", intType());
    VariableSymbol v = variable(comp.getSpannedScope(), "v");
    VariableSymbol local = variable(block(comp.getSpannedScope()), "local");
    SubcomponentSymbol s = subcomponent(comp, "s", new CompKindOfComponentType(component("K")));
    PortSymbol foreign = port(component("X"), "x", intType());
    CompKindExpression owner = resolver.kindOf(comp);

    // When && Then
    assertThat(resolver.typeOf(owner, i)).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
    assertThat(resolver.typeOf(owner, v)).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
    assertThat(resolver.typeOf(owner, local)).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
    assertThat(resolver.typeOf(owner, s)).isEmpty();
    assertThat(resolver.typeOf(owner, foreign)).isEmpty();
  }

  @Test
  public void shouldNotTypeUntypedMembers() {
    // Given
    ComponentTypeSymbol comp = component("C");
    PortSymbol i = new PortSymbol("i") { };
    SymbolService.link((IArcBasisScope) comp.getSpannedScope(), i);
    VariableSymbol v = variable(comp.getSpannedScope(), "v");
    v.setType(null);
    CompKindExpression owner = resolver.kindOf(comp);

    // When && Then
    assertThat(resolver.typeOf(owner, i)).isEmpty();
    assertThat(resolver.typeOf(owner, v)).isEmpty();
  }

  @Test
  public void shouldKeepTypeParametersOfComponentTypeFree() {
    // Given
    ComponentTypeSymbol k = component("K", "T");
    PortSymbol i = port(k, "i", typeVar(k, 0));

    // When
    CompKindExpression self = resolver.kindOf(k);

    // Then
    assertThat(resolver.typeOf(self, i)).hasValueSatisfying(type -> assertThat(type.print()).isEqualTo("T"));
  }

  @Test
  public void shouldBindInheritedPortOfNonGenericChild() {
    // Given
    ComponentTypeSymbol parent = component("P", "S");
    PortSymbol i = port(parent, "i", typeVar(parent, 0));
    ComponentTypeSymbol child = component("C");
    extend(child, new CompKindOfGenericComponentType(parent, List.of(intType())));

    // When
    CompKindExpression self = resolver.kindOf(child);

    // Then
    assertThat(resolver.resolveMany(child, "i", PortSymbol.class)).containsExactly(i);
    assertThat(resolver.typeOf(self, i)).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
  }

  @Test
  public void shouldBindSuperComponentsOfOwner() {
    // Given
    ComponentTypeSymbol generic = component("P", "S");
    ComponentTypeSymbol nonGeneric = component("Q");
    ComponentTypeSymbol child = component("C", "T");
    CompKindExpression toNonGeneric = new CompKindOfComponentType(nonGeneric);
    extend(child, new CompKindOfGenericComponentType(generic, List.of(typeVar(child, 0))), toNonGeneric);
    CompKindExpression owner = new CompKindOfGenericComponentType(child, List.of(intType()));

    // When
    List<CompKindExpression> superComponents = resolver.getSuperComponents(owner);

    // Then
    assertThat(superComponents).extracting(CompKindExpression::printFullName).containsExactly("P<int>", "Q");
    assertThat(superComponents.get(1)).isSameAs(toNonGeneric);
  }

  @Test
  public void shouldBindBoundsOfTypeParameters() {
    // Given
    ComponentTypeSymbol comp = component("K", "T", "U", "V");
    TypeVarSymbol t = comp.getTypeParameters().get(0);
    TypeVarSymbol u = comp.getTypeParameters().get(1);
    TypeVarSymbol v = comp.getTypeParameters().get(2);
    TypeSymbol list = ArcBasisMill.typeSymbolBuilder().setName("List").setSpannedScope(ArcBasisMill.scope()).build();
    u.setSuperTypesList(List.of(typeVar(comp, 0)));
    v.setSuperTypesList(List.of(SymTypeExpressionFactory.createGenerics(list, typeVar(comp, 1)), intType()));
    CompKindExpression bound = new CompKindOfGenericComponentType(comp, List.of(intType(), boolType(), intType()));
    CompKindExpression free = resolver.kindOf(comp);

    // When && Then
    assertThat(resolver.boundsOf(bound, t)).isEmpty();
    assertThat(resolver.boundsOf(bound, u)).extracting(SymTypeExpression::printFullName).containsExactly("int");
    assertThat(resolver.boundsOf(bound, v)).extracting(SymTypeExpression::printFullName).containsExactly("List<boolean>", "int");
    assertThat(resolver.boundsOf(free, v)).extracting(SymTypeExpression::print).containsExactly("List<U>", "int");
  }

  @Test
  public void shouldRejectTypeParametersOfOtherComponentTypes() {
    // Given
    CompKindExpression owner = resolver.kindOf(component("K", "T"));
    TypeVarSymbol foreign = component("L", "T").getTypeParameters().get(0);

    // When && Then
    assertThatIllegalArgumentException().isThrownBy(() -> resolver.boundsOf(owner, foreign));
  }

  @Test
  public void shouldBindTypesOfNestedGenericSubcomponents() {
    // Given
    ComponentTypeSymbol k = component("K", "U");
    PortSymbol i = port(k, "i", typeVar(k, 0));
    ComponentTypeSymbol outer = component("Outer", "T");
    SubcomponentSymbol kInst = subcomponent(outer, "k",
      new CompKindOfGenericComponentType(k, List.of(typeVar(outer, 0))));
    ComponentTypeSymbol top = component("Top");
    SubcomponentSymbol oInst = subcomponent(top, "o",
      new CompKindOfGenericComponentType(outer, List.of(intType())));

    // When
    CompKindExpression o = resolver.kindOf(resolver.kindOf(top), oInst).orElseThrow();
    CompKindExpression ok = resolver.kindOf(o, kInst).orElseThrow();

    // Then
    assertThat(o.printFullName()).isEqualTo("Outer<int>");
    assertThat(ok.printFullName()).isEqualTo("K<int>");
    assertThat(resolver.typeOf(ok, i)).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
  }

  @Test
  public void shouldBindEachTypeParameterToItsTypeArgument() {
    // Given
    ComponentTypeSymbol parent = component("P", "A", "B");
    PortSymbol a = port(parent, "a", typeVar(parent, 0));
    PortSymbol b = port(parent, "b", typeVar(parent, 1));
    ComponentTypeSymbol child = component("C", "X", "Y");
    extend(child, new CompKindOfGenericComponentType(parent, List.of(typeVar(child, 1), typeVar(child, 0))));
    ComponentTypeSymbol top = component("Top");
    SubcomponentSymbol cInst = subcomponent(top, "c",
      new CompKindOfGenericComponentType(child, List.of(intType(), boolType())));

    // When
    CompKindExpression c = resolver.kindOf(resolver.kindOf(top), cInst).orElseThrow();

    // Then
    assertThat(c.printFullName()).isEqualTo("C<int,boolean>");
    assertThat(resolver.getSuperComponents(c)).extracting(CompKindExpression::printFullName)
      .containsExactly("P<boolean,int>");
    assertThat(resolver.typeOf(c, a)).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("boolean"));
    assertThat(resolver.typeOf(c, b)).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
  }

  @Test
  public void shouldBindInheritedPortOfGenericSubcomponent() {
    // Given
    ComponentTypeSymbol parent = component("P", "S");
    PortSymbol i = port(parent, "i", typeVar(parent, 0));
    ComponentTypeSymbol child = component("Child", "T");
    extend(child, new CompKindOfGenericComponentType(parent, List.of(typeVar(child, 0))));
    ComponentTypeSymbol top = component("Top");
    SubcomponentSymbol cInst = subcomponent(top, "c",
      new CompKindOfGenericComponentType(child, List.of(intType())));

    // When
    CompKindExpression c = resolver.kindOf(resolver.kindOf(top), cInst).orElseThrow();

    // Then
    assertThat(resolver.typeOf(c, i)).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
  }

  @Test
  public void shouldTypePortAccessesAsSeenInEnclosingComponent() {
    // Given
    ComponentTypeSymbol parent = component("P", "S");
    port(parent, "pi", typeVar(parent, 0));
    ComponentTypeSymbol k = component("K", "U");
    port(k, "ki", typeVar(k, 0));
    ComponentTypeSymbol comp = component("C", "T");
    port(comp, "ci", typeVar(comp, 0));
    extend(comp, new CompKindOfGenericComponentType(parent, List.of(intType())));
    subcomponent(comp, "k", new CompKindOfGenericComponentType(k, List.of(boolType())));
    IArcBasisScope block = block(comp.getSpannedScope());

    // When && Then
    assertThat(resolver.typeOf(portAccess(comp.getSpannedScope(), "ci"))).hasValueSatisfying(type -> assertThat(type.print()).isEqualTo("T"));
    assertThat(resolver.typeOf(portAccess(comp.getSpannedScope(), "pi"))).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
    assertThat(resolver.typeOf(portAccess(block, "pi"))).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("int"));
    assertThat(resolver.typeOf(portAccess(comp.getSpannedScope(), "k", "ki"))).hasValueSatisfying(type -> assertThat(type.printFullName()).isEqualTo("boolean"));
    assertThat(resolver.typeOf(portAccess(comp.getSpannedScope(), "unknown"))).isEmpty();
  }

  @Test
  public void shouldNotTypeQualifiedPortAccessesWithoutSubcomponent() {
    // Given
    ComponentTypeSymbol comp = component("C");
    ASTPortAccess access = portAccess(comp.getSpannedScope(), "unknown", "i");
    access.setPortSymbol(port(component("K"), "i", intType()));

    // When && Then
    assertThat(resolver.typeOf(access)).isEmpty();
  }

  @Test
  public void shouldUseDeclaredTypesOfPortAccessesOutsideComponents() {
    // Given
    ASTPortAccess typed = ASTPortAccess.of(port(component("K", "T"), "i", typeVar(component("L", "U"), 0)));
    ASTPortAccess untyped = ASTPortAccess.of(new PortSymbol("o") { });

    // When && Then
    assertThat(resolver.typeOf(typed)).hasValueSatisfying(type -> assertThat(type.print()).isEqualTo("U"));
    assertThat(resolver.typeOf(untyped)).isEmpty();
  }

  @Test
  public void shouldResolveKindsOfInheritedAndNonGenericSubcomponents() {
    // Given
    ComponentTypeSymbol parent = component("P");
    CompKindExpression kType = new CompKindOfComponentType(component("K"));
    SubcomponentSymbol inherited = subcomponent(parent, "k", kType);
    ComponentTypeSymbol child = component("C");
    extend(child, new CompKindOfComponentType(parent));
    SubcomponentSymbol foreign = subcomponent(component("X"), "x", kType);
    SubcomponentSymbol untyped = ArcBasisMill.subcomponentSymbolBuilder().setName("u").build();
    SymbolService.link((IArcBasisScope) child.getSpannedScope(), untyped);
    CompKindExpression owner = resolver.kindOf(child);

    // When && Then
    assertThat(resolver.kindOf(owner, inherited)).containsSame(kType);
    assertThat(resolver.kindOf(owner, foreign)).isEmpty();
    assertThat(resolver.kindOf(owner, untyped)).isEmpty();
  }

  @Test
  public void shouldFindEnclosingComponent() {
    // Given
    ComponentTypeSymbol outer = component("Outer");
    IArcBasisScope block = block(outer.getSpannedScope());
    ComponentTypeSymbol inner = component("Inner");
    outer.getSpannedScope().addSubScope(inner.getSpannedScope());
    IArcBasisScope innerBlock = block(inner.getSpannedScope());
    IArcBasisScope innerTypeScope = typeScope(innerBlock);

    // When && Then
    assertThat(resolver.enclosingComponentOf(outer.getSpannedScope())).containsSame(outer);
    assertThat(resolver.enclosingComponentOf(block)).containsSame(outer);
    assertThat(resolver.enclosingComponentOf(innerBlock)).containsSame(inner);
    assertThat(resolver.enclosingComponentOf(innerTypeScope)).containsSame(inner);
    assertThat(resolver.enclosingComponentOf(ArcBasisMill.scope())).isEmpty();
  }

  @Test
  public void shouldGetOnlyDeclaredSymbolsOfName() {
    // Given
    ComponentTypeSymbol parent = component("P");
    port(parent, "i", intType());
    ComponentTypeSymbol child = component("C");
    extend(child, new CompKindOfComponentType(parent));
    VariableSymbol i = variable(child.getSpannedScope(), "i");
    variable(child.getSpannedScope(), "j");

    // When
    List<ISymbol> local = resolver.getLocalSymbols(child, "i");

    // Then
    assertThat(local).containsExactly(i);
  }

  @Test
  public void shouldTerminateOnCircularInheritance() {
    // Given
    ComponentTypeSymbol a = component("A");
    ComponentTypeSymbol b = component("B");
    extend(a, new CompKindOfComponentType(b));
    extend(b, new CompKindOfComponentType(a));
    PortSymbol foreign = port(component("X"), "x", intType());
    SubcomponentSymbol foreignSub = subcomponent(component("Y"), "y", new CompKindOfComponentType(component("K")));

    // When && Then
    assertThat(resolver.resolveMany(a, "x", PortSymbol.class)).isEmpty();
    assertThat(resolver.getAll(a, PortSymbol.class)).isEmpty();
    assertThat(resolver.superPathTo(a, foreign)).isEmpty();
    assertThat(resolver.typeOf(resolver.kindOf(a), foreign)).isEmpty();
    assertThat(resolver.kindOf(resolver.kindOf(a), foreignSub)).isEmpty();
  }

  @Test
  @SuppressWarnings("DataFlowIssue")
  public void shouldRejectNull() {
    // Given
    ComponentTypeSymbol comp = component("C");
    IScope scope = comp.getSpannedScope();
    CompKindExpression owner = resolver.kindOf(comp);
    PortSymbol port = port(comp, "i", intType());
    SubcomponentSymbol sub = subcomponent(comp, "s", new CompKindOfComponentType(component("K")));

    // When && Then
    assertThatNullPointerException().isThrownBy(() -> resolver.kindOf(null));
    assertThatNullPointerException().isThrownBy(() -> resolver.resolveMany((ComponentTypeSymbol) null, "i", PortSymbol.class));
    assertThatNullPointerException().isThrownBy(() -> resolver.resolveMany(comp, null, PortSymbol.class));
    assertThatNullPointerException().isThrownBy(() -> resolver.resolveMany(comp, "i", null));
    assertThatNullPointerException().isThrownBy(() -> resolver.resolveMany(scope, "i", (Class<PortSymbol>) null));
    assertThatNullPointerException().isThrownBy(() -> resolver.resolveMany(null, "i", resolver::isValue));
    assertThatNullPointerException().isThrownBy(() -> resolver.resolveMany(scope, null, resolver::isValue));
    assertThatNullPointerException().isThrownBy(() -> resolver.resolveMany(scope, "i", (Predicate<ISymbol>) null));
    assertThatNullPointerException().isThrownBy(() -> resolver.getAll((ComponentTypeSymbol) null, PortSymbol.class));
    assertThatNullPointerException().isThrownBy(() -> resolver.getAll(comp, null));
    assertThatNullPointerException().isThrownBy(() -> resolver.getAll((IScope) null, PortSymbol.class));
    assertThatNullPointerException().isThrownBy(() -> resolver.getAll(scope, null));
    assertThatNullPointerException().isThrownBy(() -> resolver.superPathTo(null, port));
    assertThatNullPointerException().isThrownBy(() -> resolver.superPathTo(comp, null));
    assertThatNullPointerException().isThrownBy(() -> resolver.typeOf(null, port));
    assertThatNullPointerException().isThrownBy(() -> resolver.typeOf(owner, null));
    assertThatNullPointerException().isThrownBy(() -> resolver.kindOf(null, sub));
    assertThatNullPointerException().isThrownBy(() -> resolver.kindOf(owner, null));
    assertThatNullPointerException().isThrownBy(() -> resolver.getSuperComponents(null));
    assertThatNullPointerException().isThrownBy(() -> resolver.typeOf((ASTPortAccess) null));
    assertThatNullPointerException().isThrownBy(() -> resolver.boundsOf(null, ArcBasisMill.typeVarSymbolBuilder().setName("T").build()));
    assertThatNullPointerException().isThrownBy(() -> resolver.boundsOf(owner, null));
    assertThatNullPointerException().isThrownBy(() -> resolver.getLocalSymbols(null, "i"));
    assertThatNullPointerException().isThrownBy(() -> resolver.getLocalSymbols(comp, (String) null));
  }

  protected static ComponentTypeSymbol component(String name, String... typeVarNames) {
    ComponentTypeSymbol comp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(name)
      .setSpannedScope(ArcBasisMill.scope())
      .setTypeParameters(Arrays.stream(typeVarNames)
        .map(n -> ArcBasisMill.typeVarSymbolBuilder().setName(n).build())
        .toList())
      .build();
    SymbolService.link(ArcBasisMill.globalScope(), comp);
    return comp;
  }

  protected static void extend(ComponentTypeSymbol child, CompKindExpression... parents) {
    child.setSuperComponentsList(List.of(parents));
  }

  protected static SubcomponentSymbol subcomponent(ComponentTypeSymbol owner, String name, CompKindExpression type) {
    SubcomponentSymbol sub = ArcBasisMill.subcomponentSymbolBuilder()
      .setName(name)
      .setType(type)
      .build();
    SymbolService.link((IArcBasisScope) owner.getSpannedScope(), sub);
    return sub;
  }

  protected static PortSymbol port(ComponentTypeSymbol owner, String name, SymTypeExpression type) {
    PortSymbol port = ArcBasisMill.portSymbolBuilder()
      .setName(name)
      .setType(type)
      .setIncoming(true)
      .build();
    SymbolService.link((IArcBasisScope) owner.getSpannedScope(), port);
    return port;
  }

  protected static VariableSymbol variable(IScope scope, String name) {
    VariableSymbol variable = ArcBasisMill.variableSymbolBuilder().setName(name).setType(intType()).build();
    SymbolService.link((IArcBasisScope) scope, variable);
    return variable;
  }

  protected static ASTPortAccess portAccess(IScope scope, String port) {
    ASTPortAccess access = ArcBasisMill.portAccessBuilder().setPort(port).build();
    access.setEnclosingScope((IArcBasisScope) scope);
    return access;
  }

  protected static ASTPortAccess portAccess(IScope scope, String subcomponent, String port) {
    ASTPortAccess access = ArcBasisMill.portAccessBuilder().setComponent(subcomponent).setPort(port).build();
    access.setEnclosingScope((IArcBasisScope) scope);
    return access;
  }

  protected static IArcBasisScope block(IScope enclosing) {
    IArcBasisScope block = ArcBasisMill.scope();
    ((IArcBasisScope) enclosing).addSubScope(block);
    return block;
  }

  protected static IArcBasisScope typeScope(IScope enclosing) {
    IArcBasisScope scope = block(enclosing);
    TypeSymbol type = ArcBasisMill.typeSymbolBuilder().setName("T").setSpannedScope(scope).build();
    scope.setSpanningSymbol(type);
    return scope;
  }

  protected static SymTypeExpression typeVar(ComponentTypeSymbol comp, int index) {
    return SymTypeExpressionFactory.createTypeVariable(comp.getTypeParameters().get(index));
  }

  protected static SymTypeExpression intType() {
    return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
  }

  protected static SymTypeExpression boolType() {
    return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
  }
}
