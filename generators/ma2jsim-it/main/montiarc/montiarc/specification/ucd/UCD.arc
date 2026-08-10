/* (c) https://github.com/MontiCore/monticore */
package montiarc.specification.ucd;

import montiarc.types.OnOff;

component UCD {

  port in OnOff i;
  port out OnOff o;

  usecasediagram {
    @i --
      Play,
      Pay;

    ShowAd extend Play [!isPremium];
    RegisterScore extend Play;

    abstract Pay include CheckPremium;
    CreditCard specializes Pay;
    Bank specializes Pay;
    ChangeProfilePicture [isPremium];
  }
}
