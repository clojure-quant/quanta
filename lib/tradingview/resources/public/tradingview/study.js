

console.log("LOADING MYSTUDY!!")

function mystudy (m) {
 console.log("Creating mystudy with: " + m)
 return function () {
    //this.main = m;
    

 }}

 function bongo () {
    console.log("bongo!")
    this.main = function(ctx, inputCallback) {
        this._context = ctx;
        this._input = inputCallback;
        //console.log("main called! context hs been set!");
        //console.log("ctx: " + ctx)
        //console.log("input: " + inputCallback)
        return [100];
  
      };
 }


var C = class { // ...
              }
Object.defineProperty (C, 'name', {value: 'TheName'});

// test: 

console.log("class name: "  (new C()).constructor.name )
// let itsName =  ;
// itsName === 'TheName' -> true



window.mystudy = mystudy;
window.bongo = bongo;

// export default {
//	history: history,
//
//    getBars: