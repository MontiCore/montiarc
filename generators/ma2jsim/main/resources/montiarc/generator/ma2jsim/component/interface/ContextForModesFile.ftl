<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign comp = ast.getComponentType()>
public interface ${comp.getName()}${suffixes.contextForModes()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters comp/>
  extends ${comp.getName()}${suffixes.parameters()} <@Util.printTypeParameters comp false/>,
          ${comp.getName()}${suffixes.fields()} <@Util.printTypeParameters comp false/>,
          ${comp.getName()}${suffixes.features()} <@Util.printTypeParameters comp false/>,
          ${comp.getName()}${suffixes.modes()} <@Util.printTypeParameters comp false/> {
  }

${tc.include("montiarc.generator.ma2jsim.component.interface.ModeInterface.ftl", ast.getComponentType())}
