;/* (c) https://github.com/MontiCore/monticore */

;map state
(assert  (ite (= (get_state curr_state) Known_con)
              (= (map_state curr_state) (S_State LoggedIn_ref))
              (= (map_state curr_state) (S_State NotLoggedIn_ref))))

(assert  (ite (= (get_state next_state) Known_con)
              (= (map_state next_state) (S_State LoggedIn_ref))
              (= (map_state next_state) (S_State NotLoggedIn_ref))))

;map output
(assert    (ite (= output (seq.unit ERROR_con))
                (= (map_output output ) (seq.unit ERROR_ref))
                (ite (= output (as seq.empty (Seq Output_con)))
                  (= (map_output output ) (as seq.empty (Seq Output_ref)) )
                  (= (map_output output ) (seq.unit RESPONSE_ref) )
                  )))

;map input
(assert   (=> (= input  GET_VALUE_con)            (= (map_input input) ACTION_ref )))
(assert   (=> (= input  LOGOUT_con)               (= (map_input input) LOGOUT_ref )))
(assert   (=> (= input  INCREASE_VALUE_con)       (= (map_input input) ACTION_ref )))
(assert   (=> (= (get_password input) "correct") (= (map_input input) LOGIN_ref )))

(assert   (=> ( and (distinct  input  INCREASE_VALUE_con LOGOUT_con GET_VALUE_con)
                    (not (= (get_password input) "correct")))
              (= (map_input input) LOGOUT_ref )))

