/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Checks that a refinement
 * <ol>
 *   <li>Does not introduce new ports (i.e. with new names)</li>
 *   <li>Does not omit ports of its specifications</li>
 *   <li>Does not change the direction of its specifications ports</li>
 *   <li>
 *     Does not strengthen its timing assumptions on the environment by introducing
 *     new sync constraints on incoming ports
 *   </li>
 *   <li>
 *     Does not weaken its timing guarantees on the environment by removing sync
 *     constraints on outgoing ports
 *   </li>
 *   <li>
 *     Does not strengthen its typing assumptions on the environment by changing the
 *     type of an incoming port to a sub type
 *   </li>
 *   <li>
 *     Does not weaken its typing guarantees on the environment by changing the the
 *     type of an outgoing port to a super type
 *   </li>
 * </ol>
 * This coco will find errors even if multiple ports in the refinement share
 * the same name. This is a different error which we do not report. However, we
 * will check every part of this coco on every of these port, if it was the only
 * port of its name.<br>
 * This coco will also attempt to find errors even if multiple ports in a specification
 * share the same name. We check all parts of this coco, if their timing and direction
 * are unambiguous.
 */
public class RefinementPortsMatch implements ArcBasisASTArcComponentTypeCoCo {

  /**
   * Provides more self speaking access to a port collection.<br>
   * Utility functions are introduced that allow nicer handling for the case
   * where multiple ports share a name.
   */
  protected static final class NameToPorts {
    public final Multimap<String, PortSymbol> portsByName;

    public NameToPorts(@NotNull Set<PortSymbol> ports) {
      Preconditions.checkNotNull(ports);

      portsByName = MultimapBuilder.hashKeys().hashSetValues().build();

      for (PortSymbol p : ports) {
        portsByName.put(p.getName(), p);
      }
    }

    public Set<String> getAllPortNames() {
      return portsByName.keySet();
    }

    public Collection<PortSymbol> getPortsWithName(@NotNull String name) {
      Preconditions.checkNotNull(name);

      return portsByName.get(name);
    }
  }

  /** Helper for clarifying the port direction, especially in comparisons. */
  protected enum PortDirection {
    INCOMING,
    OUTGOING;

    public static PortDirection of(@NotNull PortSymbol port) {
      Preconditions.checkNotNull(port);
      return port.isIncoming() ? INCOMING : OUTGOING;
    }
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol compSym = node.getSymbol();

    // Some ports may be declared multiple times with the same name.
    // We do not log this error, but we still want to check the coco for
    // the ports with unique names
    NameToPorts ownPortsByName = new NameToPorts(compSym.getAllPorts());

    for (CompKindExpression specification : compSym.getRefinementsList()) {
      NameToPorts specPortsByName = new NameToPorts(specification.getTypeInfo().getAllPorts());

      checkChangedPortNames(compSym, ownPortsByName, specification, specPortsByName);
      checkMatchingPortDirections(compSym, ownPortsByName, specification, specPortsByName);
      checkMatchingTiming(compSym, ownPortsByName, specification, specPortsByName);
      checkMatchingTypes(compSym, ownPortsByName, specification, specPortsByName);
    }
  }

  /**
   * Logs errors for cases where a refinement has ports with names that a
   * specification does not have and vice versa.
   */
  protected void checkChangedPortNames(@NotNull ComponentTypeSymbol compSym,
                                       @NotNull NameToPorts ownPorts,
                                       @NotNull CompKindExpression specification,
                                       @NotNull NameToPorts specPorts) {
    Preconditions.checkNotNull(compSym);
    Preconditions.checkNotNull(ownPorts);
    Preconditions.checkNotNull(specification);
    Preconditions.checkNotNull(specPorts);

    Set<String> ownPortNames = ownPorts.getAllPortNames();
    Set<String> specPortNames = specPorts.getAllPortNames();

    Set<String> onlyInSpec = complementIn(specPortNames, ownPortNames);
    for (String forgottenPort : onlyInSpec) {
      logErrorMaybeWithSourceLocation(
        ArcError.REFINEMENT_PORT_NAME_MISMATCH.format(
          forgottenPort, specification.getTypeInfo().getName(), compSym.getName()),
        specification.getSourceNode()
      );
    }

    Set<String> onlyInConcretization = complementIn(ownPortNames, specPortNames);
    for (String addedPort : onlyInConcretization) {
      // We have to keep in mind that the new port name may be connected with multiple port
      // declarations. We shall log an error at each of the declarations.
      Collection<PortSymbol> addedPortsWithCurrentName = ownPorts.getPortsWithName(addedPort);

      for (PortSymbol addedPortSym : addedPortsWithCurrentName) {
        logErrorMaybeWithSourceLocation(
          ArcError.REFINEMENT_PORT_NAME_MISMATCH.format(
            addedPort, compSym.getName(), specification.getTypeInfo().getName()),
          deriveLogLocationForPort(addedPortSym, compSym)
        );
      }
    }
  }

  /**
   * Checks whether the port directions of ports in the specifications are
   * maintained by the refinement's ports of the same name. <br>
   * If the specification has multiple ports with the same name by error, we still
   * check the coco if their direction is unambiguous.
   */
  protected void checkMatchingPortDirections(@NotNull ComponentTypeSymbol compSym,
                                             @NotNull NameToPorts ownPorts,
                                             @NotNull CompKindExpression specification,
                                             @NotNull NameToPorts specPorts) {
    Preconditions.checkNotNull(compSym);
    Preconditions.checkNotNull(ownPorts);
    Preconditions.checkNotNull(specification);
    Preconditions.checkNotNull(specPorts);

    // We only check matching port directions for ports that are present in both:
    // The specification and the concretization
    Set<String> commonPortNames = intersection(
      ownPorts.getAllPortNames(),
      specPorts.getAllPortNames()
    );

    for (String portName : commonPortNames) {

      // The port name could be used for multiple ports by error.
      // If the port direction is still unambiguous in the specification, we can still check whether
      // the ports in the concretizations maintain the direction.
      Collection<PortSymbol> ownPortsCurrentName = ownPorts.getPortsWithName(portName);
      Collection<PortSymbol> specPortsCurrentName = specPorts.getPortsWithName(portName);

      Optional<PortDirection> specPortDirection = getUnambiguousDirection(specPortsCurrentName);

      if (specPortDirection.isPresent()) {
        Set<PortSymbol> ownPortsWithChangedDirection = ownPortsCurrentName.stream()
          .filter(p -> PortDirection.of(p) != specPortDirection.get())
          .collect(Collectors.toSet());

        for (PortSymbol ownPortWithChangedDirection : ownPortsWithChangedDirection) {
          logErrorMaybeWithSourceLocation(
            ArcError.REFINEMENT_PORT_DIRECTION_CHANGED.format(
              ownPortWithChangedDirection.getName(),
              ownPortWithChangedDirection.isIncoming() ? "in" : "out",
              specification.getTypeInfo().getName()),
            deriveLogLocationForPort(ownPortWithChangedDirection, compSym)
          );
        }
      } else {
        logDebugMaybeWithSourceLocation(
          () -> String.format("Not checking port direction conformance of coco '%s' on port" +
            " '%s' with respect to specification '%s', as the specification's port direction is " +
            "ambiguous.", this.getClass().getName(), portName, specification.getTypeInfo().getName()),
          compSym.getPort(portName, true).flatMap(p -> deriveLogLocationForPort(p, compSym))
        );
      }
    }
  }

  /**
   * <ul>
   *   <li> For incoming ports: checks whether a refinement does not newly
   *   introduce sync constraints on the input where a specification has no such
   *   constraint.</li>
   *   <li> For outgoing ports: checks whether a refinement does not remove the
   *   sync guarantee on the output where a specification provided such a
   *   guarantee.</li>
   * </ul>
   * If the specification has multiple ports with the same name by error, we still
   * check the coco if their timing and direction are unambiguous.
   */
  protected void checkMatchingTiming(@NotNull ComponentTypeSymbol compSym,
                                     @NotNull NameToPorts ownPorts,
                                     @NotNull CompKindExpression specification,
                                     @NotNull NameToPorts specPorts) {
    Preconditions.checkNotNull(compSym);
    Preconditions.checkNotNull(ownPorts);
    Preconditions.checkNotNull(specification);
    Preconditions.checkNotNull(specPorts);

    // We only check matching timing for ports that are present in both:
    // The specification and the concretization
    Set<String> commonPortNames = intersection(
      ownPorts.getAllPortNames(),
      specPorts.getAllPortNames()
    );

    for (String portName : commonPortNames) {

      // The port name could be declared with multiple ports by error.
      // If the port timing is still unambiguous in the specification, we can still check whether
      // the ports in the concretizations have compatible timing.
      Collection<PortSymbol> ownPortsCurrentName = ownPorts.getPortsWithName(portName);
      Collection<PortSymbol> specPortsCurrentName = specPorts.getPortsWithName(portName);

      // Rules regarding timing depend on the port direction. We can only check them if the
      // direction in the specification is unambiguous. In the concretization, we can only check
      // ports that maintain that direction.
      Optional<PortDirection> specDirection = getUnambiguousDirection(specPortsCurrentName);
      Optional<Timing> specTiming = getUnambiguousTiming(specPortsCurrentName);
      if (specDirection.isPresent() && specTiming.isPresent()) {

        checkMatchingTimingForEquallyNamedPorts(
          ownPortsCurrentName,
          specPortsCurrentName,
          specDirection.get(),
          specTiming.get(),
          compSym,
          specification
        );

      } else {
        logDebugMaybeWithSourceLocation(
          () -> String.format("Not checking port timing conformance of coco '%s' on port" +
            " '%s' with respect to specification '%s', as the specification's port %s is " +
            "ambiguous.",
            this.getClass().getName(),
            portName,
            specification.getTypeInfo().getName(),
            specDirection.isEmpty() ? "direction" : "timing"),
          compSym.getPort(portName, true).flatMap(p -> deriveLogLocationForPort(p, compSym))
        );
      }
    }
  }

  /**
   * <ul>
   * <li> For incoming ports: checks whether a refinement does not newly
   *   introduce sync constraints on the input where a specification has no such
   *   constraint.</li>
   *   <li> For outgoing ports: checks whether a refinement does not remove the
   *   sync guarantee on the output where a specification provided such a
   *   guarantee.</li>
   * </ul>
   * Given that the direction of the {@code specificationPorts} is always
   * {@code directionInSpecification} and that all ports have the same name
   */
  protected final void checkMatchingTimingForEquallyNamedPorts(
    @NotNull Collection<PortSymbol> concretizationPorts,
    @NotNull Collection<PortSymbol> specificationPorts,
    @NotNull PortDirection directionInSpecification,
    @NotNull Timing timingInSpecification,
    @NotNull ComponentTypeSymbol compSym,
    @NotNull CompKindExpression specification) {

    Preconditions.checkNotNull(concretizationPorts);
    Preconditions.checkNotNull(specificationPorts);
    Preconditions.checkNotNull(directionInSpecification);
    Preconditions.checkNotNull(timingInSpecification);
    Preconditions.checkNotNull(specification);

    // Incoming ports must not newly introduce sync timing during refinement
    if (directionInSpecification == PortDirection.INCOMING
      && timingInSpecification == Timing.TIMED) {

      Map<Boolean, List<PortSymbol>> portsByIsIncoming = concretizationPorts.stream()
        .collect(Collectors.partitioningBy(PortSymbol::isIncoming));

      Set<PortSymbol> incomingSyncedPorts = portsByIsIncoming.get(true).stream()
        .filter(PortSymbol::isIncoming)
        .filter(p -> p.getTiming() == Timing.TIMED_SYNC)
        .collect(Collectors.toSet());

      for (PortSymbol newlySyncedPort : incomingSyncedPorts) {
        logErrorMaybeWithSourceLocation(
          ArcError.REFINEMENT_TIMING_MISMATCH_IN.format(
            newlySyncedPort.getName(), "not synchronous",
            specification.getTypeInfo().getName(), "synchronous"),
          deriveLogLocationForPort(newlySyncedPort, compSym)
        );
      }

      for (PortSymbol changedDirectionPort : portsByIsIncoming.get(false)) {
        logDebugMaybeWithSourceLocation(
          () -> String.format("Not checking port timing conformance of coco '%s' on port" +
            " '%s' with respect to specification '%s', as the port changes the direction from the" +
            "specification.",
            this.getClass().getName(),
            changedDirectionPort.getName(),
            specification.getTypeInfo().getName()),
          deriveLogLocationForPort(changedDirectionPort, compSym)
        );
      }

    // Outgoing ports must not remove sync timing during refinement
    } else if (directionInSpecification == PortDirection.OUTGOING
      && timingInSpecification == Timing.TIMED_SYNC) {

      Map<Boolean, List<PortSymbol>> portsByIsOutgoing = concretizationPorts.stream()
        .collect(Collectors.partitioningBy(PortSymbol::isOutgoing));

      Set<PortSymbol> outgoingPortsNotSynced = portsByIsOutgoing.get(true).stream()
        .filter(p -> p.getTiming() != Timing.TIMED_SYNC)
        .collect(Collectors.toSet());

      for (PortSymbol portNotSyncedAnymore : outgoingPortsNotSynced) {
        logErrorMaybeWithSourceLocation(
          ArcError.REFINEMENT_TIMING_MISMATCH_OUT.format(
            portNotSyncedAnymore.getName(), "not synchronous",
            specification.getTypeInfo().getName(), "synchronous"),
          deriveLogLocationForPort(portNotSyncedAnymore, compSym)
        );
      }

      for (PortSymbol changedDirectionPort : portsByIsOutgoing.get(false)) {
        logDebugMaybeWithSourceLocation(
          () -> String.format("Not checking port timing conformance of coco '%s' on port" +
            " '%s' with respect to specification '%s', as the port changes the direction from the" +
            "specification.",
            this.getClass().getName(),
            changedDirectionPort.getName(),
            specification.getTypeInfo().getName()),
          deriveLogLocationForPort(changedDirectionPort, compSym)
        );
      }
    }
  }

  /**
   * <ul>
   *   <li> For incoming ports: checks whether a refinement's port has the same
   *   or a super type of all specification ports with the same name, thereby
   *   loosening the assumption on the environment.</li>
   *   <li> For outgoing ports: checks whether a refinement's port has the same
   *   or a sub type of all specification ports with the same name, thereby
   *   fulfilling its contract to the environment.</li>
   * </ul>
   * If the specification has multiple ports with the same name by error, we still
   * check the coco if their direction is unambiguous.
   */
  protected void checkMatchingTypes(@NotNull ComponentTypeSymbol compSym,
                                    @NotNull NameToPorts ownPorts,
                                    @NotNull CompKindExpression specification,
                                    @NotNull NameToPorts specPorts) {
    Preconditions.checkNotNull(compSym);
    Preconditions.checkNotNull(ownPorts);
    Preconditions.checkNotNull(specification);
    Preconditions.checkNotNull(specPorts);

      // We only check matching types for ports that are present in both:
      // The specification and the concretization
      Set<String> commonPortNames = intersection(
        ownPorts.getAllPortNames(),
        specPorts.getAllPortNames()
      );

      for (String portName : commonPortNames) {

      // The port name could be declared with multiple ports by error.
      // We check against every port type with that name from the specification.
      Collection<PortSymbol> ownPortsCurrentName = ownPorts.getPortsWithName(portName);
      Collection<PortSymbol> specPortsCurrentName = specPorts.getPortsWithName(portName);

      // Rules regarding types depend on the port direction. We can only check them if the
      // direction is unambiguous. In the concretization, we can only check ports that maintain
      // that direction.
      Optional<PortDirection> specPortDirection = getUnambiguousDirection(specPortsCurrentName);
      if (specPortDirection.isPresent()) {

        Map<Boolean, List<PortSymbol>> ownPortsByDirectionConformance =
          ownPortsCurrentName.stream()
            .collect(Collectors.partitioningBy(p -> PortDirection.of(p) == specPortDirection.get()));

        checkMatchingTypesForEquallyNamedPorts(
          ownPortsByDirectionConformance.get(true), specPortsCurrentName,
          specPortDirection.get(), compSym, specification
        );

        for (PortSymbol changedDirectionPort : ownPortsByDirectionConformance.get(false)) {
          logDebugMaybeWithSourceLocation(
            () -> String.format("Not checking port type conformance of coco '%s' on port" +
              " '%s' with respect to specification '%s', as the port changes the direction from the" +
              "specification.",
              this.getClass().getName(),
              changedDirectionPort.getName(),
              specification.getTypeInfo().getName()),
            deriveLogLocationForPort(changedDirectionPort, compSym)
          );
        }
      } else {
        // Port direction is ambiguous
        logDebugMaybeWithSourceLocation(
          () -> String.format("Not checking port type conformance of coco '%s' on port" +
            " '%s' with respect to specification '%s', as the specification's port direction is " +
            "ambiguous.", this.getClass().getName(), portName, specification.getTypeInfo().getName()),
          compSym.getPort(portName, true).flatMap(p -> deriveLogLocationForPort(p, compSym))
        );
      }
    }
  }

  /**
   * <ul>
   *   <li> For incoming ports: checks whether a refinement's port has the same
   *   or a super type of all specification ports with the same name, thereby
   *   loosening the assumption on the environment.</li>
   *   <li> For outgoing ports: checks whether a refinement's port has the same
   *   or a sub type of all specification ports with the same name, thereby
   *   fulfilling its contract to the environment.</li>
   * </ul>
   * Given that the direction of the {@code concretizationPorts} and
   * {@code specificationPorts} is both {@code directionInSpecification} and all ports
   * have the same name.
   *
   * @param compSym The component type of the concretization. Supplied for better log messages
   * @param specification Supplied for better log messages
   */
  protected final void checkMatchingTypesForEquallyNamedPorts(
    @NotNull Collection<PortSymbol> concretizationPorts,
    @NotNull Collection<PortSymbol> specificationPorts,
    @NotNull PortDirection directionInSpecification,
    @NotNull ComponentTypeSymbol compSym,
    @NotNull CompKindExpression specification) {

    Preconditions.checkNotNull(concretizationPorts);
    Preconditions.checkNotNull(specificationPorts);
    Preconditions.checkNotNull(directionInSpecification);
    Preconditions.checkNotNull(compSym);
    Preconditions.checkNotNull(specification);

    for (PortSymbol specPort : specificationPorts) {
      SymTypeExpression specType = specPort.getType();
      for (PortSymbol ownPortSameDirection : concretizationPorts) {
        SymTypeExpression ownType = ownPortSameDirection.getType();

        // Incoming ports may turn into super types during refinement
        if (directionInSpecification == PortDirection.INCOMING
          && !SymTypeRelations.isSubTypeOf(specType, ownType)) {

          logErrorMaybeWithSourceLocation(
            ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH.format(
              ownPortSameDirection.getName(), ownType.print(),
              specType.print(), specification.getTypeInfo().getName()),
            deriveLogLocationForPort(ownPortSameDirection, compSym)
          );

        // Outgoing ports may turn into sub types during refinement
        } else if (directionInSpecification == PortDirection.OUTGOING
          && !SymTypeRelations.isSubTypeOf(ownType, specType)) {

          logErrorMaybeWithSourceLocation(
            ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH.format(
              ownPortSameDirection.getName(), ownType.print(),
              specType.print(), specification.getTypeInfo().getName()),
            deriveLogLocationForPort(ownPortSameDirection, compSym)
          );
        }
      }
    }
  }

  /** Calculates {@code baseSet} \ {@code cuttingSet}: The complement of {@code cuttingSet} in {@code baseSet} */
  protected static Set<String> complementIn(Set<String> baseSet, Set<String> cuttingSet) {
    Preconditions.checkNotNull(baseSet);
    Preconditions.checkNotNull(cuttingSet);

    Set<String> differenceSet = new LinkedHashSet<>(baseSet);
    differenceSet.removeAll(cuttingSet);

    return differenceSet;
  }

  protected static Set<String> intersection(@NotNull Set<String> a, @NotNull Set<String> b) {
    Preconditions.checkNotNull(a);
    Preconditions.checkNotNull(b);

    Set<String> intersection = new LinkedHashSet<>(a);
    intersection.retainAll(b);

    return intersection;
  }

  /** Returns the unambiguous port timing of the ports if it is the same for all. */
  protected static Optional<Timing> getUnambiguousTiming(@NotNull Collection<PortSymbol> ports) {
    return getUnambiguous(
      ports.stream().map(PortSymbol::getTiming).collect(Collectors.toList())
    );
  }

  /** Returns the unambiguous port direction of the ports if it is the same for all. */
  protected static Optional<PortDirection> getUnambiguousDirection(@NotNull Collection<PortSymbol> ports) {
    return getUnambiguous(
      ports.stream().map(PortDirection::of).collect(Collectors.toList())
    );
  }
  /** Returns the first element if all elements equal each other. Otherwise: return empty */
  protected static <T> Optional<T> getUnambiguous(List<T> elements) {
    Preconditions.checkNotNull(elements);

    if (elements.isEmpty()) {
      return Optional.empty();
    } else {

      T unambiguous = elements.get(0);
      for (T element : elements.subList(1, elements.size())) {
        if (!unambiguous.equals(element)) {
          return Optional.empty();
        }
      }

      return Optional.of(unambiguous);
    }
  }

  /**
   * Returns an AST node where to print an error related to a port of a component.<p>
   * If the port is directly declared by the component type, then the port ast
   * is returned. If the port is inherited, then the method returns the location of
   * the extension declaration of the first parent component that has the respective
   * port.
   *
   * @return An empty optional if no ast node could be determined
   *
   * @param port The port of {@code comp}, maybe inherited
   * @param comp A component that has the {@code port}, maybe inherited.
   */
  protected Optional<ASTNode> deriveLogLocationForPort(@NotNull PortSymbol port,
                                                       @NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(port);

    boolean portIsInherited = !comp.getPorts().contains(port);  // getPorts omits inherited ports
    if (portIsInherited) {
      // Inherited port -> there must be a parent declaration to which we can refer to
      // We check wich direct super component has the port (no matter whether inherited
      // transitively or not) and use the first match as finding location.
      return
        comp.getSuperComponentsList().stream().filter(
          c -> c.getTypeInfo().getAllPorts().contains(port)  // getAllPorts includes inherited ports
        )
        .findFirst()
        .flatMap(CompKindExpression::getSourceNode);
    } else {
      return port.isPresentAstNode() ? Optional.of(port.getAstNode()) : Optional.empty();
    }
  }

  /**
   * Logs an error with message {@code errorMsg}. If {@code astLocation} is present, its source
   * location is logged as well.
   */
  protected void logErrorMaybeWithSourceLocation(@NotNull String errorMsg,
                                                 @NotNull Optional<ASTNode> astLocation) {
    Preconditions.checkNotNull(errorMsg);
    Preconditions.checkNotNull(astLocation);

    if (astLocation.isPresent()) {
      Log.error(
        errorMsg,
        astLocation.get().get_SourcePositionStart(),
        astLocation.get().get_SourcePositionEnd()
      );
    } else {
      Log.error(errorMsg);
    }
  }

  /**
   * Logs a debug with message {@code debugMsg}. If {@code astLocation} is present, its source
   * location is logged as well.
   */
  protected void logDebugMaybeWithSourceLocation(@NotNull Supplier<String> debugMsg,
                                                 @NotNull Optional<ASTNode> astLocation) {
    Preconditions.checkNotNull(debugMsg);
    Preconditions.checkNotNull(astLocation);

    if (astLocation.isPresent()) {
      Log.debug(
        debugMsg,
        astLocation.get().get_SourcePositionStart(),
        "Cocos"
      );
    } else {
      Log.debug(debugMsg, "Cocos");
    }
  }
}
