<?php
class Contacto
{

    private $conexion;
    private $table = "Contactos";

    public $id;
    public $nombre;
    public $telefono;
    public float $latitud;
    public float $longitud;
    public $firmaBase64;

    public function __construct($db)
    {
        $this->conexion = $db;
    }

    // Create
    public function createContacto()
    {
        // Validación
        if(empty($this->nombre) || empty($this->firmaBase64)) {
            throw new Exception("Nombre y firma son requeridos");
        }
        
        $consulta = "INSERT INTO " . $this->table . "
                    SET 
                    nombre = :nombre,
                    telefono = :telefono,
                    latitud = :latitud,
                    longitud = :longitud,
                    firmaBase64 = :firmaBase64";

        $comando = $this->conexion->prepare($consulta);

        // Sanitización
        $this->nombre = htmlspecialchars(strip_tags($this->nombre));
        $this->telefono = htmlspecialchars(strip_tags($this->telefono));
        $this->latitud = (float) $this->latitud;
        $this->longitud = (float) $this->longitud;

        $comando->bindParam(":nombre", $this->nombre);
        $comando->bindParam(":telefono", $this->telefono);
        $comando->bindParam(":latitud", $this->latitud);
        $comando->bindParam(":longitud", $this->longitud);
        $comando->bindParam(":firmaBase64", $this->firmaBase64);

        return $comando->execute();
    }

    // Read
    public function getAllContactos()
    {
        $consulta = "SELECT * FROM " . $this->table;
        $comando = $this->conexion->prepare($consulta);
        $comando->execute();
         return $comando;
    }

    public function getContactoById($id)
    {
        $consulta = "SELECT * FROM " . $this->table . " WHERE id = :id LIMIT 1";
        $comando = $this->conexion->prepare($consulta);
        $comando->bindParam(":id", $id);
        $comando->execute();
        return $comando->fetch(PDO::FETCH_ASSOC);
    }

    // Update
    public function updateContacto()
    {
        $consulta = "UPDATE " . $this->table . " SET 
                        nombre = :nombre,  // Corregido
                        telefono = :telefono,
                        latitud = :latitud,
                        longitud = :longitud,
                        firmaBase64 = :firmaBase64
                    WHERE id = :id";

        $comando = $this->conexion->prepare($consulta);

        $this->nombre = htmlspecialchars(strip_tags($this->nombre));
        $this->telefono = htmlspecialchars(strip_tags($this->telefono));
        $this->latitud = (float) $this->latitud;
        $this->longitud = (float) $this->longitud;
        $this->id = htmlspecialchars(strip_tags($this->id));

        $comando->bindParam(':nombre', $this->nombre);
        $comando->bindParam(':telefono', $this->telefono);
        $comando->bindParam(':latitud', $this->latitud);
        $comando->bindParam(':longitud', $this->longitud);
        $comando->bindParam(':firmaBase64', $this->firmaBase64);
        $comando->bindParam(':id', $this->id);

        return $comando->execute();
    }

    // Delete
    public function deleteContacto()
    {
        $consulta = "DELETE FROM " . $this->table . " WHERE id = :id";
        $comando = $this->conexion->prepare($consulta);
        $this->id = htmlspecialchars(strip_tags($this->id));
        $comando->bindParam(':id', $this->id);
        return $comando->execute();
    }

}


?>