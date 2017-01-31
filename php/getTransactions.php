<?php
	$con=mysqli_connect("localhost",
	"root",
	"",
	"cargo_tracking_system");

	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}

	$driver_id = $_GET['driverId'];
/*	$driver_id = "5";*/

	$sql = "SELECT * FROM transaction 
		where driver_ID ='$driver_id'
		AND upper(transaction_status) = upper('new')";

    $res = mysqli_query($con,$sql);
    //$row = mysqli_fetch_array($res,MYSQLI_ASSOC);

    $row = mysqli_fetch_assoc($res);

	$transactionID = $row['transaction_ID'];
	$contID = $row['container_ID'];

	$sql = "SELECT * FROM package 
					WHERE container_number = '$contID'";

		$res = mysqli_query($con,$sql);

    while($row = mysqli_fetch_array($res)){

    	//get lat lang from delivery info 
    	$sql = "SELECT * FROM delivery_info 
					WHERE delivery_ID = '$row[delivery_ID]'";
		$deliveryInfo = mysqli_fetch_assoc(mysqli_query($con,$sql));

		$deliveryInfo = array(
			'destLong'=>$deliveryInfo['destination_longitude'],
			'destLat'=>$deliveryInfo['destination_latitude']
			);

		$package = array();

		array_push($package,
		array(
			'transactionID' => $transactionID,
			'packageID'=>$row[0],
			'packageStat'=>$row[1],
			'packageSender'=>$row[2],
			'PackageRec'=>$row[3],
			'containerNum'=>$row[4],
			'deliveryID'=>$row[5],
			'deliveryInfo'=> $deliveryInfo
		));

		echo json_encode(array("result"=>$package));  
	}
		
    

	

	mysqli_close($con);
?>