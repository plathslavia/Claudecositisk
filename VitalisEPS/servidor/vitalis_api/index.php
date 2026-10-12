<?php
// Prueba rápida: abre http://localhost/vitalis_api/ en el navegador.
// Si responde "ok": true, la API y la base de datos están funcionando.
require __DIR__ . '/conexion.php';

try {
    $total = (int) conectar()->query('SELECT COUNT(*) FROM citas')->fetchColumn();
    responder(200, ['ok' => true, 'servicio' => 'API Vitalis EPS', 'citas_en_base' => $total]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'No hay conexión con MySQL: ' . $e->getMessage()]);
}
