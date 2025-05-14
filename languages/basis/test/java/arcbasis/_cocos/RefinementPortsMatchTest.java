/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcParent;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RefinementPortsMatch}
 */
class RefinementPortsMatchTest extends ArcBasisTestBase {

  private static final String TEST_RESOURCE = "test/resources";
  private static final String PACKAGE = "cocos";

  // Constants for fluent test case descriptions
  protected static final Timing TIMED = Timing.TIMED;
  protected static final Timing SYNC = Timing.TIMED_SYNC;
  protected static final boolean IN = true;
  protected static final boolean OUT = false;

  @BeforeEach
  protected void initSymbols() {
    /*
     * Loading the following types:
     * OOType
     * OOTypeWithSuperType extends OOType
     * component Restrictive { port in OOType i; port sync out OOTypeWithSuperType o; }
     * component Liberal { port sync in OOTypeWithSuperType i; port out OOType o; }
     * component RestrictiveMorePorts { port in OOType i1, i2; port sync out OOTypeWithSuperType o1, o2; }
     * And some erroneous components for robustness testing
     * component DuplicatePortAllDifferent { port in int a; port sync out long a; }
     * component DuplicatePortSameDirection { port in int a; port sync in long a; }
     * component DuplicateSameDirectionTiming { port in int a; port in long a; }
     * component DuplicateSameTimingTypes { port in OOType a; port out OOType a; }
     * component DuplicateEverythingSame { port in OOType a; port in OOType a; }
     */
    Path ooTypesPath = Path.of(TEST_RESOURCE, PACKAGE, "OOTypeWithSuperType.cdsym");
    Path compPath = Path.of(TEST_RESOURCE, PACKAGE, "RefinementAbstractions.arcsym");
    ArcBasisSymbols2Json symbols2Json = new ArcBasisSymbols2Json();
    ArcBasisMill.globalScope().addSubScope(symbols2Json.load(ooTypesPath.toString()));
    ArcBasisMill.globalScope().addSubScope(symbols2Json.load(compPath.toString()));
  }

  protected static Stream<Arguments> validRefinementsAndPortsProvider() {
    return Stream.of(
      // Nothing refined
      arg(refines(), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o"))
      ),
      // No interface change
      arg(refines("Liberal"), withPorts(
        port(SYNC, IN, "SubType", "i"),
        port(TIMED, OUT, "OOType", "o")
      )),
      // No interface change
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")
      )),
      // Changing the timing legally
      arg(refines("Liberal"), withPorts(
        port(TIMED, IN, "SubType", "i"),
        port(SYNC, OUT, "OOType", "o")
      )),
      // Changing the types legally
      arg(refines("Liberal"), withPorts(
        port(SYNC, IN, "OOType", "i"),
        port(TIMED, OUT, "SubType", "o")
      )),
      // Refining the liberal interface with time and type changes,
      // maintaining the restrictive interface
      arg(refines("Liberal", "Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")
      ))
    );
  }

  protected static Stream<Arguments> refinementsPortsAndExpectedErrorsProvider() {
    return Stream.of(
      // Other in port name = 1 name removed, 1 name added
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "otherInPortName"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_NAME_MISMATCH,
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Other out port name = 1 name removed, 1 name added
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "otherOutPortName")),
        ArcError.REFINEMENT_PORT_NAME_MISMATCH,
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // One port less (removed in port)
      arg(refines("Restrictive"), withPorts(
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // One port less (removed out port)
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i")),
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // One port added
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o"),
        port(TIMED, IN, "OOType", "newInPort")),
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Too restrictive refined incoming port timing
      arg(refines("Restrictive"), withPorts(
        port(SYNC, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // To weak refined outgoing port timing
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(TIMED, OUT, "SubType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT),
      // Problematic timing changes on both ports
      arg(refines("Restrictive"), withPorts(
        port(SYNC, IN, "OOType", "i"),
        port(TIMED, OUT, "SubType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT),
      // Input port type is a sub type compared with before
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "SubType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Output port type is a super type compared with before
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "OOType", "o")),
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH),
      // Input port type is a sub type compared with before and output port type is a super type
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "SubType", "i"),
        port(SYNC, OUT, "OOType", "o")),
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH),
      // Incoming port becomes outgoing port
      arg(refines("Restrictive"), withPorts(
        port(TIMED, OUT, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Outgoing port becomes incoming port
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, IN, "SubType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Both ports change directions
      arg(refines("Restrictive"), withPorts(
        port(TIMED, OUT, "OOType", "i"),
        port(SYNC, IN, "SubType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED,
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // 2 timing and subtyping changes
      arg(refines("RestrictiveMorePorts"), withPorts(
        port(SYNC, IN, "SubType", "i1"),
        port(SYNC, IN, "SubType", "i2"),
        port(TIMED, OUT, "OOType", "o1"),
        port(TIMED, OUT, "OOType", "o2")),
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT,
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT),
      // Changed port direction -> no sub typing error
      // (we do not know the direction of the sub type relationship)
      arg(refines("Restrictive"), withPorts(
        port(TIMED, OUT, "SubType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Changed port direction -> no timing mismatch error
      // (we do not know which timing is allowed to be weaker)
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(TIMED, IN, "OOType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Error for the second abstraction
      arg(refines("Liberal", "Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(TIMED, OUT, "OOType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT,
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH)
    );
  }

  protected static Stream<Arguments> robustnessTestRefinementAndExpectedErrorProvider() {
    return Stream.of(
      // Ambiguous port direction in abstraction, so type / timing is ignored
      // But the presence of a similarly named port is checked
      arg(refines("DuplicatePortAllDifferent"), withPorts(
        port(TIMED, IN, "OOType", "a"))),
      // Missing port
      arg(refines("DuplicatePortAllDifferent"), withPorts(),
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Ambiguous port timing -> own timing is not checked
      arg(refines("DuplicatePortSameDirection"), withPorts(
        port(TIMED, IN, "long", "a"))),
      // Ambiguous port timing -> own timing is not checked
      // But direction is unambiguous -> is checked
      arg(refines("DuplicatePortSameDirection"), withPorts(
        port(TIMED, OUT, "long", "a")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Ambiguous port timing -> own timing is not checked
      // But direction is unambiguous -> types are checked
      arg(refines("DuplicatePortSameDirection"), withPorts(
        port(TIMED, IN, "int", "a")),
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Unambiguous abstraction port direction -> Timing and types are checked
      // Types are different -> Our type must be a subtype of both
      arg(refines("DuplicateSameDirectionTiming"), withPorts(
        port(TIMED, IN, "long", "a"))),
      // Unambiguous direction -> Timing and types are checked
      // Should find timing change
      arg(refines("DuplicateSameDirectionTiming"), withPorts(
        port(SYNC, IN, "long", "a")),
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // Unambiguous direction -> Timing and types are checked
      // Should find port type only extending one abstraction port type
      arg(refines("DuplicateSameDirectionTiming"), withPorts(
        port(TIMED, IN, "int", "a")),
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Ambiguous directions in abstraction's ports
      // -> Coco should not check timing nor port type
      arg(refines("DuplicateSameTimingTypes"), withPorts(
        port(TIMED, IN, "OOType", "a"))),
      // Ambiguous directions in abstraction's ports
      // -> Coco should not find timing change
      arg(refines("DuplicateSameTimingTypes"), withPorts(
        port(SYNC, IN, "OOType", "a"))),
      // Ambiguous directions in abstraction's ports
      // -> Coco should not find type change
      arg(refines("DuplicateSameTimingTypes"), withPorts(
        port(TIMED, IN, "SubType", "a"))),
      // Abstraction has a duplicate port, but the direction, type, and timing
      // is unambiguous -> should still find nothing, due to no error
      arg(refines("DuplicateEverythingSame"), withPorts(
        port(TIMED, IN, "OOType", "a"))),
      // Abstraction has a duplicate port, but the direction, type, and timing
      // is unambiguous -> should identify changed direction
      arg(refines("DuplicateEverythingSame"), withPorts(
        port(TIMED, OUT, "OOType", "a")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Abstraction has a duplicate port, but the direction, type, and timing
      // is unambiguous -> should identify changed timing and type
      arg(refines("DuplicateEverythingSame"), withPorts(
        port(SYNC, IN, "SubType", "a")),
        // Two type errors, as SubType is not conform to neither of the
        // refinements a-ports
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // Interface is conform though -> No error
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o"))),
      // Abstraction is correct, but refinement has duplicate
      // Timing is changed once -> One error
      arg(refines("Restrictive"), withPorts(
        port(SYNC, IN, "OOType", "i"),
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // Abstraction is correct, but refinement has duplicate
      // Timing is changed twice -> Two errors
      arg(refines("Restrictive"), withPorts(
        port(SYNC, IN, "OOType", "i"),
        port(SYNC, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // Abstraction is correct, but refinement has duplicate
      // Direction is changed once -> One error
      arg(refines("Restrictive"), withPorts(
        port(TIMED, OUT, "OOType", "i"),
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Abstraction is correct, but refinement has duplicate
      // Direction is changed twice -> Two errors
      arg(refines("Restrictive"), withPorts(
        port(TIMED, OUT, "OOType", "i"),
        port(TIMED, OUT, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED,
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Abstraction is correct, but refinement has duplicate
      // Type is changed once -> One error
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "SubType", "i"),
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // Type is changed twice -> Two errors
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "SubType", "i"),
        port(TIMED, IN, "SubType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // Timing and type are changed separately
      arg(refines("Restrictive"), withPorts(
        port(SYNC, IN, "OOType", "i"),
        port(TIMED, IN, "SubType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // Timing and type are changed simultaneously
      arg(refines("Restrictive"), withPorts(
        port(SYNC, IN, "SubType", "i"),
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // One changes timing, the other type and direction (thus, type is not checked)
      arg(refines("Restrictive"), withPorts(
        port(TIMED, OUT, "SubType", "i"),
        port(SYNC, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED,
        ArcError.REFINEMENT_TIMING_MISMATCH_IN)
    );
  }

  @ParameterizedTest
  @MethodSource({
    "refinementsPortsAndExpectedErrorsProvider",
    "validRefinementsAndPortsProvider",
    "robustnessTestRefinementAndExpectedErrorProvider"
  })
  void testRefinementWithXPorts(@NotNull List<String> refinedComponents,
                                @NotNull List<PortTestInput> usedPorts,
                                @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(refinedComponents);
    Preconditions.checkNotNull(usedPorts);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    RefinementPortsMatch coco = new RefinementPortsMatch();

    ASTComponentType compType = ArcBasisMill.componentTypeBuilder()
      .setName("Comp")
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .build();

    ComponentTypeSymbol compSym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    compType.setSymbol(compSym);
    compSym.setAstNode(compType);

    for (String refined : refinedComponents) {
      compType.getHead().addSpec(compTypeReferenceFrom(refined));
      compType.getSymbol().addRefinements(compTypeExprFrom(refined));
    }

    for (PortTestInput port : usedPorts) {
      Pair<ASTComponentInterface, PortSymbol> portWithSymbol = portWithSymbolFrom(port);
      compType.getBody().addArcElement(portWithSymbol.getLeft());
      compSym.getSpannedScope().add(portWithSymbol.getRight());
    }

    // When
    coco.check(compType);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> refinementsWithInheritanceProvider() {
    return Stream.of(
      // Refining a component that has got its ports inherited
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o"))),
      // Super comp misses one of the ports
      arg(refines("Restrictive"), withPorts(
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Super comp adds a port
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o"),
        port(TIMED, IN, "OOType", "newInPort")),
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Super comp changes direction of a port
      arg(refines("Restrictive"), withPorts(
        port(TIMED, OUT, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Super comp changes timing of a port
      arg(refines("Restrictive"), withPorts(
        port(SYNC, IN, "OOType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // Super comp changes type of a port
      arg(refines("Restrictive"), withPorts(
        port(TIMED, IN, "SubType", "i"),
        port(SYNC, OUT, "SubType", "o")),
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH)
    );
  }

  @ParameterizedTest
  @MethodSource("refinementsWithInheritanceProvider")
  void testRefinementWithInheritedPorts(@NotNull List<String> refinedComponents,
                                        @NotNull List<PortTestInput> inheritedPorts,
                                        @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(refinedComponents);
    Preconditions.checkNotNull(inheritedPorts);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    RefinementPortsMatch coco = new RefinementPortsMatch();

    ComponentTypeSymbol parentComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    for (PortTestInput port : inheritedPorts) {
      Pair<ASTComponentInterface, PortSymbol> portWithSymbol = portWithSymbolFrom(port);
      parentComp.getSpannedScope().add(portWithSymbol.getRight());
      // Simulating that the parent is from a library
      portWithSymbol.getRight().setAstNodeAbsent();
    }

    ASTComponentType compType = ArcBasisMill.componentTypeBuilder()
      .setName("Comp")
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .build();
    compType.getHead().addArcParent(ArcBasisMill.arcParentBuilder()
      .setType(astTypeFrom("Parent")).build()
    );

    ComponentTypeSymbol compSym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    compType.setSymbol(compSym);
    compSym.setAstNode(compType);
    compSym.addSuperComponents(new TypeExprOfComponent(parentComp));

    for (String refined : refinedComponents) {
      compType.getHead().addSpec(compTypeReferenceFrom(refined));
      compType.getSymbol().addRefinements(compTypeExprFrom(refined));
    }

    // When
    coco.check(compType);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  /** Used for self-speaking test-case-descriptions. Wraps the strings into a list. */
  protected static List<String> refines(String... refinements) {
    return List.of(refinements);
  }

  /** Used for self-speaking test-case-descriptions. Wraps the ports into a list. */
  protected static List<PortTestInput> withPorts(PortTestInput... ports) {
    return List.of(ports);
  }

  /** Used to create port test inputs in a fluent-like api. */
  protected static PortTestInput port(@NotNull Timing timing,
                                      boolean isIncoming,
                                      @NotNull String type,
                                      @NotNull String name) {
    Preconditions.checkNotNull(timing);
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(type);
    return new PortTestInput(type, name, isIncoming, timing);
  }

  protected static final class PortTestInput {
    public final String name;
    public final String type;
    public final boolean isIncoming;
    public final Timing timing;

    public PortTestInput(@NotNull String type,
                         @NotNull String name, boolean isIncoming,
                         @NotNull Timing timing) {
      this.type = Preconditions.checkNotNull(type);
      this.name = Preconditions.checkNotNull(name);
      this.isIncoming = isIncoming;
      this.timing = Preconditions.checkNotNull(timing);
    }

    @Override
    public String toString() {
      return timing + " " + (isIncoming ? "in" : "out") + " " + type + " " + name;
    }
  }

  protected ASTArcParent compTypeReferenceFrom(@NotNull String compName) {
    Preconditions.checkNotNull(compName);

    return ArcBasisMill.arcParentBuilder()
      .setType(astTypeFrom(compName))
      .build();
  }

  protected Pair<ASTComponentInterface, PortSymbol> portWithSymbolFrom(@NotNull PortTestInput portInfos) {
    Preconditions.checkNotNull(portInfos);

    PortSymbol portSym = ArcBasisMill.portSymbolBuilder()
      .setName(portInfos.name)
      .setType(typeExprFrom(portInfos.type))
      .setIncoming(portInfos.isIncoming)
      .setOutgoing(!portInfos.isIncoming)
      .setTiming(portInfos.timing)
      .build();

    ASTArcPort port = ArcBasisMill.arcPortBuilder()
      .setName(portInfos.name)
      .build();

    port.setSymbol(portSym);
    portSym.setAstNode(port);

    ASTPortDeclaration portDecl = ArcBasisMill.portDeclarationBuilder()
      .addArcPort(port)
      .setIncoming(portInfos.isIncoming)
      .setMCType(astTypeFrom(portInfos.type))
      .setSync(portInfos.timing == SYNC)
      .build();

    ASTComponentInterface compInterface = ArcBasisMill.componentInterfaceBuilder()
      .addPortDeclaration(portDecl)
      .build();

    return Pair.of(compInterface, portSym);
  }

  protected CompKindExpression compTypeExprFrom(@NotNull String compName) {
    Preconditions.checkNotNull(compName);
    ComponentTypeSymbol comp = ArcBasisMill.globalScope()
      .resolveComponentType(compName)
      .orElseThrow(IllegalArgumentException::new);

    return new TypeExprOfComponent(comp);
  }

  protected SymTypeExpression typeExprFrom(@NotNull String typeName) {
    Preconditions.checkNotNull(typeName);

    if (BasicSymbolsMill.PRIMITIVE_LIST.contains(typeName)) {
      return SymTypeExpressionFactory.createPrimitive(typeName);
    } else {
      TypeSymbol type = ArcBasisMill.globalScope()
        .resolveOOType(typeName)
        .orElseThrow(IllegalArgumentException::new);
      return SymTypeExpressionFactory.createTypeObject(type);
    }
  }

  protected ASTMCType astTypeFrom(@NotNull String typeName) {
    Preconditions.checkNotNull(typeName);

    return ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(
        ArcBasisMill.mCQualifiedNameBuilder()
          .addParts(typeName)
          .build()
      ).build();
  }
}
