
[a (+ (x 1) (y 1))
 b (* (y 0) (z 1))]

[smooth (/ (+ (* 4.0 (p 0))
              (* 3.0 (p 1))
              (* 2.0 (p 2))
              (p 3))
           10.0)
 detrender (* (+ (0.0962 smooth)
                 (0.5769 (smooth 2))
                 (0.5769 (smooth 4))
                 (0.0962 (smooth 6)))
              (* 0.075 (+ .54 (period 1))))]

; If CurrentBar > 5 then begin  
; Smooth = (4*Price + 3*Price[1] + 2*Price[2] + Price[3]) / 10;  
; Detrender = (.0962*Smooth + .5769*Smooth[2] - .5769*Smooth[4] - .0962*Smooth[6])*(.075*Period[1] + .54); ; Compute InPhase and Quadrature components
; Q1 = (.0962*Detrender + .5769*Detrender[2] - .5769*Detrender[4] - .0962*Detrender[6])*(.075*Period[1] + .54);   
; I1 = Detrender[3]  ;  Advance the phase of I1 and Q1 by 90 degrees
; jI = (.0962*I1 + .5769*I1[2] - .5769*I1[4] - .0962*I1[6])*(.075*Period[1] + .54); 
; jQ = (.0962*Q1 + .5769*Q1[2] - .5769*Q1[4] - .0962*Q1[6])*(.075*Period[1] + .54); Phasor addition for 3 bar averaging
; I2 = I1 - jQ;  Q2 = Q1 + jI ; Smooth the I and Q components before applying the discriminator}  
; I2 = .2*I2 + .8*I2[1];  Q2 = .2*Q2 + .8*Q2[1];  {Homodyne Discriminator}  
; Re = I2*I2[1] + Q2*Q2[1];  
; Im = I2*Q2[1] - Q2*I2[1];  Re = .2*Re + .8*Re[1];  
;Im = .2*Im + .8*Im[1];
; If Im <> 0 and Re <> 0 then Period = 360/ArcTangent(Im/Re);  
; If Period > 1.5*Period[1] then Period = 1.5*Period[1];  
; If Period < .67*Period[1] then Period = .67*Period[1];
; If Period < 6 then Period = 6; 
; If Period > 50 then Period = 50;  
; Period = .2*Period + .8*Period[1];  
; SmoothPeriod = .33*Period + .67*SmoothPeriod[1]; 
;  If I1 <> 0 then Phase = (ArcTangent(Q1 / I1));  
; DeltaPhase = Phase[1] - Phase;  
; If DeltaPhase < 1 then DeltaPhase = 1;  
; alpha = FastLimit / DeltaPhase;  
; If alpha < SlowLimit then alpha = SlowLimit;  
; MAMA = alpha*Price + (1 - alpha)*MAMA[1];   
; FAMA = .5*alpha*MAMA + (1 - .5*alpha)*FAMA[1]; 
; Plot1(MAMA, “MAMA”);  
; Plot2(FAMA, “FAMA”);
