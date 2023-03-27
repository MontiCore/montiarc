/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import montiarc.Timing;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ComponentTypeSymbolSurrogateTest extends ArcBasisAbstractTest {

  
  @Test
  public void setSpannedScopeShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    IArcBasisScope scopeToSet = ArcBasisMill.scope();

    // When
    surrogate.setSpannedScope(scopeToSet);

    // Then
    Assertions.assertSame(scopeToSet, comp.getSpannedScope());
  }

  @Test
  public void getSpannedScopeShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    // When
    IArcBasisScope scope = surrogate.getSpannedScope();

    // Then
    Assertions.assertSame(comp.getSpannedScope(), scope);
  }

  @Test
  public void getInnerComponentsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol inner = addInnerComponentTypeTo(comp, "Inner");

    // When
    List<ComponentTypeSymbol> inners = surrogate.getInnerComponents();

    // Then
    Assertions.assertArrayEquals(new ComponentTypeSymbol[] {inner}, inners.toArray());
  }

  @Test
  public void getInnerComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol inner = addInnerComponentTypeTo(comp, "Inner");

    // When
    Optional<ComponentTypeSymbol> innerOpt = surrogate.getInnerComponent("Inner");

    // Then
    Assertions.assertTrue(innerOpt.isPresent(), "Inner comp is not present");
    Assertions.assertSame(inner, innerOpt.get());
  }

  @Test
  public void getInnerComponentsWithAccessModifierShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol inner = addInnerComponentTypeTo(comp, "Inner");

    // When
    List<ComponentTypeSymbol> inners = surrogate.getInnerComponents(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new ComponentTypeSymbol[] {inner}, inners.toArray());
  }

  @Test
  public void getPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  public void getPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getPort("myPort");

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  public void getInheritedPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    comp.setParent(new TypeExprOfComponent(parent));

    PortSymbol port = addIncomingPortTo(parent, "parentPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getPort("parentPort", true);

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  public void getIncomingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getIncomingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  public void getIncomingPortsByAccessModifierShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getIncomingPorts(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  public void getIncomingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getIncomingPort("myPort");

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  public void getInheritedIncomingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    comp.setParent(new TypeExprOfComponent(parent));

    PortSymbol port = addIncomingPortTo(parent, "parentPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getIncomingPort("parentPort", true);

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  public void isInnerComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol outer = createCompWithSurrogate("Outer").getKey();
    comp.setOuterComponent(outer);

    // When
    boolean isInner = surrogate.isInnerComponent();

    // Then
    Assertions.assertTrue(isInner);
  }

  
  @Test
  public void getOuterComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol outer = createCompWithSurrogate("Outer").getKey();
    comp.setOuterComponent(outer);

    // When
    Optional<ComponentTypeSymbol> outerOpt = surrogate.getOuterComponent();

    // Then
    Assertions.assertTrue(outerOpt.isPresent(), "No outer component");
    Assertions.assertSame(outer, outerOpt.get());
  }

  
  @Test
  void setOuterComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol outer = createCompWithSurrogate("Outer").getKey();

    // When
    surrogate.setOuterComponent(outer);

    // Then
    Assertions.assertTrue(comp.getOuterComponent().isPresent(), "No outer component present");
    Assertions.assertSame(outer, comp.getOuterComponent().get());
  }

  
  @Test
  void isPresentParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    comp.setParent(new TypeExprOfComponent(parent));

    // When
    boolean parentIsPresent = surrogate.isPresentParent();

    // Then
    Assertions.assertTrue(parentIsPresent, "No parent present");
  }

  
  @Test
  void getParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompTypeExpression parentExpr = new TypeExprOfComponent(parent);
    comp.setParent(parentExpr);

    // When
    CompTypeExpression parentCalculated = surrogate.getParent();

    // Then
    Assertions.assertSame(parentExpr, parentCalculated);
  }

  
  @Test
  void setParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompTypeExpression parentExpr = new TypeExprOfComponent(parent);

    // When
    surrogate.setParent(parentExpr);

    // Then
    Assertions.assertSame(parentExpr, comp.getParent());
  }

  
  @Test
  void getParentConfigurationShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    List<ASTExpression> parentConfig = Collections.singletonList(Mockito.mock(ASTExpression.class));
    comp.setParentConfigurationExpressions(parentConfig);

    // When
    List<ASTExpression> parentConfigCalculated = surrogate.getParentConfiguration();

    // Then
    Assertions.assertArrayEquals(parentConfig.toArray(), parentConfigCalculated.toArray());
  }

  
  @Test
  void setParentConfigurationExpressionsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    List<ASTExpression> parentConfig = Collections.singletonList(Mockito.mock(ASTExpression.class));

    // When
    surrogate.setParentConfigurationExpressions(parentConfig);

    // Then
    Assertions.assertArrayEquals(parentConfig.toArray(), comp.getParentConfiguration().toArray());
  }

  
  @Test
  void getParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = addParameterTo(comp, "myParam");

    // When
    List<VariableSymbol> params = surrogate.getParameters();

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, params.toArray());
  }

  @Test
  void getParameterShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = addParameterTo(comp, "myParam");

    // When
    Optional<VariableSymbol> paramOpt = surrogate.getParameter("myParam");

    // Then
    Assertions.assertTrue(paramOpt.isPresent(), "No parameter");
    Assertions.assertSame(param, paramOpt.get());
  }

  @Test
  void addParameterShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = ArcBasisMill
      .variableSymbolBuilder()
      .setName("param")
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();

    // When
    surrogate.getSpannedScope().add(param);
    surrogate.addParameter(param);

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, comp.getParameters().toArray());
  }


  @Test
  void addParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = ArcBasisMill
      .variableSymbolBuilder()
      .setName("param")
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();

    // When
    surrogate.getSpannedScope().add(param);
    surrogate.addParameters(Collections.singletonList(param));

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, comp.getParameters().toArray());
  }

  @Test
  void hasParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addParameterTo(comp, "param");

    // When
    boolean hasParameters = surrogate.hasParameters();

    // Then
    Assertions.assertTrue(hasParameters, "No parameters found");
  }

  @Test
  void getTypeParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    TypeVarSymbol typeParam = addTypeParameterTo(comp, "T");

    // When
    List<TypeVarSymbol> typeParams = surrogate.getTypeParameters();

    // Then
    Assertions.assertArrayEquals(new TypeVarSymbol[] {typeParam}, typeParams.toArray());
  }

  @Test
  void hasTypeParameterShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addTypeParameterTo(comp, "T");

    // When
    boolean hasTypeParams = surrogate.hasTypeParameter();

    // then
    Assertions.assertTrue(hasTypeParams, "No type parameters found");
  }

  @Test
  void getFieldsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol field = addFieldTo(comp, "myField");

    // When
    List<VariableSymbol> fields = surrogate.getFields();

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {field}, fields.toArray());
  }

  @Test
  void getFieldByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol field = addFieldTo(comp, "myField");

    // When
    Optional<VariableSymbol> fieldOpt = surrogate.getFields("myField");


    // Then
    Assertions.assertTrue(fieldOpt.isPresent(), "No field");
    Assertions.assertSame(field, fieldOpt.get());
  }

  @Test
  void getOutgoingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getOutgoingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }
  @Test
  void getOutgoingPortsByAccessModifierShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getOutgoingPorts(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getOutgoingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getOutgoingPort("myPort");

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  void getInheritedOutgoingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompTypeExpression parentExpr = new TypeExprOfComponent(parent);
    comp.setParent(parentExpr);

    PortSymbol port = addOutgoingPortTo(parent, "myPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getOutgoingPort("myPort", true);

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  void getPortsWithDirectionShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();


    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getPorts(true);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllIncomingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getAllIncomingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllOutgoingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getOutgoingPorts(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllPortsWithDirectionShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getAllPorts(true);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getTimingShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    Timing timing = Timing.TIMED;
    comp.timing = timing;

    // When
    Optional<Timing> calculatedTiming = surrogate.getTiming();

    // Then
    Assertions.assertTrue(calculatedTiming.isPresent(), "No Timing");
    Assertions.assertSame(timing, calculatedTiming.get());
  }

  @Test
  void getAllPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getAllPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getSubComponentsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentInstanceSymbol sub = addSubComponentTo(comp, "sub");

    // When
    List<ComponentInstanceSymbol> subs = surrogate.getSubComponents();

    // Then
    Assertions.assertArrayEquals(new ComponentInstanceSymbol[] {sub}, subs.toArray());
  }

  @Test
  void getSubComponentsByVisibilityShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentInstanceSymbol sub = addSubComponentTo(comp, "sub");

    // When
    List<ComponentInstanceSymbol> subs = surrogate.getSubComponents(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new ComponentInstanceSymbol[] {sub}, subs.toArray());
  }

  @Test
  void getSubComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentInstanceSymbol sub = addSubComponentTo(comp, "mySub");

    // When
    Optional<ComponentInstanceSymbol> subOpt = surrogate.getSubComponent("mySub");

    // Then
    Assertions.assertTrue(subOpt.isPresent(), "Sub component is not present");
    Assertions.assertSame(sub, subOpt.get());
  }


  @Test
  void isDecomposedShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addSubComponentTo(comp, "sub");

    // When
    boolean isDecomposed = surrogate.isDecomposed();

    // Then
    Assertions.assertTrue(isDecomposed, "Should be decomposed");
  }

  @Test
  void isAtomicShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addSubComponentTo(comp, "sub");

    // When
    boolean isAtomic = surrogate.isAtomic();

    // Then
    Assertions.assertFalse(isAtomic, "Should not be atomic");
  }

  @Test
  void getBehaviorShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ASTArcBehaviorElement behavior = Mockito.mock(ASTArcBehaviorElement.class);

    ASTComponentType astComp = ArcBasisMill.componentTypeBuilder()
      .setName("Comp")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(behavior)
        .build())
      .build();
    comp.setAstNode(astComp);

    // When
    Optional<ASTArcBehaviorElement> behaviorOpt = surrogate.getBehavior();

    // Then
    Assertions.assertTrue(behaviorOpt.isPresent(), "Port is not present");
    Assertions.assertSame(behavior, behaviorOpt.get());
  }


  /**
   * Adds an incoming port symbol to the spanned scope of the component. The port type is only mocked.
   * @return the created Port
   */
  protected PortSymbol addIncomingPortTo(@NotNull ComponentTypeSymbol compType, @NotNull String portName) {
    return addPortTo(compType, portName, true);
  }

  /**
   * Adds an outgoing port symbol to the spanned scope of the component. The port type is only mocked.
   * @return the created Port
   */
  protected PortSymbol addOutgoingPortTo(@NotNull ComponentTypeSymbol compType, @NotNull String portName) {
    return addPortTo(compType, portName, false);
  }

  /**
   * Adds a port symbol to the spanned scope of the component. The port type is only mocked.
   * @return the created Port
   */
  protected PortSymbol addPortTo(@NotNull ComponentTypeSymbol compType, @NotNull String portName, boolean isIncoming) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(portName);

    PortSymbol port = ArcBasisMill
      .portSymbolBuilder()
      .setName(portName)
      .setIncoming(isIncoming)
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(port);

    return port;
  }

  /**
   * Adds a sub component to the spanned scope of the component. The sub component's type type is only mocked.
   * @return the created sub component
   */
  protected ComponentInstanceSymbol addSubComponentTo(@NotNull ComponentTypeSymbol compType, @NotNull String subCompName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(subCompName);

    ComponentInstanceSymbol subComp = ArcBasisMill
      .componentInstanceSymbolBuilder()
      .setName(subCompName)
      .setType(Mockito.mock(CompTypeExpression.class))
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(subComp);

    return subComp;
  }

  /**
   * Adds an inner component type symbol to the spanned scope of the component.
   * @return the created inner component type
   */
  protected ComponentTypeSymbol addInnerComponentTypeTo(@NotNull ComponentTypeSymbol compType, @NotNull String innerCompTypeName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(innerCompTypeName);

    ComponentTypeSymbol innerComp =  ArcBasisMill
      .componentTypeSymbolBuilder()
      .setName(innerCompTypeName)
      .setSpannedScope(ArcBasisMill.scope())
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(innerComp);

    return innerComp;
  }

  /**
   * Adds a field to the spanned scope of the component. The field type is only mocked.
   * @return the created field.
   */
  protected VariableSymbol addFieldTo(@NotNull ComponentTypeSymbol compType, @NotNull String fieldName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(fieldName);

    VariableSymbol field = ArcBasisMill
      .variableSymbolBuilder()
      .setName(fieldName)
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(field);

    return field;
  }

  /**
   * Adds a parameter to the spanned scope of the component. The parameter type is only mocked.
   * @return the created parameter.
   */
  protected VariableSymbol addParameterTo(@NotNull ComponentTypeSymbol compType, @NotNull String paramName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(paramName);

    VariableSymbol param = ArcBasisMill
      .variableSymbolBuilder()
      .setName(paramName)
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(param);
    compType.addParameter(param);

    return param;
  }

  /**
   * Adds a type parameter to the component.
   * @return the created type parameter
   */
  protected TypeVarSymbol addTypeParameterTo(@NotNull ComponentTypeSymbol compType,
                                             @NotNull String typeParamName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(typeParamName);

    TypeVarSymbol typeVar = ArcBasisMill
      .typeVarSymbolBuilder()
      .setName(typeParamName)
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(typeVar);

    return typeVar;
  }

  protected static Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> createCompWithSurrogate(
    @NotNull String compName) {
    Preconditions.checkNotNull(compName);

    IArcBasisScope commonScope = ArcBasisMill.scope();

    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    commonScope.add(symbol);

    ComponentTypeSymbolSurrogate surrogate = ArcBasisMill.componentTypeSymbolSurrogateBuilder()
      .setName(compName)
      .setEnclosingScope(commonScope)
      .build();

    return Map.entry(symbol, surrogate);
  }
}
