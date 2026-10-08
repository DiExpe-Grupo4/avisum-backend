# language: es
@EP01
Característica: Identidad y autorización del conductor
  Como conductor
  Quiero validar mi identidad antes de iniciar el servicio
  Para asegurar la trazabilidad del viaje

  @US01 @US10 @US18
  Escenario: Validación de identidad exitosa con código y contraseña correctos
    Dado un conductor registrado con contraseña "clave123"
    Cuando el conductor inicia sesión con su código y la contraseña "clave123"
    Entonces el sistema responde con código 200
    Y la respuesta contiene '"active":true'

  @US01 @US10 @US18
  Escenario: Rechazo de la validación con contraseña inválida
    Dado un conductor registrado con contraseña "clave123"
    Cuando el conductor inicia sesión con su código y la contraseña "clave-equivocada"
    Entonces el sistema responde con código 401

  @US10 @US18
  Escenario: Rechazo de la validación con un código de conductor inexistente
    Cuando el conductor inicia sesión con el código "NO-EXISTE" y la contraseña "cualquiera"
    Entonces el sistema responde con código 401

  @US14
  Escenario: Un conductor desactivado no está autorizado para operar
    Dado un conductor registrado con contraseña "clave123"
    Y el administrador desactiva al conductor
    Cuando el conductor inicia sesión con su código y la contraseña "clave123"
    Entonces el sistema responde con código 401

  @US14
  Escenario: Un conductor reactivado vuelve a estar autorizado
    Dado un conductor registrado con contraseña "clave123"
    Y el administrador desactiva al conductor
    Y el administrador reactiva al conductor
    Cuando el conductor inicia sesión con su código y la contraseña "clave123"
    Entonces el sistema responde con código 200

  @US38
  Escenario: La cuenta se bloquea tras 5 intentos fallidos
    Dado un conductor registrado con contraseña "clave123"
    Y el conductor falló 5 veces el inicio de sesión
    Cuando el conductor inicia sesión con su código y la contraseña "clave123"
    Entonces el sistema responde con código 401

  @US38
  Escenario: La cuenta no se bloquea antes del límite de intentos fallidos
    Dado un conductor registrado con contraseña "clave123"
    Y el conductor falló 4 veces el inicio de sesión
    Cuando el conductor inicia sesión con su código y la contraseña "clave123"
    Entonces el sistema responde con código 200

  @US39
  Escenario: Verificación facial registrada con la captura de la cámara
    Dado un conductor registrado
    Cuando la cámara envía una captura para verificar al conductor
    Entonces el sistema responde con código 201
    Y el resultado de la verificación es MATCH o NO_MATCH con un puntaje entre 0 y 1

  @US39
  Escenario: Verificación facial rechazada cuando no hay captura
    Dado un conductor registrado
    Cuando la cámara envía una verificación sin captura
    Entonces el sistema responde con código 400

  @US39
  Escenario: Verificación facial rechazada para un conductor inexistente
    Cuando la cámara envía una captura para un conductor inexistente
    Entonces el sistema responde con código 409
