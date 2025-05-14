/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDeclaration;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods of {@link PortSymbol}.
 */
public class PortSymbolTest extends ArcBasisTestBase {

  @Test
  public void shouldReturnResolvedType() {
    ArcBasisScopesGenitorDelegator scopesGen = ArcBasisMill.scopesGenitorDelegator();
    ArcBasisScopesGenitorP2Delegator scopesGenP2 = ArcBasisMill.scopesGenitorP2Delegator();
    ArcBasisScopesGenitorP3Delegator scopesGenP3 = ArcBasisMill.scopesGenitorP3Delegator();

    ArcBasisMill.globalScope().add(ArcBasisMill.typeSymbolBuilder()
      .setName("Integer").setSpannedScope(ArcBasisMill.scope()).build());

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

    scopesGen.createFromAST(ast);
    scopesGenP2.createFromAST(ast);
    scopesGenP3.createFromAST(ast);


    Assertions.assertFalse(ast.getSpannedScope().getPortSymbols().get("p").get(0).getTypeInfo() instanceof TypeSymbolSurrogate);
  }
}