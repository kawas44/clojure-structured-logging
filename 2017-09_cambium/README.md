# Structured logging with Cambium

Use SLF4J and Logback but depends on old versions (1.x).

Json ready and can do anything Logback can do.

Make extensive use of MDC to store data and encode them to string in a custom
codec to be able to decode Clojure values and write them later as json.

Provide functions to propagate and restore MDC context across threads.

## API: Log string & data

Nice and back compatible.

```clojure
(log/info "foo")                ;; ok => {"message":"foo"}
(log/info {:k :v} "foo")        ;; ok => {"message":"foo","k":"v"}
(log/info {:k {:sk 1/4}} "foo") ;; ok => {"message":"foo","k":{"sk":0.25}}

(log/info (ex-info "boom" {:ek :ev}))
;; ok => {"message":"boom","exception":"..."}
(log/info (ex-info "boom" {:ek :ev}) "foo")
;; ok => {"message":"foo", "exception":"..."}
(log/info {:k :v} (ex-info "boom" {:ek :ev}) "foo")
;; ok => {"message":"foo", "k":"v", "exception":"..."}
```

## API: Use Context data

Strange inconsistencies `with-raw-mdc` & `with-logging-context`, but provides
functions to propagate context across threads.

```clojure
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
;; ok => {"message":"bar","r":0.25,"tk":"tv"}

(log/with-logging-context {:r 1/4}
  (future-call
   (mlog/wrap-raw-mdc #(log/info {:tk :tv} "bar"))))
;; ok => {"message":"bar","r":0.25,"tk":"tv"}
```

## Structured data

Data are stored in the MDC context. Values are encoded as custom strings to be
able to read them back as Clojure data.

## Json logs

To achieve json logs, you need Cambium provided Java classes to properly
configure Logback encoder.
