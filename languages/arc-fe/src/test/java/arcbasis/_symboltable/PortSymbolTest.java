/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDeclaration;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods of {@link PortSymbol}.
 */
public class PortSymbolTest extends AbstractTest {

  @Test
  public void shouldFindComponentType() {
    ComponentTypeSymbol compSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(ArcBasisMill.scope()).build();
    PortSymbol portSymbol = ArcBasisMill.portSymbolBuilder().setName("p1")
      .setIncoming(true).setType(mock(SymTypeExpression.class)).build();
    compSymbol.getSpannedScope().add(portSymbol);
    Assertions.assertTrue(portSymbol.getComponent().isPresent());
  }

  @Test
  public void shouldNotFindComponentType() {
    PortSymbol portSymbol = ArcBasisMill.portSymbolBuilder().setName("p1")
      .setIncoming(true).setType(mock(SymTypeExpression.class)).build();
    Assertions.assertFalse(portSymbol.getComponent().isPresent());
  }

  @Test
  public void shouldReturnResolvedType() {
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    ArcBasisSymbolTableCompleterDelegator completer = ArcBasisMill.symbolTableCompleterDelegator();
    ASTPortDeclaration ports = ArcBasisMill.portDeclarationBuilder()
      .setPortList("p")
      .setIncoming(true)
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .setPartsList(Collections.singletonList("Integer")).build())
        .build())
      .build();
    ASTComponentInterface portInterface = ArcBasisMill.componentInterfaceBuilder().addPortDeclaration(ports).build();
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder()
      .setName("CompA").setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(portInterface)
        .build())
      .build();

    ArcBasisMill.globalScope().add(ArcBasisMill.typeSymbolBuilder().setName("Integer").build());
    symTab.createFromAST(ast);
    completer.createFromAST(ast);

    Assertions.assertFalse(ast.getSpannedScope().getPortSymbols().get("p").get(0).getTypeInfo() instanceof TypeSymbolSurrogate);
  }
}