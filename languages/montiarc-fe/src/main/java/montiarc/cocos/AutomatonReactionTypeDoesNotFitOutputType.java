package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.types.JavaDSLHelper;
import de.monticore.mcexpressions._ast.ASTArguments;
import de.monticore.mcexpressions._ast.ASTCallExpression;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTTransition;
import montiarc._ast.ASTValuation;
import montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import montiarc._cocos.MontiArcASTTransitionCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.TransitionSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Context condition for checking, if all assignments inside a reaction of a
 * transition are type-correct.
 *
 * @author Andreas Wortmann
 */
public class AutomatonReactionTypeDoesNotFitOutputType
    implements MontiArcASTTransitionCoCo, MontiArcASTInitialStateDeclarationCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.isPresentReaction()) {
      for (ASTIOAssignment assign : node.getReaction().getIOAssignmentList()) {
        checkAssignment(assign, ((TransitionSymbol) node.getSymbolOpt().get()).getSpannedScope());
      }
    }
  }
  
  /**
   * @see montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo#check(montiarc._ast.ASTInitialStateDeclaration)
   */
  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.isPresentBlock()) {
      for (ASTIOAssignment assign : node.getBlock().getIOAssignmentList()) {
        checkAssignment(assign, node.getEnclosingScopeOpt().get());
      }
    }
  }
  
  private void checkAssignment(ASTIOAssignment assignment, Scope transitionScope) {
    if (assignment.isPresentName()) {
      String currentNameToResolve = assignment.getName();
      
      Optional<VariableSymbol> vSymbol = transitionScope.resolve(currentNameToResolve,
          VariableSymbol.KIND);
      Optional<PortSymbol> pSymbol = transitionScope.resolve(currentNameToResolve,
          PortSymbol.KIND);
      if (pSymbol.isPresent()) {
        if (pSymbol.get().isIncoming()) {
          Log.error("0xMA041 Did not find matching Variable or Output with name "
              + currentNameToResolve, assignment.get_SourcePositionStart());
        }
        else {
          checkTypeCorrectness(assignment, pSymbol.get().getTypeReference(),
              currentNameToResolve);
        }
      }
      else if (vSymbol.isPresent()) {
        checkTypeCorrectness(assignment, vSymbol.get().getTypeReference(), currentNameToResolve);
      }
    }
  }
  
  private void checkTypeCorrectness(ASTIOAssignment assign,
      JTypeReference<? extends JTypeSymbol> typeRef, String currentNameToResolve) {
    for (ASTValuation val : assign.getValueList().getAllValuations()) {
      // check only if assignment is assignment of form "x = foo.bar()"
      if (assign.isAssignment()) {
        checkAssignedTypesFits(val, typeRef, currentNameToResolve);
      }
      // check whether passed method parameters fit
      else {
        checkParametersOfCall(val, typeRef);
      }
    }
  }
  
  private void checkAssignedTypesFits(ASTValuation assignment,
      JTypeReference<? extends JTypeSymbol> varType, String referencedName) {
    Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker
        .getExpressionType(assignment.getExpression());
    if (!exprType.isPresent()) {
      Log.error(
          "0xMA044 Could not resolve type of expression for checking the reaction.",
          assignment.get_SourcePositionStart());
    }
    else if (!TypeCompatibilityChecker.doTypesMatch(exprType.get(), varType)) {
      Log.error("0xMA042 Type of Variable/Output \"" + referencedName
          + "\" in the reaction does not match the type of its assigned expression. Type "
          +
          exprType.get().getName() + " can not cast to type " + varType.getName()
          + ".", assignment.get_SourcePositionStart());
    }
  }
  
  private void checkParametersOfCall(ASTValuation assignment,
      JTypeReference<? extends JTypeSymbol> varType) {
    if (assignment.getExpression() instanceof ASTCallExpression) {
      ASTCallExpression methodCallExpr = (ASTCallExpression) assignment.getExpression();
      ASTArguments args = methodCallExpr.getArguments();
      
      // 1. get types of parameters in guard method call
      List<JavaTypeSymbolReference> argTypes = new ArrayList<>();
      for (ASTExpression e : args.getExpressionList()) {
        Optional<? extends JavaTypeSymbolReference> argTypeOpt = TypeCompatibilityChecker
            .getExpressionType(e);
        if (argTypeOpt.isPresent()) {
          argTypes.add(argTypeOpt.get());
        }
        else {
          Log.error("argument Type could not be resolved");
        }
      }
      
      // 2. find the types of the correct method parameters
      String methName = ((ASTQualifiedNameExpression) methodCallExpr.getExpression())
          .getName();
      Optional<JavaMethodSymbol> methSym = ((JavaTypeSymbol) varType.getReferencedSymbol())
          .getMethod(methName);
      
      if (!methSym.isPresent()) {
        JavaTypeSymbol varJavaType = ((JavaTypeSymbol) varType.getReferencedSymbol());
        List<JavaMethodSymbol> m1 = new ArrayList<>();
        if (varJavaType.getSuperClass().isPresent()) {
          m1 = JavaDSLHelper.getAccessibleMethods(methName,
              varJavaType,
              varJavaType.getSuperClass().get(),
              false);
        }
        
        if (!m1.isEmpty()) {
          methSym = Optional.of(m1.get(0));
        }
      }
      
      if (methSym.isPresent()) {
        List<JavaTypeSymbolReference> correctParameters = new ArrayList<>();
        
        correctParameters = JavaDSLHelper
            .getParameterTypes(methSym.get());
        
        if (argTypes.size() != correctParameters.size()) {
          Log.error("Number of method parameters does not fit.");
        }
        
        // 2. substitute possible generic parameters
        for (int i = 0; i < correctParameters.size(); i++) {
          JavaTypeSymbolReference param = correctParameters.get(i);
          if (param.getReferencedSymbol().isFormalTypeParameter()) {
            int pos = TypeCompatibilityChecker.getPositionInFormalTypeParameters(
                varType.getReferencedSymbol().getFormalTypeParameters().stream()
                    .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
                param);
            
            correctParameters.set(i, (JavaTypeSymbolReference) varType
                .getActualTypeArguments().get(pos).getType());
          }
        }
        
        // 3. compare actual and correct method parameters with each other
        for (int i = 0; i < argTypes.size(); i++) {
          if (!TypeCompatibilityChecker.doTypesMatch(argTypes.get(i),
              argTypes.get(i).getReferencedSymbol().getFormalTypeParameters().stream()
                  .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
              argTypes.get(i).getActualTypeArguments().stream()
                  .map(a -> (JavaTypeSymbolReference) a.getType())
                  .collect(Collectors.toList()),
              correctParameters.get(i),
              varType.getReferencedSymbol().getFormalTypeParameters().stream()
                  .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
              varType.getActualTypeArguments().stream()
                  .map(a -> (JavaTypeSymbolReference) a.getType())
                  .collect(Collectors.toList()))) {
            Log.error("0xMA042 Types do not fit " + argTypes.get(i).getName() + ", "
                + correctParameters.get(i).getName());
          }
        }
        
      }
    }
  }
  
}
