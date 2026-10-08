# language: es
@EP03
Característica: Monitoreo de unidades y conteo de pasajeros
  Como empresa
  Quiero conocer el estado, la ubicación y la ocupación de mis unidades
  Para tener control de la operación y estimar el riesgo en una emergencia

  @US06 @US13
  Escenario: El conteo de pasajeros se actualiza con cada lectura
    Dado un servicio en curso
    Cuando el sistema registra una lectura de pasajeros con 10 subidas, 0 bajadas y 10 a bordo
    Y el sistema registra una lectura de pasajeros con 8 subidas, 3 bajadas y 15 a bordo
    Entonces el servicio tiene 2 lecturas de pasajeros
    Y la ocupación actual de la unidad es 15 pasajeros

  @US13
  Escenario: Una lectura fallida mantiene el último conteo registrado
    Dado un servicio en curso
    Y el sistema registra una lectura de pasajeros con 10 subidas, 0 bajadas y 10 a bordo
    Cuando el sistema registra una lectura de pasajeros para un servicio inexistente
    Entonces el sistema responde con código 409
    Y el servicio tiene 1 lectura de pasajeros
    Y la ocupación actual de la unidad es 10 pasajeros

  @US13
  Escenario: Error al registrar una lectura sin datos de conteo
    Dado un servicio en curso
    Cuando el sistema registra una lectura sin datos de conteo
    Entonces el sistema responde con código 400

  @US07 @US34
  Escenario: Consulta de la ocupación de una unidad con servicio en curso
    Dado un servicio en curso
    Y el sistema registra una lectura de pasajeros con 12 subidas, 2 bajadas y 10 a bordo
    Cuando se consulta la información de la unidad
    Entonces el sistema responde con código 200
    Y la ocupación actual es 10 pasajeros

  @US07
  Escenario: No hay información de ocupación cuando no hay lecturas
    Dado una unidad de bus registrada
    Cuando se consulta la información de la unidad
    Entonces el sistema responde con código 200
    Y no hay información de ocupación disponible

  @US34
  Escenario: Error al consultar la ocupación con un identificador inválido
    Cuando se consulta una unidad inexistente
    Entonces el sistema responde con código 404

  @US21
  Escenario: La empresa visualiza el estado de sus unidades
    Dado una unidad de bus registrada
    Cuando la empresa consulta el estado de las unidades
    Entonces el sistema responde con código 200
    Y la lista incluye la unidad registrada con estado "ACTIVE"

  @US28
  Escenario: Seguimiento de la ubicación actual de una unidad
    Dado una unidad de bus registrada
    Cuando la unidad reporta su ubicación "-12.05", "-77.04"
    Y se consulta la información de la unidad
    Entonces el sistema responde con código 200
    Y el sistema retorna las coordenadas "-12.05", "-77.04"

  @US28
  Escenario: La ubicación no está disponible cuando no hay datos de GPS
    Dado una unidad de bus registrada sin datos de GPS
    Cuando se consulta la información de la unidad
    Entonces el sistema indica que la ubicación no está disponible

  @US15 @EP01
  Escenario: Asociación de un conductor a una unidad
    Dado un conductor registrado
    Y una unidad de bus asignada al conductor
    Cuando se consulta la información de la unidad
    Entonces el sistema responde con código 200
    Y la unidad muestra al conductor asignado

  @US42
  Escenario: La empresa registra una unidad nueva
    Cuando la empresa registra una unidad nueva
    Entonces el sistema responde con código 201
    Y el estado registrado es "ACTIVE"

  @US42
  Escenario: Se rechaza registrar una unidad con una placa repetida
    Dado una unidad de bus registrada
    Cuando la empresa registra otra unidad con la misma placa
    Entonces el sistema responde con código 409
