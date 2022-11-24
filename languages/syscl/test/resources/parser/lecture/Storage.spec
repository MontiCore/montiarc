/* (c) https://github.com/MontiCore/monticore */
spec Storage {

  port in Screw din,
       in int id;
  port out Screw dout;

  Map<Integer, Screw> s = Map();

  ------------------------

  in din
  pre: true
  post: s.get(screw.id)==screw && dout==epsilon

  ------------------------

  in id
  pre: s.containsKey(id)
  post: s==s@pre.remove(id) &&
       dout==s@pre.get(id)

}