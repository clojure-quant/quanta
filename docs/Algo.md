What is an algo?
A namespace with this symbols defined:

(algo-calc ds algo-opts)
(algo-opts-default)
(chart-spec)
(table-spec)

Result of algo-calc is a dataset with algo-specific columns added.
The dataset can be used to create a chart.
The last row is a map of the current data which is use to display a grid of columns for multiple assets.
In backtesting the window will be really wide (limited only by RAM). In realtime the window will be as short as possible; so lookback needs to be defined depending on the algo and its options.
If trades should be generated the column :signal is added which can be #{:buy :sell :hold :no}.
Chart spec is used to create custom charts for different algos.
Table-spec is used to customize the format the table for the algo.

Realtime views:

asset list: assets that use the same algo and the same options. UI is used to switch between different assets.
chart for a single algo. Selected via asset-list-select. Streamed to live clients.
Table: formatted via table-spec. Each bar changes the table in clj and sent via websocket to live clients (webbrowser)
Backtest views:

same as realtime but calculated only once.
additionally backtest statistics.
