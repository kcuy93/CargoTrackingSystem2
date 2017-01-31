<?php

	define("OPERATION_START", "START");
	define("OPERATION_STOP", "STOP");
	define("OPERATION_LOG", "LOG");

	$delivered = "DELIVERED";
	$progress = "IN PROGRESS";
	$transit = "IN TRANSIT";
	$completed = "COMPLETED";

	$con=mysqli_connect("localhost",
	"root",
	"",
	"cargo_tracking_system");

	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}

	$transactionID = "39";
	$packageID = "23";
	$operationType =  "STOP"; //if start or stop delivery
	$latitude = "1555";
	$longitude = "1555";

/*	$transactionID = $_GET['transactionID'];
	$packageID = $_GET['packageID'];
	$operationType =  $_GET['operationType']; //if start or stop delivery
	$latitude = $_GET['latitude'];
	$longitude = $_GET['longitude'];*/


	//get transaction 
	$sql = "SELECT * FROM transaction WHERE transaction_ID = '$transactionID'";
	$transaction = mysqli_fetch_assoc(mysqli_query($con,$sql)) ;

	//get package
	$sql = "SELECT * FROM package WHERE package_id = '$packageID'";
	$package = mysqli_fetch_assoc(mysqli_query($con,$sql));
	$deliveryID = $package['delivery_ID'];

	date_default_timezone_set('Asia/Manila');
	$now  = new DateTime();
	$date = $now->format('Y-m-d H:i:s');

    if($operationType == OPERATION_START){

    	//set start travel time and delivery status
    	$sql = "UPDATE delivery_info 
     		SET start_travel = '$date', delivery_status = '$transit'
			WHERE delivery_ID ='$deliveryID'";	
		mysqli_query($con, $sql);

/*		//change status of transaction
    	$sql = "UPDATE transaction 
				SET transaction_status = '$progress'
				WHERE transaction_ID ='$transactionID'";
    	mysqli_query($con,$sql);*/

    }else if($operationType == OPERATION_STOP){
    	//set stop travel time and delivery status
    	$sql = "UPDATE delivery_info 
     		SET stop_travel = '$date', delivery_status = '$delivered'
			WHERE delivery_ID ='$deliveryID'";
		mysqli_query($con, $sql);	

		//set package status to delivered
		$sql = "UPDATE package 
     		SET package_status = '$delivered'
			WHERE package_id ='$packageID'";
		mysqli_query($con, $sql);	

		//update truck info
		$sql = "UPDATE truck_info 
				SET truck_status = 'AVAILABLE'
				WHERE truck_ID = '$transaction[truck_ID]'";
		mysqli_query($con, $sql);

		//update driver availablility
		$sql = "UPDATE driver_info
				SET driver_availability = 'AVAILABLE'
				WHERE driver_ID = '$transaction[driver_ID]'";
		mysqli_query($con, $sql);

		//query packages that have not been delivered
		$sql = "SELECT * FROM package 
						WHERE container_number = '$transaction[container_ID]'
						AND package_status != '$delivered'";

		//if query is zero, so all packages have been delivered, change status of transaction to completed
		$status = $progress;
		if(mysqli_num_rows(mysqli_query($con,$sql)) == 0){
			//change status of transaction
	    	$status = $completed;
		} 

		$sql = "UPDATE transaction 
					SET transaction_status = '$status'
					WHERE transaction_ID ='$transactionID'";
	    mysqli_query($con,$sql);
    }

	//set lat lang
	$sql = "SELECT * FROM delivery_info WHERE delivery_ID ='$deliveryID'";
	$res = mysqli_query($con,$sql);
    $row = mysqli_fetch_assoc($res);
    $deliveryCode = $row['delivery_code'];

   	//set lat lang
    $sql = "UPDATE delivery_log 
     		SET current_latitude = '$latitude', current_longitude = '$longitude', time = '$date'
			WHERE delivery_code = '$deliveryCode'";

	mysqli_query($con, $sql);
	 

	mysqli_close($con);
?>