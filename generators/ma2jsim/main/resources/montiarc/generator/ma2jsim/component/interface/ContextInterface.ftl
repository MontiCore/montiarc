<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
public interface ${ast.getName()}${suffixes.context()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>
  extends ${ast.getName()}${suffixes.input()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.output()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.parameters()} <@Util.printTypeParameters ast false/> {
  <#if helper.getAutomatonBehavior(ast).isPresent()>
    ${ast.getName()}${suffixes.automaton()}<@Util.printTypeParameters ast false/> getBehavior();
  <#elseif helper.getComputeBehavior(ast).isPresent()>
    ${ast.getName()}${suffixes.compute()}<@Util.printTypeParameters ast false/> getBehavior();
  </#if>
}