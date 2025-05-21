/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._cocos.ConfigurationParameterAssignment;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;

class MontiArcDeSerializationEndToEndTest extends MontiArcTestBase {

  /**
   * @param paramModel Model with parameters of which some may be default parameters.
   * @param instantiatingModel Model that contains instantiations of {@code paramModel}.
   */
  @ParameterizedTest
  @MethodSource("provideValidDefaultParamModels")
  void testValidDeSerializationOfDefaultParameters(@NotNull String paramModel, @NotNull String instantiatingModel) {
    Preconditions.checkNotNull(paramModel);
    Preconditions.checkNotNull(instantiatingModel);

    // Given
    addStandardTypesAndVarsToGlobalScope("TA", "TB", "TC", "TD", "TE", "TF");
    final ASTMACompilationUnit ast = compile(paramModel);
    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new ConfigurationParameterAssignment());

    // When
    final String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());
    MontiArcMill.globalScope().clear();
    addStandardTypesAndVarsToGlobalScope("TA", "TB", "TC", "TD", "TE", "TF");
    MontiArcMill.globalScope().addSubScope(s2j.deserialize(json));
    final ASTMACompilationUnit cUnit = compile(instantiatingModel);
    checker.checkAll(cUnit);


    // Then
    assertThat(Log.getErrorCount()).as("Log Error count").isZero();
  }

  static Stream<Arguments> provideValidDefaultParamModels() {
    return Stream.of(
      // NoParam
      arg(named("[] []",
        createComponentWithSignature("")),
        createComponentWithInstantiations(List.of(
          "Comp c1;",
          "Comp c2();"
        )
      )),

      // OneMandatoryParam
      arg(named("[a] []",
        createComponentWithSignature("TA a")),
        createComponentWithInstantiations(List.of(
          "Comp c1(tA);",
          "Comp c2(a = tA);"
        )
      )),

      // SomeMandatoryParams
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiations(List.of(
          "Comp c1(tA, tB, tC);",
          "Comp c2(c = tC, b = tB, a = tA);",
          "Comp c3(tA, b = tB, c = tC);"
        )
      )),

      // OneOptionalParam
      arg(named("[] [a]",
        createComponentWithSignature("TA a = tA")),
        createComponentWithInstantiations(List.of(
          "Comp c1;",
          "Comp c2();",
          "Comp c3(tA);",
          "Comp c4(a = tA);"
        )
      )),

      // SomeOptionalParams
      arg(named("[] [a, b, c]",
        createComponentWithSignature("TA a = tA, TB b = tB, TC c = tC")),
        createComponentWithInstantiations(List.of(
          "Comp c1;",
          "Comp c2();",
          "Comp c3(tA);",
          "Comp c4(tA, tB);",
          "Comp c5(tA, tB, tC);",
          "Comp c6(c = tC, b = tB, a = tA);",
          "Comp c7(a = tA);",
          "Comp c8(b = tB);",
          "Comp c9(c = tC);",
          "Comp c10(b = tB, c = tC);",
          "Comp c11(c = tC, b = tB);",
          "Comp c12(c = tC, a = tA);"
        )
      )),

      // MandatoryOptionalPair
      arg(named("[a] [b]",
        createComponentWithSignature("TA a, TB b = tB")),
        createComponentWithInstantiations(List.of(
          "Comp c1(tA);",
          "Comp c2(tA, b = tB);",
          "Comp c3(a = tA, b = tB);",
          "Comp c4(b = tB, a = tA);",
          "Comp c5(a = tA);"
        )
      )),

      // SomeMandatoryOneOptional
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiations(List.of(
          "Comp c1(tA, tB, tC);",
          "Comp c2(tA, tB, tC, tD);",
          "Comp c3(tA, tB, tC, d = tD);",
          "Comp c4(a = tA, b = tB, c = tC, d = tD);",
          "Comp c5(tA, b = tB, c = tC, d = tD);",
          "Comp c6(tA, b = tB, c = tC);",
          "Comp c7(tA, tB, c = tC, d = tD);",
          "Comp c8(d = tD, a = tA, b = tB, c = tC);",
          "Comp c9(a = tA, d = tD, c = tC, b = tB);",
          "Comp c10(b = tB, d = tD, a = tA, c = tC);"
        )
      )),

      // SomeMandatorySomeOptional
      arg(named("[a, b, c] [d, e, f]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD, TE e = tE, TF f = tF")),
        createComponentWithInstantiations(List.of(
          "Comp c1(tA, tB, tC);",
          "Comp c2(tA, tB, tC, tD);",
          "Comp c3(tA, tB, tC, tD, tE);",
          "Comp c4(tA, tB, tC, tD, tE, tF);",
          "Comp c5(tA, tB, tC, f = tF);",
          "Comp c6(tA, tB, tC, f = tF, e = tE);",
          "Comp c7(f = tF, e = tE, d = tD, c = tC, b = tB, a = tA);",
          "Comp c8(f = tF, c = tC, b = tB, a = tA);",
          "Comp c9(e = tE, c = tC, b = tB, a = tA);",
          "Comp c10(c = tC, a = tA, b = tB);",
          "Comp c11(tA, c = tC, b = tB);"
        )
      ))
    );
  }

  /**
   * @param paramModel Model with parameters of which some may be default parameters.
   * @param instantiatingModel Model that contains instantiations of {@code paramModel}.
   */
  @ParameterizedTest
  @MethodSource("provideInvalidDefaultParamModels")
  void testInvalidDeSerializationOfDefaultParameter(@NotNull String paramModel,
                                                    @NotNull String instantiatingModel,
                                                    @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(paramModel);
    Preconditions.checkNotNull(instantiatingModel);

    // Given
    addStandardTypesAndVarsToGlobalScope("TA", "TB", "TC", "TD", "TE", "TF");
    final ASTMACompilationUnit ast = compile(paramModel);
    final MontiArcSymbols2Json s2j = new MontiArcSymbols2Json();
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new ConfigurationParameterAssignment());

    // When
    final String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());
    MontiArcMill.globalScope().clear();
    addStandardTypesAndVarsToGlobalScope("TA", "TB", "TC", "TD", "TE", "TF");
    MontiArcMill.globalScope().addSubScope(s2j.deserialize(json));
    final ASTMACompilationUnit cUnit = compile(instantiatingModel);
    checker.checkAll(cUnit);


    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  static Stream<Arguments> provideInvalidDefaultParamModels() {
    return Stream.of(
      // NoParam
      arg(named("[] []",
        createComponentWithSignature("")),
        createComponentWithInstantiation("Comp c1(tB);"),
        ArcError.TOO_MANY_ARGUMENTS
      ),

      // OneMandatoryParam
      arg(named("[a] []",
        createComponentWithSignature("TA a")),
        createComponentWithInstantiation("Comp c1(tB);"),
        ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a] []",
        createComponentWithSignature("TA a")),
        createComponentWithInstantiation("Comp c2(a = tB);"),
        ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a] []",
          createComponentWithSignature("TA a")),
        createComponentWithInstantiation("Comp c3(a = tA, a = tA);"),  // Double assignment
        ArcError.TOO_MANY_ARGUMENTS, ArcError.KEY_NOT_UNIQUE
      ),
      arg(named("[a] []",
        createComponentWithSignature("TA a")),
        createComponentWithInstantiation("Comp c4();"),  // Wrong arg count
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a] []",
        createComponentWithSignature("TA a")),
        createComponentWithInstantiation("Comp c5;"),  // Wrong arg count
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a] []",
        createComponentWithSignature("TA a")),
        createComponentWithInstantiation("Comp c6(tA, tB);"),  // Wrong arg count
        ArcError.TOO_MANY_ARGUMENTS
      ),

      // SomeMandatoryParams
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiation("Comp c1;"),  // Wrong arg count
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiation("Comp c2();"),  // Wrong arg count
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiation("Comp c3(tA);"),  // Wrong arg count
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiation("Comp c4(tA, tB, tC, tD);"),  // Wrong arg count
        ArcError.TOO_MANY_ARGUMENTS
      ),
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiation("Comp c5(tA, tA, tA);"),  // Wrong type
        ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiation("Comp c6(c = tA, a = tB, b = tC);"),  // Types in correct order, but params not
        ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiation("Comp c7(a = tA, a = tB, a = tC);"),  // Types in correct order, but param duplicated
        ArcError.KEY_NOT_UNIQUE, ArcError.KEY_NOT_UNIQUE
      ),
      arg(named("[a, b, c] []",
        createComponentWithSignature("TA a, TB b, TC c")),
        createComponentWithInstantiation("Comp c8(a = tA, b = tB, tC);"),  // Keywoards last
        ArcError.COMP_ARG_VALUE_AFTER_KEY
      ),

      // OneOptionalParam
      arg(named("[] [a]",
        createComponentWithSignature("TA a = tA")),
        createComponentWithInstantiation("Comp c1(tB);"),  // Wrong type
        ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[] [a]",
        createComponentWithSignature("TA a = tA")),
        createComponentWithInstantiation("Comp c2(a = tB);"),  // Wrong type
        ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[] [a]",
        createComponentWithSignature("TA a = tA")),
        createComponentWithInstantiation("Comp c3(tA, tB);"),  // Wrong arg count
        ArcError.TOO_MANY_ARGUMENTS
      ),

      // SomeOptionalParams
      arg(named("[] [a, b, c]",
        createComponentWithSignature("TA a = tA, TB b = tB, TC c = tC")),
        createComponentWithInstantiation("Comp c1(tB, tB, tC);"),  // Wrong type
        ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[] [a, b, c]",
        createComponentWithSignature("TA a = tA, TB b = tB, TC c = tC")),
        createComponentWithInstantiation("Comp c2(tB, tB, tB);"),  // Wrong type
        ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[] [a, b, c]",
        createComponentWithSignature("TA a = tA, TB b = tB, TC c = tC")),
        createComponentWithInstantiation("Comp c3(a = tA, tB, tC);"),  // Keywords last
        ArcError.COMP_ARG_VALUE_AFTER_KEY, ArcError.COMP_ARG_VALUE_AFTER_KEY
      ),
      arg(named("[] [a, b, c]",
        createComponentWithSignature("TA a = tA, TB b = tB, TC c = tC")),
        createComponentWithInstantiation("Comp c4(tA, c = tB, b = tB);"),  // Wrong type
        ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[] [a, b, c]",
        createComponentWithSignature("TA a = tA, TB b = tB, TC c = tC")),
        createComponentWithInstantiation("Comp c5(c = tA, a = tB, b = tC);"),  // Correct type order, but not params
        ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[] [a, b, c]",
        createComponentWithSignature("TA a = tA, TB b = tB, TC c = tC")),
        createComponentWithInstantiation("Comp c6(tA, c = tB);"),  // Correct type order, but not params
        ArcError.COMP_ARG_TYPE_MISMATCH
      ),

      // MandatoryOptionalPair
      arg(named("[a] [b]",
        createComponentWithSignature("TA a, TB b = tB")),
        createComponentWithInstantiation("Comp c1(b = tB);"),  // Missing mandatory
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a] [b]",
        createComponentWithSignature("TA a, TB b = tB")),
        createComponentWithInstantiation("Comp c2(b = tA);"),  // Missing mandatory and wrong type
        ArcError.TOO_FEW_ARGUMENTS, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a] [b]",
        createComponentWithSignature("TA a, TB b = tB")),
        createComponentWithInstantiation("Comp c3(tA, a = tA);"),  // Double assignment
        ArcError.COMP_ARG_MULTIPLE_VALUES
      ),
      arg(named("[a] [b]",
        createComponentWithSignature("TA a, TB b = tB")),
        createComponentWithInstantiation("Comp c4(a = tA, tB);"),  // Keywords last
        ArcError.COMP_ARG_VALUE_AFTER_KEY
      ),

      // SomeMandatoryOneOptional
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiation("Comp c1(tA, tB);"),  // Mandatory missing
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiation("Comp c2(tA, tB, c = tC, tD);"),  // Keywords last
        ArcError.COMP_ARG_VALUE_AFTER_KEY
      ),
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiation("Comp c3(tA, c = tB, d = tC, b = tB);"),  // Wrong type
        ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiation("Comp c4(d = tA, b = tB, c = tC);"),  // Mandatory missing, wrong type, but types are in order
        ArcError.TOO_FEW_ARGUMENTS, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiation("Comp c5(d = tA, b = tB, c = tC, a = tD);"),  // Wrong types, but types are in order
        ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiation("Comp c6(d = tD, b = tB, c = tC);"),  // Mandatory missing
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiation("Comp c7(d = tD, d = tD, d = tD);"),  // Mandatory missing, double assignments
        ArcError.TOO_FEW_ARGUMENTS, ArcError.KEY_NOT_UNIQUE, ArcError.KEY_NOT_UNIQUE
      ),
      arg(named("[a, b, c] [d]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD")),
        createComponentWithInstantiation("Comp c8(d = tA, d = tB, d = tC, d = tD);"),  // Mandatory missing, double assignments, but types are in order
        ArcError.TOO_FEW_ARGUMENTS, ArcError.KEY_NOT_UNIQUE, ArcError.KEY_NOT_UNIQUE, ArcError.KEY_NOT_UNIQUE
      ),

      // SomeMandatorySomeOptional
      arg(named("[a, b, c] [d, e, f]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD, TE e = tE, TF f = tF")),
        createComponentWithInstantiation("Comp c1(f = tA, e = tB, d = tC);"),  // Mandatory parameters missing, wrong type, but types are in order
        ArcError.TOO_FEW_ARGUMENTS, ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a, b, c] [d, e, f]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD, TE e = tE, TF f = tF")),
        createComponentWithInstantiation("Comp c2(f = tF, e = tE, d = tD);"),  // Mandatory parameters missing
        ArcError.TOO_FEW_ARGUMENTS
      ),
      arg(named("[a, b, c] [d, e, f]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD, TE e = tE, TF f = tF")),
        createComponentWithInstantiation("Comp c3(d = tA, e = tB, f = tC, a = tD, b = tE, c = tF);"),  // Wrong types (but types are in order)
        ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
      ),
      arg(named("[a, b, c] [d, e, f]",
        createComponentWithSignature("TA a, TB b, TC c, TD d = tD, TE e = tE, TF f = tF")),
        createComponentWithInstantiation("Comp c4(d = tD, e = tE, f = tF, d = tD, e = tE, f = tF);"),  // Mandatory missing, double assignments
        ArcError.TOO_FEW_ARGUMENTS, ArcError.KEY_NOT_UNIQUE, ArcError.KEY_NOT_UNIQUE, ArcError.KEY_NOT_UNIQUE
      )
    );
  }

  /** Returns a component 'Comp' in package 'foo' with the given parameters signature */
  protected static String createComponentWithSignature(String formattedSignature) {
    return String.format("package foo; component Comp (%s) {}", formattedSignature);
  }

  protected static String createComponentWithInstantiations(List<String> instantiationStatements) {
    String instantiationsFormatted = String.join(" ", instantiationStatements);
    return String.format("import foo.Comp; component Instantiating { %s }", instantiationsFormatted);
  }

  protected static String createComponentWithInstantiation(String instantiationStatement) {
    return String.format("import foo.Comp; component Instantiating { %s }", instantiationStatement);
  }

  /**
   * For each name, adds a type with that name to the global scope and also a variable with the same name, but a
   * decapitalized first letter.
   * <br>
   * The first letter of each name must be an uppercase letter.
   */
  protected static void addStandardTypesAndVarsToGlobalScope(String... typeNames) {
    Preconditions.checkArgument(Arrays.stream(typeNames).map(s -> s.charAt(0)).allMatch(Character::isUpperCase));

    for (String typeName : typeNames) {
      TypeSymbol type = createType(typeName);
      String decapitalizedVarName = Character.toLowerCase(typeName.charAt(0)) + typeName.substring(1);
      VariableSymbol correspondingVar = createVar(decapitalizedVarName, type);

      MontiArcMill.globalScope().add(type);
      MontiArcMill.globalScope().add(correspondingVar);
    }

  }

  protected static TypeSymbol createType(String name) {
    return MontiArcMill.typeSymbolBuilder()
      .setName(name)
      .setFullName(name)
      .setEnclosingScope(MontiArcMill.globalScope())
      .setSpannedScope(MontiArcMill.scope())
      .setAccessModifier(AccessModifier.ALL_INCLUSION)
      .build();
  }

  protected static VariableSymbol createVar(String name, TypeSymbol type) {
    return MontiArcMill.variableSymbolBuilder()
      .setName(name)
      .setFullName(name)
      .setType(SymTypeExpressionFactory.createTypeExpression(type))
      .setEnclosingScope(MontiArcMill.globalScope())
      .setAccessModifier(AccessModifier.ALL_INCLUSION)
      .build();
  }
}
