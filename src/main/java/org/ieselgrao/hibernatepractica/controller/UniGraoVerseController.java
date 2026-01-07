package org.ieselgrao.hibernatepractica.controller;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import com.google.gson.Gson;
import org.ieselgrao.hibernatepractica.model.Planet;
import org.ieselgrao.hibernatepractica.model.SolarSystem;
/**
 * Controller class for the EdUOCation application
 */
public class UniGraoVerseController {

    // private LinkedList<Planet> planets;
    private LinkedList<SolarSystem> solarSystems;

    // TODO: create a persistence unit and give it here the name
    private static final String PERSISTENCE_UNIT_NAME = "";

    public UniGraoVerseController() {

        solarSystems = loadSolarSystems();
        loadPlanets();
    }

    // TODO: Load all planets from the selected persistence unit
    private LinkedList<SolarSystem> loadSolarSystems()
    {
        // This method is "hardcoded" to have some initial data
        LinkedList<SolarSystem> solarSystems = new LinkedList<>();
        solarSystems.add(new SolarSystem("Sistema Solar", "Sol", 0, 40));
        solarSystems.get(0).setId(1); // Also, the id should not be setted like this!! Remove setId method in the future

        solarSystems.add(new SolarSystem("Sistema Inventado", "Aitana", 100, 55));
        solarSystems.get(1).setId(2);

        return solarSystems;
    }

    // TODO: Load all planets from the selected persistence unit
    private void loadPlanets()
    {
        // This method is "hardcoded" to have some initial data
        // Should be removed in the future
        LinkedList<Planet> solarPlanets = new LinkedList<>();
        solarPlanets.add(new Planet("Mercurio", 0, 3.3011e23, 2439.7, 3.7, LocalDate.of(2023, 1, 15), false));
        solarPlanets.add(new Planet("Venus", 0, 4.8675e24, 6051.8, 8.87, LocalDate.of(2023, 2, 20), false));
        solarPlanets.add(new Planet("Tierra", 1, 5.972e24, 6371.0, 9.80, LocalDate.of(2024, 1, 1), false));
        solarPlanets.add(new Planet("Marte", 2, 6.4171e23, 3389.5, 3.71, LocalDate.of(2023, 5, 5), false));
        solarPlanets.add(new Planet("Júpiter", 95, 1.898e27, 69911.0, 24.79, LocalDate.of(2022, 11, 30), true));
        solarPlanets.add(new Planet("Saturno", 146, 5.683e26, 58232.0, 10.44, LocalDate.of(2023, 10, 10), true));
        solarPlanets.add(new Planet("Urano", 27, 8.681e25, 25362.0, 8.69, LocalDate.of(2023, 9, 25), true));
        solarPlanets.add(new Planet("Neptuno", 16, 1.024e26, 24622.0, 11.15, LocalDate.of(2023, 7, 18), true));
        solarPlanets.add(new Planet("Plutón", 5, 1.309e22, 1188.3, 0.62, LocalDate.of(2023, 4, 12), false));
        solarSystems.getFirst().setPlanets(solarPlanets);

        LinkedList<Planet> inventedPlanets = new LinkedList<>();
        inventedPlanets.add(new Planet("Tatooine", 0, 3.3011e23, 2439.7, 3.7, LocalDate.of(2023, 1, 15), false));
        inventedPlanets.add(new Planet("Arrakis", 0, 4.8675e24, 6051.8, 8.87, LocalDate.of(2023, 2, 20), false));
        inventedPlanets.add(new Planet("Pandora", 1, 5.972e24, 6371.0, 9.80, LocalDate.of(2024, 1, 1), false));
        inventedPlanets.add(new Planet("Solaris", 2, 6.4171e23, 3389.5, 3.71, LocalDate.of(2023, 5, 5), false));
        solarSystems.get(1).setPlanets(inventedPlanets);

        // Asign IDs.
        // TODO: remove this loop. IDs should be added in other way
        // Also, they should not repeat even in different solar sistems
        for (SolarSystem ss : solarSystems)
        {
            System.out.println("Loaded " + ss.getPlanets().size() + " planets " + " for " + ss.getName());

            List<Planet> planets = ss.getPlanets();
            for (int i = 0; i < planets.size(); i++)
            {
                planets.get(i).setId(i+1); // Id starts by 1
            }
        }
    }


    /**
     * TODO:Add a solar system to the controller. Also, call persistence!
     */
    public void addSolarSystem(String name, String starName, double starDistance, double Radius) {
        // solarSystems.add(new ...)
    }
    /**
     * TODO: Update an existing planet to the controller. Also, call persistence!
     * @return true is succesful, false if fail
     */
    public boolean updatePlanet(int ID, String name, double mass, double Radius, double gravity, LocalDate date) {
        for (SolarSystem solarSystem : solarSystems) {
            for (Planet planet : solarSystem.getPlanets()) {
                if (planet.getId() == ID) {
                    planet.setName(name);
                    planet.setMass(mass);
                    planet.setRadius(Radius);
                    planet.setGravity(gravity);
                    planet.setLastAlbedoMeasurement(date);
                }
            }
        }
        return true;
    }

    /**
     * TODO: You might need to change this code to call persistence. Also, ensure ID is managed somehow!
     * Add a planet to the controller. Also, call persistence!
     */
    public void addPlanet(int solarSystemId, String name, double mass, double radius, double gravity, LocalDate lastAlbedoMeasurement) {
        for (SolarSystem ss : solarSystems)
        {
            if (ss.getId() == solarSystemId)
            {
                ss.getPlanets().add(new Planet(name, mass, radius, gravity, lastAlbedoMeasurement));
                System.out.println("Added new planet to Solar System " + solarSystemId + ". Current size is: " + ss.getPlanets().size());
                return;
            }
        }
        System.err.println("Could not find solar system with id: " + solarSystemId);
    }

    /**
     * Get the list of solar systems
     * @return List of solar system in JSON format with keys name, star and radius
     */
    public List<String> getSolarSystemsData() {
        List<String> solarSystemsData = new ArrayList<>();
        Gson gson = new Gson();
        for (SolarSystem s : solarSystems){
            Map<String, String> data = new HashMap<>();
            data.put("id", String.valueOf(s.getId()));
            data.put("name", s.getName());
            data.put("star",s.getStarName());
            data.put("distance",String.valueOf(s.getStarDistance()));
            data.put("radius", String.valueOf(s.getRadius()));
            solarSystemsData.add(gson.toJson(data));
        }

        return solarSystemsData;
    }
    /**
     * Get the list of planets for a given solar system
     * @return A list with all planets data, in json format with keys 'name', 'mass' and 'radius'
     */
    public List<String> getPlanetsData(int solarSystemId) {
        List<String> planetsData = new ArrayList<>();
        Gson gson = new Gson();
        for (SolarSystem s : solarSystems)
        {
            if (s.getId() != solarSystemId)   // Chose planets from right solar system
            {
                continue;
            }
            for (Planet p : s.getPlanets()){
                Map<String, String> data = new HashMap<>();
                data.put("id", String.valueOf(p.getId()));
                data.put("name", p.getName());
                data.put("mass", String.valueOf(p.getMass()));
                data.put("radius", String.valueOf(p.getRadius()));
                data.put("gravity", String.valueOf(p.getGravity()));
                data.put("date", String.valueOf(p.getLastAlbedoMeasurement()));
                planetsData.add(gson.toJson(data));
            }
        }

        return planetsData;
    }




}
