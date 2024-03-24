# Template spec

| key      | mandatory | values                                                     |
|----------|-----------|------------------------------------------------------------|
| :id      | yes       | algo name                                                  |
| :algo    | yes       | [Algo spec](#algo-spec)                                    |
| :options | yes       | [Algo options](#algo-options) fields in the interactive ui |
| :chart   | yes       | [Chart spec](#chart-spec)                                  |
| :table   | no        | [Table spec](#table-spec)                                  |
| :metrics | no        | [Metrics spec](#metrics-spec)                              |

# Algo spec
| key         | mandatory | values                                                                         |
|-------------|-----------|--------------------------------------------------------------------------------|
| :type       | yes       | :time<br/>:trailing-bar                                                        |
| :calendar   | yes       | vector with the market and timeframe:<br/>markets [...]<br/>timeframes: [....] |
| :asset      | yes       | default symbol name                                                            |
| :trailing-n | yes       | default displayed bar amount on chart                                          |
| :import     | yes       | datafeed for missing bars                                                      |
| :feed       | yes       | datafeed for existing bars                                                     |
| :algo       | yes       | algo namespace incl. function                                                  |
| *           | no        | custom params passed to algo function as options                               |

# Algo options
Array of fields. One field has the following format: 

| key   | mandatory | values                                                                                           |
|-------|-----------|--------------------------------------------------------------------------------------------------|
| :type | yes       | :select (Select Box)<br/>:string (Input Field)<br/>:bool (Checkbox)                              |
| :path | yes       | matching keyword of [Algo spec](#algo-spec)                                                      |
| :name | yes       | Label of the option field                                                                        |
| :spec | yes*      | array of or function (which returns an array) for :select<br/>not mandatory for :bool or :string |

# Chart spec
| key          | mandatory | values                                                                                                                                    |
|--------------|-----------|-------------------------------------------------------------------------------------------------------------------------------------------|
| :viz         | yes       | chart renderer namespace<br/>**highchart ns**: ta.viz.ds.highchart/highstock-render-spec<br/>**vega ns**: ta.viz.ds.vega/vega-render-spec |
| :viz-options | yes       | viz-options spec                                                                                                                          |

### :viz-options (highchart)
| key     | mandatory | values                                                                                         |
|---------|-----------|------------------------------------------------------------------------------------------------|
| :chart  | yes       |                                                                                                |
| :charts | yes       | array of indicator panes and main chart itself. an indicator is referenced by a tdm column key |

### Line colors
TODO

# Table spec

| key          | mandatory | values                                                                         |
|--------------|-----------|--------------------------------------------------------------------------------|
| :viz         | yes       | table renderer namespace<br/>**table ns:** ta.viz.ds.rtable/rtable-render-spec |
| :viz-options | yes       | viz-options spec                                                               |

# Metrics spec
| key          | mandatory | values                                                                               |
|--------------|-----------|--------------------------------------------------------------------------------------|
| :viz         | yes       | metrics renderer namespace<br/>**metrics ns:** ta.viz.ds.metrics/metrics-render-spec |
| :viz-options | yes       | viz-options spec                                                                     |