//package com.example.a730n.fairguidingsystem;
//
///**
// * Created by 730N on 14.03.2018.
// */
//
////document.addEventListener("DOMContentLoaded", function(event) {
//
//        //Page elements and other variables
//        var controlDevice = document.getElementById('controlDevice');
//        var triangulatedDevice = document.getElementById('triangulatedDevice');
//        var toleranceInput = document.getElementById('tolerance');
//        var mathTrilatCheck = document.getElementById('mathTrilatCheck');
//        var bruteTrilatCheck = document.getElementById('bruteTrilatCheck');
//        var beacons = document.getElementsByClassName('beacon');
//        var map = document.getElementById('mapContainer');
//        var mapWidth = map.clientWidth;
//        var mapHeight = map.clientHeight;
//        var mapOffsets = [Math.floor(map.getBoundingClientRect().left), Math.floor(map.getBoundingClientRect().top)]; //This is necessary as beacon/device positions are absolute (relative to the page) rather than relative to the map rectangle
//        var beaconDataArray = []; //X coord, Y coord, Distance from controlDevice, HTML ID
//        var controlDeviceCoords = []; //X coord, Y coord
//        var tolerance = 0.5; //How close the estimated distance between beacon and device must be to the real distance to be acceptable
//        var onDragUpdate = false;
//        var useMathBasedAlgorithm = false;
//
//        randomiseLocations();
//
//        toleranceInput.addEventListener("change", function(event){
//        tolerance = parseFloat(toleranceInput.value);
//        triangulatePosition();
//        }, false);
//
//        bruteTrilatCheck.addEventListener("change", function(event){
//        useMathBasedAlgorithm = mathTrilatCheck.checked;
//        toleranceInput.disabled = false;
//        triangulatePosition();
//        }, false);
//
//        mathTrilatCheck.addEventListener("change", function(event){
//        useMathBasedAlgorithm = mathTrilatCheck.checked;
//        toleranceInput.disabled = true;
//        triangulatePosition();
//        }, false);
//
//        function randomiseLocations(){
//        for(b = 0; b<beacons.length; b++){
//        beacons[b].style.left = Math.floor(((0.025 + Math.random()/1.1) * mapWidth) + mapOffsets[0]) + 'px'; //Little bit of extra maths to stop the beacons being really close to the edges
//        beacons[b].style.top = Math.floor(((0.025 + Math.random()/1.1) * mapHeight) + mapOffsets[1]) + 'px';
//        }
//        controlDevice.style.left = Math.floor(((0.25 + Math.random()/2) * mapWidth) + mapOffsets[0]) + 'px';
//        controlDevice.style.top = Math.floor(((0.25 + Math.random()/2) * mapHeight) + mapOffsets[1]) + 'px';
//        triangulatePosition();
//        }
//
//        function triangulatePosition(){
//        //console.log('Triangulating position...');
//        getBeacons();
//        //Sort the beacons by proximity to the controlDevice (mimics signal strength)
//        beaconDataArray.sort(function(a,b){
//        return a[2] - b[2];
//        });
//        highlightClosestBeacons();
//
//        if (useMathBasedAlgorithm == false){
//        bruteForceTrilat();
//        }else{
//        mathBasedTrilat();
//        }
//        }
//
//        function bruteForceTrilat(){
//        var coordFound = false;
//
//        //Goes through all coordinates left to right, top to bottom (hence bottom right locations have worse performance than top left)
//        for (y=mapOffsets[1]; (y<(mapHeight + mapOffsets[1])) && (coordFound==false); y++){
//        for (x=mapOffsets[0]; (x<(mapWidth + mapOffsets[0])) && (coordFound==false); x++){
//        //Determine the hypot between this point and each of the 3 beacons and determine if it is close enough.
//        //Beacon 1
//        var distance = Math.hypot((x - beaconDataArray[0][0]), (y - beaconDataArray[0][1]));
//        if ((distance > (beaconDataArray[0][2] - tolerance)) && (distance < (beaconDataArray[0][2] + tolerance))){
//        //Beacon 2
//        distance = Math.hypot((x - beaconDataArray[1][0]), (y - beaconDataArray[1][1]));
//        if ((distance > (beaconDataArray[1][2] - tolerance)) && (distance < (beaconDataArray[1][2] + tolerance))){
//        //Beacon 3
//        distance = Math.hypot((x - beaconDataArray[2][0]), (y - beaconDataArray[2][1]));
//        if ((distance > (beaconDataArray[2][2] - tolerance)) && (distance < (beaconDataArray[2][2] + tolerance))){
//        //If you got this far you're close enough
//        triangulatedDevice.style.left = x + 'px';
//        triangulatedDevice.style.top = y + 'px';
//        coordFound = true;
//        }
//        }
//        }
//        }
//        }
//        }
//
//        function mathBasedTrilat(){
//        //This is kind of messy but it makes the maths below easier to read
//        var dA = beaconDataArray[0][2];
//        var dB = beaconDataArray[1][2];
//        var dC = beaconDataArray[2][2];
//        var ax = beaconDataArray[0][0];
//        var ay = beaconDataArray[0][1];
//        var bx = beaconDataArray[1][0];
//        var by = beaconDataArray[1][1];
//        var cx = beaconDataArray[2][0];
//        var cy = beaconDataArray[2][1];
//
//        //Maths taken from https://stackoverflow.com/a/20976803
//        var W = dA*dA - dB*dB - ax*ax - ay*ay + bx*bx + by*by;
//        var Z = dB*dB - dC*dC - bx*bx - by*by + cx*cx + cy*cy;
//        var x = (W*(cy-by) - Z*(by-ay)) / (2 * ((bx-ax)*(cy-by) - (cx-bx)*(by-ay)));
//        var y;
//
//        //If two of the three beacons share an identical coordinate then the certain operations can break (divide by 0), this code helps avoid that.
//        if (by-ay != 0){
//        y = (W - 2*x*(bx-ax)) / (2*(by-ay));
//        if (cy-by != 0){
//        //This helps to make the result more accurate and reduce the margin of error.
//        var y2 = (Z - 2*x*(cx-bx)) / (2*(cy-by));
//        y = (y + y2) / 2;
//        }
//        }else{
//        y = (Z - 2*x*(cx-bx)) / (2*(cy-by));
//        }
//
//        triangulatedDevice.style.left = x + 'px';
//        triangulatedDevice.style.top = y + 'px';
//        }
//
//        function getBeacons(){
//        beacons = document.getElementsByClassName('beacon');
//        for (i=0; i<beacons.length; i++){
//        beaconDataArray[i] = []; //JavaScript is weird so you have to initialize each line in a 2D array
//        beaconDataArray[i][0] = parseStyleValue(beacons[i].style.left); //X Coordinate
//        beaconDataArray[i][1] = parseStyleValue(beacons[i].style.top); //Y Coordinate
//        beaconDataArray[i][2] = calcDistToControlDevice(beaconDataArray[i][0], beaconDataArray[i][1]); //Direct distance between device and beacon (mimics signal strength)
//        beaconDataArray[i][3] = beacons[i].getAttribute('id'); //This is enables changing the color of nearest beacons and stuff
//        }
//        }
//
//        function parseStyleValue(styleString){
//        //Style values are returned as a string (e.g '150px'). This function converts it to a number (e.g 150).
//        var splitString = styleString.split('px');
//        if ((splitString[0] === "") || (splitString[0] === null)){
//        return 0;
//        }else{
//        return parseInt(splitString[0]);
//        }
//        }
//
//        function calcDistToControlDevice(xCoord, yCoord){
//        getControlDeviceCoords();
//        var distance = Math.hypot((xCoord - controlDeviceCoords[0]), (yCoord - controlDeviceCoords[1])); //Simple trigonometry
//        return distance;
//        }
//
//        function getControlDeviceCoords(){
//        controlDeviceCoords[0] = parseStyleValue(controlDevice.style.left); //X Coordinate
//        controlDeviceCoords[1] = parseStyleValue(controlDevice.style.top); //Y Coordinate
//        }
//
//        function highlightClosestBeacons(){
//        for (i=1; i<beacons.length; i++){
//        document.getElementById(beaconDataArray[i][3]).style.boxShadow = 'none';
//        }
//        for (i=0; i<3; i++){
//        document.getElementById(beaconDataArray[i][3]).style.backgroundColor = '#0099FF';
//        }
//        for (i=3; i<beacons.length; i++){
//        document.getElementById(beaconDataArray[i][3]).style.backgroundColor = '#0000FF';
//        }
//        document.getElementById(beaconDataArray[0][3]).style.boxShadow = '0px 0px 10px 5px #00CCFF';
//        }
//
//        $('#updateModeCheck').change(function(){
//        if (onDragUpdate == false){
//        onDragUpdate = true;
//        }else{
//        onDragUpdate = false;
//        }
//        });
//
//        $(".beacon").on("drag", function(event, ui){
//        if (onDragUpdate == true) triangulatePosition();
//        });
//
//        $("#controlDevice").on("drag", function(event, ui){
//        if (onDragUpdate == true) triangulatePosition();
//        });
//
//        $(".beacon").on("dragstop", function(event, ui){
//        triangulatePosition();
//        });
//
//        $("#controlDevice").on("dragstop", function(event, ui){
//        triangulatePosition();
//        });
//        });