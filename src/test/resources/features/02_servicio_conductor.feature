# language: es
@EP01
Característica: Inicio y cierre del servicio
  Como conductor
  Quiero registrar el inicio y el fin del servicio
  Para dejar evidencia del recorrido y cerrar el registro del viaje

  @US02 @US11
  Escenario: Registro exitoso del inicio de servicio
    Dado un conductor registrado
    Y una unidad de bus registrada
    Cuando el conductor inicia el servicio en esa unidad
    Entonces el sistema responde con código 201
    Y el estado registrado es "ACTIVE"
    Y queda registrada la hora de inicio

  @US02
  Escenario: Se impide iniciar el servicio a un empleado no registrado
    Dado una unidad de bus registrada
    Cuando un empleado inexistente intenta iniciar el servicio
    Entonces el sistema responde con código 409
    Y la respuesta contiene "Empleado no encontrado"

  @US11
  Escenario: Error al iniciar un servicio con una solicitud inválida
    Dado un conductor registrado
    Cuando se envía una solicitud de inicio de servicio sin unidad
    Entonces el sistema responde con código 400

  @US20 @US24
  Escenario: Cierre exitoso del servicio en curso
    Dado un servicio en curso
    Cuando el conductor finaliza el servicio
    Entonces el sistema responde con código 200
    Y el estado registrado es "FINISHED"
    Y queda registrada la hora de cierre

  @US20 @US24
  Escenario: Se rechaza finalizar un servicio que ya fue finalizado
    Dado un servicio en curso
    Y el servicio ya fue finalizado
    Cuando el conductor finaliza el servicio
    Entonces el sistema responde con código 409
    Y la respuesta contiene "ya fue finalizado"

  @US24
  Escenario: Error al finalizar un servicio inexistente
    Cuando el conductor intenta finalizar un servicio inexistente
    Entonces el sistema responde con código 409

  @US33
  Escenario: Consulta del estado de un servicio existente
    Dado un servicio en curso
    Cuando se consulta el estado del servicio
    Entonces el sistema responde con código 200
    Y el estado registrado es "ACTIVE"

  @US33
  Escenario: Error al consultar el estado con un identificador inválido
    Cuando se consulta el estado de un servicio inexistente
    Entonces el sistema responde con código 404

  @US40
  Escenario: No se permite iniciar un servicio en una unidad que ya tiene uno activo
    Dado un servicio en curso
    Cuando otro conductor intenta iniciar servicio en la misma unidad
    Entonces el sistema responde con código 409
    Y la respuesta contiene "ya tiene un turno activo"

  @US40
  Escenario: No se permite que un conductor tenga dos servicios activos
    Dado un servicio en curso
    Cuando el mismo conductor intenta iniciar servicio en otra unidad
    Entonces el sistema responde con código 409
    Y la respuesta contiene "ya tiene un turno activo"
