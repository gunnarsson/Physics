package nu.epsilon.physics;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.examples.BasePhysicsJointsExample;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class FallingImage extends BaseGameActivity implements
		IAccelerometerListener, IOnSceneTouchListener {

	protected static final int CAMERA_WIDTH = 480 * 2;
	protected static final int CAMERA_HEIGHT = 320 * 2;

	private static final int BLOCKS = 16;
	private static final int BLOCK_WIDTH = 256 / BLOCKS;

	private Texture texture;
	private TiledTextureRegion tileRegions;

	protected PhysicsWorld physicsWorld;
	protected FixtureDef fixtureDefinition = PhysicsFactory.createFixtureDef(
			10, 0.4f, 0.5f);

	@Override
	public Engine onLoadEngine() {
		
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final RatioResolutionPolicy ratio = new RatioResolutionPolicy(
				CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				ratio, camera));
	}

	@Override
	public void onLoadResources() {
		this.enableAccelerometerSensor(this);
		this.texture = new Texture(256, 256, TextureOptions.BILINEAR);
		this.tileRegions = TextureRegionFactory
				.createTiledFromAsset(this.texture, this,
						"gfx/hero_flying.png", 0, 0, BLOCKS, BLOCKS);
		this.mEngine.getTextureManager().loadTexture(this.texture);
	}

	@Override
	public Scene onLoadScene() {

		final Scene scene = new Scene(2);
		scene.setBackground(new ColorBackground(0, 0, 0));
		scene.setOnSceneTouchListener(this);

		// Add blocks
		for (int i = 0; i < BLOCKS; i++) {
			for (int j = 0; j < BLOCKS; j++) {
				TiledSprite sprite = new TiledSprite(
						90 + i * (BLOCK_WIDTH),
						-50 + j * (BLOCK_WIDTH), this.tileRegions.clone());
				sprite.setCurrentTileIndex(i + (j * BLOCKS));
				scene.getTopLayer().addEntity(sprite);
			}
		}

		this.physicsWorld = new PhysicsWorld(new Vector2(0,
				2 * SensorManager.GRAVITY_EARTH), false, 5, 5);

		int count = scene.getTopLayer().getEntityCount();
		for (int i = 0; i < count; i++) {
			IShape sprite = (IShape) scene.getTopLayer().getEntity(i);

			Body body = PhysicsFactory.createBoxBody(physicsWorld, sprite,
					BodyType.DynamicBody, fixtureDefinition);
			
			sprite.setUpdatePhysics(false);
			this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(
					sprite, body, true, true, false, false));
		}

		final Shape ground = new Rectangle(-CAMERA_WIDTH * 2,
				CAMERA_HEIGHT - 2, CAMERA_WIDTH * 5, 2);
		PhysicsFactory.createBoxBody(this.physicsWorld, ground,
				BodyType.StaticBody, this.fixtureDefinition);

		scene.getBottomLayer().addEntity(ground);

		return scene;
	}


	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {
		if (this.physicsWorld != null) {
			if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
				this.runOnUpdateThread(new Runnable() {
					@Override
					public void run() {

						FallingImage.this.mEngine.getScene()
								.registerUpdateHandler(
										FallingImage.this.physicsWorld);
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
