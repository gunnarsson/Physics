package nu.epsilon.physics;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class Example1 extends BaseGameActivity {

	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 320;
	private static final int BLOCKS = 6;

	private Texture texture;
	private TiledTextureRegion tileRegions;

	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final RatioResolutionPolicy ratio = new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, ratio, camera));
	}

	@Override
	public void onLoadResources() {
		this.texture = new Texture(256, 32, TextureOptions.BILINEAR);
		this.tileRegions = TextureRegionFactory.createTiledFromAsset(this.texture, this, "gfx/jfokus.png", 0, 0, BLOCKS, 1);
		this.mEngine.getTextureManager().loadTexture(this.texture);
	}

	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(2);
		scene.setBackground(new ColorBackground(0, 0, 0));

		// Add blocks
		for (int i = 0; i < BLOCKS; i++) {
			TiledSprite sprite = new TiledSprite(94 + i * 52, 144, this.tileRegions.clone());
			sprite.setCurrentTileIndex(i);
			scene.getTopLayer().addEntity(sprite);
		}

		return scene;
	}

	@Override
	public void onLoadComplete() {

	}

}
