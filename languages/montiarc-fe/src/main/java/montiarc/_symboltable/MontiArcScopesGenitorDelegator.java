/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;


import arcbasis._symboltable.ArcBasisScopesGenitor;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import genericarc._symboltable.GenericArcScopesGenitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * aka the Lambda Hell
 * - well maybe not
 */
public class MontiArcScopesGenitorDelegator extends MontiArcScopesGenitorDelegatorTOP {

  public MontiArcScopesGenitorDelegator(){
    init();
  }

  /**
   * updates some of the genitors to be capable of dealing with simpleGenericTypes
   */
  protected void init(){
    MCSimpleGenericTypesFullPrettyPrinter betterPrinter = MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter();
    new Updater<GenericArcScopesGenitor>().fix(traverser.getGenericArcVisitorList(),
        g -> g.setMapper(getBetterMapper(betterPrinter)));
    new Updater<ArcBasisScopesGenitor>().fix(traverser.getArcBasisVisitorList(),
        g -> g.updateConverter(getBetterMapper(betterPrinter))
                 .setTypePrinter(betterPrinter));
  }

  /**
   * MontiCore Version < 6.8 cannot deal with generic types. Since MontiArc uses simple generic types,
   * we have to provide a way to create sym tab expressions from generic types
   * @param printer printer be used to print a type
   * @return a mapper that can create a symTypeExpression from a simple-, collection- or simpleGeneric-MontiCore-Type
   */
  public BiFunction<ASTMCType, IArcBasisScope, SymTypeExpression> getBetterMapper(MCSimpleGenericTypesFullPrettyPrinter printer){
    Preconditions.checkNotNull(printer);
    return (type, scope) -> createTypeFromString(
        Preconditions.checkNotNull(type).printType(printer),
        Preconditions.checkNotNull(scope));
  }

  /**
   * The Method {@link SymTypeExpressionFactory#createTypeExpression(String, IBasicSymbolsScope)} is not capable
   * of creating generic types,
   * so this method recursively creates generic types or delegates to the broken method.
   * I assume the string is a valid generic, since the parser should have complained if not
   * @param type name of the type as string with valid bracing
   */
  private SymTypeExpression createTypeFromString(String type, IArcBasisScope scope){
    int start = type.indexOf("<");
    if(start==-1){
      return SymTypeExpressionFactory.createTypeExpression(type, scope);
    }
    Preconditions.checkArgument(type.length() - start > 1);

    List<String> betweenBrackets = iterateBrackets(type, start);
    String beforeBrackets = type.substring(0, start);
    return SymTypeExpressionFactory.createGenerics(beforeBrackets, scope, getSubGenerics(betweenBrackets, scope));
  }

  /**
   * recursively calls {@link #createTypeFromString(String, IArcBasisScope)}.
   * If that method for example received {@code Map<String, List<String>>} this Method should get {@code [String, List<String>]} as parameter
   * @param inBrackets list of generics nested one level
   */
  private List<SymTypeExpression> getSubGenerics(List<String> inBrackets, IArcBasisScope scope){
    return inBrackets.stream()
               .map(String::trim)
               .map(name -> this.createTypeFromString(name, scope))
               .collect(Collectors.toList());
  }

  /**
   * splits the type-string along commas, but only on such that are on the first depth of generics
   * @param type type-string, like {@code Map<Double, HashMap<String, Integer>>}
   * @param start first occurrence of an opening generic
   * @return all first sub-generics as a list, like {@code [Double; HashMap<String, Integer>]}
   */
  private List<String> iterateBrackets(String type, int start){
    List<String> list = new ArrayList<>();
    int depth = 0;
    for(int i = 0; i < type.toCharArray().length; i++) {
      char c = type.toCharArray()[i];
      if(c == '<'){
        depth++;
      }
      if(depth == 1 && (c == ',' || c == '>')){
        list.add(type.substring(start+1, i));
        start = i;
      }
      if(c == '>'){
        depth--;
      }
    }
    return list;
  }

  /**
   * facilitates iterating visitors and updating some genitors hidden among them
   * @param <Genitor> type of genitor to edit
   */
  private static class Updater<Genitor>{
    /**
     * @param allVisitors a list that may contain some genitors that have to be updated
     * @param editor function to apply on each found genitor
     */
    private void fix(List<?> allVisitors, Consumer<Genitor> editor){
      allVisitors
          .stream()
          .map(v -> {
            try {
              return (Genitor) v;
            } catch(Exception e) {
              return null;
            }
          })
          .filter(Objects::nonNull)
          .forEach(editor);
    }
  }
}