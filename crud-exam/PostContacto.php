<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once 'database.php';
include_once 'Contacto.php';

$db = new DataBase();
$instant = $db->getConnection();

$pinst = new Contacto($instant);

$data = json_decode(file_get_contents("php://input"));


if(isset($data))
{

    $pinst->nombre= $data->nombre;
    $pinst->telefono= $data->telefono;
    $pinst->latitud= $data->latitud;
    $pinst->longitud= $data->longitud;
    $pinst->firmaBase64= $data->firmaBase64;

    if($pinst->createContacto())
    {
        http_response_code(200);
        echo json_encode( 
            array( "issuccess" => true,
            "message" => "Creado con exito"));
    }
    else
    {
        http_response_code(503); // Servicio no disponible
        echo json_encode( 
            array("issuccess" => false,
            "message" => "Error al crear"));
    }
}
else
{
    http_response_code(400);
    echo json_encode(array(
        "issuccess" => false,
        "message" => "Datos incompletos o inválidos"));

}



?>