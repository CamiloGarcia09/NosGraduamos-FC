print('Iniciando script de configuración de MongoDB');

const username = _getEnv("MONGO_INITDB_ROOT_USERNAME");
const password = _getEnv("MONGO_INITDB_ROOT_PASSWORD");
const dbName   = _getEnv("MONGO_INITDB_DATABASE");

try {
  db = db.getSiblingDB(dbName);
  db.createUser({
    user: username,
    pwd: password,
    roles: [
      {
        role: "readWrite",
        db: dbName
      }
    ]
  });
  print('Usuario creado exitosamente');
  print('Inicialización completada con éxito');
} catch (error) {
  print('Error durante la inicialización de MongoDB: ' + error);
  throw error;
}