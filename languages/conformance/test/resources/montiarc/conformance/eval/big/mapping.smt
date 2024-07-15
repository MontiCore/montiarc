;/* (c) https://github.com/MontiCore/monticore */

;map current states
(assert  (=> (= curr_state (S_State Start_con))         (= (map_state curr_state) (S_State LoggedOut_ref))))
(assert  (=> (= curr_state (S_State Connecting_con))    (= (map_state curr_state) (S_State LoggedOut_ref))))
(assert  (=> (= curr_state (S_State Registering_con))   (= (map_state curr_state) (S_State LoggedOut_ref))))
(assert  (=> (= curr_state (S_State OnDashboard_con))   (= (map_state curr_state) (S_State LoggedIn_ref))))
(assert  (=> (= curr_state (S_State OnProfile_con))     (= (map_state curr_state) (S_State LoggedIn_ref))))
(assert  (=> (= curr_state (S_State OnProduct_con))     (= (map_state curr_state) (S_State LoggedIn_ref))))
(assert  (=> (= curr_state (S_State OnSearchResult_con))(= (map_state curr_state) (S_State LoggedIn_ref))))
(assert  (=> (= curr_state (S_State Searching_con))     (= (map_state curr_state) (S_State LoggedIn_ref))))
(assert  (=> (= curr_state (S_State InCart_con))        (= (map_state curr_state) (S_State LoggedIn_ref))))
(assert  (=> (= curr_state (S_State Confirming_con))    (= (map_state curr_state) (S_State LoggedIn_ref))))
(assert  (=> (= curr_state (S_State CheckingOut_con))   (= (map_state curr_state) (S_State LoggedIn_ref))))

;map next state
(assert  (=> (= next_state (S_State Start_con))         (= (map_state next_state) (S_State LoggedOut_ref))))
(assert  (=> (= next_state (S_State Connecting_con))    (= (map_state next_state) (S_State LoggedOut_ref))))
(assert  (=> (= next_state (S_State Registering_con))   (= (map_state next_state) (S_State LoggedOut_ref))))
(assert  (=> (= next_state (S_State OnDashboard_con))   (= (map_state next_state) (S_State LoggedIn_ref))))
(assert  (=> (= next_state (S_State OnProfile_con))     (= (map_state next_state) (S_State LoggedIn_ref))))
(assert  (=> (= next_state (S_State OnProduct_con))     (= (map_state next_state) (S_State LoggedIn_ref))))
(assert  (=> (= next_state (S_State OnSearchResult_con))(= (map_state next_state) (S_State LoggedIn_ref))))
(assert  (=> (= next_state (S_State Searching_con))     (= (map_state next_state) (S_State LoggedIn_ref))))
(assert  (=> (= next_state (S_State InCart_con))        (= (map_state next_state) (S_State LoggedIn_ref))))
(assert  (=> (= next_state (S_State Confirming_con))    (= (map_state next_state) (S_State LoggedIn_ref))))
(assert  (=> (= next_state (S_State CheckingOut_con))   (= (map_state next_state) (S_State LoggedIn_ref))))

;map input
(assert   (=> (= input  LOG_IN_BTN_con)        (= (map_input input) LOGIN_ref )))
(assert   (=> (= input  LOG_OUT_BTN_con)       (= (map_input input) LOGOUT_ref )))
(assert   (=> (= input  SEARCH_BTN_con)        (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  VIEW_PROFILE_BTN_con)  (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  CART_BTN_con)          (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  DASHBOARD_BTN_con)     (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  ENTER_TEXT_BTN_con)    (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  EDIT_PROFILE_BTN_con)  (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  ADD_TO_CART_BTN_con)   (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  CHECKOUT_BTN_con)      (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  CONTINUE_BTN_con)      (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  CHOOSE_PRODUCT_BTN_con)(= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  CONFIRMATION_BTN_con)  (= (map_input input) POST_LOGIN_ACTION_ref )))
(assert   (=> (= input  REGISTER_BTN_con)      (= (map_input input) PRE_LOGIN_ACTION_ref )))
(assert   (=> (= input  CONNECT_BTN_con)       (= (map_input input) PRE_LOGIN_ACTION_ref )))


;map output
(assert   (=> (= output  (seq.unit ERROR_con))    (= (map_output output ) (seq.unit ERROR_ref))))
(assert   (=> (= output  (seq.unit ACTION_DONE_con))    (= (map_output output ) (seq.unit RESPONSE_ref))))
(assert   (=> (= output  (as seq.empty (Seq Output_con)))    (= (map_output output ) (as seq.empty (Seq Output_ref)))))




