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
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

/**
 * Holds tests for the handwritten methods of {@link PortTypeExists}.
 */
public class PortTypeExistsTest extends AbstractTest {

  @Test
  public void shouldFindPrimitiveType() {
    // Given
    ASTMCPrimitiveType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(1).build();
    ASTComponentType comp = createCompWithPort("CompA", "p1", type, true);
    ASTPortDeclaration portDec = getFirstPortDeclaration(comp).get();
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
    ASTComponentType compWithInPort = createCompWithPort("CompA", "p1", type, true);
    ASTComponentType compWithOutPort = createCompWithPort("CompB", "p1", type, false);
    ASTPortDeclaration inPortDec = getFirstPortDeclaration(compWithInPort).get();
    ASTPortDeclaration outPortDec = getFirstPortDeclaration(compWithOutPort).get();

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
    ASTPortDeclaration portDec = getFirstPortDeclaration(comp).get();
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
    ASTPortDeclaration portDec = getFirstPortDeclaration(comp).get();
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
    ASTPortDeclaration portDec = getFirstPortDeclaration(comp).get();
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
    ASTPortDeclaration portDec = getFirstPortDeclaration(comp).get();
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

    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName(compName)
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(portInterface)
        .build())
      .build();

    return comp;
  }

  protected static Optional<ASTPortDeclaration> getFirstPortDeclaration(@NotNull ASTComponentType comp) {
    Preconditions.checkNotNull(comp);

    return comp.getBody().getArcElementList().stream()
      .filter(arcE -> arcE instanceof ASTComponentInterface)
      .map(arcE -> (ASTComponentInterface) arcE)
      .flatMap(ports -> ports.getPortDeclarationList().stream())
      .findFirst();
  }
}