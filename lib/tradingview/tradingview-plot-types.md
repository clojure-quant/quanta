

Colors:
You need to return color codes supported by tradingview.
for this you can use (color :red)




## Line

* `type`- 'line'
* `visible`- boolean
* `plottype`- number, one of the following:
  * `0`- line
  * `1`- histogram
  * `3`- cross
  * `4`- area
  * `5`- columns
  * `6`- circles
  * `7`- line with breaks
  * `8`- area with breaks
  * `9`- step line
* `color`- string
* `linestyle`- number
* `linewidth`- number
* `trackPrice`- boolean

## Shapes

* `type`- 'shapes'
* `visible`- boolean
* `plottype`- string, can have following values:
  * `shape_arrow_down`
  * `shape_arrow_up`
  * `shape_circle`
  * `shape_cross`
  * `shape_xcross`
  * `shape_diamond`
  * `shape_flag`
  * `shape_square`
  * `shape_label_down`
  * `shape_label_up`
  * `shape_triangle_down`
  * `shape_triangle_up`
* `location`- string, one of the following:
  * `AboveBar`
  * `BelowBar`
  * `Top`
  * `Bottom`
  * `Right`
  * `Left`
  * `Absolute`
  * `AbsoluteUp`
  * `AbsoluteDown`
* `color`- string
* `textColor`- string

## Chars

* `type`- 'chars'
* `visible`- boolean
* `char`- string
* `location`- string, one of the following:
  * `AboveBar`
  * `BelowBar`
  * `Top`
  * `Bottom`
  * `Right`
  * `Left`
  * `Absolute`
  * `AbsoluteUp`
  * `AbsoluteDown`
* `color`- string
* `textColor`- string

## Arrows

* `type`- 'arrows'
* `visible`- boolean
* `colorup`- string
* `colordown`- string