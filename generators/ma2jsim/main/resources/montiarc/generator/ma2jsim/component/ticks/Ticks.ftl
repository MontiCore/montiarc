<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

${tc.include("montiarc.generator.ma2jsim.component.ticks.GeneralMethods.ftl")}

<#if ast.getSymbol().getAllIncomingPorts()?size == 0>
    ${tc.include("montiarc.generator.ma2jsim.component.ticks.ReceiveTick.ftl")}
</#if>

${tc.include("montiarc.generator.ma2jsim.component.ticks.ProcessTick.ftl")}
