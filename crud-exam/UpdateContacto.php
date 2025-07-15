<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: PUT");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once 'database.php';
include_once 'Contacto.php';

$db = new DataBase();
$instant = $db->getConnection();

$pinst = new Contacto($instant);
$data = json_decode(file_get_contents("php://input"));

if (
    isset($data) &&
    !empty($data->id) &&
    !empty($data->nombre) &&
    !empty($data->telefono)
) {
    $pinst->id = $data->id;
    $pinst->nombre = $data->nombre;
    $pinst->telefono = $data->telefono;
    $pinst->latitud  = isset($data->latitud)  ? (float)$data->latitud  : 0.0;
    $pinst->longitud = isset($data->longitud) ? (float)$data->longitud : 0.0;
    $pinst->firmaBase64 = $data->firmaBase64 ?? '';

    if ($pinst->updateContacto()) {
        http_response_code(200);
        echo json_encode([
            "issuccess" => true,
            "message" => "Registro actualizado correctamente"
        ]);
    } else {
        http_response_code(503);
        echo json_encode([
            "issuccess" => false,
            "message" => "No se pudo actualizar el registro"
        ]);
    }
} else {
    http_response_code(400);
    echo json_encode([
        "issuccess" => false,
        "message" => "Datos incompletos"
    ]);
}
?>