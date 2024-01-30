

We define our pipeline in a helper function called make_pipeline, using code copied from an earlier notebook (note that we factor out our 252-day momentum window into a module-level attribute, MOMENTUM_WINDOW, which will facilitate running a parameter scan later):

MOMENTUM_WINDOW = 252

def make_pipeline():
"""
Create a pipeline that filters by dollar volume and
calculates return.
"""
pipeline = Pipeline(
columns={
"returns": Returns(window_length=MOMENTUM_WINDOW),
},
screen=AverageDollarVolume(window_length=30) > 10e6
)
return pipeline
In the initialize function (required in all Zipline strategies), we attach the pipeline to the algorithm, and we schedule a custom function called rebalance that will run every market day 30 minutes before the close:

def initialize(context: algo.Context):
"""
Called once at the start of a backtest, and once per day in
live trading.
"""
# Attach the pipeline to the algo
algo.attach_pipeline(make_pipeline(), 'pipeline')

# Rebalance every day, 30 minutes before market close.
algo.schedule_function(
    rebalance,
    algo.date_rules.every_day(),
    algo.time_rules.market_close(minutes=30),
)
In before_trading_start, another built-in function which Zipline calls once per day before the market opens, we gather the pipeline output for that day and select our winners (copying code from an earlier notebook):

def before_trading_start(context: algo.Context, data: algo.BarData):
"""
Called every day before market open.
"""
factors = algo.pipeline_output('pipeline')

# Get the top 3 stocks by return
returns = factors["returns"].sort_values(ascending=False)
context.winners = returns.index[:3]
Finally, in the custom rebalance function which we scheduled to run before the close, we calculate the intraday returns (again copying code from an earlier notebook) and add logic for the entering and exiting of positions:

def rebalance(context: algo.Context, data: algo.BarData):
# calculate intraday returns for our winners
current_prices = data.current(context.winners, "price")
prior_closes = data.history(context.winners, "close", 2, "1d").iloc[0]
intraday_returns = (current_prices - prior_closes) / prior_closes

positions = context.portfolio.positions

# Exit positions we no longer want to hold
for asset, position in positions.items():
    ...


Every Zipline algorithm consists of two functions you have to define:

initialize(context) and * handle_data(context, data)
Before the start of the algorithm, Zipline calls the initialize() function and passes in a context variable. Context is a global variable that allows you to store variables you need to access from one algorithm iteration to the next.

def initialize(context):
context.security = symbol('SPY')


def handle_data(context, data):
MA1 = data[context.security].mavg(50)
MA2 = data[context.security].mavg(100)
date = str(data[context.security].datetime)[:10]
current_price = data[context.security].price
current_positions = context.portfolio.positions[symbol('SPY')].amount
cash = context.portfolio.cash
value = context.portfolio.portfolio_value
current_pnl = context.portfolio.pnl
handle_data() contains all the