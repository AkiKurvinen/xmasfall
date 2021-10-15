<?php
function lvlcalc($xp){
    // $lvl = round(0.715*(pow($xp, 0.453)));
    $lvl = round(0.5133*pow($xp, 0.3288));
    return ($lvl);
}
