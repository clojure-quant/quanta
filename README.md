# ta [![GitHub Actions status |pink-gorilla/trateg](https://github.com/pink-gorilla/trateg/workflows/CI/badge.svg)](https://github.com/pink-gorilla/trateg/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/ta.svg)](https://clojars.org/org.pinkgorilla/ta)

Trateg is an experimental platform for backtesting and analyzing financial instrument trading strategies in clojure.

Seeks to provide tools to run backtests on financial time series and analyze results.

Provides a light convenience wrapper over [ta4j](https://github.com/ta4j/ta4j) as well as a very early attempt at a pure clojure implementation of something similar. 

## Usage

See the [examples](dev/examples) directory for some usage examples.

## PinkGorilla Notebook

TA ships some sample-notebooks that you can edit in PinkGorilla notebook.
In your IDE, jack-in to a new repl with +notebook profile.

The advantage of running the notebook in the IDE is that changed code in the TA library will be reflected in the notebook; this helps TA development.

For testing you can also run pink-gorilla standalone:
```
lein notebook
```


## run unit tests / speed tests

```
lein test
lein speed
```

## License

Copyright © 2019 Justin Tirrell

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
