<?php
require 'header.php';
header("Content-Type:application/json");
include('apikey.php');

include('lvlcalc.php');

function findUserByID($user_id){

    function response($id,$uname,$score,$lvl,$date){
        $response['uid'] = intval($id);
        $response['uname'] = $uname;
        $response['hiscore'] = intval($score);
        $response['lvl'] = intval($lvl);
        $response['date'] = $date;
        $json_response = json_encode($response);
        echo $json_response;
    }
	include('db.php');

	$result = mysqli_query($con,"SELECT * FROM xmasfall WHERE id=$user_id");
	if(mysqli_num_rows($result)>0){
        $row = mysqli_fetch_array($result);
        $id = $row['id'];
        $uname = $row['uname'];
        $score = $row['hiscore'];
        $lvl = lvlcalc($row['xp']);
        $date = $row['date'];
        mysqli_close($con);
        response($id, $uname, $score, $lvl, $date);
	}
    else{
        $arr = array('error' => 'No User Found');
        echo json_encode($arr);
    }
}
function addHash($user_id){
	include('db.php');
        $keyhash = bin2hex(random_bytes(20));
        $expires = date("U") + 1800;

       $sql = "UPDATE xmasfall SET keyhash=?, valid=?  WHERE id=?";
        $stmt = mysqli_stmt_init($con);
        if(!mysqli_stmt_prepare($stmt, $sql)){
            mysqli_close($con);
                return 1;
        }
        else{
        mysqli_stmt_bind_param($stmt,'ssi', $keyhash, $expires, $user_id);
        mysqli_stmt_execute($stmt);

        if ($stmt->execute()) {
            mysqli_close($con);
            return $keyhash;
            }
        else {
            mysqli_close($con);
            return 1;
            }
        }


}
function login(){

    require 'db.php';

    $mailuid = $_POST['username'];
    $password = $_POST['password'];

    $sql = "SELECT * FROM xmasfall WHERE uname=?;";

    $stmt = mysqli_stmt_init($con);
    if(!mysqli_stmt_prepare($stmt, $sql)){
            $arr = array('error' => 'Cannot make request');
            echo json_encode($arr);
    }
    else{
        mysqli_stmt_bind_param($stmt, "s", $mailuid);
        mysqli_stmt_execute($stmt);

        $result = mysqli_stmt_get_result($stmt);

        if($row = mysqli_fetch_assoc($result)){
            $pwdcheck = password_verify($password,$row['passw']);

            if($pwdcheck == false){
                $arr = array('error' => 'Wrong password or username');
                echo json_encode($arr);
            }
            else if($pwdcheck == true){
                session_start();
                $_SESSION['id'] = $row['id'];
                $_SESSION['uname'] = $row['uname'];
                $_SESSION['xp'] = $row['xp'];
                $_SESSION['hiscore'] = $row['hiscore'];
                $truelvl = lvlcalc($row['xp']);
                $_SESSION['lvl'] = $truelvl;
                $keyhash = addHash($row['id']);
                $arr = array('id' => $row['id'],'uname'=> $row['uname'],'xp'=>$row['xp'],'hiscore'=>$row['hiscore'], 'lvl'=>$truelvl, 'keyhash'=>  $keyhash);

                echo json_encode($arr);
            }

            else{
                $arr = array('error' => 'Wrong password or username');
                echo json_encode($arr);
            }

        }
        else{
            $arr = array('error' => 'User not found');
            echo json_encode($arr);
        }
    }


    mysqli_close($con);

}
function addNewUser($username){
    include('db.php');

    /* Prepared statement, stage 1: prepare */
    $stmt = $con->prepare("INSERT INTO xmasfall (uname, passw)
    VALUES (?,?)");

    /* Prepared statement, stage 2: bind and execute */

    $passw = $_POST['password'];
    $rpassw = $_POST['rpassword'];

    $hash = password_hash($passw, PASSWORD_DEFAULT);
    $stmt->bind_param("ss", $username, $hash);

    $stmt->execute();

    if ($stmt->error){
        $err = $stmt->error;
        if (strpos($err, 'Duplicate entry') !== false) {
            $arr = array('error' => 'Username already taken');
            echo json_encode($arr);
        }
        else{
            $arr = array('error' => $err);
            echo json_encode($arr);
        }
    }
    else{
        login();
    }

    /* close statement */
    $stmt->close();

}
function getAllScores($whatScores){
    include('db.php');

    if ($con->connect_error) {
             $arr = array("Connection failed: " . $con->connect_error);
        echo json_encode($arr);

    }

    if($whatScores=="all"){
        $sql = "SELECT hiscore, uname, xp, date FROM xmasfall WHERE hiscore > 0 ORDER BY hiscore DESC LIMIT 50";
    }
    else if($whatScores=="day"){
        $sql = "SELECT hiscore, uname, xp, date FROM xmasfall WHERE (hiscore > 0) AND (date = CURDATE()) ORDER BY hiscore DESC LIMIT 50";
    }
    else if($whatScores=="week"){
        $sql = "SELECT hiscore, uname, xp, date FROM xmasfall WHERE (hiscore > 0) AND (date > DATE(NOW() - INTERVAL 1 WEEK)) ORDER BY hiscore DESC LIMIT 50";
    }
    else if($whatScores=="month"){
        $sql = "SELECT hiscore, uname, xp, date FROM xmasfall WHERE (hiscore > 0) AND (date > DATE(NOW() - INTERVAL 1 MONTH)) ORDER BY hiscore DESC LIMIT 50";
    }
    else if($whatScores=="year"){
        $sql = "SELECT hiscore, uname, xp, date FROM xmasfall WHERE (hiscore > 0) AND (date > DATE(NOW() - INTERVAL 1 YEAR)) ORDER BY hiscore DESC LIMIT 50";
    }


    $result = $con->query($sql);

    $data = $result->fetch_all( MYSQLI_ASSOC );
    foreach($data as &$a){
        $a['hiscore'] = intval($a['hiscore']);
            $a['xp'] = intval($a['xp']);
            $a['lvl'] = lvlcalc(intval($a['xp']));
    }

   echo json_encode( array( 'scores'=>$data ));


    $con->close();

}
function getAllScoresNew($whatScores, $limit){
    include('db.php');
    $limit = intval($limit);

    if ($con->connect_error) {
             $arr = array("Connection failed: " . $con->connect_error);
        echo json_encode($arr);

    }
    $whatScores = strtolower($whatScores);

    if($whatScores=="all"){
        $sql = "SELECT hiscore, uname, xp, date FROM xmasfall WHERE hiscore > 0 ORDER BY hiscore DESC LIMIT $limit";
    }
    else if($whatScores=="day"){
        $sql = "SELECT max(r.score) AS hiscore, x.uname, x.xp, r.date
                FROM xmasfallrounds as r
                INNER JOIN xmasfall AS x ON r.userid = x.id
                WHERE (hiscore > 0) AND (r.date = CURDATE())
                GROUP BY x.uname
                ORDER BY hiscore DESC
                LIMIT $limit
                ";
    }
    else if($whatScores=="week"){
        $sql ="SELECT max(r.score) AS hiscore, x.uname, x.xp, r.date
                FROM xmasfallrounds as r
                INNER JOIN xmasfall AS x ON r.userid = x.id
                WHERE (hiscore > 0) AND (r.date > DATE(NOW() - INTERVAL 1 WEEK))
                GROUP BY x.uname
                ORDER BY hiscore DESC
                LIMIT $limit"
                ;
    }
    else if($whatScores=="month"){
        $sql = "SELECT  max(r.score) AS hiscore, x.uname, x.xp, r.date
            FROM xmasfallrounds as r
            INNER JOIN xmasfall AS x ON r.userid = x.id
            WHERE (hiscore > 0) AND (r.date > DATE(NOW() - INTERVAL 1 MONTH))
            GROUP BY x.uname
            ORDER BY hiscore DESC
            LIMIT $limit"
            ;
    }
    else if($whatScores=="year"){

        $sql = "SELECT max(r.score) AS hiscore, x.uname,  x.xp, r.date
            FROM xmasfallrounds as r
            INNER JOIN xmasfall AS x ON r.userid = x.id
            WHERE (hiscore > 0) AND (r.date > DATE(NOW() - INTERVAL 1 YEAR))
            GROUP BY x.uname
            ORDER BY hiscore DESC
            LIMIT $limit";
        }
    else{
        $arr = array('error' => 'No valid period');
        echo json_encode($arr);
        exit();
    }

    if ($result = mysqli_query($con,$sql)) {
        $data = $result->fetch_all( MYSQLI_ASSOC );
        foreach($data as &$a){
            $a['hiscore'] = intval($a['hiscore']);
                $a['xp'] = intval($a['xp']);
                $a['lvl'] = lvlcalc(intval($a['xp']));
        }

    echo json_encode( array( 'scores'=>$data ));


        $con->close();

    }
    else{
            $arr = array('error' => 'No results');
                echo json_encode($arr);
    }
}
function updateHiScore($id, $score){


    function getMaxScore($user_id){
        require 'db.php';
        $query = "SELECT hiscore, xp  FROM xmasfall WHERE id={$user_id}";
        $result = $con->query($query);
        $row = $result->fetch_assoc();
        mysqli_close($con);
        if(is_null($row)){

              return null;
        }
        else{

            $arr = [];
            $arr[0]=$row["score"];
            $arr[1]=$row["xp"];

        return $arr;
        }

    }

    $userScoreArr = getMaxScore($id);

    if(is_null($userScoreArr)){
        $arr = array('error' =>'User not found');
        echo json_encode($arr);
    }
    else{
          $lvlScore = $userScoreArr[1] + $score;
    $date = date_create()->format('Y-m-d');

    require 'db.php';

    if($userScoreArr[0] < $score){
        $sql = "UPDATE xmasfall SET hiscore=?, xp=?, date=?  WHERE id=?";
        $stmt = mysqli_stmt_init($con);
        if(!mysqli_stmt_prepare($stmt, $sql)){
                $arr = array('error' => 'Cannot make request');
                echo json_encode($arr);
        }
        else{
        mysqli_stmt_bind_param($stmt,'iisi', $score, $lvlScore,$date, $id );
        mysqli_stmt_execute($stmt);
            if ($stmt->execute()) {
            $arr = array('success' => 'Hi-Score updated');
            echo json_encode($arr);
        } else {
            $arr = array('error' =>'Cannot update user stats');
            echo json_encode($arr);
        }
        }
        mysqli_close($con);
    }
    else{
        $sql = "UPDATE xmasfall SET xp=? WHERE id=?";
        $stmt = mysqli_stmt_init($con);
        if(!mysqli_stmt_prepare($stmt, $sql)){
                $arr = array('error' => 'Cannot make request');
                echo json_encode($arr);
        }
        else{
        mysqli_stmt_bind_param($stmt,'ii', $lvlScore, $id);
        mysqli_stmt_execute($stmt);
            if ($stmt->execute()) {
        // it worked
            $arr = array('success' => 'XP added');
            echo json_encode($arr);
        } else {
        // it didn't
            $arr = array('error' =>'Cannot update user stats');
            echo json_encode($arr);
        }
        }
        mysqli_close($con);
    }
    }


}
// APIKEY VALIDATION
    $api_key = null;
    if (isset($_GET['apikey']) ){$api_key = $_GET['apikey'];}
    else if (isset($_POST['apikey'])) {$api_key = $_POST['apikey'];}

if ($api_key==$apikey){

// SELECT
    if (isset($_GET['user_id']) && $_GET['user_id']!=""&&!isset($_GET['hiscore']) ) {
        $user_id = intval($_GET['user_id']);
        findUserByID($user_id);
    }
    else if(isset($_POST['username']) && isset($_POST['password']) & !isset($_POST['rpassword'])){

        login();
    }
    else if(isset($_GET['scores'])&&isset($_GET['limit'])){
        $whatScores = $_GET['scores'];
        $limit = $_GET['limit'];

        if($limit<50){
            getAllScoresNew($whatScores,$limit);
        }
        else{
            getAllScoresNew($whatScores,50);
        }


    }
      else if(isset($_GET['scores'])){
        $whatScores = $_GET['scores'];
        getAllScoresNew($whatScores,50);
    }
    else if(isset($_POST['user_id'])){

    require 'db.php';

    $sql = "SELECT * FROM xmasfall WHERE id=?";

    $stmt = mysqli_stmt_init($con);

    if(!mysqli_stmt_prepare($stmt, $sql)){
            $arr = array('error' => 'Cannot make request');
            echo json_encode($arr);
    }
    else{
        $uid = intval($_POST['user_id']);
        mysqli_stmt_bind_param($stmt, "i", $uid );
        mysqli_stmt_execute($stmt);

        $result = mysqli_stmt_get_result($stmt);

        if($row = mysqli_fetch_assoc($result)){

            $arr = array('username' => $row['uname'], 'xp'=>$row['xp'], 'lvl'=>lvlcalc($row['xp']),'hiscore'=>$row['hiscore'] );
            echo json_encode($arr);
        }

        else{

            $arr = array('error' => 'Wrong or expired key');
            echo json_encode($arr);
        }
    }

    mysqli_close($con);

    }
// UPDATE
    else if(isset($_GET['user_id']) && isset($_GET['hiscore'])){
        $id=intval($_GET['user_id']);
        $score=intval($_GET['hiscore']);

        if($id != 0 ){updateHiScore($id,$score);}
        else{
            $arr = array('error' => 'Invalid id');
            echo json_encode($arr);}
        }

// INSERT
    else if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['rpassword'])){

        require "filter.php";

        if(strlen($_POST['username']) > 20){
            $arr = array('error' => 'Username is too long. (max. 20 chars)');
            echo json_encode($arr);
        }

        else if(strlen($_POST['username']) < 1){
            $arr = array('error' => 'Username is not set.');
            echo json_encode($arr);
        }
        else if($_POST['password'] != $_POST['rpassword']){
            $arr = array('error' => 'Passwords not matching.');
            echo json_encode($arr);
        }
        else if(!preg_match('/^[a-zA-Z0-9_]+$/', $_POST['username'] )){

            $arr = array('error' => 'Username can only contain letters, numbers, and underscores.');
            echo json_encode($arr);
        }

        else if(filter($_POST['username'])== 1){
            $arr = array('error' => 'Bad words');
            echo json_encode($arr);
        }
        else if(filter($_POST['username'])== 2){
            $arr = array('error' => 'Tuhmia sanoja');
            echo json_encode($arr);
        }
        else{
        addNewUser($_POST['username']);
        }
    }


// NO ACTION
    else{
        $arr = array('error' => 'No valid action');
        echo json_encode($arr);
    }
}
// APIKEY FAILED
else{
    $arr = array('error' => 'API-key is incorrect or not set');
    echo json_encode($arr);
}
