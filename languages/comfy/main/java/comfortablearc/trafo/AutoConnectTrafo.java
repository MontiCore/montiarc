/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.trafo;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTArcACMode;
import comfortablearc._ast.ASTArcAutoConnect;
import de.monticore.types3.ISymTypeRelations;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
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

  protected final ISymTypeRelations tr;

  protected Stack<ASTComponentType> comps;
  protected Stack<List<ASTArcElement>> toAdd;

  public AutoConnectTrafo(@NotNull ISymTypeRelations tr) {
    this.tr = Preconditions.checkNotNull(tr);
    this.comps = new Stack<>();
    this.toAdd = new Stack<>();
  }

  @Override
  public void apply(@NotNull ASTArcAutoConnect node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(!this.comps.isEmpty());
    Preconditions.checkState(!this.toAdd.isEmpty());

    List<ComponentInstanceSymbol> connectableSubComps = this.comps.peek().getSymbol()
      .getSubComponents().stream()
      .filter(subComp -> !isAFullyConnectedComponent(subComp, this.comps.peek().getSymbol()))
      .collect(Collectors.toList());

    List<ASTPortAccess> unconnectedSources = new ArrayList<>(getUnconnectedOuterSourcePorts(this.comps.peek().getSymbol()));
    List<ASTPortAccess> unconnectedTargets = new ArrayList<>(getUnconnectedOuterTargetPorts(this.comps.peek().getSymbol()));

    connectableSubComps.stream()
      .map(subComp -> getUnconnectedSourcePorts(subComp, this.comps.peek().getSymbol()))
      .forEach(unconnectedSources::addAll);
    connectableSubComps.stream()
      .map(subComp -> getUnconnectedTargetPorts(subComp, this.comps.peek().getSymbol()))
      .forEach(unconnectedTargets::addAll);

    Map<ASTPortAccess, List<ASTPortAccess>> matches = this.findMatches(unconnectedSources, unconnectedTargets, node.getArcACMode());

    List<ASTArcElement> connectors = new ArrayList<>(matches.size());

    for (ASTPortAccess source : matches.keySet()) {
      for (ASTPortAccess target : matches.get(source)) {
        source.setEnclosingScope(node.getEnclosingScope());
        target.setEnclosingScope(node.getEnclosingScope());
        connectors.add(node.connectPorts(source, target));
      }
    }

    this.toAdd.peek().addAll(connectors);
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    this.comps.add(node);
  }

  @Override
  public void endVisit(@NotNull ASTComponentType node) {
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

    Map<ASTPortAccess, List<ASTPortAccess>> matches = new HashMap<>();

    for (ASTPortAccess source : sources) {
      matches.put(source, this.findMatches(source, targets, mode));
    }

    return matches;
  }

  /**
   * Find to the given source all the targets that are matches in the context
   * of the given auto-connect mode.
   *
   * @param source  the source to match
   * @param targets the targets to match
   * @param mode    the matching condition
   * @return the matches to the source
   */
  protected List<ASTPortAccess> findMatches(@NotNull ASTPortAccess source,
                                            @NotNull List<ASTPortAccess> targets,
                                            @NotNull ASTArcACMode mode) {
    Preconditions.checkNotNull(source);
    Preconditions.checkNotNull(targets);

    List<ASTPortAccess> matches = new ArrayList<>();

    for (ASTPortAccess target : targets) {
      if (mode.matches(source.getPortSymbol(), target.getPortSymbol(), this.tr)) matches.add(target);
    }

    return matches;
  }
}
