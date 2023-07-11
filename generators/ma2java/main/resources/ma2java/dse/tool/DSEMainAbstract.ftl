<#-- (c) https://github.com/MontiCore/monticore -->

package main;

import montiarc.rte.dse.ResultI;

abstract public class DSEMain{
	abstract public ResultI runController(String[] args) throws Exception;
}