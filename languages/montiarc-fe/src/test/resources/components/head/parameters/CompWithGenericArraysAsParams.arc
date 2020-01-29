/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

import types.*;

/*
 * Invalid model. Has arrays of generic types.
 */
component CompWithGenericArraysAsParams (
      List<String>[] configStringListArray, // ERROR
      List<String>[][] configStringListArray2, // ERROR
      List<List<String>[]> innerGenericArray, // ERROR
      List<List[]> allowedArray, // Correct
      Map<List<String[]>, List<String>[]>[] map, // 2 ERRORs
      String[][] configStringArray, // Correct
      List<String[]> configListStringArray){ // Correct
}