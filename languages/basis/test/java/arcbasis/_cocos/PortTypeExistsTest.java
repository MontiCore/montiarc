/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.IArcBasisArtifactScope;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * Holds tests for the handwritten methods of {@link PortTypeExists}.
 */
public class PortTypeExistsTest extends AbstractTest {

  @Test
  public void shouldFindPrimitiveType() {
    // Given
    ASTMCPrimitiveType type =
      ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
    ASTComponentType comp = createCompWithPort("CompA", "p1", type, true);
    ASTPortDeclaration portDec = comp.getPortDeclarations().get(0);
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    PortTypeExists coco = new PortTypeExists();
    coco.check(portDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindUnqualifiedType() {
    // Given
    createTypeSymbolInGlobalScope("MyType");
    ASTMCQualifiedType type = createQualifiedType("MyType");
    ASTComponentType compWithInPort = createCompWithPort("CompA", "i", type, true);
    ASTComponentType compWithOutPort = createCompWithPort("CompB", "o", type, false);
    ASTPortDeclaration inPortDec = compWithInPort.getPortDeclarations().get(0);
    ASTPortDeclaration outPortDec = compWithInPort.getPortDeclarations().get(0);

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(compWithInPort);
    symTab.createFromAST(compWithOutPort);

    // When
    PortTypeExists coco = new PortTypeExists();
    coco.check(inPortDec);
    coco.check(outPortDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldNotFindUnqualifiedType() {
    // Given
    createTypeSymbolInScope("MyType", "package.name");
    ASTMCQualifiedType type = createQualifiedType("MyType");
    ASTComponentType comp = createCompWithPort("CompA", "p1", type, true);
    ASTPortDeclaration portDec = comp.getPortDeclarations().get(0);
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    PortTypeExists coco = new PortTypeExists();
    coco.check(portDec);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE);
  }

  @Test
  public void shouldFindQualifiedType() {
    // Given
    createTypeSymbolInScope("MyType", "foopackage");
    ASTMCQualifiedType type = createQualifiedType("foopackage.MyType");
    ASTComponentType comp = createCompWithPort("CompA", "p1", type, true);
    ASTPortDeclaration portDec = comp.getPortDeclarations().get(0);
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    PortTypeExists coco = new PortTypeExists();
    coco.check(portDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindTypeByImport() {
    // Given
    createTypeSymbolInScope("MyType", "foopack");
    ASTMCQualifiedType type = createQualifiedType("MyType");
    ASTComponentType comp = createCompWithPort("CompA", "p1", type, true);
    ASTPortDeclaration portDec = comp.getPortDeclarations().get(0);
    IArcBasisArtifactScope scopeWithImports = ArcBasisMill.artifactScope();
    scopeWithImports.setImportsList(Collections.singletonList(new ImportStatement("foopack", true)));
    scopeWithImports.setName("ju");

    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);
    portDec.getMCType().setEnclosingScope(scopeWithImports);
    ArcBasisMill.globalScope().addSubScope(scopeWithImports);

    // When
    PortTypeExists coco = new PortTypeExists();
    coco.check(portDec);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldNotFindQualifiedType() {
    // Given
    ASTMCQualifiedType type = createQualifiedType("unknown.Type");
    ASTComponentType comp = createCompWithPort("CompA", "p1", type, true);
    ASTPortDeclaration portDec = comp.getPortDeclarations().get(0);
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(comp);

    // When
    PortTypeExists coco = new PortTypeExists();
    coco.check(portDec);

    // Then
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE);
  }

  protected static ASTComponentType createCompWithPort(@NotNull String compName, @NotNull String portName,
                                                       @NotNull ASTMCType portType, boolean isIncomingPort) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(portName);
    Preconditions.checkNotNull(portType);

    ASTPortDeclaration portDecl = ArcBasisMill.portDeclarationBuilder()
      .setIncoming(isIncomingPort)
      .setMCType(portType)
      .addPort(portName)
      .build();

    ASTComponentInterface portInterface = ArcBasisMill.componentInterfaceBuilder()
      .addPortDeclaration(portDecl)
      .build();

    return ArcBasisMill.componentTypeBuilder()
      .setName(compName)
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(portInterface)
        .build())
      .build();
  }
}