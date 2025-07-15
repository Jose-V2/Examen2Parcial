<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once 'database.php';
include_once 'Contacto.php';

$db = new DataBase();
$instant = $db->getConnection();

$pinst = new Contacto($instant);

// ✅ Leer id desde la URL
if (isset($_GET['id']) && !empty($_GET['id'])) {
    $pinst->id = $_GET['id'];

    if ($pinst->deleteContacto()) {
        http_response_code(200);
        echo json_encode([
            "issuccess" => true,
            "message" => "Registro eliminado correctamente"
        ]);
    } else {
        http_response_code(503);
        echo json_encode([
            "issuccess" => false,
            "message" => "No se pudo eliminar el registro"
        ]);
    }
} else {
    http_response_code(400);
    echo json_encode([
        "issuccess" => false,
        "message" => "ID no proporcionado"
    ]);
}
?>