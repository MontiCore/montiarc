/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.codegen;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arccompute._ast.ASTArcCompute;
import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._ast.ASTModeAutomaton;
import basicmodeautomata.util.ComponentModeTool;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTArcInstant;
import montiarc._ast.ASTArcSync;
import montiarc._ast.ASTArcUntimed;
import montiarc._visitor.MontiArcTraverser;
import montiarc.generator.ma2kotlin.prettyprint.MontiArcFullPrettyPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * contains some funtionality that cannot be recreated in freemarker-templates because of syntax or complexity
 */
public class TemplateUtilities {
  public <T> T getNull(){
    return null;
  }

  public ComponentModeTool getModeTool(){
    return BasicModeAutomataMill.getModeTool();
  }

  public StatesTool getStateTool(){
    return new StatesTool();
  }

  /**
   * @return a helper for types
   */
  public TypeTrimTool getTypes(){
    return new TypeTrimTool();
  }

  /**
   * @return this expression in kotlin
   */
  public String printExpression(ASTExpression expression){
    return new MontiArcFullPrettyPrinter(0).prettyprint(expression);
  }

  /**
   * Prints the statement with the given indent.
   * If the given statement is a Java-Block-Statement, the statements children are printed instead,
   * to avoid unnecessary curly braces
   * @return kotlin code representing this statement
   */
  public String printStatement(int indent, ASTMCBlockStatement statement){
    return new MontiArcFullPrettyPrinter(indent).prettyprint(statement);
  }

  /**
   * @return transitions of the mode automaton of this component, or an empty list
   */
  public List<ASTSCTransition> getModeTransitions(ComponentTypeSymbol component){
    return getModeTool()
        .streamAutomata(component.getAstNode())
        .flatMap(ASTModeAutomaton::streamTransitions)
        .collect(Collectors.toList());
  }

  public String getTiming(ComponentTypeSymbol component) {
    List<String> foundTimings = new ArrayList<>(2);
    component.getAstNode().getBody().accept(new MontiArcTraverser() {
      @Override
      public void traverse(ASTComponentType node) {
        // do nothing, because we don't want to get confused by the timing of inner components
      }

      @Override
      public void visit(ASTArcInstant node) {
        foundTimings.add("timed");
      }

      @Override
      public void visit(ASTArcUntimed node) {
        foundTimings.add("untimed");
      }

      @Override
      public void visit(ASTArcSync node) {
        foundTimings.add("sync");
      }
    });
    if(foundTimings.size()>1){
      Log.warn("Found multiple timings for "+component.getName()+": "+ foundTimings,
          component.getAstNode().get_SourcePositionStart());
    }
    foundTimings.add("sync");
    return foundTimings.get(0);
  }

  /**
   * @return the behavior of this component, if it has a statechart
   */
  public Optional<ASTArcStatechart> getStatechart(ComponentTypeSymbol component){
    return component.getAstNode().getBody()
        .streamArcElements()
        .filter(e -> e instanceof ASTArcStatechart)
        .map(e -> (ASTArcStatechart) e)
        .reduce((a, b) -> {throw new IllegalStateException(component.getName()+" has multiple statecharts");});
  }

  /**
   * @return the behavior of this component, if it has any, in the form of compute-blocks
   */
  public List<ASTArcCompute> getComputes(ComponentTypeSymbol component){
    return component.getAstNode().getBody()
        .streamArcElements()
        .filter(ASTArcCompute.class::isInstance)
        .map(ASTArcCompute.class::cast)
        .collect(Collectors.toList());
  }
}
