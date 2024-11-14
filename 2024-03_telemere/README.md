# Structured logging with mulog

Custom implementation does not follow any Java API. But may try to be compatible
with openTelemetry.

Not really Json ready, need to provide a serialize function.

May act as an implementation of the SLF4J API.

## API: Log string & data

Maybe to complicated... No success, trying to log error.

```clojure
(t/log! "foo")         ;; ok => {"kind":"log","msg_":"foo"}
(t/log! :info "foo")   ;; ok => {"kind":"log","msg_":"foo"}
(t/log! {:k :v} "foo") ;; ok => {"kind":"log","kvs":{"k":"v"},"msg_":"foo","k":"v"}

(t/log! {:k :v :data {:dk :dv}} "foo")
;; ok => {"kind":"log", "data":{"dk":"dv"}, "kvs":{"k":"v"},
;;        "msg_":"foo", "k":"v"}

(t/log! {:k :v :error (ex-info "boom" {:ek :ev})} "foo")
;; ???? does nothing!

(t/event! "foo" {:data {:dk :dv} :k :v})
;; ok => {"kind":"event", "id":"foo", "data":{"k":"v"}, "kvs":{"k":"v"},
;;        "msg_":null, "k":"v"}
```

## API: Use Context data

It works! In a `ctx` key ?!

```clojure
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
```

## Structured data

Data serialized as EDN by default, need to provide your own Json serializer.

## Json logs

Nothing provided to log to Json file. Only current handler is a console handler
where you can configure a `stream` reference.
