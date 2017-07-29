<?php
    $jsonObj = new stdClass();
    $jsonObj->newversion = 2;
    $jsonObj->title = "New Update";
    $jsonObj->message = "A new update is available for this application.\n * Few bugs fixed.\n * New login page.";
    $jsonObj->positivebutton = "Update";
    $jsonObj->negativebutton = "Cancel";
    $jsonObj->apkurl = "https://www.dropbox.com/sh/2v2ehz7a8jx8xss/AABv0qg1I7eFY_T2KimkfWsQa?dl=0";
    echo json_encode($jsonObj);
?>


