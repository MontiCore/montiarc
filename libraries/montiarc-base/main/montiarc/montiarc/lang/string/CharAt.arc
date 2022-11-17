/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.string;

component CharAt(char dflt) {
  port <<sync>> in String string,
       <<sync>> in int index,
       <<sync>> out char letter;

  compute {
    if (index >= 0 && index < string.length()) {
      letter = string.charAt(index);
    } else {
      letter = dflt;
    }
  }
}
