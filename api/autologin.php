<?php
session_start();
if (isset($_SESSION)) {
    session_unset();
    session_destroy();
}
$keyh ="";
$currentDate = date("U");

include('lvlcalc.php');

if (isset($_GET['key']) && isset($_GET['uid'])){

    $keyh = $_GET['key'];
    $id = intval($_GET['uid']);

    if($keyh==""){




        $files = scandir('android', SCANDIR_SORT_DESCENDING);
        $newest_file = $files[0];
        header("location: android/".$newest_file);
    }
    else{

    require 'db.php';

    $sql = "SELECT * FROM xmasfall WHERE id = ? AND keyhash=? AND valid >= ?;";
    $stmt = mysqli_stmt_init($con);

    if(!mysqli_stmt_prepare($stmt, $sql)){
            $arr = array('error' => 'Cannot make request');
            echo json_encode($arr);
    }
    else{
        mysqli_stmt_bind_param($stmt, "iss", $id, $keyh, $currentDate );
        mysqli_stmt_execute($stmt);

        $result = mysqli_stmt_get_result($stmt);

        if($row = mysqli_fetch_assoc($result)){
                session_start();
                $_SESSION["loggedin"] = true;
                $_SESSION['id'] = $row['id'];
                $_SESSION['username'] = $row['uname'];
                $_SESSION['xp'] = $row['xp'];
                $truelvl = lvlcalc($row['xp']);
                $_SESSION['lvl'] = $truelvl;

            if(file_exists('android') && $_SESSION['id'] != '') {

                $files = scandir('android', SCANDIR_SORT_DESCENDING);
                $newest_file = $files[0];
                header("location: android/".$newest_file);
            }
            else{
                echo 'Failed to load game <a href="index.php">Back to Login</a>';
            }
        }

        else{

           header("location: http://augergames.com/xmasfall/exit_browser.php");


        }
    }

    mysqli_close($con);
}    }
else{
    $arr = array('error' => 'No key or user ID');
    echo json_encode($arr);
}