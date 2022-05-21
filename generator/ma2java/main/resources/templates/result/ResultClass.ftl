<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the result class for a component. -->
${tc.signature("comp", "compHelper", "isTOPClass")}
<#assign comp=comp><#assign compHelper=compHelper>
<#import "/templates/util/Copyright.ftl" as Copyright>
<#import "/templates/util/Imports.ftl" as Imports>
<#import "/templates/result/ResultHeader.ftl" as Header>
<#import "/templates/result/ResultBody.ftl" as Body>
<@Copyright.defaultCopyrightComment/>

<#if comp.getPackageName() != "">
  package ${comp.getPackageName()};
</#if>

<@Imports.printImportsForResultClass comp=comp compHelper=compHelper/>

<@Header.printResultClassHeader comp=comp compHelper=compHelper isTOPClass=isTOPClass/> {

<@Body.printResultClassBody comp=comp compHelper=compHelper isTOPClass=isTOPClass/>
}