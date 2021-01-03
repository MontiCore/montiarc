/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
<<<<<<< HEAD
=======
import arcbasis._ast.ASTPortDeclaration;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

<<<<<<< HEAD
=======
import static org.mockito.Mockito.mock;

>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
/**
 * Holds tests for the handwritten methods of {@link PortSymbol}.
 */
public class PortSymbolTest extends AbstractTest {

  @Test
  public void shouldFindComponentType() {
    ComponentTypeSymbol compSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(new ArcBasisScope()).build();
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
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    ASTPortDeclaration ports = ArcBasisMill.portDeclarationBuilder()
      .setPortList("p")
      .setIncoming(true)
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .setPartsList(Collections.singletonList("Integer")).build())
        .build())
      .build();
    symTab.handle(ports);
    scope.add(ArcBasisMill.typeSymbolBuilder().setName("Integer").build());
    Assertions.assertFalse(scope.getPortSymbols().get("p").get(0).getTypeInfo() instanceof TypeSymbolSurrogate);
  }
}