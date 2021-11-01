<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the component class for atomic and composed components. -->
${tc.signature("comp", "compHelper", "identifier", "isTOPClass")}
<#assign comp=comp><#assign compHelper=compHelper><#assign identifier=identifier><#assign isTOPClass=isTOPClass> <#--TODO Remove later. This is only here to lower the amount or errors intelliJ reports.-->
<#import "/templates/util/Copyright.ftl" as Copyright>
<#import "/templates/util/Imports.ftl" as Imports>
<#import "/templates/component/ComponentHeader.ftl" as Header>
<#import "/templates/component/ComponentBody.ftl" as Body>
<@Copyright.defaultCopyrightComment/>

<#if comp.getPackageName() != "">
  package ${comp.getPackageName()};
</#if>

<@Imports.printImportsForComponentClass comp=comp compHelper=compHelper/>

<@Header.printComponentClassHeader comp=comp compHelper=compHelper isTOPClass=isTOPClass /> {

<@Body.printComponentClassBody comp=comp compHelper=compHelper identifier=identifier isTOPClass=isTOPClass />
}