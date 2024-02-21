# Message Types


## tick
emitted from quote-feed ta.quote.core


## bar-generator-output


# bar-ds
- a dataset containing bars
- has to have [:date :open :high :low :close] columns

# calendar
- a vector with two values [:market :interval]
- example [:us :d] [:us :h]

# calendar-seq
- a sequence of date-time, typically produced from a calendar and a range

# calendar-ds
- a dataset with :date column, values come from a calendar-seq

# calendar-range
- a map containing :start :en, values must be date-time
- example: {:start 2024-01-01 :end 2024-02-01}
- can be calculated from a calendar-seq
- if calendar-range is used as input to get-bars, then it has to be aligned 
  to the calendar (so use ta.calender.* functions to generate it)