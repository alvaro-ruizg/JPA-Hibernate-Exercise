# UniGraoVerso
Un ejercicio increíble con un nombre terrible.

El objetivo de esta práctica es transformar una aplicación de escritorio básica en un sistema robusto de gestión de datos astronómicos utilizando JPA (Jakarta Persistence API) e Hibernate.

## Persistencia en dos bases de datos

El reto principal consiste en lograr que la aplicación sea capaz de persistir datos de forma intercambiable entre dos motores de base de datos (MySQL y SQLite) **sin modificar apenas el código fuente.**
Para ello, he decidido poner dos botones de inicio. Sentíos libre de modificar el mecanismo para intercambiar entre un sistema y otro.

**La aplicación final tiene que cargar datos desde ambos sistemas, y poder actualizar, añadir y eliminar planetas y sistemas solares.**


Conviene saber que:
- MySQL es un **servidor** de base de datos. Funciona con comunicaciones cliente-servidor en el puerto 3306.
- SQLite es una versión muy portable de MySQL. La base de datos se guarda en un fichero .db, y el usuario no necesita instalar nada para poder ejecutar la aplicación localmente.

## La estructura de la plantilla

La plantilla tiene todo el *frontend* de la aplicación ya realizado. Sigue el estándar Modelo-Vista-Controlador.
- Modelo: clases `Planet` y `SolarSystem`. Las clases que definen los datos y su estructura
- `UniGraoVerseController`: aquí se cargan y se centraliza el uso de datos
- `View`: consiste tanto en los archivos `.fxml` como en las clases `XViewController.java`. En estas, se maneja la lógica del frontend y se llama al controlador cuando se le requiere.
**Deberías crear tus clases propias para manejar la persistencia**

## Objetivos desglosados
Por si necesitas ir paso a paso:
- Añade al archivo `pom.xml` las dependencias necesarias de Hibernate, el driver de MySQL y SQLite
- Añade las etiquetas necesarias de Hibernate a las clases Planet y SolarSystem. Ten en cuenta que, si quieres que los planetas en SQL tengan una referencia a su SolarSystem correspondiente (recomendado), deberás añadir una referencia de cada Planet a su SolarSystem, junto a la lógica necesaria para esto.
- Crea el archivo `persistence.xml` en el directorio `resources/META-INF`. Deberás crear 2 unidades de persistencia
- Implementa las funcionalidades de persistencia. Es tu decisión editar y crear las clases necesarias para esto.
  - Nombres como `PlanetDAO` o `SolarSystemDAO` son bastantes adecuados. Estas clases técnicamente se consideran parte del model, aunque también puedes crear un package `DAO` para estas.
  - El `UniGraoVerseController` debe llamar a estas clases. Revisa todos los métodos que tiene, muchos de ellos deberán ser modificados.
  - En `PlayViewController` deberás editar el método `setupDeleteButton`. Puedes editar más métodos si lo crees conveniente, o si quieres añadir funcionalidades de manera opcional.
  - En `MainViewController` edita dos líneas para seleccionar la unidad de persistencia adecuada.

## Entregar
- Un .zip con este repositorio o un link a un repositorio online. Puedes hacer un fork en github si lo prefieres. ¡No borres la carpeta `.git`!
- Un archivo `README.md` (borra/edita este) en el que expliques qué clases has creado, qué retos y problemas has tenido y las funcionalidades finales conseguidas.
- La base de datos exportada en formato tanto `.sql` (para MySQL) y el archivo `.db` (o como hayas decidido llamar a la base de datos SQLite)


## Problemas (y soluciones) comunes
¡Paciencia! Aquí iré subiendo los inconvenientes más importantes que encuentro.

- En la clase `SolarSystem` hay un atributo (`planets`) que es una lista de instancias de la clase `Planet`. ¿Como se guarda esta referencia en ORM? No es algo sencillo, así que tendrás que leer el archivo [Relaciones Entre Entidades](./RelacionesEntreEntidades.md) para aprender como hacer esto en JPA.

- Para las operaciones de borrado, actualizado y guardado, es recomendable usar __transacciones__ aunque se trate de un único elemento.

- Para SQLite necesitamos la dependencia [Hibernate Community dialects](https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-community-dialects). La versión tiene que coincidir con tu versión de Hibernate!. Además, en tu unidad de persistencia deberías incluir, entre otras, las siguientes _propierties_:
```xml
<property name="hibernate.connection.driver_class" value="org.sqlite.JDBC" />
<property name="hibernate.dialect" value="org.hibernate.community.dialect.SQLiteDialect"/>

```
Además, la url de SQLite tiene una estructura del tipo `jdbc:sqlite:universe.db`, siendo `universe.db` el fichero que almacenará la base de datos.

- Deberías borrar la generación de IDs cuanto antes. Este fragmento de código
```Java
		//TODO: borrar
        for (SolarSystem ss : solarSystems)
        {
            System.out.println("Loaded " + ss.getPlanets().size() + " planets " + " for " + ss.getName());

            List<Planet> planets = ss.getPlanets();
            for (int i = 0; i < planets.size(); i++)
            {
                planets.get(i).setId(i+1); // Id starts by 1
            }
        }
```
crea IDs repetidos para planetas de distintos sistemas solares. Esto te va a dar fallo al guardar en hibernate. Incluso te recomiendo borrar los métodos `setId()` para asegurarse que no asignamos los ids nosotros.

- Borra el archivo module-info.java! Este archivo **restringe** la comunicación entre dependencias, cosa que en nuestro caso nos dará más problemas que ventajas.

- ¿No se te refresca correctamente la tabla al añadir/borrar un planeta o sistema solar? Puedes probar lo siguiente:
  - Llamar a `loadPlanetsTable()` o `loadSolarSystemTable()` (según corresponda) en `PlayViewController`, seguido de `mainTable.refresh()`
  - Actualizar los datos tras una actualización/borrado. Te recomiendo tener un método `loadAllData()` para iniciar el programa, que también puedas llamar para forzar el 'refrescado' de datos desde la BD.

