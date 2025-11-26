/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.trafo;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTArcACMode;
import comfortablearc._ast.ASTArcAutoConnect;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import static comfortablearc.trafo.AutoConnectFilters.getUnconnectedOuterSourcePorts;
import static comfortablearc.trafo.AutoConnectFilters.getUnconnectedOuterTargetPorts;
import static comfortablearc.trafo.AutoConnectFilters.getUnconnectedSourcePorts;
import static comfortablearc.trafo.AutoConnectFilters.getUnconnectedTargetPorts;
import static comfortablearc.trafo.AutoConnectFilters.isAFullyConnectedComponent;

public class AutoConnectTrafo implements IAutoConnectTrafo {

  protected Stack<ASTArcComponentType> comps;
  protected Stack<List<ASTArcElement>> toAdd;

  public AutoConnectTrafo() {
    this.comps = new Stack<>();
    this.toAdd = new Stack<>();
  }

  @Override
  public void apply(@NotNull ASTArcAutoConnect node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(!this.comps.isEmpty());
    Preconditions.checkState(!this.toAdd.isEmpty());

    List<SubcomponentSymbol> connectableSubComps = this.comps.peek().getSymbol()
      .getSubcomponents().stream()
      .filter(subComp -> !isAFullyConnectedComponent(subComp, this.comps.peek().getSymbol()))
      .collect(Collectors.toList());

    List<ASTPortAccess> unconnectedSources = new ArrayList<>(getUnconnectedOuterSourcePorts(this.comps.peek().getSymbol()));
    List<ASTPortAccess> unconnectedTargets = new ArrayList<>(getUnconnectedOuterTargetPorts(this.comps.peek().getSymbol()));

    connectableSubComps.stream()
      .filter(SubcomponentSymbol::isTypePresent)
      .map(subComp -> getUnconnectedSourcePorts(subComp, this.comps.peek().getSymbol()))
      .forEach(unconnectedSources::addAll);
    connectableSubComps.stream()
      .filter(SubcomponentSymbol::isTypePresent)
      .map(subComp -> getUnconnectedTargetPorts(subComp, this.comps.peek().getSymbol()))
      .forEach(unconnectedTargets::addAll);

    List<ASTPortAccess> connectedTargets = new ArrayList<>();

    List<ASTArcElement> connectors = new ArrayList<>();
    for (ASTPortAccess target : new ArrayList<>(unconnectedTargets)) {
      List<ASTPortAccess> matches = findMatches(target, unconnectedSources, node.getArcACMode());

      if (matches.size() == 1) {
        ASTPortAccess source = matches.get(0);
        if (!connectedTargets.contains(target)) {
          source.setEnclosingScope(node.getEnclosingScope());
          target.setEnclosingScope(node.getEnclosingScope());
          connectors.add(node.connectPorts(source, target));
          connectedTargets.add(target);
        }
      } else if (matches.size() > 1) {
        Log.debug("Multiple matching source ports found for target port: " + target.getQName(), this.getClass().getName());
      }
    }

    this.toAdd.peek().addAll(connectors);
  }

  @Override
  public void visit(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    this.comps.add(node);
  }

  @Override
  public void endVisit(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(!this.comps.isEmpty());
    Preconditions.checkState(this.comps.peek().equals(node));
    this.comps.pop();
  }

  @Override
  public void visit(@NotNull ASTComponentBody node) {
    Preconditions.checkNotNull(node);
    this.toAdd.add(new ArrayList<>());
  }

  @Override
  public void endVisit(@NotNull ASTComponentBody node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(!this.toAdd.isEmpty());
    node.addAllArcElements(this.toAdd.pop());
  }

  /**
   * Find to each of the given sources all the targets that are matches in the
   * context of the given auto-connect mode.
   *
   * @param sources the sources to match
   * @param targets the targets to match
   * @param mode    the matching condition
   * @return the matches to each source
   */
  protected Map<ASTPortAccess, List<ASTPortAccess>> findMatches(@NotNull List<ASTPortAccess> sources,
                                                                @NotNull List<ASTPortAccess> targets,
                                                                @NotNull ASTArcACMode mode) {
    Preconditions.checkNotNull(sources);
    Preconditions.checkNotNull(targets);
    Preconditions.checkNotNull(mode);

    Map<ASTPortAccess, List<ASTPortAccess>> matches = new LinkedHashMap<>();

    for (ASTPortAccess source : sources) {
      matches.put(source, this.findMatches(source, targets, mode));
    }

    return matches;
  }

  /**
   * Find to the given source all the targets that are matches in the context
   * of the given auto-connect mode.
   *
   * @param sources the source to match
   * @param target  the targets to match
   * @param mode    the matching condition
   * @return the matches to the source
   */
  protected List<ASTPortAccess> findMatches(@NotNull ASTPortAccess target,
                                            @NotNull List<ASTPortAccess> sources,
                                            @NotNull ASTArcACMode mode) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(sources);

    List<ASTPortAccess> matches = new ArrayList<>();

    for (ASTPortAccess source : sources) {
      if (mode.matches(source.getPortSymbol(), target.getPortSymbol())) matches.add(source);
    }

    return matches;
  }
}
