/* (c) https://github.com/MontiCore/monticore */
package genericarc;

import arcbasis.ArcBasisAbstractTest;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import genericarc._symboltable.IGenericArcArtifactScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;

public class GenericArcAbstractTest extends ArcBasisAbstractTest {

  @BeforeEach
  @Override
  public void setUp() {
    GenericArcMill.globalScope().clear();
    GenericArcMill.reset();
    GenericArcMill.init();
    GenericArcAbstractTest.addBasicTypes2Scope();
  }

  /**
   * Wraps the given component type symbol into an artifact scope with the given package name.
   * Note that this artifact scope is not yet registered to be part of the global scope!
   */
  protected static IGenericArcArtifactScope wrapInArtifactScope(@NotNull String packageName,
                                                              @NotNull ComponentTypeSymbol compSymToWrap) {
    Preconditions.checkNotNull(packageName);
    Preconditions.checkNotNull(compSymToWrap);

    IGenericArcArtifactScope scope = GenericArcMill.artifactScope();
    scope.setPackageName(packageName);

    SymbolService.link(scope, compSymToWrap);

    return scope;
  }

  /**
   * Wraps the given type symbol into an artifact scope with the given package name.
   * Note that this artifact scope is not yet registered to be part of the global scope!
   */
  protected static IGenericArcArtifactScope wrapInArtifactScope(@NotNull String packageName,
                                                                @NotNull OOTypeSymbol typeSymToWrap) {
    Preconditions.checkNotNull(packageName);
    Preconditions.checkNotNull(typeSymToWrap);

    IGenericArcArtifactScope scope = GenericArcMill.artifactScope();
    scope.setPackageName(packageName);

    SymbolService.link(scope, typeSymToWrap);

    return scope;
  }

  protected void addTypeParamsToComp(@NotNull ComponentTypeSymbol comp, @NotNull String... typeParamNames) {
    for (String typeParamName : typeParamNames) {
      TypeVarSymbol typeParam = GenericArcMill
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

    return GenericArcMill.oOTypeSymbolBuilder()
      .setName(name)
      .setSpannedScope(GenericArcMill.scope())
      .build();
  }
}
