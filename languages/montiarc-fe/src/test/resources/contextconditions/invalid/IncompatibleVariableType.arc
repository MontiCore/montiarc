package contextconditions.invalid;

import java.util.HashMap;

component IncompatibleVariableType {

  port in String s;

  var HashMap<String, Integer> x;
  
  init {
    x = new HashMap<String, Integer>();
  }
  
}