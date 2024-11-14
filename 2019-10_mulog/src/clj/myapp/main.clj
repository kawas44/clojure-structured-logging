(ns myapp.main
  (:gen-class)
  (:require
   [com.brunobonacci.mulog :as u]))

;; https://github.com/BrunoBonacci/mulog

(defn -main [& _]
  (u/start-publisher! {:type :console-json})

  ;;;; API usage

  (u/log "foo")
  ;; ok => {"mulog/event-name":"foo"}

  (u/log :event :k :v)
  ;; ok => {"mulog/event-name":"event","k":"v"}

  (u/log :event :exception (ex-info "boom" {:ek :ev}))
  ;; ok => {"mulog/event-name":"event","exception":"..."}

  (u/log :event :k :v :exception (ex-info "boom" {:ek :ev}))
  ;; ok => {"mulog/event-name":"event","k":"v","exception":"..."}

  (u/log :event :k {:sk 1/4})
  ;; ok => {"mulog/event-name":"event","k":{"sk":0.25}}

  ;;;; Logging context & threads

  (u/with-context {:r 1/4}
    (u/log :event :k :v))
  ;; ok {"r":0.25,"mulog/event-name":"event","k":"v"}

  (u/with-context {:r 1/4}
    (future (u/log :bar :tk :tv)))
  ;; KO, ratio is missing! => {"mulog/event-name":"bar","tk":"tv"}

  (u/set-global-context! {:app 'myapp})

  (u/with-context {:r 1/4}
    (future-call ((fn [ctx] #(u/with-context ctx
                               (u/log :bar :tk :tv)))
                  (u/local-context))))
  ;; ok => {"app":"myapp","mulog/event-name":"bar","r":0.25,"tk":"tv"}
  )
