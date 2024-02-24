# ta.engine

ta.engine.protocol defines how algos and environments can interact with it.

## algo <=> engine <=> environment
The engine can be considered as an excel spreadsheet.
The environment interacts wirh the engine to manage calendar-time.
Algos use the engine to calculate useful things.

## cell types
- calendar cells: a formula cell that gets the current calendar time as input 
- combined cell: a formula cell that gets obe or more cells as input.
- value-cells: a cell whose value can be set/read.

## calendar-time
the environment can set the calendar time

## javelin-engine
an engine whose calculations are powered by javelin.
all cells have atom syntax; and can therefire be @derefed.

## xxx-engine
The problem of how to re-calculate a dag of calculations can be solved in many ways.
Alternatives are a distributed networked engine; a channel based calculation, etc.



 
