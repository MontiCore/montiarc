/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.automotive.bcu;

import montiarc.example.automotive.bcu.Datatypes.*;
import montiarc.lang.logic.gate.Or;

/**
 * Adapted from the Body Comfort System product line presented in
 * Sascha Lity, Remo Lachmann, Malte Lochau, and Ina Schaefer. "Delta-oriented
 * Software Product Line Test Models - The Body Comfort System Case Study."
 * Informatik-Bericht 2012-07, Technische Universität Braunschweig (2017).
 */
component BodyComfortSystem {

  feature HumanMachineInterface, StatusLED,
          LEDAlarmSystem, LEDFingerProtection, LEDCentralLockingSystem,
          LEDPowerWindow, LEDExteriorMirror, LEDHeatable;

  feature DoorSystem, PowerWindow, ManualPowerWindow, AutomaticPowerWindow, FingerProtection,
          ExteriorMirror, Electric, Heatable;

  feature Security, AlarmSystem, InteriorMonitoring, CentralLockingSystem, AutomaticLocking,
          RemoteControlKey, ControlAlarmSystem, SafetyFunction, ControlAutomaticPowerWindow,
          AdjustExteriorMirror;

  // ===== Feature Tree ===== //

  constraint(HumanMachineInterface);
  constraint(!StatusLED || HumanMachineInterface);
  constraint(StatusLED == (LEDAlarmSystem || LEDFingerProtection || LEDCentralLockingSystem
                           || LEDPowerWindow || LEDExteriorMirror || LEDHeatable));

  constraint(DoorSystem);
  constraint(PowerWindow == DoorSystem);
  constraint(PowerWindow == (ManualPowerWindow || AutomaticPowerWindow));
  constraint(FingerProtection == PowerWindow);
  constraint(!(ManualPowerWindow && AutomaticPowerWindow));
  constraint(ExteriorMirror == DoorSystem);
  constraint(Electric == ExteriorMirror);
  constraint(!Heatable || ExteriorMirror);

  constraint(!(AlarmSystem || CentralLockingSystem || RemoteControlKey) || Security);
  constraint(!InteriorMonitoring || AlarmSystem);
  constraint(!AutomaticLocking || CentralLockingSystem);
  constraint(!(ControlAlarmSystem || SafetyFunction || ControlAutomaticPowerWindow
               || AdjustExteriorMirror) || RemoteControlKey);

  // ===== Cross-tree Constraints ===== //

  constraint(!LEDAlarmSystem || AlarmSystem);
  constraint(!LEDCentralLockingSystem || CentralLockingSystem);
  constraint(!LEDHeatable || Heatable);
  constraint(!ControlAlarmSystem || AlarmSystem);
  constraint(!RemoteControlKey || CentralLockingSystem);
  constraint(!(ControlAutomaticPowerWindow && ManualPowerWindow));

  // ===== Human Machine Interface ===== //

  port sync in boolean pw_but_mv_dn, pw_but_mv_up,
                       em_but_mv_left, em_but_mv_right, em_but_mv_up, em_but_mv_dn;

  HumanMachineInterface hmi(AlarmSystem, LEDAlarmSystem, ManualPowerWindow, LEDPowerWindow);

  pw_but_mv_dn -> hmi.pw_but_mv_dn;
  pw_but_mv_up -> hmi.pw_but_mv_up;
  em_but_mv_left -> hmi.em_but_mv_left;
  em_but_mv_right -> hmi.em_but_mv_right;
  em_but_mv_up -> hmi.em_but_mv_up;
  em_but_mv_dn -> hmi.em_but_mv_dn;

  // ===== Power Window ===== //

  port sync in boolean pw_pos_up, pw_pos_dn;
  port in Trigger finger_detected;

  FingerProtection fp;

  finger_detected -> fp.finger_detected;

  varif (AutomaticPowerWindow) {
    port out AutoMove pw_mv;
  } else {
    port out Move pw_mv;
  }

  PowerWindow pw(AutomaticPowerWindow, CentralLockingSystem);

  pw_pos_up -> pw.pos_up;
  pw_pos_dn -> pw.pos_dn;
  fp.on -> pw.fp_on;
  fp.off -> pw.fp_off;
  pw.mv -> pw_mv;

  varif (!ControlAutomaticPowerWindow) {
    hmi.pw_but_up -> pw.but_up;
    hmi.pw_but_dn -> pw.but_dn, fp.pw_but_dn;
  }

  // ===== Exterior Mirror ===== //

  port sync in boolean em_pos_left, em_pos_right, em_pos_top, em_pos_bottom;
  port out Direction em_mv;

  ExteriorMirror em(Heatable, LEDExteriorMirror);

  hmi.em_cmd -> em.cmd;
  em_pos_left -> em.pos_left;
  em_pos_right -> em.pos_right;
  em_pos_top -> em.pos_top;
  em_pos_bottom -> em.pos_bottom;
  em.mv -> em_mv;

  varif (Heatable) {
    port sync in boolean em_too_cold;
    port in Trigger time_heating_elapsed;
    port out Trigger heating_on, heating_off;

    em_too_cold -> em.too_cold;
    time_heating_elapsed -> em.time_heating_elapsed;
    em.heating_on -> heating_on;
    em.heating_off -> heating_off;
  }

  // ===== Alarm System ===== //

  varif (AlarmSystem) {
    port in Trigger activate_as, deactivate_as,
                    as_alarm_detected, time_alarm_elapsed;
    port out Trigger as_active_on, as_active_off, as_alarm_on, as_alarm_off;

    AlarmSystem as(ControlAlarmSystem, InteriorMonitoring);

    activate_as -> hmi.activate_as;
    deactivate_as -> hmi.deactivate_as;
    hmi.as_activated -> as.activated;
    hmi.as_deactivated -> as.deactivated;
    as_alarm_detected -> as.alarm_detected;
    time_alarm_elapsed -> as.time_alarm_elapsed;
    key_pos_lock -> as.key_pos_lock;
    key_pos_unlock -> as.key_pos_unlock;
    as.active_on -> as_active_on;
    as.active_off -> as_active_off;
    as.alarm_on -> as_alarm_on;
    as.alarm_off -> as_alarm_off;
  }

  varif (AlarmSystem && !LEDAlarmSystem) {
    port out Trigger as_alarm_was_detected;

    as.alarm_was_detected -> as_alarm_was_detected;
  }

  varif (InteriorMonitoring) {
    port in Trigger im_alarm_detected;
    port out Trigger as_im_alarm_on, as_im_alarm_off;

    im_alarm_detected -> as.im_alarm_detected;
    as.im_alarm_on -> as_im_alarm_on;
    as.im_alarm_off -> as_im_alarm_off;
  }

  // ===== Central Locking System ===== //

  varif (AlarmSystem || CentralLockingSystem) {
    port in Trigger key_pos_lock, key_pos_unlock;
  }

  varif (CentralLockingSystem) {
    port out Trigger cls_lock, cls_unlock;

    CentralLockingSystem cls(RemoteControlKey, AutomaticLocking);

    key_pos_lock -> cls.key_pos_lock;
    key_pos_unlock -> cls.key_pos_unlock;
    cls.lock -> cls_lock;
    cls.unlock -> cls_unlock;
    cls.lock -> pw.cls_lock;
    cls.unlock -> pw.cls_unlock;
  }

  varif (SafetyFunction || AutomaticLocking) {
    port sync in boolean door_open;
  }

  varif (AutomaticLocking) {
    port sync in boolean car_drives;
    port out Trigger car_lock, car_unlock;

    car_drives -> cls.car_drives;
    door_open -> cls.door_open;
    cls.car_lock -> car_lock;
    cls.car_unlock -> car_unlock;
  }

  // ===== Remote Control Key ===== //

  varif (RemoteControlKey) {
    port in Trigger rck_but_lock, rck_but_unlock;

    RemoteControlKeyController rckCtrl(ControlAutomaticPowerWindow, SafetyFunction);

    rck_but_lock -> rckCtrl.but_lock;
    rck_but_unlock -> rckCtrl.but_unlock;
    rckCtrl.lock -> cls.rck_lock;
    rckCtrl.unlock -> cls.rck_unlock;
  }

  varif (SafetyFunction) {
    port in Trigger time_rck_sf_elapsed;

    door_open -> rckCtrl.door_open;
    time_rck_sf_elapsed -> rckCtrl.time_sf_elapsed;
  }

  varif (ControlAlarmSystem) {
    rckCtrl.lock -> as.rck_lock;
    rckCtrl.unlock -> as.rck_unlock;
  }

  varif (ControlAutomaticPowerWindow) {
    port sync in boolean pw_rm_up, pw_rm_dn;

    // the window buttons of the HMI and of the remote key both control the window
    Or pwButUp, pwButDn;

    pw_rm_up -> rckCtrl.pw_rm_up;
    pw_rm_dn -> rckCtrl.pw_rm_dn;
    hmi.pw_but_up -> pwButUp.a;
    rckCtrl.pw_but_up -> pwButUp.b;
    hmi.pw_but_dn -> pwButDn.a;
    rckCtrl.pw_but_dn -> pwButDn.b;
    pwButUp.q -> pw.but_up;
    pwButDn.q -> pw.but_dn, fp.pw_but_dn;
  }

  // ===== Status LEDs ===== //

  varif (LEDFingerProtection) {
    port sync out boolean led_fp;

    LED ledFP;

    fp.on -> ledFP.turn_on;
    fp.off -> ledFP.turn_off;
    ledFP.led -> led_fp;
  }

  varif (LEDPowerWindow) {
    port sync out boolean led_pw_up, led_pw_dn;
  }

  varif (LEDPowerWindow && ManualPowerWindow) {
    port in Trigger release_pw_but_up, release_pw_but_dn;

    LEDManualPowerWindow ledManPW;

    release_pw_but_up -> hmi.release_pw_but_up;
    release_pw_but_dn -> hmi.release_pw_but_dn;
    pw.mv -> ledManPW.pw_mv;
    hmi.release_pw_but -> ledManPW.release_pw_but;
    ledManPW.led_up -> led_pw_up;
    ledManPW.led_dn -> led_pw_dn;
  }

  varif (LEDPowerWindow && AutomaticPowerWindow) {
    LEDAutomaticPowerWindow ledAutoPW(CentralLockingSystem);

    pw.mv -> ledAutoPW.pw_auto_mv;
    ledAutoPW.led_up -> led_pw_up;
    ledAutoPW.led_dn -> led_pw_dn;
  }

  varif (LEDPowerWindow && AutomaticPowerWindow && CentralLockingSystem) {
    port sync out boolean led_pw_cls_up;

    cls.lock -> ledAutoPW.cls_lock;
    cls.unlock -> ledAutoPW.cls_unlock;
    ledAutoPW.led_cls_up -> led_pw_cls_up;
  }

  varif (LEDExteriorMirror) {
    port sync out boolean led_em_top, led_em_bottom, led_em_left, led_em_right;

    LED ledEMT, ledEMB, ledEML, ledEMR;

    em.pos_vert_top -> ledEMT.turn_on;
    em.pos_vert_bottom -> ledEMB.turn_on;
    em.pos_vert_pend -> ledEMT.turn_off, ledEMB.turn_off;
    em.pos_hor_left -> ledEML.turn_on;
    em.pos_hor_right -> ledEMR.turn_on;
    em.pos_hor_pend -> ledEML.turn_off, ledEMR.turn_off;
    ledEMT.led -> led_em_top;
    ledEMB.led -> led_em_bottom;
    ledEML.led -> led_em_left;
    ledEMR.led -> led_em_right;
  }

  varif (LEDHeatable) {
    port sync out boolean led_em_heating;

    LED ledEMH;

    em.heating_on -> ledEMH.turn_on;
    em.heating_off -> ledEMH.turn_off;
    ledEMH.led -> led_em_heating;
  }

  varif (LEDCentralLockingSystem) {
    port sync out boolean led_cls;

    LED ledCLS;

    cls.lock -> ledCLS.turn_on;
    cls.unlock -> ledCLS.turn_off;
    ledCLS.led -> led_cls;
  }

  varif (LEDAlarmSystem) {
    port in Trigger confirm_alarm;
    port sync out boolean led_as_active, led_as_alarm, led_as_alarm_detected;

    LED ledASAC, ledASAL, ledASAD;

    confirm_alarm -> hmi.confirm_alarm;
    as.active_on -> ledASAC.turn_on;
    as.active_off -> ledASAC.turn_off;
    as.alarm_on -> ledASAL.turn_on;
    as.alarm_off -> ledASAL.turn_off;
    as.alarm_was_detected -> ledASAD.turn_on;
    hmi.as_alarm_was_confirmed -> ledASAD.turn_off;
    ledASAC.led -> led_as_active;
    ledASAL.led -> led_as_alarm;
    ledASAD.led -> led_as_alarm_detected;
  }

  varif (LEDAlarmSystem && InteriorMonitoring) {
    port sync out boolean led_as_im_alarm;

    LED ledASIM;

    as.im_alarm_on -> ledASIM.turn_on;
    as.im_alarm_off -> ledASIM.turn_off;
    ledASIM.led -> led_as_im_alarm;
  }

}
