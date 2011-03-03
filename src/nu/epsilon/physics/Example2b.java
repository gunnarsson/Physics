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
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Example2b extends BaseGameActivity {
	
	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 320;
	protected static final int BLOCKS = 7;
	
	protected Texture texture;
	protected TiledTextureRegion tileRegions;
	
	protected PhysicsWorld physicsWorld;
	protected FixtureDef fixtureDefinition = PhysicsFactory.createFixtureDef(10, 0.5f, 0.5f);
	
	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final RatioResolutionPolicy ratio = new RatioResolutionPolicy(camera.getWidth(), camera.getHeight());
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, ratio, camera));
	}
	
	@Override
	public void onLoadResources() {
		this.texture = new Texture(256, 32, TextureOptions.BILINEAR);
		this.tileRegions = TextureRegionFactory.createTiledFromAsset(this.texture, this, "gfx/blocks.png", 0, 0, BLOCKS+1,
				1);
		this.mEngine.getTextureManager().loadTexture(this.texture);
	}
	
	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0, 0, 0));
		
		this.physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		
		// Add blocks
		for (int i = 0; i < BLOCKS; i++) {
			TiledSprite sprite = new TiledSprite(68 + i * 52, 144, this.tileRegions.clone());
			sprite.setCurrentTileIndex(i);
			scene.getTopLayer().addEntity(sprite);
			
			sprite.setUpdatePhysics(false);
			Body body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.DynamicBody, fixtureDefinition);
			physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body));
		}
		
		scene.registerUpdateHandler(physicsWorld);
		return scene;
	}
	
	@Override
	public void onLoadComplete() {
		
	}
}
