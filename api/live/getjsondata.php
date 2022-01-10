<?php

$date = 'games/'.date("d-m-Y").'/';
$files = null;

if(!is_dir($date)){

    echo 'There is no ongoing game';

}

else{
    $lobbyname = '';
    if(isset($_GET['lobby'])){
        $lobbyname = $_GET['lobby'];
        $date.=$lobbyname.='/';
    }

    if(!is_dir($date)){
        echo 'There is no such lobby';
    }
    else{


    $files = scandir($date);
    function isJson($string) {
        json_decode($string);

        if (json_last_error() === JSON_ERROR_NONE) {
            return true;
        }
        else{
            return false;
        }
    }
}



$data = array(
    array('zz', 'name' => 'Jack', 'number' => 22, 'birthday' => '12/03/1980'),
    array('xx', 'name' => 'Adam', 'number' => 16, 'birthday' => '01/12/1979'),
    array('aa', 'name' => 'Paul', 'number' => 16, 'birthday' => '03/11/1987'),
    array('cc', 'name' => 'Helen', 'number' => 44, 'birthday' => '24/06/1967'),
);
$data = array();

if($files){
foreach($files as $json) {

    $json = file_get_contents($date.$json);

    if(isJson($json)){
        $json = json_decode($json, true);
        foreach($json['data'] as $item) {


         $playerhp = $item['hp'];

         if(intval($playerhp)<1){
                $playerhp = '💀';
         }
         else{
             $iter = intval($playerhp);
             $playerhp = '';
             for($val = 0; $val < $iter; $val ++){
                    $playerhp .='🔴';
             }
         }

         $acc = 100;
         if($item['hiscore'] > 0 && $item['acc']>0){
            $acc =  round((intval($item['hiscore'])/intval($item['acc']))*100);
         }

        $new_array = array(
            "username" =>  $item['username'],
            "hiscore" => $item['hiscore'],
            "acc" => $acc,
            "hp" => $playerhp,
        );
        array_push($data, $new_array);


        }
    }

}
}



function make_comparer() {
    // Normalize criteria up front so that the comparer finds everything tidy
    $criteria = func_get_args();
    foreach ($criteria as $index => $criterion) {
        $criteria[$index] = is_array($criterion)
            ? array_pad($criterion, 3, null)
            : array($criterion, SORT_ASC, null);
    }

    return function($first, $second) use ($criteria) {
        foreach ($criteria as $criterion) {
            // How will we compare this round?
            list($column, $sortOrder, $projection) = $criterion;
            $sortOrder = $sortOrder === SORT_DESC ? -1 : 1;

            // If a projection was defined project the values now
            if ($projection) {
                $lhs = call_user_func($projection, $first[$column]);
                $rhs = call_user_func($projection, $second[$column]);
            }
            else {
                $lhs = $first[$column];
                $rhs = $second[$column];
            }

            // Do the actual comparison; do not return if equal
            if ($lhs < $rhs) {
                return -1 * $sortOrder;
            }
            else if ($lhs > $rhs) {
                return 1 * $sortOrder;
            }
        }

        return 0; // tiebreakers exhausted, so $first == $second
    };
}
//
usort($data, make_comparer(['hiscore',SORT_DESC],['acc', SORT_DESC]));
//print_r($data);
$html_table = '<table><tr>
    <th>#</th>
    <th>Username</th>
    <th>Score</th>
    <th>Accuracy</th>
    <th>HP</th>
    </tr>';

 $index = 1;
     foreach ($data as $mks){
        $html_table .=  "<tr><td>".$index."</td><td>".$mks['username']."</td><td>".$mks['hiscore']."</td><td>".$mks['acc']." %</td><td>".$mks['hp']."</td></tr>";
        $index++;
    }
$html_table .=  "</table></div>";

$html_table .= '</table>';
echo $html_table;

}
