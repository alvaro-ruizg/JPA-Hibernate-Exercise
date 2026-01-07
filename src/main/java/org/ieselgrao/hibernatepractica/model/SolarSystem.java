package org.ieselgrao.hibernatepractica.model;

import java.util.LinkedList;

public class SolarSystem {
    private int id;
    private String name;
    private String starName;
    private double starDistance;
    private double Radius;
    private LinkedList<Planet> planets;

    public SolarSystem(String name, String starName, double starDistance, double Radius) {
        this.name = name;
        this.starName = starName;
        this.starDistance = starDistance;
        this.Radius = Radius;
        this.planets = new LinkedList<>();
    }

    // Setters and getters
    public int getId(){
        return id;
    }
    public void setId(int id){  // Should be removed in the future, since ID should not be assigned this way
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new UniverseException(UniverseException.INVALID_NAME);
        }
        this.name = name;
    }

    public String getStarName() {
        return starName;
    }

    public void setStarName(String starName) {
        if (name == null || name.trim().isEmpty()) {
            throw new UniverseException(UniverseException.INVALID_NAME);
        }
        this.starName = starName;
    }

    public double getStarDistance() {
        return starDistance;
    }

    public void setStarDistance(double starDistance) {
        if (starDistance < 0) {
            throw new UniverseException(UniverseException.INVALID_DISTANCE);
        }
        this.starDistance = starDistance;
    }

    public double getRadius() {
        return Radius;
    }

    public void setRadius(double Radius) {
        // Perhaps we could add a minimum solar system radius
        this.Radius = Radius;
    }

    public LinkedList<Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(LinkedList<Planet> planets) {
        if (planets == null || planets.isEmpty())
        {
            throw new UniverseException(UniverseException.INVALID_PLANET_LIST);
        }
        this.planets = planets;
    }
}