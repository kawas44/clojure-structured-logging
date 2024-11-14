(ns myapp.main
  (:gen-class)
  (:require
   [com.kroo.epilogue :as log]))

;; https://github.com/b-social/epilogue

(defn -main [& _]

  ;;;; API usage

  (log/info "foo")
  ;; ok => {"message":"foo"}

  (log/info "foo" {:k :v})
  ;; ok => {"message":"foo","k":"v"}

  (log/info "foo" {} :markers ["red"])
  ;; ok => {"message":"foo","k":"v","markers":["red"]}

  (log/info "foo" {:k {:sk 1/4}})
  ;; ok => {"message":"foo","k":{"sk":0.25}}

  (log/info "foo" {} :cause (ex-info "boom" {:ek :ev}))
  ;; ok => {"error.kind":"clojure.lang.ExceptionInfo", "error.message":"boom",
  ;;        "error.stack":"...", "error.data":{"ek":"ev"}, "message":"foo"}


  ;;;; Logging context & threads

  (log/with-context {:r 1/4}
    (log/info "foo" {:k :v}))
  ;; ok {"message":"foo","k":"v","r":0.25}

  (log/with-context {:r 1/4}
    (future (log/info "bar" {:tk :tv})))
  ;; ok {"message":"bar","tk":"tv","r":0.25}
  )
