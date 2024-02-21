<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
protected ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(ast.getSymbol())}<#if isTop>${suffixes.top()}</#if> (
  ${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/> ${ast.getName()?uncap_first}${suffixes.context()},
  java.util.List${"<"}montiarc.rte.automaton.State${">"} states,
  java.util.List${"<"}montiarc.rte.automaton.Transition${">"} transitions,
  montiarc.rte.automaton.State initial) {
    super(${ast.getName()?uncap_first}${suffixes.context()}, states, transitions, initial);
}