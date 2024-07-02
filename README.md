# quanta [![GitHub Actions status |clojure-quant/quanta](https://github.com/clojure-quant/quanta/workflows/CI/badge.svg)](https://github.com/clojure-quant/quanta/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/io.github.clojure-quant/quanta.svg)](https://clojars.org/io.github.clojure-quant/quanta)


## What is quanta?

Quanta is a technical analysis datascience platform written in Clojure.

- Quanta works predominantly with vectors. It uses [TechML Dataset](https://github.com/techascent/tech.ml.dataset) to be fast. A similar python project is [vectorbt](https://github.com/polakowo/vectorbt).


- Ease of Use: TA tries to get out of your way so that you can focus on algorithm development. See below for a code example.

- "Batteries Included": many common statistics like moving average and linear regression can be readily accessed from within a user-written algorithm.

- Statistics and Machine Learning Libraries: 
You can use libraries similar to matplotlib, scipy, statsmodels, and sklearn to support development, analysis, and visualization of state-of-the-art trading systems.

## DOCS

Quanta is a library. 
[quanta docs](docs/README.md)

## DEMO APP

[demo](https://github.com/clojure-quant/quanta-demo)


## for developers

*code linter*  `clj -M:lint`

*code formatter `clj -M:cljfmt-fix`

*unit tests* `bb test-clj`

*warehouse io-performance speed test* `bb performance-test`







