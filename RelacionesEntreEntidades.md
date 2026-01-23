# Referencias entre entidades en ORM

De los mayores retos es cómo la información entre Planets y SolarSystems queda guardada. En tablas SQL, lo lógico sería tener en la tabla `planets` una columna llamada `solar_system_id`.
Esta columna sería la _clave foránea_ a la tabla de `solar_systems`.

Pero , ¿cómo hacemos esto en JPA/Hibernate?
No podemos simplemente indicar como `@Column` el atributo `private List<Planet> planets` de la clase `SolarSystem`. ¿Qué clase de columna sería esta? No existe un tipo de dato elemental llamado _planet_.

## Primer paso: añadir referencia de Planet a SolarSystem.
Aunque la nomenclatura puede variar según el contexto (SQL, POO) vamos a llamar a la clase `SolarSystem` __padre__ y a la clase `Planet` __hijo__ (nada que ver con la herencia entre clases). Aunque ahora mismo el padre referencie al hijo (enfoque común de POO), en SQL es más común que el hijo tenga una columna que referencie al padre, mediante una _foreign key_ a otra tabla.

¿Con qué enfoque nos quedamos? Aunque puede haber excepciones a esto, el mejor enfoque para nuestra aplicación es __ambos__. 
Así pues, vamos a asegurar que la clase `Planet` tiene un atributo `private SolarSystem solarSystem` que haga referencia al sistema solar al cual pertenece. Crearemos también su _getter_ y _setter_.

Hay que tener cuidado con esta relación bidireccional: tenemos que asegurarnos que si un sistema solar tiene X planetas, cada uno de estos planetas tiene como valor de solarSystem este mismo sistema solar, y no otro. Para ello, modificaremos el método `setPlanets` de la clase `SolarSystem` de la siguiente manera:

```Java
public void setPlanets(List<Planet> planets) {
    if (planets == null || planets.isEmpty())
    {
        throw new UniverseException(UniverseException.INVALID_PLANET_LIST);
    }
    for (Planet p : planets)
    {
        p.setSolarSystem(this);  // Asignamos a cada planeta este sistema solar
    }
    this.planets = planets; // Asignamos a este sistema solar la lista de planetas
}
```

## Segundo paso: etiquetas de JPA para mapeo entre entidades

Para que Hibernate gestione correctamente la columna 
Existen etiquetas especiales de Hibernate para estos casos:

- `@OneToMany`: seria el caso de la lista de planetas en la clase SolarSystem.

```Java
    // Clase SolarSystem
    @OneToMany
    private List<Planet> planets;
```
- `@ManyToOne`: seria el caso del sistema solar dentro de la clase Planet. Puesto que en esta clase sí queremos una columna que haga referencia al sistema solar, añadiremos también la etiqueta `@JoinColumn`:
```Java
    // Clase Planet
    @ManyToOne
    private SolarSystem solarSystem;
```

- También existen los casos `@OneToOne` y `@ManyToMany`, aunque no los usaremos en este proyecto.

### Indicar quién contiene la referencia en SQL.

Además de estas etiquetas, tenemos que desambiguar una cuestión en nuestro código: ¿qué entidad contiene una columna referenciando a la otra? En nuestro caso, ya hemos mencionado que la tabla `planet` tendrá la foreign key a `solarSystem`. Esto lo expresamos mediante otras etiquetas:

- En la entidad que **no** tiene una referencia a la otra en SQL, lo indicamos añadiendo el parámetro `mappedBy` a la etiqueta anterior.

```Java
    // Clase SolarSystem
    @OneToMany(mappedBy = "solarSystem")
    private List<Planet> planets;
```

- `@JoinColumn`: En la entidad que **sí** tiene una referencia a la otra en SQL, lo indicamos con esta etiqueta junto al nombre que queramos darle a la columna SQL.
```Java
    // Clase Planet
    @ManyToOne
    @JoinColumn(name = "solar_system_id")
    private SolarSystem solarSystem;
```

## Cascading (Operaciones en cascada)

Cuando trabajamos con entidades relacionadas, surge una duda: ¿Qué ocurre con los "hijos" cuando realizamos una acción sobre el "padre"? Por ejemplo, si borramos un `SolarSystem`, ¿qué debería pasar con sus `Planet`?

El **Cascading** permite que las operaciones realizadas sobre una entidad se propaguen automáticamente a sus entidades relacionadas.

### Tipos de Cascade comunes:

- **`CascadeType.PERSIST`**: Al guardar el padre, se guardan automáticamente los hijos. Útil si creas un sistema y sus planetas a la vez en el código y quieres hacer un solo `persist(sistema)`.
- **`CascadeType.REMOVE`**: Al borrar el padre, se borran todos sus hijos. Es fundamental para mantener la integridad si un planeta no puede existir sin su sistema.
- **`CascadeType.MERGE`**: Actualiza los hijos si el padre es actualizado.
- **`CascadeType.ALL`**: Aplica todas las anteriores. Es la opción más común en relaciones Padre-Hijo fuertes.

### Ejemplo de implementación

En nuestro proyecto, lo más lógico es aplicarlo en la clase `SolarSystem`, para que al gestionar el sistema, sus planetas sigan el mismo destino:

```java
    // Clase SolarSystem
    @OneToMany(mappedBy = "solarSystem", cascade = CascadeType.ALL)
    private List<Planet> planets;
```
Por ultimo, también podemos añadir después de `mappedBy` y `cascade` el parámetro `orphanRemoval = true`: Esta propiedad es muy potente. Si eliminamos un objeto Planet de la lista planets en Java, Hibernate se encargará de borrarlo físicamente de la base de datos SQL al hacer el commit, ya que lo considera un "huérfano".

## Referencias, más lecturas.

- [Difference Between @JoinColumn and mappedBy](https://www.baeldung.com/jpa-joincolumn-vs-mappedby)
- [Overview of JPA/Hibernate Cascade Types](https://www.baeldung.com/jpa-cascade-types)
- [JPA CascadeType.REMOVE vs orphanRemoval](https://www.baeldung.com/jpa-cascade-remove-vs-orphanremoval)
- [All Articles about Entity Relationships in JPA](https://www.baeldung.com/tag/entity-relationships)

