# ta [![GitHub Actions status |pink-gorilla/trateg](https://github.com/pink-gorilla/trateg/workflows/CI/badge.svg)](https://github.com/pink-gorilla/trateg/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/ta.svg)](https://clojars.org/org.pinkgorilla/ta)

Trateg is an experimental platform for backtesting and analyzing financial instrument trading strategies in clojure.

Seeks to provide tools to run backtests on financial time series and analyze results.

Provides a light convenience wrapper over [ta4j](https://github.com/ta4j/ta4j) as well as a very early attempt at a pure clojure implementation of something similar. 

## Demo

See the [demo] directory for some usage examples.

TA ships a goldly web-app. 
Clone TA and run: `cd demo && clojure -X:goldlyb`

TA ships some notebooks that you can edit in PinkGorilla notebook.
Clone TA and run: `cd demo && clojure -X:notebook`

If you want to build the notebook from scratch, use: `cd demo && clojure -X:notebook2`

** Remote REPL **
 - in terminalyou have pinkgorilla notebook running
 - in webbrowser go to :8000 and create a new notebook
 - in your ide connect nrepl to port 9100 
   (in vscode called nrepl jack in)  
   port can be seen in .webly/config.edn
 - all evals after evaling :gorilla/on will show up in notebook
 - :gorilla/off stops evals in sniffer


## run unit tests / speed tests

```
clojure -M:test
lein speed
```

https://github.com/cnuernber/dtype-next
https://github.com/cgrand/xforms


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
