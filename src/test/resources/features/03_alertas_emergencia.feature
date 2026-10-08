# language: es
@EP02
Característica: Alertas de emergencia
  Como conductor
  Quiero enviar una alerta de emergencia
  Para notificar una situación de riesgo a la central

  @US03 @US12
  Escenario: Alerta de emergencia enviada durante el servicio
    Dado un servicio en curso
    Cuando el conductor activa una alerta de emergencia de tipo "PANIC" con ubicación
    Entonces el sistema responde con código 201
    Y la respuesta contiene '"alertType":"PANIC"'
    Y el estado registrado es "ACTIVE"

  @US04 @US12
  Escenario: Se rechaza una alerta con datos incompletos
    Dado un servicio en curso
    Cuando se envía una alerta sin tipo de alerta
    Entonces el sistema responde con código 400

  @US05
  Escenario: Una alerta válida queda almacenada y se puede consultar
    Dado un servicio en curso
    Cuando el conductor activa una alerta de emergencia de tipo "ROBBERY" con ubicación
    Y se consulta la alerta registrada
    Entonces el sistema responde con código 200
    Y la respuesta contiene '"alertType":"ROBBERY"'

  @US05
  Escenario: Una alerta con formato inválido no se almacena
    Dado un servicio en curso
    Y se cuenta el número de alertas almacenadas
    Cuando se envía una alerta sin conductor
    Entonces el sistema responde con código 400
    Y el número de alertas almacenadas no ha cambiado

  @US27
  Escenario: Se guarda la ubicación del evento de emergencia
    Dado un servicio en curso
    Cuando el conductor activa una alerta de emergencia de tipo "ACCIDENT" con ubicación
    Entonces el sistema responde con código 201
    Y la alerta guarda las coordenadas "-12.0464", "-77.0428"

  @US27
  Escenario: Se registra la ausencia de ubicación en la alerta
    Dado un servicio en curso
    Cuando el conductor activa una alerta de emergencia de tipo "EXTORTION" sin ubicación
    Entonces el sistema responde con código 201
    Y la alerta queda sin datos de ubicación

  @US16 @US32
  Escenario: La empresa consulta el historial de alertas de un conductor
    Dado un servicio en curso
    Y el conductor ha registrado una alerta de tipo "PANIC"
    Y el conductor ha registrado una alerta de tipo "ACCIDENT"
    Cuando la central consulta el historial de alertas del conductor
    Entonces el sistema responde con código 200
    Y el historial contiene 2 alertas

  @US16
  Escenario: El historial de un conductor sin alertas está vacío
    Dado un conductor registrado
    Cuando la central consulta el historial de alertas del conductor
    Entonces el sistema responde con código 200
    Y el historial está vacío

  @US32
  Escenario: Error al consultar una alerta con un identificador inválido
    Cuando se consulta una alerta inexistente
    Entonces el sistema responde con código 404

  @US41
  Escenario: La central resuelve una alerta activa
    Dado un servicio en curso
    Y el conductor ha registrado una alerta de tipo "PANIC"
    Cuando la central resuelve la alerta
    Entonces el sistema responde con código 200
    Y el estado registrado es "RESOLVED"

  @US41
  Escenario: Se rechaza resolver una alerta inexistente
    Cuando la central intenta resolver una alerta inexistente
    Entonces el sistema responde con código 404
