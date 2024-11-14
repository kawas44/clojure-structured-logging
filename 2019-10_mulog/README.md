# Structured logging with mulog

Custom implementation does not follow any Java API.

Json ready but with few configurations.

Does not use SLF4J, you may need to add some custom code to interop with
legacy code.

## API: Log string & data

Nice, event oriented, not back compatible. Logging levels don't exist.

```clojure

(u/log "foo")             ;; ok => {"mulog/event-name":"foo"}
(u/log :foo :k :v)        ;; ok => {"mulog/event-name":"foo","k":"v"}
(u/log :foo :k {:sk 1/4}) ;; ok => {"mulog/event-name":"foo","k":{"sk":0.25}}

(u/log :foo :exception (ex-info "boom" {:ek :ev}))
;; ok => {"mulog/event-name":"foo","exception":"..."}
(u/log :foo :k :v :exception (ex-info "boom" {:ek :ev}))
;; ok => {"mulog/event-name":"foo","k":"v","exception":"..."}
```

## API: Use Context data

Only provides a thread-local context function. We need to write our own helper
function to propagate context across threads.

```clojure
(u/with-context {:r 1/4}
  (u/log :foo :k :v))
;; ok {"r":0.25,"mulog/event-name":"foo","k":"v"}

(u/with-context {:r 1/4}
  (future (u/log :bar :tk :tv)))
;; KO ratio is missing! => {"mulog/event-name":"bar","tk":"tv"}
```

## Structured data

Everything is json ready when using json publishers.

## Json logs

Everything is json ready when using the advance json file publisher but there
are very few option to set. You will have to deal with log management
(naming, rotating, deleting).

You may also need to add a publisher transformer to add/rename some keys for
your ingestion pipeline (timestamp, log level, etc).
