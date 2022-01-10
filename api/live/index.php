<?php
require '../header.php';

?>

<!DOCTYPE html>
<html lang="en">
<head>

<title>XmasFall Live</title>
<link rel="stylesheet" href="livestyle.css">

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>

<body>
    <main>
<div class="headerinfo">
        <h1>XmasFall Live</h1>
       <p style="text-align:center;"><i>A Game by Auger Games</i></p>
        <div> <label for="lobbyname">Lobby:</label>
            <input type="text" name="lobbyname" class="form-control" onchange="loader();" value="<?php if(isset($_GET['lobby'])){echo $_GET['lobby'];}?>" id="lobbyname"></div>
            <div> <input type="Checkbox" id="autoPlay"></Input>
            <label for="autoPlay" class="autoLabel" onclick="goLive()">◉ Live</label></div>



</div>
        <div id="data_table">&emsp;Press Live to continue</div>
    </main>
<script>

</script>
    <script>
        function loader(){
            document.getElementById("data_table").innerHTML = `<div class="loader"></div> `;
        }
        function goLive(){
        var checkBox = document.getElementById("autoPlay");
        var int;
        var lobbyname = document.getElementById("lobbyname").value;



        checkBox.addEventListener('change', function() { // bind change event handler

        if(!lobbyname.match(/^[a-zA-Z0-9_]+$/)){
         document.getElementById("data_table").innerHTML = 'Lobby name can only contain letters, numbers, and underscores.';
        }
        else{

        if (int) clearInterval(int); // clear any previous interval
            if (this.checked) { // if checked then start the interval
                document.getElementById("data_table").innerHTML = `<div class="loader"></div> `;
                int = setInterval(function () {
                lobbyname = document.getElementById("lobbyname").value;
                $.get(`getjsondata.php?lobby=${lobbyname}`, function(result) {
                document.getElementById("data_table").innerHTML = result;
            });
            }, 2000);
            }
}
    });}

  </script>
  <footer>
    <a class="button-all button-sea" href="../stats">Stats</a>

    <?php
    // Check if the user is logged in
    if(isset($_SESSION["loggedin"])){
        if($_SESSION["loggedin"] == true){
        echo ' <a class="button-all button-sea"href="../myaccount.php">My Account</a>';


        }
    }
    if(!isset($_SESSION["loggedin"])){
        echo '<a class="button-all button-sea" href="../">Sign up</a>';
    }

    if(file_exists('../game')){
            $files = scandir('../game', SCANDIR_SORT_DESCENDING);
            $newest_file = $files[0];
            echo ' <a class="button-all button-sea" href="https://augergames.com/xmasfall/game/'.$newest_file.'" >Play</a>';
            }
    ?>
</footer>
</body>
</html>

<?php
