
# color hex/rgba codes
- Colors:
- You need to return color codes supported by tradingview.
- for this you can use (color :red)



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

Location value for plotshape, plotchar functions. Shape is plotted near the top chart border.

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
The main
difference between plotshape and plotchar is that with plotchar, the shape is an ASCII or Unicode symbol (provided it’s supported by the TradingView standard font) defined with the char parameter.

The default character is ★ (U+2605, the “BLACK STAR” character). It’s possible to use any letter or digit and many symbols, for example: ❤, ☀, €, ⚑, ❄, ◆, ⬆, ⬇. The supported character codes are those of the Trebuchet MS font family.

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