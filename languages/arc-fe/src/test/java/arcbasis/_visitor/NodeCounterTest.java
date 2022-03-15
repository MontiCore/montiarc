/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDirection;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

public class NodeCounterTest {

  @BeforeAll
  public static void init() {
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    LogStub.init();
  }

  @ParameterizedTest
  @MethodSource("nodeAndExpectedSymbolCountProvider")
  public void shouldDetectMissingEnclosingScopeOfSymbol(@NotNull ASTArcBasisNode node, int expNumOfSymbols,
    int expNumOfScopeSpanningSymbols, int expNumOfTypeSymbols, int expNumOfVarSymbols, int expNumOfTypeVarSymbols,
    int expNumOfFunctionSymbols, int expNumOfDiagramSymbols, int expNumOfOOSymbols, int expNumOfFieldSymbols,
    int expNumOfMethodSymbols, int expNumOfPortSymbols, int expNumOfComponentInstanceSymbols,
    int expNumOfComponentTypeSymbols) {

    Preconditions.checkNotNull(node);

    // Given
    NodeCounter delegator = new NodeCounter();

    // When
    delegator.countNodes(node);

    // Then
    Assertions.assertTrue(Log.getFindings().isEmpty(),
      "Findings: " + Log.getFindings());
    Assertions.assertEquals(expNumOfSymbols, delegator.getNumSymbols(),
      "The number of visited symbols does not match the expected number of symbols.");
    Assertions.assertEquals(expNumOfScopeSpanningSymbols, delegator.getNumScopeSpanningSymbols(),
      "The number of visited scope spanning symbols does not match the expected number of scope spanning symbols.");
    Assertions.assertEquals(expNumOfTypeSymbols, delegator.getNumTypeSymbols(),
      "The number of visited type symbols does not match the expected number of type symbols.");
    Assertions.assertEquals(expNumOfVarSymbols, delegator.getNumVarSymbols(),
      "The number of visited variable symbols does not match the expected number of variable symbols.");
    Assertions.assertEquals(expNumOfTypeVarSymbols, delegator.getNumTypeVarSymbols(),
      "The number of visited type variable symbols does not match the expected number of type variable symbols.");
    Assertions.assertEquals(expNumOfFunctionSymbols, delegator.getNumFunctionSymbols(),
      "The number of visited function symbols does not match the expected number of function symbols.");
    Assertions.assertEquals(expNumOfDiagramSymbols, delegator.getNumDiagramSymbols(),
      "The number of diagram symbols does not match the expected number of diagram symbols.");
    Assertions.assertEquals(expNumOfOOSymbols, delegator.getNumOOTypeSymbols(),
      "The number of object type symbols does not match the expected number of object type symbols.");
    Assertions.assertEquals(expNumOfFieldSymbols, delegator.getNumFieldSymbols(),
      "The number of visited field symbols does not match the expected number of field symbols.");
    Assertions.assertEquals(expNumOfMethodSymbols, delegator.getNumMethodSymbols(),
      "The number of method symbols does not match the expected number of method symbols.");
    Assertions.assertEquals(expNumOfPortSymbols, delegator.getNumPortSymbols(),
      "The number of visited port symbols does not match the expected number of port symbols.");
    Assertions.assertEquals(expNumOfComponentInstanceSymbols, delegator.getNumComponentSymbols(),
      "The number of visited component symbols does not match the expected number of component symbols.");
    Assertions.assertEquals(expNumOfComponentTypeSymbols, delegator.getNumComponentTypeSymbols(),
      "The number of visited component type symbols does not match the expected number of component type symbols.");
  }

  protected static Stream<Arguments> nodeAndExpectedSymbolCountProvider() {

    TypeSymbol type1 = ArcBasisMill.typeSymbolBuilder()
      .setName("type")
      .build();
    OOTypeSymbol ooType1 = ArcBasisMill.oOTypeSymbolBuilder()
      .setName("OOType")
      .build();
    FunctionSymbol fun1 = ArcBasisMill.functionSymbolBuilder()
      .setName("fun")
      .build();
    MethodSymbol method1 = ArcBasisMill.methodSymbolBuilder()
      .setName("method")
      .build();
    FieldSymbol field1 = ArcBasisMill.fieldSymbolBuilder()
      .setName("field")
      .build();
    VariableSymbol var1 = ArcBasisMill.variableSymbolBuilder()
      .setName("var1")
      .build();
    DiagramSymbol diagram1 = ArcBasisMill.diagramSymbolBuilder()
      .setName("D")
      .build();
    TypeVarSymbol typeVarSymbol = ArcBasisMill.typeVarSymbolBuilder()
      .setName("T")
      .build();
    PortSymbol p1 = ArcBasisMill.portSymbolBuilder()
      .setName("port1")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setDirection(Mockito.mock(ASTPortDirection.class))
      .build();
    ComponentInstanceSymbol c1 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("comp1")
      .setType(Mockito.mock(CompTypeExpression.class))
      .build();
    ComponentTypeSymbol cT1 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp1")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    IArcBasisScope scope1 = ArcBasisMill.scope();
    ASTComponentType ast1 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp1")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast1.setSpannedScope(scope1);
    scope1.add(type1);

    IArcBasisScope scope2 = ArcBasisMill.scope();
    ASTComponentType ast2 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp2")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast2.setSpannedScope(scope2);
    scope2.add(ooType1);

    IArcBasisScope scope3 = ArcBasisMill.scope();
    ASTComponentType ast3 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp3")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast3.setSpannedScope(scope3);
    scope3.add(type1);
    scope3.add(ooType1);

    IArcBasisScope scope4 = ArcBasisMill.scope();
    ASTComponentType ast4 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp4")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast4.setSpannedScope(scope4);
    scope4.add(fun1);

    IArcBasisScope scope5 = ArcBasisMill.scope();
    ASTComponentType ast5 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp5")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast5.setSpannedScope(scope5);
    scope5.add(method1);

    IArcBasisScope scope6 = ArcBasisMill.scope();
    ASTComponentType ast6 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp6")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast6.setSpannedScope(scope6);
    scope6.add(fun1);
    scope6.add(method1);

    IArcBasisScope scope7 = ArcBasisMill.scope();
    ASTComponentType ast7 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp7")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast7.setSpannedScope(scope7);
    scope7.add(var1);

    IArcBasisScope scope8 = ArcBasisMill.scope();
    ASTComponentType ast8 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp8")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast8.setSpannedScope(scope8);
    scope8.add(field1);

    IArcBasisScope scope9 = ArcBasisMill.scope();
    ASTComponentType ast9 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp9")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast9.setSpannedScope(scope9);
    scope9.add(var1);
    scope9.add(field1);

    IArcBasisScope scope10 = ArcBasisMill.scope();
    ASTComponentType ast10 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp10")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast10.setSpannedScope(scope10);
    scope10.add(diagram1);

    IArcBasisScope scope11 = ArcBasisMill.scope();
    ASTComponentType ast11 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp11")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast11.setSpannedScope(scope11);
    scope11.add(typeVarSymbol);

    IArcBasisScope scope12 = ArcBasisMill.scope();
    ASTComponentType ast12 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp12")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast12.setSpannedScope(scope12);
    scope12.add(p1);

    IArcBasisScope scope13 = ArcBasisMill.scope();
    ASTComponentType ast13 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp13")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast13.setSpannedScope(scope13);
    scope13.add(c1);

    IArcBasisScope scope14 = ArcBasisMill.scope();
    ASTComponentType ast14 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp14")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast14.setSpannedScope(scope14);
    scope14.add(cT1);

    IArcBasisScope scope15 = ArcBasisMill.scope();
    ASTComponentType ast15 = ArcBasisMill.componentTypeBuilder()
      .setName("Comp15")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast15.setSpannedScope(scope15);
    scope15.add(type1);
    scope15.add(ooType1);
    scope15.add(fun1);
    scope15.add(method1);
    scope15.add(var1);
    scope15.add(field1);
    scope15.add(diagram1);
    scope15.add(typeVarSymbol);
    scope15.add(p1);
    scope15.add(c1);
    scope15.add(cT1);

    return Stream.of(
      Arguments.of(ast1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      Arguments.of(ast2, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
      Arguments.of(ast3, 2, 2, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
      Arguments.of(ast4, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0),
      Arguments.of(ast5, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0),
      Arguments.of(ast6, 2, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0),
      Arguments.of(ast7, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      Arguments.of(ast8, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0),
      Arguments.of(ast9, 2, 0, 0, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0),
      Arguments.of(ast10, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
      Arguments.of(ast11, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
      Arguments.of(ast12, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0),
      Arguments.of(ast13, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
      Arguments.of(ast14, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
      Arguments.of(ast15, 11, 6, 3, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1)
    );
  }
}
