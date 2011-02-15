package nu.epsilon.physics;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSCounter;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PerformanceDemo extends BaseGameActivity implements
		IAccelerometerListener, IOnSceneTouchListener {

	protected static final int CAMERA_WIDTH = 480 * 2;
	protected static final int CAMERA_HEIGHT = 320 * 2;

	private Texture texture;
	private TextureRegion tileRegions;

	protected PhysicsWorld physicsWorld;
	protected FixtureDef fixtureDefinition = PhysicsFactory.createFixtureDef(
			10, 0.4f, 0.5f);

	private Camera camera;

	private ChangeableText textCenter;
	private int objectCounter;

	private FPSCounter fpsCounter = new FPSCounter();

	@Override
	public Engine onLoadEngine() {

		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final RatioResolutionPolicy ratio = new RatioResolutionPolicy(
				CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				ratio, camera));
	}

	@Override
	public void onLoadResources() {
		this.enableAccelerometerSensor(this);
		this.texture = new Texture(8, 64, TextureOptions.BILINEAR);
		this.tileRegions = TextureRegionFactory.createFromAsset(this.texture,
				this, "gfx/stone_base.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.texture);
	}

	@Override
	public Scene onLoadScene() {

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0, 0, 0));
		scene.setOnSceneTouchListener(this);

		this.physicsWorld = new PhysicsWorld(new Vector2(0,
				2 * SensorManager.GRAVITY_EARTH), false, 5, 5);

		scene.registerUpdateHandler(PerformanceDemo.this.physicsWorld);
		scene.registerUpdateHandler(fpsCounter);
		scene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				textCenter.setText("FPS: " + Math.round(fpsCounter.getFPS())
						+ ", Nbr of objects: " + objectCounter);

			}
		});

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH,
				2);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		PhysicsFactory.createBoxBody(this.physicsWorld, ground,
				BodyType.StaticBody, this.fixtureDefinition);
		PhysicsFactory.createBoxBody(this.physicsWorld, roof,
				BodyType.StaticBody, this.fixtureDefinition);
		PhysicsFactory.createBoxBody(this.physicsWorld, left,
				BodyType.StaticBody, this.fixtureDefinition);
		PhysicsFactory.createBoxBody(this.physicsWorld, right,
				BodyType.StaticBody, this.fixtureDefinition);

		scene.getBottomLayer().addEntity(ground);
		scene.getBottomLayer().addEntity(roof);
		scene.getBottomLayer().addEntity(left);
		scene.getBottomLayer().addEntity(right);

		HUD hud = new HUD();

		Texture fontTexture = new Texture(512, 256, TextureOptions.BILINEAR);
		Font font = new Font(fontTexture, Typeface.create(Typeface.DEFAULT,
				Typeface.BOLD), 32, true, Color.WHITE);
		textCenter = new ChangeableText(10, 10, font,
				"FPS: 000, Nbr of objects: 000");

		this.mEngine.getTextureManager().loadTexture(fontTexture);
		this.mEngine.getFontManager().loadFont(font);
		hud.getTopLayer().addEntity(textCenter);
		camera.setHUD(hud);

		return scene;
	}

	private void add(final int x, final int y) {
		Sprite sprite = new Sprite(x, y, this.tileRegions.clone());
		sprite.setRotation((float) (Math.random() * Math.PI));
		sprite.setUpdatePhysics(false);
		Scene scene = mEngine.getScene();
		scene.getTopLayer().addEntity(sprite);
		Body body = PhysicsFactory.createBoxBody(physicsWorld, sprite,
				BodyType.DynamicBody, fixtureDefinition);
		this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite,
				body, true, true, false, false));

	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {
		if (this.physicsWorld != null) {
			if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
				this.runOnUpdateThread(new Runnable() {
					@Override
					public void run() {

						add((int) pSceneTouchEvent.getX(),
								(int) pSceneTouchEvent.getY());
						add((int) pSceneTouchEvent.getX() - 5,
								(int) pSceneTouchEvent.getY() - 5);
						add((int) pSceneTouchEvent.getX() + 5,
								(int) pSceneTouchEvent.getY() + 5);
						add((int) pSceneTouchEvent.getX() - 5,
								(int) pSceneTouchEvent.getY() + 5);
						add((int) pSceneTouchEvent.getX() + 5,
								(int) pSceneTouchEvent.getY() - 5);

						objectCounter+=5;
						if (objectCounter % 25 == 0) {
							fpsCounter.reset();
						}
					}
				});
				return true;
			}
		}

		return false;
	}

	@Override
	public void onLoadComplete() {

	}

	@Override
	public void onAccelerometerChanged(
			final AccelerometerData pAccelerometerData) {
		this.physicsWorld.setGravity(new Vector2(4 * pAccelerometerData.getY(),
				4 * pAccelerometerData.getX()));
	}

}
