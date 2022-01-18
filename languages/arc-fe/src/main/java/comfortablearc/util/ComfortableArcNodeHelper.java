/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.util;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import comfortablearc._ast.*;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

/**
 * Provides utility functions for handling nodes of ComfortableArc.
 */
public class ComfortableArcNodeHelper {

  /**
   * Searches the body of the provided component type node for auto connect
   * statements and returns a {@code Collection} of the found occurrences.
   *
   * @param node the component type whose body to search
   * @return a possibly empty {@code Collection} of auto connect statements
   */
  public static Collection<ASTArcAutoConnect> getAutoConnects(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return node.getBody().streamArcElements().filter(element -> element instanceof ASTArcAutoConnect)
      .map(ac -> (ASTArcAutoConnect) ac).collect(Collectors.toList());
  }

  /**
   * Searches the body of the provided component type node for its auto connect
   * statement and returns it. Assumes that there is exactly one.
   *
   * @param node the component type whose body to search
   * @return the unique auto connect statement of the provided component type
   * @throws NoSuchElementException   if the body of the provided component type
   *                                  contains no auto connect statement
   * @throws IllegalArgumentException if the body of the provided component type
   *                                  contains more than one auto connect statement
   */
  public static ASTArcAutoConnect getAutoConnect(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return Iterables.getOnlyElement(getAutoConnects(node));
  }

  /**
   * Searches the body of the provided component type node for its auto connect
   * statements and returns a {@code Collection} the modes of the found occurrences.
   *
   * @param node the component type whose body to search
   * @return a possibly empty {@code Collection} of auto connect modes
   */
  public static Collection<ASTArcACMode> getACModes(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getAutoConnects(node).stream().map(ASTArcAutoConnect::getArcACMode).collect(Collectors.toList());
  }

  /**
   * Searches the body of the provided component type node for its auto connect
   * statement and returns its mode. Assumes that there is exactly one.
   *
   * @param node the component type whose body to search
   * @return the unique auto connect mode of the provided component type
   * @throws NoSuchElementException   if the body of the provided component type
   *                                  contains no auto connect statement
   * @throws IllegalArgumentException if the body of the provided component type
   *                                  contains more than one auto connect statement
   */
  public static ASTArcACMode getACMode(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return Iterables.getOnlyElement(getACModes(node));
  }

  public static boolean isMaxOneACModePresent(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getACModes(node).size() <= 1;
  }

  public static boolean isUniqueACModePresent(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getACModes(node).size() == 1;
  }

  public static boolean isACOff(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getACModes(node).isEmpty() || getAutoConnects(node).stream()
      .findFirst().map(ASTArcAutoConnect::isACOff).orElse(false);
  }

  public static boolean isACPortActive(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return !getACModes(node).isEmpty() && getAutoConnects(node).stream()
      .findFirst().map(ASTArcAutoConnect::isACPortActive).orElse(false);
  }

  public static boolean isACTypeActive(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return !getACModes(node).isEmpty() && getAutoConnects(node).stream()
      .findFirst().map(ASTArcAutoConnect::isACTypeActive).orElse(false);
  }

  /**
   * Searches the body of the provided component type node for its port complete
   * statements and returns a {@code Collection} of the found occurrences.
   *
   * @param node the component type of which body to search
   * @return a possibly empty {@code Collection} of port complete nodes
   */
  public static Collection<ASTPortComplete> getPortCompletes(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return node.getBody().streamArcElements().filter(element -> element instanceof ASTPortComplete)
      .map(pc -> (ASTPortComplete) pc).collect(Collectors.toList());
  }

  /**
   * Searches the body of the provided component type node for its port complete
   * statement and returns it. Assumes that there is exactly one.
   *
   * @param node the component type whose body to search
   * @return the unique port complete statement of the provided component type
   * @throws NoSuchElementException   if the body of the provided component type
   *                                  contains no port complete statement
   * @throws IllegalArgumentException if the body of the provided component type
   *                                  contains more than one port complete statement
   */
  public static ASTPortComplete getPortComplete(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return Iterables.getOnlyElement(getPortCompletes(node));
  }

  public static boolean isMaxOnePortCompletePresent(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getPortCompletes(node).size() <= 1;
  }

  public static boolean isPortCompleteActive(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return !getPortCompletes(node).isEmpty();
  }

  /**
   * Searches the body of the provided component type node for its auto instantiate
   * statements and returns a {@code Collection} of the found occurrences.
   *
   * @param node the component type of which body to search
   * @return a possibly empty {@code Collection} of auto instantiate nodes
   */
  public static Collection<ASTArcAutoInstantiate> getAutoInstantiates(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return node.getBody().streamArcElements().filter(element -> element instanceof ASTArcAutoInstantiate)
      .map(ai -> (ASTArcAutoInstantiate) ai).collect(Collectors.toList());
  }

  /**
   * Searches the body of the provided component type node for its auto instantiate
   * statement and returns it. Assumes that there is exactly one.
   *
   * @param node the component type whose body to search
   * @return the unique auto instantiate statement of the provided component type
   * @throws NoSuchElementException   if the body of the provided component type
   *                                  contains no auto instantiate statement
   * @throws IllegalArgumentException if the body of the provided component type
   *                                  contains more than one auto instantiate statement
   */
  public static ASTArcAutoInstantiate getAutoInstantiate(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return Iterables.getOnlyElement(getAutoInstantiates(node));
  }

  /**
   * Searches the body of the provided component type node for its auto instantiate
   * statements and returns a {@code Collection} the modes of the found occurrences.
   *
   * @param node the component type whose body to search
   * @return a possibly empty {@code Collection} of auto instantiate modes
   */
  public static Collection<ASTArcAIMode> getAIModes(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getAutoInstantiates(node).stream().map(ASTArcAutoInstantiate::getArcAIMode).collect(Collectors.toList());
  }

  /**
   * Searches the body of the provided component type node for its auto instantiate
   * statement and returns its mode. Assumes that there is exactly one.
   *
   * @param node the component type whose body to search
   * @return the unique auto instantiate mode of the provided component type
   * @throws NoSuchElementException   if the body of the provided component type
   *                                  contains no auto instantiate statement
   * @throws IllegalArgumentException if the body of the provided component type
   *                                  contains more than one auto instantiate statement
   */
  public static ASTArcAIMode getAIMode(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return Iterables.getOnlyElement(getAIModes(node));
  }

  public static boolean isMaxOneAIModePresent(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getAIModes(node).size() <= 1;
  }

  public static boolean isUniqueAIModePresent(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getAIModes(node).size() == 1;
  }

  public static boolean isAIOff(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getAIModes(node).isEmpty() || getAutoInstantiates(node).stream()
      .findFirst().map(ASTArcAutoInstantiate::isAIOff).orElse(false);
  }

  public static boolean isAIOn(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return getAutoInstantiates(node).stream().findFirst().map(ASTArcAutoInstantiate::isAIOn).orElse(false);
  }

  /**
   * @param node         the component type whose body to search
   * @param subcomponent the name of component instance of interest
   * @return {@code true} if the provided component type node has a fully
   * connected subcomponent of the given name, else {@code false}
   */
  public static boolean isFullyConnected(@NotNull ASTComponentType node, @NotNull String subcomponent) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(subcomponent);
    return node.getBody().getArcElementList().stream().anyMatch(e -> e instanceof ASTFullyConnectedComponentInstantiation
      && ((ASTComponentInstantiation) e).getInstancesNames().contains(subcomponent));
  }
}