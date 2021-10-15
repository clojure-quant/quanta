

; 0 = aktuell
; 1 = 1. letzte
; von rechts nahc links

protected override void OnBarUpdate() 
{ double close0 = Close[0];  aktueller close
  double atr0 = atr[0]; 
  
  if (CurrentBar == Period)  ; letze bar. 
  {Lower[0] = close0 - NumATR * atr0; 
  } 
  
 ; currentbar > period.
 if (CurrentBar > Period)
  {if (tend==false && close0 > Upper[1])
      { tend=true; 
        Lower[0] = close0 - NumATR * atr0; 
        return; 
      } 
    
   if (tend == true && close0<Lower[1])
      { tend=false;
        Upper[0] = close0 + NumATR * atr0; 
        return; 
      }
    
   if (tend==true)
     { Lower[0] = Math.Max(Math.Max( Lower[1],close0 - NumATR * atr0),
                           Lower[1]+(NumATR *(atr[1]-atr0)));
     } 
   else 
     { Upper[0] = Math.Min( Math.Min(Upper[1], close0 + NumATR * atr0),
                           Upper[1]-(NumATR *(atr[1]-atr0))); 
      } 
    } 
 
 } ; function 



(defn cross-up? [p c]
  (if (or (nil? p) (nil? c))
    false
    (and (< p 0) (> c 0))))

(defn cross-down? [p c]
  (if (or (nil? p) (nil? c))
    false
    (and (> p 0) (< c 0))))

(defn cross [cross? series]
  (vec (conj (map cross? series (rest series)) false)))

(def cross-up (partial cross cross-up?))
(def cross-down (partial cross cross-down?))