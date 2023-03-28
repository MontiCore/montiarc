/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc.util.AbstractTest;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

public abstract class MontiArcAbstractTest extends AbstractTest {

  @BeforeEach
  public void setUp() {
    Log.clearFindings();
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
    MontiArcMill.init();
    addBasicTypes2Scope();
  }

  protected static ASTMCQualifiedName createQualifiedName(@NotNull String... parts) {
    assert parts != null && !Arrays.asList(parts).contains(null);
    return ArcBasisMill.mCQualifiedNameBuilder().setPartsList(Arrays.asList(parts)).build();
  }

  protected static ASTMCQualifiedType createQualifiedType(@NotNull String... parts) {
    assert parts != null && !Arrays.asList(parts).contains(null);
    return ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(createQualifiedName(parts)).build();
  }

  /**
   * Creates an empty ASTComponentType that is linked to a ComponentTypeSymbol with the same name. Beware that the
   * ComponentTypeSymbol is not part of any scope.
   */
  protected static ASTComponentType createComponentTypeWithSymbol(@NotNull String name4Comp) {
    Preconditions.checkNotNull(name4Comp);

    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName(name4Comp)
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol sym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(name4Comp)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    comp.setSymbol(sym);
    comp.setSpannedScope(sym.getSpannedScope());
    sym.setAstNode(comp);

    return comp;
  }

  /**
   * Wraps the given component type symbol into an artifact scope with the given package name.
   * Note that this artifact scope is not yet registered to be part of the global scope!
   */
  protected static IMontiArcArtifactScope wrapInArtifactScope(@NotNull String packageName,
                                                              @NotNull ComponentTypeSymbol compSymToWrap) {
    Preconditions.checkNotNull(packageName);
    Preconditions.checkNotNull(compSymToWrap);

    IMontiArcArtifactScope scope = MontiArcMill.artifactScope();
    scope.setPackageName(packageName);

    SymbolService.link(scope, compSymToWrap);

    return scope;
  }

  /**
   * Wraps the given OOType into an artifact scope with the given package name.
   * Note that this artifact scope is not yet registered to be part of the global scope!
   */
  protected static IMontiArcArtifactScope wrapInArtifactScope(@NotNull String packageName,
                                                              @NotNull OOTypeSymbol typeToWrap) {
    Preconditions.checkNotNull(packageName);
    Preconditions.checkNotNull(typeToWrap);

    IMontiArcArtifactScope scope = MontiArcMill.artifactScope();
    scope.setPackageName(packageName);

    SymbolService.link(scope, typeToWrap);

    return scope;
  }

  protected void addTypeParamsToComp(@NotNull ComponentTypeSymbol comp, @NotNull String... typeParamNames) {
    for (String typeParamName : typeParamNames) {
      TypeVarSymbol typeParam = MontiArcMill
        .typeVarSymbolBuilder()
        .setName(typeParamName)
        .build();

      comp.getSpannedScope().add(typeParam);
    }
  }

  /**
   * Creates an OOTypeSymbol with the given name. Beware that the symbol is not part of any scope.
   */
  protected static OOTypeSymbol createOOTypeSymbol(@NotNull String name) {
    Preconditions.checkNotNull(name);

    return MontiArcMill.oOTypeSymbolBuilder()
      .setName(name)
      .setSpannedScope(MontiArcMill.scope())
      .build();
  }
}