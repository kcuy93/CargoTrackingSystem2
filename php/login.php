<?php
	$con=mysqli_connect("localhost",
	"root",
	"",
	"cargo_tracking_system");

	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}

	$username = $_POST['username'];
	$password = $_POST['password'];

/*	$username = "john";
	$password = "doe";*/

	$sql = "SELECT * FROM driver_info where driver_username ='$username' and driver_password='$password'";
    $res = mysqli_query($con,$sql);
    //$row = mysqli_fetch_array($res,MYSQLI_ASSOC);

    $result = array();
    while($row = mysqli_fetch_array($res)){
		array_push($result,
		array(
			'id'=>$row[0],
			'fname'=>$row[1],
			'mname'=>$row[2],
			'lname'=>$row[3],
			'licenseNo'=>$row[7],
			'address'=>$row[5]
		));
	}

	echo json_encode(array("result"=>$result));
      
 /*   $count = mysqli_num_rows($res);

	if($count == 1) {
        echo true;
	}else {
		echo false;
	}*/

	mysqli_close($con);
?>