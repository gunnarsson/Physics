package nu.epsilon.physics;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Example4 extends BaseGameActivity implements IAccelerometerListener {
	
	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 320;
	protected static final int BLOCKS = 7;
	
	protected Texture texture;
	protected TiledTextureRegion tileRegions;
	
	protected PhysicsWorld physicsWorld;
	protected FixtureDef fixtureDefinition = PhysicsFactory.createFixtureDef(10, 0.5f, 0.1f);
	
	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final RatioResolutionPolicy ratio = new RatioResolutionPolicy(camera.getWidth(), camera.getHeight());
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, ratio, camera));
	}
	
	@Override
	public void onLoadResources() {
		this.texture = new BuildableTexture(256, 32, TextureOptions.BILINEAR);
		this.tileRegions = TextureRegionFactory.createTiledFromAsset(this.texture, this, "gfx/blocks.png", 0, 0,
				BLOCKS + 1, 1);
		this.mEngine.getTextureManager().loadTexture(this.texture);
		
		this.enableAccelerometerSensor(this);
	}
	
	@Override
	public Scene onLoadScene() {
		this.physicsWorld = new PhysicsWorld(new Vector2(0, 2 * SensorManager.GRAVITY_EARTH), false);
		
		final Scene scene = new Scene(2);
		scene.setBackground(new ColorBackground(0, 0, 0));
		
		// Add blocks
		for (int i = 0; i < BLOCKS; i++) {
			float x = 68 + i * 52;
			
			// Create block
			TiledSprite block = new TiledSprite(x, 144, this.tileRegions.clone());
			block.setCurrentTileIndex(i);
			block.setUpdatePhysics(false);
			scene.getTopLayer().addEntity(block);
			
			TiledTextureRegion anchorRegion = this.tileRegions.clone();
			anchorRegion.setCurrentTileIndex(BLOCKS);
			
			// Create anchor
			TiledSprite anchor = new TiledSprite(x, 10, anchorRegion);
			anchor.setUpdatePhysics(false);
			scene.getTopLayer().addEntity(anchor);
			
			// Create block body
			Body blockBody = PhysicsFactory.createBoxBody(physicsWorld, block, BodyType.DynamicBody, fixtureDefinition);
			this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(block, blockBody, true, true, false, false));
			
			// Create anchor body
			Body anchorBody = PhysicsFactory.createBoxBody(physicsWorld, anchor, BodyType.StaticBody, fixtureDefinition);
			this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(anchor, anchorBody));
			
			// Create joint
			final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
			revoluteJointDef.initialize(anchorBody, blockBody, anchorBody.getWorldCenter());
			
			physicsWorld.createJoint(revoluteJointDef);
		}
		
		scene.registerUpdateHandler(this.physicsWorld);
		return scene;
	}
	
	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		this.physicsWorld.setGravity(new Vector2(4 * pAccelerometerData.getY(), 4 * pAccelerometerData.getX()));
	}
	
	@Override
	public void onLoadComplete() {
	}
	
}
