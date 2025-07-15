<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once 'database.php';
include_once 'Contacto.php';

$db = new DataBase();
$instant = $db->getConnection();

$pinst = new Contacto($instant);
$cmd = $pinst->getAllContactos();
$count = $cmd->rowCount();

if($count > 0)
{
    $contactoarray = array();

    while($row = $cmd->fetch(PDO::FETCH_ASSOC))
    {
        extract($row);
        $e = array(
            "id" => $id,
            "nombre" => $nombre,
            "telefono" => $telefono,
            "latitud" => $latitud,
            "longitud" => $longitud,
            "firmaBase64" => $firmaBase64
        );


        array_push($contactoarray, $e);
    }


    http_response_code(200);
    echo json_encode($contactoarray);
}
else
{
    http_response_code(404);
    echo json_encode( 
        array( "issuccess" => false,
               "message" => "No hay Datos")
    );
}

?>