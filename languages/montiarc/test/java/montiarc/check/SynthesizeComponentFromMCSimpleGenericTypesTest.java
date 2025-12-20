/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.CompKindCheckResult;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SynthesizeCompKindFromMCSimpleGenericTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericTypeBuilder;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class SynthesizeComponentFromMCSimpleGenericTypesTest extends MontiArcTestBase {

  @Test
  public void shouldHandleMCBasicGenericType() {
    // Given
    // First, we build OOSymbols for String and List<T> and a ComponentTypeSymbol for Comp<K,V>. We put them in a
    // common sub scope of the global scope.
    String compName = "Comp";
    ComponentTypeSymbol compSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(MontiArcMill.scope())
      .setTypeParameters(ImmutableList.of(
        MontiArcMill.typeVarSymbolBuilder().setName("K").build(),
        MontiArcMill.typeVarSymbolBuilder().setName("V").build()
      )).build();

    String nameOfCompScope = "scoop";
    IMontiArcScope scopeOfComp = MontiArcMill.scope();
    scopeOfComp.setName(nameOfCompScope);
    scopeOfComp.add(compSym);
    scopeOfComp.addSubScope(compSym.getSpannedScope());
    MontiArcMill.globalScope().addSubScope(scopeOfComp);

    String stringName = "String";
    OOTypeSymbol stringSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    scopeOfComp.add(stringSym);
    scopeOfComp.addSubScope(stringSym.getSpannedScope());

    String listName = "List";
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    scopeOfComp.add(listSym);
    scopeOfComp.addSubScope(listSym.getSpannedScope());

    // Now we build generic ast types Comp<String, List<String>> and scoop.Comp<scoop.List<scoop.String>, scoop.String>
    // That lay a) in the scope where the symbols lay and b) in the global scope.
    ASTMCQualifiedType astString = MontiArcMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
        .addParts(stringName)
        .build())
      .build();
    ASTMCQualifiedType astQualString = MontiArcMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
        .addParts(nameOfCompScope)
        .addParts(stringName)
        .build())
      .build();
    astString.setEnclosingScope(scopeOfComp);
    astString.getMCQualifiedName().setEnclosingScope(scopeOfComp);
    astQualString.setEnclosingScope(MontiArcMill.globalScope());
    astQualString.getMCQualifiedName().setEnclosingScope(scopeOfComp);

    ASTMCType astListOfString = createGenericType(ImmutableList.of(listName), scopeOfComp, astString);
    ASTMCType astQualListOfString = createGenericType(
      ImmutableList.of(nameOfCompScope, listName), MontiArcMill.globalScope(), astQualString);

    // Now build qualified and unqualified generic types
    ASTMCBasicGenericType astNormalComp = createGenericType(
      ImmutableList.of(compName),
      scopeOfComp,
      astString, astListOfString
    );
    ASTMCBasicGenericType astQualComp = createGenericType(
      ImmutableList.of(nameOfCompScope, compName),
      MontiArcMill.globalScope(),
      astQualListOfString, astQualString
    );

    astNormalComp.setEnclosingScope(scopeOfComp);
    astQualComp.setEnclosingScope(MontiArcMill.globalScope());

    CompKindCheckResult result4normal = new CompKindCheckResult();
    CompKindCheckResult result4qual = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth4normal = new SynthesizeCompKindFromMCSimpleGenericTypes(result4normal);
    SynthesizeCompKindFromMCSimpleGenericTypes synth4qual = new SynthesizeCompKindFromMCSimpleGenericTypes(result4qual);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);

    // Then
    Assertions.assertTrue(result4normal.getResult().isPresent());
    Assertions.assertTrue(result4qual.getResult().isPresent());
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, result4normal.getResult().get());
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, result4qual.getResult().get());


    CompKindOfGenericComponentType result4normalAsGeneric =
      (CompKindOfGenericComponentType) result4normal.getResult().get();
    CompKindOfGenericComponentType result4qualAsGeneric =
      (CompKindOfGenericComponentType) result4qual.getResult().get();

    Assertions.assertEquals(compSym, result4normal.getResult().get().getTypeInfo());
    Assertions.assertEquals(compSym, result4qual.getResult().get().getTypeInfo());
    Assertions.assertInstanceOf(SymTypeOfObject.class, result4normalAsGeneric.getTypeBindingFor("K").get());
    Assertions.assertInstanceOf(SymTypeOfGenerics.class, result4normalAsGeneric.getTypeBindingFor("V").get());
    Assertions.assertEquals(stringSym, result4normalAsGeneric.getTypeBindingFor("K").get().getTypeInfo());
    Assertions.assertEquals(listSym, result4normalAsGeneric.getTypeBindingFor("V").get().getTypeInfo());
    Assertions.assertEquals(stringSym,
      ((SymTypeOfGenerics) result4normalAsGeneric.getTypeBindingFor("V").get()).getArgument(0).getTypeInfo()
    );
    Assertions.assertInstanceOf(SymTypeOfGenerics.class, result4qualAsGeneric.getTypeBindingFor("K").get());
    Assertions.assertInstanceOf(SymTypeOfObject.class, result4qualAsGeneric.getTypeBindingFor("V").get());
    Assertions.assertEquals(stringSym, result4qualAsGeneric.getTypeBindingFor("V").get().getTypeInfo());
    Assertions.assertEquals(listSym, result4qualAsGeneric.getTypeBindingFor("K").get().getTypeInfo());
    Assertions.assertEquals(stringSym,
      ((SymTypeOfGenerics) result4qualAsGeneric.getTypeBindingFor("K").get()).getArgument(0).getTypeInfo()
    );
    assertThat(Log.getFindings()).isEmpty();
    assertThat(result4normalAsGeneric.getSourceNode()).contains(astNormalComp);
    assertThat(result4qualAsGeneric.getSourceNode()).contains(astQualComp);
  }

  @Test
  public void shouldNotHandleMCBasicGenericTypeBecauseCompTypeUnresolvable() {
    // Given
    String stringName = "String"; // Opposed to the component type, the type argument is present.
    OOTypeSymbol stringSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(stringSym);
    MontiArcMill.globalScope().addSubScope(stringSym.getSpannedScope());

    ASTMCQualifiedType astString = MontiArcMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
        .addParts(stringName)
        .build())
      .build();
    astString.setEnclosingScope(MontiArcMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(MontiArcMill.globalScope());

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      MontiArcMill.globalScope(),
      astString
    );

    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth = new SynthesizeCompKindFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
    assertThat(getLoggedErrorCodes()).isEmpty(); // Error logged by ISynthesizeComponent
  }

  @Test
  public void shouldHandleMCBasicGenericTypeBecauseTypeArgumentUnresolvable() {
    // Given
    String compName = "Comp";
    ComponentTypeSymbol compSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(MontiArcMill.scope())
      .setTypeParameters(ImmutableList.of(
        MontiArcMill.typeVarSymbolBuilder().setName("T").build()
      )).build();
    MontiArcMill.globalScope().add(compSym);
    MontiArcMill.globalScope().addSubScope(compSym.getSpannedScope());

    ASTMCQualifiedType astString = MontiArcMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
        .addParts("String")
        .build())
      .build();
    astString.setEnclosingScope(MontiArcMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(MontiArcMill.globalScope());

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      MontiArcMill.globalScope(),
      astString
    );

    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth = new SynthesizeCompKindFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
    assertThat(getLoggedErrorCodes()).isEmpty(); // Error logged by ISynthesizeComponent
  }

  @Test
  public void shouldHandleMCBasicGenericTypeBecauseNestedTypeArgumentUnresolvable() {
    // Given
    String compName = "Comp";
    ComponentTypeSymbol compSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(MontiArcMill.scope())
      .setTypeParameters(ImmutableList.of(
        MontiArcMill.typeVarSymbolBuilder().setName("T").build()
      )).build();
    MontiArcMill.globalScope().add(compSym);
    MontiArcMill.globalScope().addSubScope(compSym.getSpannedScope());

    String listName = "List";
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());

    ASTMCQualifiedType astString = MontiArcMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
        .addParts("String")
        .build())
      .build();
    astString.setEnclosingScope(MontiArcMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(MontiArcMill.globalScope());

    ASTMCType astListOfString = createGenericType(ImmutableList.of(listName), MontiArcMill.globalScope(), astString);

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      MontiArcMill.globalScope(),
      astListOfString
    );

    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth = new SynthesizeCompKindFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
    assertThat(getLoggedErrorCodes()).isEmpty(); // Error logged by ISynthesizeComponent
  }

  /**
   * Returns a {@link ASTMCBasicGenericType} whose format is {@code name.parts<typeArg[0], typeArg[1], ...>}.
   * All newly created AST objects are enclosed by {@code enclScope}.
   */
  protected static ASTMCBasicGenericType createGenericType(@NotNull List<String> nameParts,
                                                           @NotNull IMontiArcScope enclScope,
                                                           @NotNull ASTMCType... typeArgs) {
    Preconditions.checkNotNull(nameParts);
    Preconditions.checkNotNull(enclScope);
    Preconditions.checkNotNull(typeArgs);
    Preconditions.checkArgument(Arrays.stream(typeArgs).allMatch(Objects::nonNull));

    ASTMCBasicGenericTypeBuilder builder = MontiArcMill.mCBasicGenericTypeBuilder()
      .setNamesList(nameParts);

    for (ASTMCType typeArg : typeArgs) {
      if (typeArg instanceof ASTMCPrimitiveType) {
        ASTMCPrimitiveType asPrimitiveType = (ASTMCPrimitiveType) typeArg;
        ASTMCPrimitiveTypeArgument asArg = MontiArcMill.mCPrimitiveTypeArgumentBuilder()
          .setMCPrimitiveType(asPrimitiveType).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);

      } else if (typeArg instanceof ASTMCQualifiedType) {
        ASTMCQualifiedType asQualType = (ASTMCQualifiedType) typeArg;
        ASTMCBasicTypeArgument asArg = MontiArcMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(asQualType).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);

      } else {
        ASTMCCustomTypeArgument asArg = MontiArcMill.mCCustomTypeArgumentBuilder().setMCType(typeArg).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);
      }
    }

    ASTMCBasicGenericType type = builder.build();
    type.setEnclosingScope(enclScope);
    return type;
  }
}
