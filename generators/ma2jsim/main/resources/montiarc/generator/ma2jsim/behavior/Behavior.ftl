<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}
/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#-- TODO just delegate to whatever is the right template, currently we just support automata -->
<#if helper.getAutomatonBehavior(ast.getComponentType()).isPresent()>
${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.Automaton.ftl", ast.getComponentType(), [isTop])}
<#elseif helper.getComputeBehavior(ast.getComponentType()).isPresent()>
${tc.includeArgs("montiarc.generator.ma2jsim.behavior.compute.Compute.ftl", ast.getComponentType(), helper.asList(isTop))}
</#if>
