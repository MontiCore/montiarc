<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}
/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.include("montiarc.generator.Import.ftl", ast.getImportStatementList())}

${tc.includeArgs("montiarc.generator.ma2jsim.dynamics.modeAutomaton.ModeAutomatonClass.ftl", ast.getComponentType(), [isTop])}

${tc.includeArgs("montiarc.generator.ma2jsim.dynamics.modeAutomaton.ModesClass.ftl", ast.getComponentType(), [isTop])}