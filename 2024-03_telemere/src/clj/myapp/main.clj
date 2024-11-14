(ns clj.myapp.main
  (:gen-class)
  (:require
   [clojure.data.json :as json]
   [taoensso.telemere :as t]))

;; https://github.com/taoensso/telemere

(defn -main [& _]

  (t/add-handler! :default/console
                  (t/handler:console
                   {:output-fn json/write-str}))

  (t/set-middleware!
   (fn [signal]
     (into {}
           (keep (fn [kv]
                   (when (and (second kv)
                              (not (#{:location :line :column :file :host}
                                    (first kv))))
                     kv)))
           signal)))

  ;;;; API usage

  (t/log! "foo")
  ;; ok => {"kind":"log","msg_":"foo"}

  (t/log! :info "foo")
  ;; ok => {"kind":"log","msg_":"foo"}

  (t/log! {:k :v} "foo")
  ;; ok => {"kind":"log","kvs":{"k":"v"},"msg_":"foo","k":"v"}

  (t/log! {:k :v :data {:dk :dv}} "foo")
  ;; ok => {"kind":"log", "data":{"dk":"dv"}, "kvs":{"k":"v"},
  ;;        "msg_":"foo", "k":"v"}

  (t/log! {:k :v :error (ex-info "boom" {:ek :ev})} "foo")
  ;; ???? does nothing!

  (t/event! "foo" {:data {:dk :dv} :k :v})
  ;; ok => {"kind":"event", "id":"foo", "data":{"k":"v"}, "kvs":{"k":"v"},
  ;;        "msg_":null, "k":"v"}

  ;;;; Logging context & threads

  (t/with-ctx+ {:r 1/4}
    (t/log! {:k :v} "foo"))
  ;; ok => {"kvs":{"k":"v"},"msg_":"foo","k":"v","ctx":{"r":0.25}}

  (t/with-ctx+ {:r 1/4}
    (future (t/log! {:k :v} "foo")))
  ;; ok => {"kvs":{"k":"v"},"msg_":"foo","k":"v","ctx":{"r":0.25}}


  (t/with-ctx {:r 1/4}
    (t/log! {:k :v} "foo"))
  ;; ok => {"kvs":{"k":"v"},"msg_":"foo","k":"v","ctx":{"r":0.25}}

  (t/with-ctx {:r 1/4}
    (future (t/log! {:k :v} "foo")))
  ;; ok => {"kvs":{"k":"v"},"msg_":"foo","k":"v","ctx":{"r":0.25}}
  )
