package montiarc.cocos;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAlternative;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTValuation;
import montiarc._ast.ASTValueList;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._parser.MontiArcAntlrParser;
import montiarc._symboltable.MontiArcSymbolTableCreator;

import java.util.*;

/**
 * Context condition for checking, if an IOAssignment has no name. This can only
 * be the case, if the {@link MontiArcSymbolTableCreator} could not calculate a unique
 * match for a IOAssignment.
 * 
 * @implements [Wor16] AR5: Types of valuations and assignments without names are unambiguous. (p. 104, Lst. 5.22)
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AssignmentHasNoName implements MontiArcASTIOAssignmentCoCo {  

  @Override
  public void check(ASTIOAssignment node) {
    if (!node.nameIsPresent()) {
      //TODO: Add case for function calls on classes
      boolean errorFlag = true;
      // Check the ValueList, as there might be an expression which contains the name, for example
      // 'varName++'
      Optional<ASTValueList> valueListOptional = node.getValueList();

      // As with the ValueList there might be a ValueList in the Alternative which could contain
      // an expression with a name.
      Optional<ASTAlternative> alternativeOptional = node.getAlternative();

      List<ASTValueList> valueListList = new ArrayList<>();
      if(valueListOptional.isPresent()) {
        valueListList.add(valueListOptional.get());
      }
      if(alternativeOptional.isPresent()) {
        valueListList = alternativeOptional.get().getValueLists();
      }

      //Work through the chain of ValueLists and Expressions until we get a primaryExpression with a name
      for(ASTValueList valueList: valueListList) {
        List<ASTValuation> valuations = valueList.getAllValuations();

        for(ASTValuation valuation : valuations) {


          /*
            Expression has to be the suffix operator like '++' and an expression that consists
            of a primary expression with the name of the variable.
            Alternatively there might be a call of an instance method.
           */
          ASTExpression expression = valuation.getExpression();


          // Possibility 1: instance method call. ex: list.add(1)
          if(isInstanceMethodCall(expression)){
            errorFlag = false;
          }

          // Possibility 2: Check whether a suffix/prefix operator is present
          if(expression.getSuffixOp().isPresent() || expression.getPrefixOp().isPresent()){
            if (expression.getExpression().isPresent()) {
              if(isInstanceMethodCall(expression.getExpression().get())){
                errorFlag = false;
              }
              Optional<ASTPrimaryExpression> primaryExpression = expression.getExpression().get()
                  .getPrimaryExpression();
              if (primaryExpression.isPresent()) {
                if (primaryExpression.get().getName().isPresent()) {
                  errorFlag = false;
                }
              }
            }
          }
        }
      }

      if(errorFlag) {
        // It is not possible that the name is in an expression
      Log.error("0xMA024 Could not find a unique matching type for the assignment '" + node + "'.", node.get_SourcePositionStart());
      }
    }    
  }

  private boolean isInstanceMethodCall(ASTExpression expression){
    if (expression.getCallExpression().isPresent()) {
      ASTExpression callExpression = expression.getCallExpression().get();
      if(callExpression.getExpression().isPresent()){
        if(callExpression.getExpression().get().getPrimaryExpression().isPresent()){
          if(callExpression.getExpression().get().getPrimaryExpression().get().getName()
              .isPresent()){
            return true;
          }
        }
      }
    }
    return false;
  }
}
