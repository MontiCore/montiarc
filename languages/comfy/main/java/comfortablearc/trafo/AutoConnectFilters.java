/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.trafo;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTFullyConnectedComponentInstantiation;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities for auto-connection implementations.
 */
public final class AutoConnectFilters {

  /**
   * @return whether the component instance was instantiated by an {@link ASTFullyConnectedComponentInstantiation}.
   */
  public static boolean isAFullyConnectedComponent(@NotNull ComponentInstanceSymbol subComp,
                                                   @NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.getSubComponents().contains(subComp));
    Preconditions.checkState(comp.isPresentAstNode());

    return comp.getAstNode().getBody()
      .streamArcElements()
      .filter(ASTFullyConnectedComponentInstantiation.class::isInstance)
      .map(ASTFullyConnectedComponentInstantiation.class::cast)
      .map(ASTFullyConnectedComponentInstantiation::getComponentInstanceList).flatMap(List::stream)
      .anyMatch(i -> i.getSymbol() == subComp);
  }

  public static List<ASTPortAccess> getUnconnectedOuterSourcePorts(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.isPresentAstNode());

    return comp.getIncomingPorts().stream()
      .filter(p -> comp.getAstNode().getConnectors().stream()
        .noneMatch(c -> c.getSource().isPresentPortSymbol() && c.getSource().getPortSymbol().equals(p)))
      .map(ASTPortAccess::of)
      .collect(Collectors.toList());
  }

  public static List<ASTPortAccess> getUnconnectedOuterTargetPorts(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.isPresentAstNode());

    return comp.getOutgoingPorts().stream()
      .filter(p -> comp.getAstNode().getConnectors().stream()
        .noneMatch(c -> c.getTargetList().stream()
          .noneMatch(t -> t.isPresentPortSymbol() && t.getPortSymbol().equals(p))))
      .map(ASTPortAccess::of)
      .collect(Collectors.toList());
  }

  public static List<ASTPortAccess> getUnconnectedSourcePorts(@NotNull ComponentInstanceSymbol subComp,
                                                              @NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(subComp);
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.getSubComponents().contains(subComp));
    Preconditions.checkState(comp.isPresentAstNode());

    return subComp.getType().getTypeInfo().getOutgoingPorts().stream()
      .filter(p -> comp.getAstNode().getConnectors().stream()
        .noneMatch(c -> c.getSource().isPresentPortSymbol() && c.getSource().getPortSymbol().equals(p)))
      .map(p -> ASTPortAccess.of(subComp, p))
      .collect(Collectors.toList());
  }

  public static List<ASTPortAccess> getUnconnectedTargetPorts(@NotNull ComponentInstanceSymbol subComp,
                                                              @NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(subComp);
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.getSubComponents().contains(subComp));
    Preconditions.checkState(comp.isPresentAstNode());

    return subComp.getType().getTypeInfo().getIncomingPorts().stream()
      .filter(p -> comp.getAstNode().getConnectors().stream()
        .noneMatch(c -> c.getTargetList().stream()
          .noneMatch(t -> t.isPresentPortSymbol() && t.getPortSymbol().equals(p))))
      .map(p -> ASTPortAccess.of(subComp, p))
      .collect(Collectors.toList());
  }
}
