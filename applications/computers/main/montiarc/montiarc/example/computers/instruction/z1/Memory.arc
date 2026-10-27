/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import java.util.List;

/**
 * The mechanical memory of 64 words with 22 bits each. It always emits the
 * content of the addressed cell.
 */
component Memory {

  port sync in int address,
       sync in boolean write,
       sync in int data,
       sync out int word;

  List<Integer> cells = [];

  init {
    // all cells initially hold zero (exponent -64)
    for (int i = 0; i < 64; i++) {
      cells.add(1048576);
    }
  }

  compute {
    if (write) {
      cells.set(address, data);
    }
    word = cells.get(address);
  }
}
