# Sistema informático para el registro de citas médicas
**Clínica San Pedro - Sede Los Olivos** · Técnicas de Programación Orientada a Objetos

Aplicación de escritorio en Java (Swing) con MySQL. Es un **proyecto de NetBeans listo para abrir**: los formularios
tienen su archivo `.form`, así que se editan con la paleta y el diseñador visual (pestaña **Design**), con el mismo
estilo del curso (Absolute Layout).

## 1. Cómo ponerlo a funcionar

1. **Base de datos.** En MySQL Workbench abre `sql/clinica_san_pedro.sql` (*File → Open SQL Script*) y ejecútalo completo
   con el rayo. Crea la base `clinica_san_pedro` con datos de ejemplo. (Es una base nueva: no toca la anterior `citas_medicas`.)
2. **Abrir el proyecto.** En NetBeans: *File → Open Project…* y elige la carpeta `ClinicaSanPedro` (la que contiene `nbproject`).
   La librería **Absolute Layout** ya está configurada, igual que en los proyectos de clase.
3. **Conector MySQL.** Clic derecho en **Libraries → Add JAR/Folder…** y elige `mysql-connector-j-x.x.x.jar`.
4. **Tu contraseña.** Abre `Datos/Conexion.java` y cambia `TU_CONTRASEÑA` por la clave de `root` de tu MySQL.
5. **Ejecutar.** Presiona `F6` (la clase principal es `Principal.Main`).
6. **Editar un formulario.** Doble clic en `Vista/FrmCita.java`, por ejemplo, y usa la pestaña **Design**.

## 2. Usuarios de prueba

| Usuario | Contraseña | Rol | Qué ve |
|---|---|---|---|
| `admin` | `admin123` | ADMINISTRADOR | Todo el sistema |
| `asistente` | `asistente123` | ASISTENTE | Solo Citas y Pacientes |

Las contraseñas se guardan cifradas (SHA-256), no en texto plano.

## 3. Estructura

```
src/
├─ Modelo/     Persona (abstracta), Paciente, Medico, Usuario (abstracta), Administrador,
│              Asistente, Especialidad, Cita, Periodo, Resumen, Seguridad,
│              interfaces Reportable y Cobrable
├─ Datos/      Conexion, interfaz Crud<T>, PacienteDAO, MedicoDAO, UsuarioDAO,
│              EspecialidadDAO, CitaDAO, CruceHorarioException
├─ Vista/      FrmLogin, FrmMenu, FrmPaciente, FrmMedico, FrmUsuarios, FrmCita,
│              FrmReportes, FrmHistorial  (cada uno con su .java y su .form)
│              Util (métodos de apoyo compartidos)
└─ Principal/  Main
sql/           clinica_san_pedro.sql
docs/          diagrama_clases_modelo.png (+ modelo.dot, el código fuente del diagrama)
```

## 4. Indicaciones de la profesora → dónde se cumplen

| Indicación | Dónde está |
|---|---|
| Cita: jalar al paciente por DNI, elegir médico, fecha y una tabla que se va mostrando | `FrmCita` |
| Reporte de citas atendidas y canceladas por día, mes o año | `FrmReportes` (periodo diario, semanal, mensual, anual) |
| Ganancia del día, la semana y el mes en tiempo real | Tarjetas de `FrmReportes`, se actualizan solas cada 30 s |
| Administrador: todo el sistema, registra médicos, clientes y usuarios | `FrmMedico`, `FrmPaciente`, `FrmUsuarios` |
| Usuario (asistente/enfermera): solo registra citas y pacientes | `Asistente.puedeAcceder()` y el menú de `FrmMenu` |
| Asignar usuario y contraseña, y que el login muestre solo los módulos permitidos | `FrmUsuarios`, `FrmLogin`, `FrmMenu` |
| Más de 5 formularios | 8 formularios (7 de uso + historial) |
| Base de datos MySQL con Workbench | `sql/clinica_san_pedro.sql` y el paquete `Datos` |
| Informe: filtrado de pacientes e historial | Buscador y botón "Ver historial" en `FrmPaciente` |
| Informe: cruces de horarios | `FrmCita` solo ofrece horas libres y `CitaDAO` lanza `CruceHorarioException` |
| Informe: seguimiento de citas | Estados Pendiente, Atendida y Cancelada |
| Informe: control de acceso y Ley 29733 | Roles y contraseñas cifradas |

## 5. Conceptos del curso → dónde se aplican

| Semana | Concepto | Dónde |
|---|---|---|
| 1 | Clases, objetos, constructores | Todo el paquete `Modelo` |
| 2 | Encapsulamiento y `protected` | Atributos `private`/`protected`; validación dentro de los setters (`Persona.setDni`, `Medico.setTarifa`…) |
| 3 | Sobrecarga | Constructores de `Cita`; `PacienteDAO.listar()` / `listar(filtro)`; `CitaDAO.listar()` / `listar(desde, hasta)` |
| 3 | `static` y `final` | `Cita.PENDIENTE`, `Persona.DNI_LONGITUD`, `Seguridad.hash()`, `Util`, clase `Conexion` final |
| 3 | Excepciones | `try/catch` en todos los formularios y `CruceHorarioException` (propia) |
| 4 | `ArrayList` | Los DAO devuelven `ArrayList<...>` y los formularios los recorren |
| 5 | Diagrama de clases | `docs/diagrama_clases_modelo.png` |
| 7 | Herencia y polimorfismo | `Paciente`, `Medico` y `Usuario` heredan de `Persona`; `Administrador` y `Asistente` heredan de `Usuario` |
| 8 | Clases abstractas y métodos `final` | `Persona` y `Usuario` (abstractas); setters `final` |
| 9 | Interfaces y herencia múltiple | `Cita implements Reportable, Cobrable`; `Crud<T>` en los DAO |
| 9 | Asociación | `Cita` tiene un `Paciente`, un `Medico` y un `Usuario`; `Medico` tiene una `Especialidad` |

**Polimorfismo en acción:** `FrmMenu` pregunta `usuario.puedeAcceder(modulo)` sin importar si es Administrador o Asistente,
y `Util.llenarTabla()` llena una tabla con cualquier lista de objetos `Reportable`.

## 6. Problemas comunes

- **"No se pudo conectar con la base de datos":** MySQL apagado, clave incorrecta en `Conexion.java` o falta el `.jar` en *Libraries*.
  Si persiste, prueba quitando todo lo que va después del `?` en la URL de `Conexion.java`.
- **"Unknown database":** falta ejecutar el script SQL.
- **NetBeans dice que falta la librería Absolute Layout:** clic derecho en **Libraries → Add Library…** y elige *Absolute Layout*.
- **Tildes o ñ mal escritas:** clic derecho en el proyecto → *Properties → Sources → Encoding* → `UTF-8`.
- **No hay horas disponibles hoy:** si ya pasó el horario de atención, cambia la fecha a mañana.
- **Un formulario no abre en la pestaña Design:** cierra y vuelve a abrir el archivo; si el error continúa, copia el mensaje exacto.
