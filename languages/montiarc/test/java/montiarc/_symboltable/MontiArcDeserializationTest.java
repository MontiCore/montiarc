/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.Names;
import montiarc.MontiArcAbstractTest;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MontiArcDeserializationTest extends MontiArcAbstractTest {

  protected static final String PACKAGE = "symboltable";

  @ParameterizedTest
  @Order(0)
  @ValueSource(strings = {
    "Type",
    "TypeWithFunction",
    "TypeWithFunctions",
    "TypeWithSuperType",
    "TypeWithSuperTypes",
    "TypeWithVariable",
    "TypeWithVariables"
  })
  public void shouldLoadType(@NotNull String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());

    //Given
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final TypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getPackageName()).isEqualTo(PACKAGE),
      () -> assertThat(symbol.getName()).isEqualTo(name),
      () -> assertThat(symbol.getEnclosingScope()).isEqualTo(scope),
      () -> assertThat(symbol.getSpannedScope().getEnclosingScope()).isEqualTo(scope),
      () -> assertThat(symbol.getSpannedScope()).isEqualTo(scope.getSubScopes().get(0))
    );
  }

  @Test
  @Order(1)
  public void shouldLoadTypeAttributes() {
    // Given
    final String fn = "Type.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final TypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadTypeWithSuperType() {
    // Given
    final String fn = "TypeWithSuperType.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final TypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(1),
      () -> assertThat(symbol.getSuperTypesList().get(0).getTypeInfo().getName()).isEqualTo("symboltable.Type"),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadTypeWithSuperTypes() {
    // Given
    final String fn = "TypeWithSuperTypes.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final TypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(2),
      () -> assertThat(symbol.getSuperTypesList().get(0).getTypeInfo().getName()).isEqualTo("symboltable.Type"),
      () -> assertThat(symbol.getSuperTypesList().get(1).getTypeInfo().getName()).isEqualTo("symboltable.Type"),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadTypeWithFunction() {
    // Given
    final String fn = "TypeWithFunction.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final TypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(1),
      () -> assertThat(symbol.getFunctionList().get(0).getName()).isEqualTo("f"),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadTypeWithFunctions() {
    // Given
    final String fn = "TypeWithFunctions.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final TypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(2),
      () -> assertThat(symbol.getFunctionList().get(0).getName()).isEqualTo("g"),
      () -> assertThat(symbol.getFunctionList().get(1).getName()).isEqualTo("h"),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadTypeWithVariable() {
    // Given
    final String fn = "TypeWithVariable.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final TypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(1),
      () -> assertThat(symbol.getVariableList().get(0).getName()).isEqualTo("a")
    );
  }

  @Test
  @Order(2)
  public void shouldLoadTypeWithVariables() {
    // Given
    final String fn = "TypeWithVariables.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final TypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(2),
      () -> assertThat(symbol.getVariableList().get(0).getName()).isEqualTo("b"),
      () -> assertThat(symbol.getVariableList().get(1).getName()).isEqualTo("c")
    );
  }

  @ParameterizedTest
  @Order(0)
  @ValueSource(strings = {
    "OOType",
    "OOTypeWithField",
    "OOTypeWithFields",
    "OOTypeWithFunction",
    "OOTypeWithFunctions",
    "OOTypeWithMethod",
    "OOTypeWithMethods",
    "OOTypeWithSuperType",
    "OOTypeWithSuperTypes",
    "OOTypeWithVariable",
    "OOTypeWithVariables",
  })
  public void shouldLoadOOType(@NotNull String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());

    // Given
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getPackageName()).isEqualTo(PACKAGE),
      () -> assertThat(symbol.getName()).isEqualTo(name),
      () -> assertThat(symbol.getEnclosingScope()).isEqualTo(scope),
      () -> assertThat(symbol.getSpannedScope().getEnclosingScope()).isEqualTo(scope),
      () -> assertThat(symbol.getSpannedScope()).isEqualTo(scope.getSubScopes().get(0))
    );
  }

  @ParameterizedTest
  @Order(1)
  @CsvSource(value = {
    "0, false, false, false, false, false, false, false, false, false", // default all false
    "1, false, false, false, false, false, false, false, false, false", // explicitly all false
    //"2, true, true, true, true, true, true, true, true, true", // explicitly all true
    "3, true, false, false, false, false, false, true, false, false", // public class
    "4, true, false, false, true, false, false, true, false, false", // public abstract class
    "5, true, false, false, false, true, false, true, false, false", // public final class
    "6, false, true, false, false, false, false, true, false, false", // public interface
    "7, false, false, true, false, false, false, true, false, false", // public enum
  })
  public void shouldLoadOOTypeAttributes(int index,
                                         boolean isClass,
                                         boolean isInterface,
                                         boolean isEnum,
                                         boolean isAbstract,
                                         boolean isFinal,
                                         boolean isStatic,
                                         boolean isPublic,
                                         boolean isProtected,
                                         boolean isPrivate) {
    // Given
    final String fn = "OOType.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    SymTypeRelations.init();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(index));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0),
      () -> assertThat(symbol.isIsClass()).isEqualTo(isClass),
      () -> assertThat(symbol.isIsInterface()).isEqualTo(isInterface),
      () -> assertThat(symbol.isIsEnum()).isEqualTo(isEnum),
      () -> assertThat(symbol.isIsAbstract()).isEqualTo(isAbstract),
      () -> assertThat(symbol.isIsFinal()).isEqualTo(isFinal),
      () -> assertThat(symbol.isIsStatic()).isEqualTo(isStatic),
      () -> assertThat(symbol.isIsPublic()).isEqualTo(isPublic),
      () -> assertThat(symbol.isIsProtected()).isEqualTo(isProtected),
      () -> assertThat(symbol.isIsPrivate()).isEqualTo(isPrivate)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithSuperType() {
    // Given
    final String fn = "OOTypeWithSuperType.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(1),
      () -> assertThat(symbol.getSuperTypesList().get(0).getTypeInfo().getName()).isEqualTo("symboltable.OOType"),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithSuperTypes() {
    // Given
    final String fn = "OOTypeWithSuperTypes.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(2),
      () -> assertThat(symbol.getSuperTypesList().get(0).getTypeInfo().getName()).isEqualTo("symboltable.OOType"),
      () -> assertThat(symbol.getSuperTypesList().get(1).getTypeInfo().getName()).isEqualTo("symboltable.OOType"),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithFunction() {
    // Given
    final String fn = "OOTypeWithFunction.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(1),
      () -> assertThat(symbol.getFunctionList().get(0).getName()).isEqualTo("f"),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithFunctions() {
    // Given
    final String fn = "OOTypeWithFunctions.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(2),
      () -> assertThat(symbol.getFunctionList().get(0).getName()).isEqualTo("g"),
      () -> assertThat(symbol.getFunctionList().get(1).getName()).isEqualTo("h"),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithMethod() {
    // Given
    final String fn = "OOTypeWithMethod.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(1),
      () -> assertThat(symbol.getMethodList().get(0).getName()).isEqualTo("f"),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithMethods() {
    // Given
    final String fn = "OOTypeWithMethods.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(2),
      () -> assertThat(symbol.getMethodList().get(0).getName()).isEqualTo("g"),
      () -> assertThat(symbol.getMethodList().get(1).getName()).isEqualTo("h"),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithVariable() {
    // Given
    final String fn = "OOTypeWithVariable.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(1),
      () -> assertThat(symbol.getVariableList().get(0).getName()).isEqualTo("a"),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithVariables() {
    // Given
    final String fn = "OOTypeWithVariables.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(2),
      () -> assertThat(symbol.getVariableList().get(0).getName()).isEqualTo("b"),
      () -> assertThat(symbol.getVariableList().get(1).getName()).isEqualTo("c"),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(0)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithField() {
    // Given
    final String fn = "OOTypeWithField.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(1),
      () -> assertThat(symbol.getFieldList().get(0).getName()).isEqualTo("a")
    );
  }

  @Test
  @Order(2)
  public void shouldLoadOOTypeWithFields() {
    // Given
    final String fn = "OOTypeWithFields.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final OOTypeSymbol symbol = Preconditions.checkNotNull(scope.getLocalOOTypeSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getSuperTypesList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFunctionList().size()).isEqualTo(0),
      () -> assertThat(symbol.getMethodList().size()).isEqualTo(0),
      () -> assertThat(symbol.getVariableList().size()).isEqualTo(0),
      () -> assertThat(symbol.getFieldList().size()).isEqualTo(2),
      () -> assertThat(symbol.getFieldList().get(0).getName()).isEqualTo("b"),
      () -> assertThat(symbol.getFieldList().get(1).getName()).isEqualTo("c")
    );
  }

  @ParameterizedTest
  @Order(0)
  @ValueSource(strings = {
    "Function",
    "FunctionWithEllipticParameter",
    "FunctionWithParameter",
    "FunctionWithParameters",
  })
  public void shouldLoadFunction(@NotNull String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());

    // Given
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final FunctionSymbol symbol = Preconditions.checkNotNull(scope.getLocalFunctionSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getPackageName()).isEqualTo(PACKAGE),
      () -> assertThat(symbol.getName()).isEqualTo(name),
      () -> assertThat(symbol.getEnclosingScope()).isEqualTo(scope),
      () -> assertThat(symbol.getSpannedScope().getEnclosingScope()).isEqualTo(scope),
      () -> assertThat(symbol.getSpannedScope()).isEqualTo(scope.getSubScopes().get(0))
    );
  }

  @Test
  @Order(1)
  public void shouldLoadFunctionAttributes() {
    // Given
    final String fn = "Function.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final FunctionSymbol symbol = Preconditions.checkNotNull(scope.getLocalFunctionSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getParameterList()).isEmpty(),
      () -> assertThat(symbol.isIsElliptic()).isFalse()
    );
  }

  @Test
  @Order(2)
  public void shouldLoadFunctionWithParameter() {
    // Given
    final String fn = "FunctionWithParameter.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final FunctionSymbol symbol = Preconditions.checkNotNull(scope.getLocalFunctionSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getParameterList().size()).isEqualTo(1),
      () -> assertThat(symbol.getParameterList().get(0).getName()).isEqualTo("p"),
      () -> assertThat(symbol.isIsElliptic()).isFalse()
    );
  }

  @Test
  @Order(2)
  public void shouldLoadFunctionWithParameters() {
    // Given
    final String fn = "FunctionWithParameters.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final FunctionSymbol symbol = Preconditions.checkNotNull(scope.getLocalFunctionSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getParameterList().size()).isEqualTo(2),
      () -> assertThat(symbol.getParameterList().get(0).getName()).isEqualTo("p1"),
      () -> assertThat(symbol.getParameterList().get(1).getName()).isEqualTo("p2"),
      () -> assertThat(symbol.isIsElliptic()).isFalse()
    );
  }

  @Test
  @Order(2)
  public void shouldLoadFunctionWithEllipticParameter() {
    // Given
    final String fn = "FunctionWithEllipticParameter.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final FunctionSymbol symbol = Preconditions.checkNotNull(scope.getLocalFunctionSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getParameterList().size()).isEqualTo(1),
      () -> assertThat(symbol.getParameterList().get(0).getName()).isEqualTo("p"),
      () -> assertThat(symbol.isIsElliptic()).isTrue()
    );
  }

  @ParameterizedTest
  @Order(0)
  @ValueSource(strings = {
    "Method",
    "MethodWithEllipticParameter",
    "MethodWithParameter",
    "MethodWithParameters",
  })
  @Disabled
  public void shouldLoadMethod(@NotNull String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());

    // Given
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final MethodSymbol symbol = Preconditions.checkNotNull(scope.getLocalMethodSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getPackageName()).isEqualTo(PACKAGE),
      () -> assertThat(symbol.getName()).isEqualTo(name),
      () -> assertThat(symbol.getEnclosingScope()).isEqualTo(scope),
      () -> assertThat(scope.getSubScopes().get(0)).isEqualTo(symbol.getSpannedScope()),
      () -> assertThat(symbol.getSpannedScope().getEnclosingScope()).isEqualTo(scope),
      () -> assertThat(symbol.getSpannedScope()).isEqualTo(scope.getSubScopes().get(0))
    );
  }

  @ParameterizedTest
  @Order(1)
  @CsvSource(value = {
    "0, false, false, false, false, false, false, false", // default all false
    "1, false, false, false, false, false, false, false", // explicit all false
    // "2, true, true, true, true, true, true, true", // explicit all true
    "3, false, false, true, false, true, false, false", // public method
    "4, true, false, true, false, true, false, false", // public static method
    "5, false, true, true, false, true, false, false", // public final method
    "6, true, true, true, false, true, false, false", // public static final method
    "7, false, false, false, true, true, false, false", // public constructor
  })
  public void shouldLoadMethodAttributes(int index,
                                         boolean isStatic,
                                         boolean isFinal,
                                         boolean isMethod,
                                         boolean isConstructor,
                                         boolean isPublic,
                                         boolean isProtected,
                                         boolean isPrivate) {
    // Given
    final String fn = "Method.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    SymTypeRelations.init();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final MethodSymbol symbol = Preconditions.checkNotNull(scope.getLocalMethodSymbols().get(index));

    // Then
    assertAll(
      () -> assertThat(symbol.getParameterList().size()).isEqualTo(0),
      () -> assertThat(symbol.isIsElliptic()).isFalse(),
      () -> assertThat(symbol.isIsStatic()).isEqualTo(isStatic),
      () -> assertThat(symbol.isIsFinal()).isEqualTo(isFinal),
      () -> assertThat(symbol.isIsMethod()).isEqualTo(isMethod),
      () -> assertThat(symbol.isIsConstructor()).isEqualTo(isConstructor),
      () -> assertThat(symbol.isIsPublic()).isEqualTo(isPublic),
      () -> assertThat(symbol.isIsProtected()).isEqualTo(isProtected),
      () -> assertThat(symbol.isIsPrivate()).isEqualTo(isPrivate)
    );
  }

  @Test
  @Order(2)
  public void shouldLoadMethodWithParameter() {
    // Given
    final String fn = "MethodWithParameter.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final MethodSymbol symbol = Preconditions.checkNotNull(scope.getLocalMethodSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getParameterList().size()).isEqualTo(1),
      () -> assertThat(symbol.getParameterList().get(0).getName()).isEqualTo("p"),
      () -> assertThat(symbol.isIsElliptic()).isFalse()
    );
  }

  @Test
  @Order(2)
  public void shouldLoadMethodWithParameters() {
    // Given
    final String fn = "MethodWithParameters.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final MethodSymbol symbol = Preconditions.checkNotNull(scope.getLocalMethodSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getParameterList().size()).isEqualTo(2),
      () -> assertThat(symbol.getParameterList().get(0).getName()).isEqualTo("p1"),
      () -> assertThat(symbol.getParameterList().get(1).getName()).isEqualTo("p2"),
      () -> assertThat(symbol.isIsElliptic()).isFalse()
    );
  }

  @Test
  @Order(2)
  public void shouldLoadMethodWithEllipticParameter() {
    // Given
    final String fn = "MethodWithEllipticParameter.sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final MethodSymbol symbol = Preconditions.checkNotNull(scope.getLocalMethodSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getParameterList().size()).isEqualTo(1),
      () -> assertThat(symbol.getParameterList().get(0).getName()).isEqualTo("p"),
      () -> assertThat(symbol.isIsElliptic()).isTrue()
    );
  }

  @Test
  @Order(0)
  public void shouldLoadVariable() {
    // Given
    final String name = "Variable";

    // Given
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final VariableSymbol symbol = Preconditions.checkNotNull(scope.getLocalVariableSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getPackageName()).isEqualTo(PACKAGE),
      () -> assertThat(symbol.getName()).isEqualTo(name),
      () -> assertThat(symbol.getEnclosingScope()).isEqualTo(scope)
    );
  }

  @ParameterizedTest
  @Order(1)
  @CsvSource(value = {
    "0, false", // default all false
    "1, false", // explicit all false
    "2, true", // explicit all true
  })
  public void shouldLoadVariableAttributes(int index, boolean isReadOnly) {
    // Given
    final String name = "Variable";
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final VariableSymbol symbol = Preconditions.checkNotNull(scope.getLocalVariableSymbols().get(index));

    // Then
    assertThat(symbol.isIsReadOnly()).isEqualTo(isReadOnly);
  }


  @Test
  @Order(0)
  public void shouldLoadField() {
    // Given
    final String name = "Field";
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final FieldSymbol symbol = Preconditions.checkNotNull(scope.getLocalFieldSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getPackageName()).isEqualTo(PACKAGE),
      () -> assertThat(symbol.getName()).isEqualTo(name),
      () -> assertThat(symbol.getEnclosingScope()).isEqualTo(scope)
    );
  }


  @ParameterizedTest
  @Order(1)
  @CsvSource(value = {
    "0, false, false, false, false, false, false, false", // default all false
    "1, false, false, false, false, false, false, false", // explicit all false
    //"2, true, true, true, true, true, true, true", // explicit all true
    "3, false, false, false, false, true, false, false", // public variable
    "4, false, false, false, false, false, true, false", // protected variable
    "5, false, false, false, false, false, false, true", // private variable
    "6, true, false, false, false, true, false, false", // public readonly variable
    "7, false, true, false, false, true, false, false", // public static variable
    "8, false, false, true, false, true, false, false", // public final variable
    "9, false, true, true, false, true, false, false", // public static final variable
  })
  public void shouldLoadFieldAttributes(int index,
                                        boolean isReadOnly,
                                        boolean isStatic,
                                        boolean isFinal,
                                        boolean isDerived,
                                        boolean isPublic,
                                        boolean isProtected,
                                        boolean isPrivate) {
    // Given
    final String name = "Field";
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    SymTypeRelations.init();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final FieldSymbol symbol = Preconditions.checkNotNull(scope.getLocalFieldSymbols().get(index));

    // Then
    assertAll(
      () -> assertThat(symbol.isIsReadOnly()).isEqualTo(isReadOnly),
      () -> assertThat(symbol.isIsStatic()).isEqualTo(isStatic),
      () -> assertThat(symbol.isIsFinal()).isEqualTo(isFinal),
      () -> assertThat(symbol.isIsDerived()).isEqualTo(isDerived),
      () -> assertThat(symbol.isIsPublic()).isEqualTo(isPublic),
      () -> assertThat(symbol.isIsProtected()).isEqualTo(isProtected),
      () -> assertThat(symbol.isIsPrivate()).isEqualTo(isPrivate)
    );
  }

  @Test
  @Order(0)
  public void shouldLoadState() {
    // Given
    final String name = "SCState";
    final String fn = name + ".sym";

    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();

    // When
    final IMontiArcArtifactScope scope = Preconditions.checkNotNull(
      s2j.load(Paths.get(RELATIVE_MODEL_PATH, Names.getPathFromPackage(PACKAGE), fn).toString())
    );
    final SCStateSymbol symbol = Preconditions.checkNotNull(scope.getLocalSCStateSymbols().get(0));

    // Then
    assertAll(
      () -> assertThat(symbol.getPackageName()).isEqualTo(PACKAGE),
      () -> assertThat(symbol.getName()).isEqualTo(name),
      () -> assertThat(symbol.getEnclosingScope()).isEqualTo(scope)
    );
  }
}