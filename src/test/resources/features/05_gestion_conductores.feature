# language: es
@EP05
Característica: Gestión de datos de conductores mediante la API
  Como developer
  Quiero actualizar la información de los conductores mediante la API
  Para poder mantenerla al día

  @US31
  Escenario: Actualización exitosa de los datos de un conductor
    Dado un conductor registrado
    Cuando el administrador actualiza los datos del conductor con el nombre "Nombre Actualizado"
    Entonces el sistema responde con código 200
    Y los datos del conductor reflejan el nombre "Nombre Actualizado"

  @US31
  Escenario: Error al actualizar un conductor con datos inválidos
    Dado un conductor registrado
    Cuando el administrador actualiza al conductor con un DNI de 7 dígitos
    Entonces el sistema responde con código 400
