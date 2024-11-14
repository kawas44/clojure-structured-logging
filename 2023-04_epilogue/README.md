# Structured logging with Epilogue

Use SLF4J 2.x API and usable with any implementation.

Json ready if you pair it with sister library _typeset.logback_. Otherwise
you need to add some Java code to properly print Clojure data in json strings.

Make use of SLF4J 2 key/value and markers.

## API: Log string & data

Can use markers. Very complete handling of exceptions. Not back compatible.

```clojure
(log/info "foo")                ;; ok => {"message":"foo"}
(log/info "foo" {:k :v})        ;; ok => {"message":"foo","k":"v"}
(log/info "foo" {:k {:sk 1/4}}) ;; ok => {"message":"foo","k":{"sk":0.25}}

(log/info "foo" {} :markers ["red"])
;; ok => {"message":"foo","k":"v","markers":["red"]}

(log/info "foo" {} :cause (ex-info "boom" {:ek :ev}))
;; ok => {"error.kind":"clojure.lang.ExceptionInfo", "error.message":"boom",
;;        "error.stack":"...", "error.data":{"ek":"ev"}, "message":"foo"}
```

## API: Use Context data

It just works!

```clojure
(log/with-context {:r 1/4}
  (log/info "foo" {:k :v}))
;; ok {"message":"foo","k":"v","r":0.25}

(log/with-context {:r 1/4}
  (future (log/info "bar" {:tk :tv})))
;; ok {"message":"bar","tk":"tv","r":0.25}
```

## Structured data

Data are stored using SLF4J 2.x key/val feature.

## Json logs

To achieve json logs, you need a way to serialize Clojure data to
json string. With Logback, you may use the _typeset.logback_ library, for any
other implementation you need to add custom code to serialize Clojure
classes properly.
