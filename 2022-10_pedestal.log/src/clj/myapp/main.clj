(ns myapp.main
  (:gen-class)
  (:require
   [io.pedestal.log :as log]))

;; http://pedestal.io/pedestal/0.7/reference/logging.html

(defn -main [& _]

  (System/setProperty "io.pedestal.log.formatter"
                      "clojure.data.json/write-str")

  ;;;; API usage

  (log/info :k :v)
  ;; ok raw => {"message":"{\"k\":\"v\"}"}

  (log/info :k {:sk 1/4})
  ;; ok raw => {"message":"{\"k\":{\"sk\":0.25}}"}

  (log/info :k :v :exception (ex-info "boom" {:ek :ev}))
  ;; ok raw => {"message":"{\"k\":\"v\",\"line\":18}","stack_trace":"..."}

  ;;;; Logging context & threads

  (log/with-context {:r 1/4}
    (log/info :k :v))
  ;; ok {"message":"{\"k\":\"v\"}","io.pedestal":"{\"r\":0.25}"}

  (log/with-context {:r 1/4}
    (future (log/info :tk :tv)))
  ;; KO ratio is missing, bug? => {"message":"{\"tk\":\"tv\"}"}

  (log/with-context {:r 1/4}
    (future
      (log/put-formatted-mdc log/*mdc-context*) ;; manually set MDC
      (log/info :tk :tv)))
  ;; ok {"message":"{\"tk\":\"tv\"}","io.pedestal":"{\"r\":0.25}"}

  (log/with-context {:r 1/4}
    (future
      (log/with-context {}    ;; hack to set MDC!
        (log/info :tk :tv))))
  ;; ok {"message":"{\"tk\":\"tv\"}","io.pedestal":"{\"r\":0.25}"}
  )
