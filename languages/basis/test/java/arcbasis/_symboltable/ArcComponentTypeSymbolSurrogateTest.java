/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTArcComponentType;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ArcComponentTypeSymbolSurrogateTest extends ArcBasisTestBase {
  
  @Test
  public void setSpannedScopeShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    IArcBasisScope scopeToSet = ArcBasisMill.scope();

    // When
    surrogate.setSpannedScope(scopeToSet);

    // Then
    Assertions.assertSame(scopeToSet, comp.getSpannedScope());
  }

  @Test
  public void getSpannedScopeShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    // When
    IArcBasisScope scope = surrogate.getSpannedScope();

    // Then
    Assertions.assertSame(comp.getSpannedScope(), scope);
  }

  @Test
  public void getInnerComponentsShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol inner = addInnerComponentTypeTo(comp, "Inner");

    // When
    List<ArcComponentTypeSymbol> inners = surrogate.getInnerComponents();

    // Then
    Assertions.assertArrayEquals(new ArcComponentTypeSymbol[] {inner}, inners.toArray());
  }

  @Test
  public void getInnerComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol inner = addInnerComponentTypeTo(comp, "Inner");

    // When
    Optional<ArcComponentTypeSymbol> innerOpt = surrogate.getInnerComponent("Inner");

    // Then
    Assertions.assertTrue(innerOpt.isPresent(), "Inner comp is not present");
    Assertions.assertSame(inner, innerOpt.get());
  }

  @Test
  public void getInnerComponentsWithAccessModifierShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol inner = addInnerComponentTypeTo(comp, "Inner");

    // When
    List<ArcComponentTypeSymbol> inners = surrogate.getInnerComponents(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new ArcComponentTypeSymbol[] {inner}, inners.toArray());
  }

  @Test
  public void getPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  public void getPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

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
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol parent = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    comp.setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parent)));

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
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getIncomingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  public void getIncomingPortsByAccessModifierShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getIncomingPorts(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  public void getIncomingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

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
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    comp.setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parent)));

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
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol outer = createCompWithSurrogate("Outer").getKey();
    comp.setOuterComponent(outer);

    // When
    boolean isInner = surrogate.isInnerComponent();

    // Then
    Assertions.assertTrue(isInner);
  }

  
  @Test
  public void getOuterComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol outer = createCompWithSurrogate("Outer").getKey();
    comp.setOuterComponent(outer);

    // When
    Optional<ArcComponentTypeSymbol> outerOpt = surrogate.getOuterComponent();

    // Then
    Assertions.assertTrue(outerOpt.isPresent(), "No outer component");
    Assertions.assertSame(outer, outerOpt.get());
  }

  
  @Test
  void setOuterComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol outer = createCompWithSurrogate("Outer").getKey();

    // When
    surrogate.setOuterComponent(outer);

    // Then
    Assertions.assertTrue(comp.getOuterComponent().isPresent(), "No outer component present");
    Assertions.assertSame(outer, comp.getOuterComponent().get());
  }

  
  @Test
  void isPresentParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    comp.setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parent)));

    // When
    boolean parentIsPresent = !surrogate.isEmptySuperComponents();

    // Then
    Assertions.assertTrue(parentIsPresent, "No parent present");
  }

  
  @Test
  void getParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompTypeExpression parentExpr = new TypeExprOfComponent(parent);
    comp.setSuperComponentsList(Collections.singletonList(parentExpr));

    // When
    CompKindExpression parentCalculated = surrogate.getSuperComponents(0);

    // Then
    Assertions.assertSame(parentExpr, parentCalculated);
  }

  
  @Test
  void setParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompTypeExpression parentExpr = new TypeExprOfComponent(parent);

    // When
    surrogate.setSuperComponentsList(Collections.singletonList(parentExpr));

    // Then
    Assertions.assertSame(parentExpr, comp.getSuperComponents(0));
  }

  @Test
  void isPresentRefinementShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair = createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol abstraction = createCompWithSurrogate("Abstraction").getKey();
    CompKindExpression abstractionExpr = new TypeExprOfComponent(abstraction);
    comp.setRefinementsList(Collections.singletonList(abstractionExpr));

    // When
    boolean refinedCompIsPresent = !surrogate.isEmptyRefinements();

    // Then
    Assertions.assertTrue(refinedCompIsPresent, "No refined component present");
  }


  @Test
  void getRefinementShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair = createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol abstraction = createCompWithSurrogate("Abstraction").getKey();
    CompTypeExpression abstractionExpr = new TypeExprOfComponent(abstraction);
    comp.setRefinementsList(Collections.singletonList(abstractionExpr));

    // When
    CompKindExpression parentCalculated = surrogate.getRefinements(0);

    // Then
    Assertions.assertSame(abstractionExpr, parentCalculated);
  }


  @Test
  void setRefinementShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair = createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol abstraction = createCompWithSurrogate("Abstraction").getKey();
    CompTypeExpression abstractionExpr = new TypeExprOfComponent(abstraction);

    // When
    surrogate.setRefinementsList(Collections.singletonList(abstractionExpr));

    // Then
    Assertions.assertSame(abstractionExpr, comp.getRefinements(0));
  }
  
  @Test
  void getParameterListShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = addParameterTo(comp, "myParam");

    // When
    List<VariableSymbol> params = surrogate.getParameterList();

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, params.toArray());
  }

  @Test
  void getParameterShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

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
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = ArcBasisMill
      .variableSymbolBuilder()
      .setName("param")
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();

    // When
    surrogate.getSpannedScope().add(param);
    surrogate.addParameter(param);

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, comp.getParameterList().toArray());
  }


  @Test
  void addParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = ArcBasisMill
      .variableSymbolBuilder()
      .setName("param")
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();

    // When
    surrogate.getSpannedScope().add(param);
    surrogate.addParameters(Collections.singletonList(param));

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, comp.getParameterList().toArray());
  }

  @Test
  void hasParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addParameterTo(comp, "param");

    // When
    boolean hasParameters = surrogate.hasParameters();

    // Then
    Assertions.assertTrue(hasParameters, "No parameters found");
  }

  @Test
  void getTypeParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    TypeVarSymbol typeParam = addTypeParameterTo(comp, "T");

    // When
    List<TypeVarSymbol> typeParams = surrogate.getTypeParameters();

    // Then
    Assertions.assertArrayEquals(new TypeVarSymbol[] {typeParam}, typeParams.toArray());
  }

  @Test
  void hasTypeParameterShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addTypeParameterTo(comp, "T");

    // When
    boolean hasTypeParams = surrogate.hasTypeParameter();

    // then
    Assertions.assertTrue(hasTypeParams, "No type parameters found");
  }

  @Test
  void getFieldsShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol field = addFieldTo(comp, "myField");

    // When
    List<VariableSymbol> fields = surrogate.getFields();

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {field}, fields.toArray());
  }

  @Test
  void getFieldByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

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
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getOutgoingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }
  @Test
  void getOutgoingPortsByAccessModifierShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getOutgoingPorts(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getOutgoingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

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
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ArcComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompTypeExpression parentExpr = new TypeExprOfComponent(parent);
    comp.setSuperComponentsList(Collections.singletonList(parentExpr));

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
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();


    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getPorts(true, false);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllIncomingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    Set<PortSymbol> ports = surrogate.getAllIncomingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllOutgoingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getOutgoingPorts(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllPortsWithDirectionShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    Set<PortSymbol> ports = surrogate.getAllPorts(true, false);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    Set<PortSymbol> ports = surrogate.getAllPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getSubComponentsShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    SubcomponentSymbol sub = addSubComponentTo(comp, "sub");

    // When
    List<SubcomponentSymbol> subs = surrogate.getSubcomponents();

    // Then
    Assertions.assertArrayEquals(new SubcomponentSymbol[] {sub}, subs.toArray());
  }

  @Test
  void getSubComponentsByVisibilityShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    SubcomponentSymbol sub = addSubComponentTo(comp, "sub");

    // When
    List<SubcomponentSymbol> subs = surrogate.getSubcomponents(BasicAccessModifier.PUBLIC);

    // Then
    Assertions.assertArrayEquals(new SubcomponentSymbol[] {sub}, subs.toArray());
  }

  @Test
  void getSubComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    SubcomponentSymbol sub = addSubComponentTo(comp, "mySub");

    // When
    Optional<SubcomponentSymbol> subOpt = surrogate.getSubcomponents("mySub");

    // Then
    Assertions.assertTrue(subOpt.isPresent(), "Sub component is not present");
    Assertions.assertSame(sub, subOpt.get());
  }


  @Test
  void isDecomposedShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addSubComponentTo(comp, "sub");

    // When
    boolean isDecomposed = surrogate.isDecomposed();

    // Then
    Assertions.assertTrue(isDecomposed, "Should be decomposed");
  }

  @Test
  void isAtomicShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addSubComponentTo(comp, "sub");

    // When
    boolean isAtomic = surrogate.isAtomic();

    // Then
    Assertions.assertFalse(isAtomic, "Should not be atomic");
  }

  @Test
  void getBehaviorShouldSkipSurrogate() {
    // Given
    Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ArcComponentTypeSymbol comp = pair.getKey();
    ArcComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ASTArcBehaviorElement behavior = Mockito.mock(ASTArcBehaviorElement.class);

    ASTArcComponentType astComp = ArcBasisMill.arcComponentTypeBuilder()
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
  protected PortSymbol addIncomingPortTo(@NotNull ArcComponentTypeSymbol compType, @NotNull String portName) {
    return addPortTo(compType, portName, true);
  }

  /**
   * Adds an outgoing port symbol to the spanned scope of the component. The port type is only mocked.
   * @return the created Port
   */
  protected PortSymbol addOutgoingPortTo(@NotNull ArcComponentTypeSymbol compType, @NotNull String portName) {
    return addPortTo(compType, portName, false);
  }

  /**
   * Adds a port symbol to the spanned scope of the component. The port type is only mocked.
   * @return the created Port
   */
  protected PortSymbol addPortTo(@NotNull ArcComponentTypeSymbol compType, @NotNull String portName, boolean isIncoming) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(portName);

    PortSymbol port = ArcBasisMill
      .portSymbolBuilder()
      .setName(portName)
      .setIncoming(isIncoming)
      .setOutgoing(!isIncoming)
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
  protected SubcomponentSymbol addSubComponentTo(@NotNull ArcComponentTypeSymbol compType, @NotNull String subCompName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(subCompName);

    SubcomponentSymbol subComp = ArcBasisMill
      .subcomponentSymbolBuilder()
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
  protected ArcComponentTypeSymbol addInnerComponentTypeTo(@NotNull ArcComponentTypeSymbol compType, @NotNull String innerCompTypeName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(innerCompTypeName);

    ArcComponentTypeSymbol innerComp =  ArcBasisMill
      .arcComponentTypeSymbolBuilder()
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
  protected VariableSymbol addFieldTo(@NotNull ArcComponentTypeSymbol compType, @NotNull String fieldName) {
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
  protected VariableSymbol addParameterTo(@NotNull ArcComponentTypeSymbol compType, @NotNull String paramName) {
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
  protected TypeVarSymbol addTypeParameterTo(@NotNull ArcComponentTypeSymbol compType,
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

  protected static Map.Entry<ArcComponentTypeSymbol, ArcComponentTypeSymbolSurrogate> createCompWithSurrogate(
    @NotNull String compName) {
    Preconditions.checkNotNull(compName);

    IArcBasisScope commonScope = ArcBasisMill.scope();

    ArcComponentTypeSymbol symbol = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    commonScope.add(symbol);

    ArcComponentTypeSymbolSurrogate surrogate = ArcBasisMill.arcComponentTypeSymbolSurrogateBuilder()
      .setName(compName)
      .setEnclosingScope(commonScope)
      .build();

    return Map.entry(symbol, surrogate);
  }
}
