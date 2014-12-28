package com.pennypop.project;

/**
 * Class to store the weather information for one city. Includes
 * name, description, degrees and speed
 * 
 * @author Erik Gallegos
 * */
public class CityWeather {
	
	private String name;
	private String description;
	private float degrees;
	private float speed;
	
	/*
	 * Constructor with default information in case the url cannot
	 * be accessed
	 * */
	public CityWeather(){
		name = "Weather information not available";
		description = "";
		degrees = 0;
		speed = 0;
	}
	
	//Getters
	public String getName(){
		return this.name;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public float getDegreesFarenheit(){
		return (float) (this.degrees*(9.0/5.0) -459.67);
	}
	
	public float getDegreesKelvin(){
		return this.degrees;
	}
	
	public float getSpeed(){
		return this.speed;
	}
	
	//Setters
	public void setName(String argName){
		this.name = argName;
	}
	
	public void setDescription(String argDescription){
		this.description = argDescription;
	}
	
	public void setDegrees(float argDegrees){
		this.degrees = argDegrees;
	}
	
	public void setSpeed(float argSpeed){
		this.speed = argSpeed;
	}

}
