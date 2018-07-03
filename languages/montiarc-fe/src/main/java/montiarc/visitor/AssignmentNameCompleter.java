package montiarc.visitor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.monticore.commonexpressions._ast.ASTCallExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTConstantsMontiArc;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTTransition;
import montiarc._ast.ASTValuation;
import montiarc._ast.ASTValueList;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc._visitor.MontiArcVisitor;
import montiarc.helper.TypeCompatibilityChecker;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/47

/**
 * Computes the missing assignment names in reactions, stimuli and initial state declarations.
 */
public class AssignmentNameCompleter implements MontiArcVisitor {
  private final Scope automatonScope;
  
  private enum Direction {
    INPUT, OUTPUT, VARIABLE;
  }
  
  public AssignmentNameCompleter(Scope automatonScope) {
    this.automatonScope = automatonScope;
  }
  
  @Override
  public void visit(ASTInitialStateDeclaration node) {
    // set missing assignment names in all blocks of all initial state
    // declarations
    if (node.isPresentBlock()) {
      for (ASTIOAssignment assign : node.getBlock().getIOAssignmentList()) {
        if (!assign.isPresentName()) {
          // no assignment name found, so compute one based on value type
          Optional<String> sinkName = findFor(assign, false);
          if (sinkName.isPresent()) {
            assign.setName(sinkName.get());
          }
          else {
            info("No sink for initial assignment '" + assign + "'.");
          }
        }
      }
    }
  }
  
  @Override
  public void visit(ASTTransition node) {
    // set missing assignment names of all reactions of all transitions
    if (node.isPresentReaction()) {
      for (ASTIOAssignment assign : node.getReaction().getIOAssignmentList()) {
        if (!assign.isPresentName()) {
          Optional<String> sinkName = Optional.empty();
          ASTExpression expr = getFirstAssigntElement(assign).getExpression();
          // if (expr instanceof ASTCallExpression) { TODO@AB
          // if (expr.getCallExpression().get().getExpression().isPresent()) {
          // if (expr.getCallExpression().get().getExpression().get().getPrimaryExpression()
          // .isPresent()) {
          // sinkName = expr.getCallExpression().get().getExpression().get()
          // .getPrimaryExpression().get().getName();
          // }
          // }
          // }
          // no assignment name found, so compute one based on value type
          if (!sinkName.isPresent()) {
            sinkName = findFor(assign, false);
          }
          if (sinkName.isPresent()) {
            assign.setName(sinkName.get());
          }
          else {
            info("No sink for reaction assignment '" + assign + "'.");
          }
        }
      }
    }
  }
  
  private Optional<String> findFor(ASTIOAssignment assignment, boolean forSource) {
    ASTExpression expr = getFirstAssigntElement(assignment).getExpression();
    Optional<? extends JavaTypeSymbolReference> assignmentType = TypeCompatibilityChecker
        .getExpressionType(expr);
    ;
    
    if (!assignmentType.isPresent()) {
      info("no type of expression '" + expr + "' found.");
      return Optional.empty();
    }
    
    // find all type compatible sink/source names
    Set<String> names;
    
    if (forSource) {
      names = findVariableNameFor(assignmentType.get(), Direction.INPUT);
    }
    else {
      names = findVariableNameFor(assignmentType.get(), Direction.OUTPUT);
    }
    names.addAll(findVariableNameFor(assignmentType.get(), Direction.VARIABLE));
    
    if (names.isEmpty()) {
      info("No sink for type '" + assignmentType.get().getName() + "'.");
      return Optional.empty();
    }
    else if (names.size() == 1) {
      return Optional.of(names.iterator().next());
    }
    else {
      info("Too many sinks for type '" + assignmentType.get().getName() + "'.");
      return Optional.empty();
    }
  }
  
  /**
   * Returns the valuation of the assignment. If there are multiple, return the first one.
   * 
   * @param assignment
   * @return
   */
  private ASTValuation getFirstAssigntElement(ASTIOAssignment assignment) {
    ASTValueList valueList = null;
    if (assignment.isPresentAlternative()) {
      valueList = assignment.getAlternative().getValueListList().get(0);
    }
    else {
      valueList = assignment.getValueList();
    }
    return valueList.getAllValuations().get(0);
  }
  
  /**
   * Find all names of variables/inputs/outputs that match to the given type.
   * 
   * @param assignmentType
   * @param input
   * @param varDirection
   * @return
   */
  private Set<String> findVariableNameFor(JTypeReference<? extends JTypeSymbol> assignmentType,
      Direction direction) {
    Set<String> names = new HashSet<>();
    // TODO BUG!: Might return an empty list when resolving transitive adapted
    // symbols. So assignment name completer in not working!
    // reason:
    // Collection<Symbol> filter(ResolvingInfo resolvingInfo,
    // List<Symbol> symbols) is not correctly implemented
    
    if (direction == Direction.VARIABLE) {
      for (VariableSymbol varSymbol : automatonScope.getEnclosingScope().get()
          .<VariableSymbol> resolveLocally(
              VariableSymbol.KIND)) {
        if (TypeCompatibilityChecker.doTypesMatch(assignmentType, varSymbol.getTypeReference())) {
          names.add(varSymbol.getName());
        }
      }
    }
    else {
      for (PortSymbol portSymbol : automatonScope.getEnclosingScope().get()
          .<PortSymbol> resolveLocally(
              PortSymbol.KIND)) {
        if (TypeCompatibilityChecker.doTypesMatch(assignmentType, portSymbol.getTypeReference())) {
          if ((direction == Direction.OUTPUT && portSymbol.isOutgoing())
              || (direction == Direction.INPUT && portSymbol.isIncoming())) {
            names.add(portSymbol.getName());
          }
        }
      }
    }
    
    return names;
  }
  
  private static void info(String msg) {
    Log.info(msg, "AssignmentNameCompleter -");
  }
}
