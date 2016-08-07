package de.monticore.automaton.ioautomatonjava._symboltable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.monticore.automaton.ioautomaton.TypeCompatibilityChecker;
import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._ast.ASTConstantsIOAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomaton._ast.ASTValueList;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._ast.ASTVariableDeclaration;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomaton._visitor.IOAutomatonVisitor;
import de.monticore.automaton.ioautomatonjava._visitor.IOAutomatonJavaVisitor;
import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

/**
 * Computes the missing assignment names in reactions, stimuli and initial state
 * declarations.
 * 
 * TODO may not work in monti arc automaton because
 * input/output/variable declarations are missing (emulated by resolving
 * adapters)
 */
public class AssignmentNameCompleter implements IOAutomatonJavaVisitor {
  private final ASTAutomatonContext context;

  public AssignmentNameCompleter(ASTAutomatonContext context) {
    this.context = context;
  }
  
  @Override
  public void visit(ASTInitialStateDeclaration node) {
    if (node.blockIsPresent()) {
      for (ASTIOAssignment assign : node.getBlock().get().getIOAssignments()) {
        if (!assign.nameIsPresent()) {
          // no assignment name found, so compute one based on value type
          Optional<String> sinkName = findFor(assign, false);
          if (sinkName.isPresent()) {
            assign.setName(sinkName.get());
            assign.setOperator(ASTConstantsIOAutomaton.SINGLE);
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
    if (node.stimulusIsPresent()) {
      for (ASTIOAssignment assign : node.getStimulus().get().getIOAssignments()) {
        if (!assign.nameIsPresent()) {
          // no assignment name found, so compute one based on value type
          Optional<String> sourceName = findFor(assign, true);
          if (sourceName.isPresent()) {
            assign.setName(sourceName.get());
            assign.setOperator(ASTConstantsIOAutomaton.DOUBLE);
          }
          else {
            info("No source for stimulus assignment '" + assign + "'.");
          }
        }
      }
    }
    if (node.reactionIsPresent()) {
      for (ASTIOAssignment assign : node.getReaction().get().getIOAssignments()) {
        if (!assign.nameIsPresent()) {
          // no assignment name found, so compute one based on value type
          Optional<String> sinkName = findFor(assign, false);
          if (sinkName.isPresent()) {
            assign.setName(sinkName.get());
            assign.setOperator(ASTConstantsIOAutomaton.SINGLE);
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
    Optional<? extends JavaTypeSymbolReference> assignmentType = TypeCompatibilityChecker.getExpressionType(expr);
    
    if (!assignmentType.isPresent()) {
      info("no type of expression '" + expr + "' found.");
      return Optional.empty();
    }
    
    // find all type compatible sink/source names
    Set<String> names;
    if (forSource) {
      names = findInputFor(assignmentType.get());
    }
    else {
      names = findOutputFor(assignmentType.get());
    }
    names.addAll(findVariableFor(assignmentType.get()));
    
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
    
  private ASTValuationExt getFirstAssigntElement(ASTIOAssignment assignment) {
    ASTValueList valueList = null;
    if (assignment.alternativeIsPresent()) {
      valueList =  assignment.getAlternative().get().getValueLists().get(0);
    } else {
      valueList = assignment.getValueList().get();
    }
    return valueList.getAllValuations().get(0);
  }
  
  private Set<String> findInputFor(JTypeReference<? extends JTypeSymbol> assignmentType) {
    Set<String> names = new HashSet<>();
    for (ASTInputDeclaration dec : context.getInputDeclarations()) {
      for (ASTVariable var : dec.getVariables()) {
        VariableSymbol varSymbol = (VariableSymbol)var.getSymbol().get();
        if (TypeCompatibilityChecker.doTypesMatch(assignmentType, varSymbol.getTypeReference())) {
          names.add(var.getName());
        }
      }
    }
    return names;
  }
  
  private Set<String> findVariableFor(JTypeReference<? extends JTypeSymbol> assignmentType) {
    Set<String> names = new HashSet<>();
    for (ASTVariableDeclaration dec : context.getVariableDeclarations()) {
      for (ASTVariable var : dec.getVariables()) {
        VariableSymbol varSymbol = (VariableSymbol) var.getSymbol().get();
        if (TypeCompatibilityChecker.doTypesMatch(assignmentType, varSymbol.getTypeReference())) {
          names.add(var.getName());
        }
      }
    }
    return names;
  }
  
  private Set<String> findOutputFor(JTypeReference<? extends JTypeSymbol> assignmentType) {
    Set<String> names = new HashSet<>();
    for (ASTOutputDeclaration dec : context.getOutputDeclarations()) {
      for (ASTVariable var : dec.getVariables()) {
        VariableSymbol varSymbol = (VariableSymbol) var.getSymbol().get();
        if (TypeCompatibilityChecker.doTypesMatch(assignmentType, varSymbol.getTypeReference())) {
          names.add(var.getName());
        }
      }
    }
    return names;
  }
  
  private static void info(String msg) {
    Log.info(msg, "AssignmentNameCompleter -");
  }
}
