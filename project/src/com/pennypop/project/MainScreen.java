package com.pennypop.project;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.Net.HttpRequest;



/**
 * This is where the Main Menu UI is located at. The appropriate buttons 
 * and sound effects are loaded here. The listeners are set up and the appropriate
 * information is displayed at specific instances
 * 
 * @author Richard Taylor
 */
public class MainScreen implements Screen {
	
	private final Stage stage;
	private final SpriteBatch spriteBatch;
	private BitmapFont font;
	private final Sound sound;
	private final TextureAtlas buttonAtlas;
	private final Skin buttonSkin;
	private final Button sfxButton;
	private final Button apiButton;
	private final Button gameButton;
	private HttpRequest httpRequest;
	private JsonReader jsonReader;
	private CityWeather displayCity;
	private boolean displayWeather = false;
	private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=San%20Francisco,US";
	private static final int BUTTONPADDING = 10;
	
	private Game game;
	
	public MainScreen(Game game) {
		this.game = game;
		
		spriteBatch = new SpriteBatch();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, spriteBatch);
		font = new BitmapFont(Gdx.files.internal("font.fnt"),false);
		//setting up button atlas and skin from the Button package
		buttonAtlas = new TextureAtlas(Gdx.files.internal("Buttons.pack"));
		buttonSkin = new Skin(buttonAtlas);
		buttonSkin.addRegions(buttonAtlas);
		//setting up each button 
		sfxButton = new Button(buttonSkin.getDrawable("sfxButton"));
		apiButton = new Button(buttonSkin.getDrawable("apiButton"));
		gameButton = new Button(buttonSkin.getDrawable("gameButton"));
		//cityWeather object to hold information on the weather
		displayCity = new CityWeather();
		//location of buttons with padding for space between them
		sfxButton.setPosition(200, 300);
		apiButton.setPosition(200+sfxButton.getMinWidth()+BUTTONPADDING, 300);
		gameButton.setPosition(200+apiButton.getMinWidth()*2+BUTTONPADDING*2, 300);
		//adding the buttons to the stage
		stage.addActor(sfxButton);
		stage.addActor(apiButton);
		stage.addActor(gameButton);
		sound = Gdx.audio.newSound(Gdx.files.internal("button_click.wav"));
		//calling method to add click listener because it's a lot of lines of code
		weatherListenerSetup();
		sfxButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x ,float y){
				sound.play(1.0f);
			}
		});
		gameButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				loadGame();
			}	
		});
	}
	
	public void loadGame(){
		game.setScreen(new ConnectFourScreen(game));
	}

	@Override
	public void dispose(){
		spriteBatch.dispose();
		stage.dispose();
		font.dispose();
		buttonAtlas.dispose();
		buttonSkin.dispose();
		sound.dispose();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
		
		spriteBatch.begin();
		font.setColor(1,0,0,1);
		font.draw(spriteBatch,"Tic Tac Toe and Your Weather Forecast",Gdx.graphics.getWidth()/2-450,Gdx.graphics.getHeight()/2+200);
		if(displayWeather){
			displayWeatherInfo();
		}
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		
		stage.setViewport(width, height, false);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void show() {	
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void pause() {
		// Irrelevant on desktop, ignore this
	}

	@Override
	public void resume() {
		// Irrelevant on desktop, ignore this
	}

	/*Helper method to setup the information when retrieving the weather information
	from the url*/
	private void weatherListenerSetup(){
		apiButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x ,float y){
				//If display weather is turned on
				if(!displayWeather){
					String requestContent = null;
					httpRequest = new HttpRequest(Net.HttpMethods.GET);
					httpRequest.setUrl(WEATHER_URL);
					httpRequest.setHeader("Content-Type", "text/plain");
					httpRequest.setContent(requestContent);
				
					Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener(){
						public void handleHttpResponse(HttpResponse httpResponse){
							//Create a json reader to read the json information
							jsonReader = new JsonReader();
							//Get the information in a string
							String responseJson = httpResponse.getResultAsString();	
							//Pass it to the json reader to parse into an ordered map
							OrderedMap map = (OrderedMap)jsonReader.parse(responseJson);
							displayCity.setName((String) map.get("name"));
						
							OrderedMap windMap = (OrderedMap) map.get("wind");
							displayCity.setSpeed((Float)windMap.get("speed"));
						
							Array<OrderedMap> weatherMap = (Array<OrderedMap>) map.get("weather");
							displayCity.setDescription((String) weatherMap.get(0).get("description"));
						
							OrderedMap mainMap = (OrderedMap) map.get("main");
							displayCity.setDegrees((Float)mainMap.get("temp"));
							//so we can show the weather information since the button was pressed
							displayWeather = true;
						}
						public void failed(Throwable t){
							System.out.println("Http request failed!");
						}
					});
				}
				else{
					displayWeather = false;
				}
			}
		});
	}
	
	//Helper method to display weather information from the CityWeather object
	private void displayWeatherInfo(){
		font.setColor(Color.BLACK);
		font.draw(spriteBatch, "Current Weather", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2+100);
	
		font.setColor(Color.BLUE);
		font.draw(spriteBatch, displayCity.getName(), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2+50);
	
		font.setColor(Color.RED);
		font.draw(spriteBatch, displayCity.getDescription(), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
	
		font.setColor(Color.RED);
		font.draw(spriteBatch, String.format("%.1f", displayCity.getDegreesFarenheit())+
			" degrees, " + displayCity.getSpeed()+" mph wind", Gdx.graphics.getWidth()/2, 
					Gdx.graphics.getHeight()/2-50);
	}
}
