(ns myapp.main
  (:gen-class)
  (:require
   [cambium.codec :as codec]
   [cambium.core :as log]
   [cambium.logback.json.flat-layout :as flat]
   [cambium.mdc :as mlog]))

;; https://github.com/cambium-clojure/cambium.core/tree/master

(defn -main [& _]
  (flat/set-decoder! codec/destringify-val)

  ;;;; API usage

  (log/info "foo")
  ;; ok => {"message":"foo"}

  (log/info (ex-info "boom" {:ek :ev}))
  ;; ok => {"message":"boom", "exception":"..."}

  (log/info (ex-info "boom" {:ek :ev}) "foo")
  ;; ok => {"message":"foo", "exception":"..."}

  (log/info {:k :v} "foo")
  ;; ok => {"message":"foo", "k":"v"}

  (log/info {:k :v} (ex-info "boom" {:ek :ev}) "foo")
  ;; ok => {"message":"foo", "k":"v", "exception":"..."}

  (log/info {:k {:sk 1/4}} "foo")
  ;; ok => {"message":"foo","k":{"sk":0.25}}


  ;;;; Logging context & threads

  (log/with-logging-context {:r 1/4}
    (log/info {:k :v} "foo"))
  ;; ok => {"message":"foo", "k":"v", "r":0.25}

  (log/with-logging-context {:r 1/4}
    (future (log/info {:tk :tv} "bar")))
  ;; KO ratio is missing! => {"message":"bar","tk":"tv"}

  (log/with-logging-context {:r 1/4}
    (future-call
     (log/wrap-logging-context (log/get-context)
                               #(log/info {:tk :tv} "bar"))))
  ;; ok => {"r":0.25,"tk":"tv","message":"bar"}

  (log/with-logging-context {:r 1/4}
    (future-call
     (mlog/wrap-raw-mdc #(log/info {:tk :tv} "bar"))))
  ;; ok => {"r":0.25,"tk":"tv","message":"bar"}
  )
