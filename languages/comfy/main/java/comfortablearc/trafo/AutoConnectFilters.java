/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.trafo;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTFullyConnectedComponentInstantiation;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
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
  public static boolean isAFullyConnectedComponent(@NotNull SubcomponentSymbol subComp,
                                                   @NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.getSubcomponents().contains(subComp));
    Preconditions.checkState(comp.isPresentAstNode());
    ASTArcComponentType astComp = (ASTArcComponentType) comp.getAstNode();

    return astComp
      .getBody()
      .streamArcElements()
      .filter(ASTFullyConnectedComponentInstantiation.class::isInstance)
      .map(ASTFullyConnectedComponentInstantiation.class::cast)
      .map(ASTFullyConnectedComponentInstantiation::getComponentInstanceList).flatMap(List::stream)
      .anyMatch(i -> i.getSymbol() == subComp);
  }

  public static List<ASTPortAccess> getUnconnectedOuterSourcePorts(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.isPresentAstNode());
    ASTArcComponentType astComp = (ASTArcComponentType) comp.getAstNode();

    return comp.getIncomingPorts().stream()
      .filter(p -> astComp
        .getConnectors().stream()
        .noneMatch(c -> c.getSource().isPresentPortSymbol() && c.getSource().getPortSymbol().equals(p)))
      .map(ASTPortAccess::of)
      .collect(Collectors.toList());
  }

  public static List<ASTPortAccess> getUnconnectedOuterTargetPorts(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.isPresentAstNode());
    ASTArcComponentType astComp = (ASTArcComponentType) comp.getAstNode();

    return comp.getOutgoingPorts().stream()
      .filter(p -> astComp
        .getConnectors().stream()
        .noneMatch(c -> c.getTargetList().stream()
          .noneMatch(t -> t.isPresentPortSymbol() && t.getPortSymbol().equals(p))))
      .map(ASTPortAccess::of)
      .collect(Collectors.toList());
  }

  public static List<ASTPortAccess> getUnconnectedSourcePorts(@NotNull SubcomponentSymbol subComp,
                                                              @NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(subComp);
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.getSubcomponents().contains(subComp));
    Preconditions.checkState(comp.isPresentAstNode());
    ASTArcComponentType astComp = (ASTArcComponentType) comp.getAstNode();

    return subComp.getType().getTypeInfo().getOutgoingPorts().stream()
      .filter(p -> astComp
        .getConnectors().stream()
        .noneMatch(c -> c.getSource().isPresentPortSymbol() && c.getSource().getPortSymbol().equals(p)))
      .map(p -> ASTPortAccess.of(subComp, p))
      .collect(Collectors.toList());
  }

  public static List<ASTPortAccess> getUnconnectedTargetPorts(@NotNull SubcomponentSymbol subComp,
                                                              @NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(subComp);
    Preconditions.checkNotNull(comp);
    Preconditions.checkArgument(comp.getSubcomponents().contains(subComp));
    Preconditions.checkState(comp.isPresentAstNode());
    ASTArcComponentType astComp = (ASTArcComponentType) comp.getAstNode();

    return subComp.getType().getTypeInfo().getIncomingPorts().stream()
      .filter(p -> astComp
        .getConnectors().stream()
        .noneMatch(c -> c.getTargetList().stream()
          .noneMatch(t -> t.isPresentPortSymbol() && t.getPortSymbol().equals(p))))
      .map(p -> ASTPortAccess.of(subComp, p))
      .collect(Collectors.toList());
  }
}
