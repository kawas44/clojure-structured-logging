# Structured logging with Pedestal.log

Use SLF4J API and usable with any implementation.

Bundled with Pedestal but can be used as a standalone lib.
As of version 0.7.1, the library pulls various dependencies for runtime
observation (dropwizard, opentracing). Current _master_ branch has those
dependencies removed.

Nothing is json ready, but most things can be customized with code and
configuration specified as environment vars, JVM properties or in data.

Serialize data in `message` key and deal with MDC data independently.

Provide functions to propagate and restore MDC across threads with some
gotchas and bugs (??).

## API: Log string & data

Simple but not back compatible.

```clojure

(log/info :k :v)        ;; ok raw => {"message":"{\"k\":\"v\"}"}
(log/info :k {:sk 1/4}) ;; ok raw => {"message":"{\"k\":{\"sk\":0.25}}"}

(log/info :k :v :exception (ex-info "boom" {:ek :ev}))
;; ok raw => {"message":"{\"k\":\"v\",\"line\":18}","stack_trace":"..."}
```

## API: Use Context data

Provides functions to propagate context across threads but with gotchas
& bugs (really?).

```clojure
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
    (log/with-context {}    ;; hack to set MDC, sometimes (bug?)!
      (log/info :tk :tv))))
;; ok {"message":"{\"tk\":\"tv\"}","io.pedestal":"{\"r\":0.25}"}
```

## Structured data

Data are serialized as string in log message field or MDC context value.
Default serialization use `pr-str` but can be customized with environment var
or System property or in data map under `:io.pedestal.log/formatter` key.

MDC data are stored as string under a predefined key `io.pedestal`.

## Json logs

To achieve json logs, you need to set a custom formatter to serialize Clojure
data as a json string. This json string will be the `message` key content.

MDC data also need to be serialize as json string with a custom formatter
and will be stored under a `io.pedestal` key.

You will have to configure you ingestion pipeline to extract json data from
the `message` and the `io.pedestal` keys of the json line.
