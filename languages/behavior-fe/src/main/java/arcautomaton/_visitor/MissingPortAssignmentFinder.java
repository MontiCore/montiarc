package arcautomaton._visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsHandler;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MissingPortAssignmentFinder implements ExpressionsBasisVisitor2, MCCommonStatementsHandler {

  /**
   * key: output-port name
   * value: ast node where the port does [not] occur
   */
  protected Deque<Map<String, PortAssignment>> mapStack = new LinkedList<>();

  /**
   * @return current map
   */
  protected Map<String, PortAssignment> getMap(){
    return mapStack.getLast();
  }

  /**
   * searches through the given expressions and finds port access
   * @param expressions code fragments that are executed together
   * @return map with:
   * key -> name of an output port symbol;
   * value -> {@link PortAssignment};
   * Warning: there is no guarantee that every output-port of a component occurs in this map.
   * So when analyzing the returned map, one should rather iterate over {@link ComponentTypeSymbol#getOutgoingPorts()} instead of {@link Map#keySet()}
   */
  public Map<String, PortAssignment> searchIn(ASTNode... expressions){
    setTraverser(MCCommonStatementsMill.traverser());
    getTraverser().add4ExpressionsBasis(this);

    mapStack.push(new HashMap<>());
    for(ASTNode statement: expressions) {
      try {
        statement.accept(getTraverser());
      } catch(BreakOccurred b){
        // assuming that breaks that occur here do not affect the following statements
      }
    }
    return mapStack.pop();
  }

  /**
   * Searches for port-occurrence in alternatives, by traversing the given expressions/statements
   * For each port occurrence:
   * If the map already contains an occurrence for that port, the map remains unchanged.
   * If no branch contains an occurrence, the map remains unchanged.
   * If every branch contains an occurrence of that port, a random one is put to the map.
   * If there are branches where the port occurs and others where it does not, a missing-occurrence is put to the map.
   * @param expressions mutually exclusive branches of the program flow.
   * @throws BreakOccurred if a such a statement occurred in any of the alternatives to analyze
   */
  protected void searchAlternatives(ASTNode... expressions) throws BreakOccurred{
    Preconditions.checkNotNull(expressions);
    boolean[] broken = {false};
    Map<String, List<PortAssignment>> map = inlineMap(Arrays.stream(expressions).map(branch -> {
      mapStack.push(new HashMap<>());
      try {
        branch.accept(getTraverser());
      } catch(BreakOccurred e){
        broken[0] = true;
      }
      return mapStack.pop();
    }).collect(Collectors.toList()));
    map.keySet().stream()
        .filter(key -> !getMap().containsKey(key) || !getMap().get(key).isOccurring())
        .filter(key -> map.get(key).stream().anyMatch(PortAssignment::isOccurring))
        .forEach(key -> getMap().put(key, map.get(key).stream().filter(p -> !p.isOccurring()).findFirst().orElse(map.get(key).get(0))));

    if(broken[0]){
      throw new BreakOccurred();
    }
  }

  /**
   * helper-function that reorders elements
   * @param list a list of maps with elements
   * @param <K> key-type
   * @param <V> value-type
   * @return a map of lists with the same elements
   */
  protected <K, V> Map<K, List<V>> inlineMap(List<Map<K, V>> list){
    Preconditions.checkNotNull(list);
    Map<K, List<V>> returnee = new HashMap<>();
    list.forEach(map -> map.forEach((key, value) -> returnee.computeIfAbsent(key, k -> new ArrayList<>()).add(value)));
    return returnee;
  }

  @Override
  public void visit(ASTNameExpression node) {
    new StatechartNameResolver(node.getEnclosingScope())
        .resolvePort(node.getName())
        .filter(PortSymbol::isOutgoing)
        .ifPresent(port -> getMap().put(port.getFullName(), new PortAssignment(true, node)));
  }

  @Override
  public void handle(ASTIfStatement node) {
    node.getCondition().accept(getTraverser());
    if(node.isPresentElseStatement()){
      searchAlternatives(node.getThenStatement(), node.getElseStatement());
    } else {
      // if there is no else, use an empty statement to complete analysis
      searchAlternatives(node.getThenStatement(), new ASTEmptyStatementBuilder().set_SourcePositionStart(node.getThenStatement().get_SourcePositionEnd()).set_SourcePositionEnd(node.get_SourcePositionEnd()).build());
    }
  }

  @Override
  public void handle(ASTWhileStatement node) {
    // while-body is not guaranteed to run once
    // but the condition is
    node.getCondition().accept(getTraverser());
  }

  @Override
  public void handle(ASTDoWhileStatement node) {
    node.getCondition().accept(getTraverser());
    try {
      // the body of a do-while loop is guaranteed to be executed at least once...
      node.getMCStatement().accept(getTraverser());
    } catch(BreakOccurred e){
      // ... but some of it may be skipped by using a break or continue
    }
  }

  @Override
  public void handle(ASTForStatement node) {
    // only for-control is guaranteed to run, body is not
    node.getForControl().accept(getTraverser());
  }

  @Override
  public void handle(ASTCommonForControl node) {
    // expression is not guaranteed to occur, but condition and init are
    node.getForInit().accept(getTraverser());
    node.getCondition().accept(getTraverser());
  }

  @Override
  public void handle(ASTSwitchStatement node) {
    node.getExpression().accept(getTraverser());
    // only analyze switch-body if it has a default option
    if(Stream.concat(node.streamSwitchBlockStatementGroups().flatMap(ASTSwitchBlockStatementGroup::streamSwitchLabels), node.streamSwitchLabels()).anyMatch(e -> e instanceof ASTDefaultSwitchLabel)){
      // add all options that occur below a case to that case
      ASTSwitchStatement newSwitch = node.deepClone();
      for(int i = newSwitch.getSwitchBlockStatementGroupList().size(); i >= 2; i--) {
        // add all elements from bottom up, because -without a break- the last statement can be reached through the first label
        newSwitch.getSwitchBlockStatementGroup(i-2).addAllMCBlockStatements(
            newSwitch.getSwitchBlockStatementGroup(i-1).getMCBlockStatementList());
      }
      try {
        searchAlternatives(Stream.concat(newSwitch.streamSwitchBlockStatementGroups(), node.streamSwitchLabels().filter(e -> e instanceof ASTDefaultSwitchLabel).limit(1)).toArray(ASTNode[]::new));
      } catch(BreakOccurred b){
        // do not let this switch's break-exceptions propagate through
      }
    }
  }

  @Override
  public void handle(ASTBreakStatement node) {
    // do not search after break
    throw new BreakOccurred();
  }

  /**
   * saves whether and where a port is or should be written.
   * see {@link #written} and {@link #position}
   */
  public static class PortAssignment implements Comparable<PortAssignment>{
    /**
     * true, if the port has a value assigned, false if the port does not occur
     */
    protected final boolean written;

    /**
     * if {@link #written} is false:
     * most precise position for inserting a missed port statement
     * if {@link #written} is true:
     * the position where the port occurs
     */
    protected final ASTNode position;

    public PortAssignment(boolean written, ASTNode node) {
      this.written = written;
      this.position = node;
    }

    public boolean isOccurring(){
      return written;
    }

    public ASTNode getNode(){
      return position;
    }

    @Override
    public int compareTo(@NotNull PortAssignment other) {
      return Boolean.compare(written, other.written);
    }
  }

  /**
   * this exception is thrown after a break- or continue-statement occurred, and has the effect,
   * that searching cancelled until the respective structure is left
   */
  public static class BreakOccurred extends RuntimeException{
    public BreakOccurred(){
      super("This exception should not leave this class. If it is not caught, this code is faulty.");
    }
  }
}