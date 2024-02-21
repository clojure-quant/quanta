(ns notebook.playground.techml)



;(require '[tech.v3.datatype.casting :as casting])
;(println @casting/valid-datatype-set)

 [:byte :int8 :int :uint64 :uint8 :long :int32  :short :int64,  :uint16 :big-integer :uint32 :int16
  :boolean :bool
  :float32 :decimal :double :float :float64
  :char :text :string,
  :native-buffer :array-buffer
  :keyword
  :uuid
  :object
  :list
  :dataset
  :persistent-map :persistent-vector :persistent-set
  :bitmap :tensor

  :local-date-time :epoch-milliseconds :time-microseconds :packed-instant
  :days :seconds :microseconds :time-nanoseconds :epoch-days :instant
  :zoned-date-time :epoch-nanoseconds :years :time-seconds :epoch-microseconds
  :milliseconds :local-time :nanoseconds :duration :packed-duration
  :hours :epoch-seconds :packed-local-date :epoch-hours :time-milliseconds
  :weeks :local-date :packed-local-time]