<?php
require 'header.php';
require 'apikey.php';

 // APIKEY VALIDATION
    $api_key = null;
    if (isset($_GET['apikey']) ){$api_key = $_GET['apikey'];}
    else if (isset($_POST['apikey'])) {$api_key = $_POST['apikey'];}

if ($api_key==$apikey){
include('lvlcalc.php');
// Create
function addRound(int $id, int $score){

    require 'db.php';

    $sql = "INSERT INTO xmasfallrounds (userid, score) VALUES ({$id},{$score})";

    if (mysqli_query($con, $sql)) {
        return "Round added successfully";
    } else {
        return "Error while adding Round";
    }

    mysqli_close($con);

}
function updateHiScore($id, $score){

    function getMaxScore($user_id){
        $id = intval($user_id);
        require 'db.php';
        $query = "SELECT hiscore, xp  FROM xmasfall WHERE id={$id}";
        $result = $con->query($query);
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if(is_null($row)){

              return null;
        }
        else{

        $arr = [];
        $arr[0]=$row["hiscore"];
        $arr[1]=$row["xp"];
        return $arr;
        }

    }
    $userScoreArr = getMaxScore($id);

    if(is_null($userScoreArr)){
        return 'User not found';
    }
    else{
    addRound($id, $score);

    $lvlFirst = lvlcalc($_SESSION["xp"]);

    $lvlScore = $userScoreArr[1] + $score;
    $_SESSION["xp"] = $lvlScore;

    $lvlNow = lvlcalc($_SESSION["xp"]);

    $date = date_create()->format('Y-m-d');

    require 'db.php';



    if($userScoreArr[0] < $score){

        $sql = "UPDATE xmasfall SET hiscore=?, xp=?, date=?  WHERE id=?";
        $stmt = mysqli_stmt_init($con);
        if(!mysqli_stmt_prepare($stmt, $sql)){

            return 'Cannot make request';
        }
        else{
            mysqli_stmt_bind_param($stmt,'iisi', $score, $lvlScore, $date, $id );
            mysqli_stmt_execute($stmt);
            if ($stmt->execute() &&($lvlNow > $lvlFirst)) {
                return 'Hi-Score updated, LVL rise';
            }
            else if($stmt->execute()){
                 return 'Hi-Score updated';
            } else {
                return 'Cannot update user stats';
            }
        }
        mysqli_close($con);
    }
    else{
        $sql = "UPDATE xmasfall SET xp=? WHERE id=?";
        $stmt = mysqli_stmt_init($con);
        if(!mysqli_stmt_prepare($stmt, $sql)){

            return 'Cannot make request';
        }
        else{
            mysqli_stmt_bind_param($stmt,'ii', $lvlScore, $id);
            mysqli_stmt_execute($stmt);

            if ($stmt->execute()&&($lvlNow > $lvlFirst)) {
                return 'XP added, LVL rise';
            }
            else if($stmt->execute()){
                return 'XP added';
            } else {
                return 'Cannot update user stats';
            }
        }
        mysqli_close($con);
    }
    }

}

// SESSION
    if(isset($_GET['getsessiondata'])){

        if(isset($_SESSION['id'])&&isset($_SESSION["username"])&&isset($_SESSION["xp"])) {
        echo $_SESSION['id'].'-'.$_SESSION["username"].'-'.lvlcalc(intval($_SESSION["xp"]));
        }
        else{
            echo 'no session';
        }
    }
// UPDATE
    else if(isset($_GET['user_id']) && isset($_GET['hiscore'])){
        $id=intval($_GET['user_id']);
        $score=intval($_GET['hiscore']);
        echo(updateHiScore($id,$score));
    }
}


// APIKEY FAILED
else{
    echo 'API-key is incorrect or not set';
}
