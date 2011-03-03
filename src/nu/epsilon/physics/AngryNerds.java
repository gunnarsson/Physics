package nu.epsilon.physics;

import java.util.HashMap;
import java.util.Map;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.source.AssetTextureSource;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

// 0418 18370

public class AngryNerds extends BaseGameActivity implements
		IOnSceneTouchListener {

	protected static final int CAMERA_WIDTH = 854;
	protected static final int CAMERA_HEIGHT = 480;

	private Texture woodTexture;
	private Texture smallWoodTexture;
	private Texture largeWoodTexture;

	private Texture stoneTexture;
	private Texture smallStoneTexture;
	private Texture largeStoneTexture;

	private Texture groundTexture;
	private Texture backgroundTexture;

	private Texture joltTexture;
	private Texture bossTexture;

	private TextureRegion woodRegion;
	private TextureRegion smallWoodRegion;
	private TextureRegion largeWoodRegion;

	private TextureRegion stoneRegion;
	private TextureRegion smallStoneRegion;
	private TextureRegion largeStoneRegion;

	private TextureRegion groundRegion;
	private TextureRegion joltRegion;
	private TextureRegion bossRegion;

	private TextureRegion backgroundRegion;

	private Body can;
	private boolean initiated = false;
	private int shootCount = 3;
	private int hitCount = 0;

	protected PhysicsWorld physicsWorld;
	protected FixtureDef fixtureDefinition = PhysicsFactory.createFixtureDef(
			10f, 0.4f, 0.8f);

	protected FixtureDef joltDefinition = PhysicsFactory.createFixtureDef(20f,
			0.4f, 0.8f);

	private int startX;
	private int startY;


	private final Map<Body, Sprite> bosses = new HashMap<Body, Sprite>();

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

		/*
		 * Create textures
		 */
		woodTexture = new Texture(8, 64, TextureOptions.BILINEAR);
		smallWoodTexture = new Texture(16, 16, TextureOptions.BILINEAR);
		largeWoodTexture = new Texture(16, 32, TextureOptions.BILINEAR);

		stoneTexture = new Texture(8, 64, TextureOptions.BILINEAR);
		smallStoneTexture = new Texture(16, 16, TextureOptions.BILINEAR);
		largeStoneTexture = new Texture(16, 32, TextureOptions.BILINEAR);

		groundTexture = new Texture(512, 64, TextureOptions.BILINEAR);
		backgroundTexture = new Texture(512, 1024, TextureOptions.BILINEAR);

		joltTexture = new Texture(64, 256, TextureOptions.BILINEAR);
		bossTexture = new Texture(64, 64, TextureOptions.BILINEAR);

		/*
		 * Create regions
		 */
		woodRegion = TextureRegionFactory.createFromAsset(woodTexture, this,
				"gfx/wood_base.png", 0, 0);
		smallWoodRegion = TextureRegionFactory.createFromAsset(
				smallWoodTexture, this, "gfx/wood_small.png", 0, 0);
		largeWoodRegion = TextureRegionFactory.createFromAsset(
				largeWoodTexture, this, "gfx/wood_large.png", 0, 0);

		stoneRegion = TextureRegionFactory.createFromAsset(stoneTexture, this,
				"gfx/stone_base.png", 0, 0);
		smallStoneRegion = TextureRegionFactory.createFromAsset(
				smallStoneTexture, this, "gfx/stone_small.png", 0, 0);
		largeStoneRegion = TextureRegionFactory.createFromAsset(
				largeStoneTexture, this, "gfx/stone_large.png", 0, 0);

		groundRegion = TextureRegionFactory.createFromAsset(groundTexture,
				this, "gfx/ground.png", 0, 0);
		backgroundRegion = TextureRegionFactory.createFromAsset(
				backgroundTexture, this, "gfx/background.png", 0, 0);

		joltRegion = TextureRegionFactory.createFromAsset(joltTexture, this,
				"gfx/jolt.png", 0, 0);
		bossRegion = TextureRegionFactory.createFromAsset(bossTexture, this,
				"gfx/boss.png", 0, 0);

		/*
		 * Load textures
		 */
		this.mEngine.getTextureManager().loadTexture(woodTexture);
		this.mEngine.getTextureManager().loadTexture(smallWoodTexture);
		this.mEngine.getTextureManager().loadTexture(largeWoodTexture);

		this.mEngine.getTextureManager().loadTexture(stoneTexture);
		this.mEngine.getTextureManager().loadTexture(smallStoneTexture);
		this.mEngine.getTextureManager().loadTexture(largeStoneTexture);

		this.mEngine.getTextureManager().loadTexture(groundTexture);
		this.mEngine.getTextureManager().loadTexture(backgroundTexture);
		this.mEngine.getTextureManager().loadTexture(joltTexture);
		this.mEngine.getTextureManager().loadTexture(bossTexture);
	}

	@Override
	public Scene onLoadScene() {

		final Scene scene = new Scene(1);

		// set background that repeats itself.
		scene.setBackground(new RepeatingSpriteBackground(CAMERA_WIDTH,
				CAMERA_HEIGHT, mEngine.getTextureManager(),
				new AssetTextureSource(this, "gfx/background.png")));

		// register to be notified of touch events
		scene.setOnSceneTouchListener(this);

		this.physicsWorld = new FixedStepPhysicsWorld(120, new Vector2(0,
				2 * SensorManager.GRAVITY_EARTH), true, 3, 2);

//		this.physicsWorld = new PhysicsWorld(new Vector2(0,
//				2 * SensorManager.GRAVITY_EARTH), true, 3, 2);

		// Add listener to be notified about collisions.
		this.physicsWorld.setContactListener(getContactListener(scene));

		// constants to make the scene creation easier
		float WIDTH = woodRegion.getWidth();
		float HEIGHT = woodRegion.getHeight();
		float GROUND = CAMERA_HEIGHT - 43;
		float TRANSLATE1 = 600;
		float TRANSLATE2 = TRANSLATE1 + HEIGHT - WIDTH;
		float WPH = WIDTH + HEIGHT;
		float HMW = HEIGHT - WIDTH;
		float floor1 = GROUND - HEIGHT;
		float floor2 = floor1 - WPH;
		float floor3 = floor2 - WPH;
		float floor4 = floor3 - WPH;
		float floor5 = floor4 - WPH;

		// 1 floor
		add(scene, stoneRegion, TRANSLATE1, floor1, 0);
		add(scene, stoneRegion, TRANSLATE2, floor1, 0);
		add(scene, woodRegion, TRANSLATE1 + HMW / 2f, floor1 - WPH / 2f, -90f);

		// 2 floor
		add(scene, stoneRegion, TRANSLATE1, floor2, 0);
		add(scene, stoneRegion, TRANSLATE2, floor2, 0);
		add(scene, woodRegion, TRANSLATE1 + HMW / 2f, floor2 - WPH / 2f, -90);

		// 3 floor
		add(scene, stoneRegion, TRANSLATE1, floor3, 0);
		add(scene, stoneRegion, TRANSLATE2, floor3, 0);
		add(scene, woodRegion, TRANSLATE1 + HMW / 2f, floor3 - WPH / 2f, -90);

		// 4 floor
		add(scene, stoneRegion, TRANSLATE1, floor4, 0);
		add(scene, stoneRegion, TRANSLATE2, floor4, 0);
		add(scene, woodRegion, TRANSLATE1 + HMW / 2f, floor4 - WPH / 2f, -90);

		// 5 floor
		add(scene, stoneRegion, TRANSLATE1, floor5, 0);
		add(scene, stoneRegion, TRANSLATE2, floor5, 0);
		add(scene, woodRegion, TRANSLATE1 + HMW / 2f, floor5 - WPH / 2f, -90);

		add(scene, groundRegion, -512, CAMERA_HEIGHT - 43, 0,
				BodyType.StaticBody);
		add(scene, groundRegion, 0, CAMERA_HEIGHT - 43, 0, BodyType.StaticBody);
		add(scene, groundRegion, 512, CAMERA_HEIGHT - 43, 0,
				BodyType.StaticBody);
		add(scene, groundRegion, 1024, CAMERA_HEIGHT - 43, 0,
				BodyType.StaticBody);

		// bosses
		add(scene, bossRegion, TRANSLATE2 + 50, floor1, 0);
		add(scene, bossRegion, TRANSLATE1 + 20, floor1, 0);

		// add jolt cola to throw at the bosses
		can = add(scene, joltRegion, 50, 300, 0);

		return scene;
	}

	private ContactListener getContactListener(final Scene scene) {
		return new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();

				for (final Body bossBody : bosses.keySet()) {

					Fixture boss = bossBody.getFixtureList().get(0);

					if (fixtureA == boss || fixtureB == boss) {

						hitCount++;
						if (hitCount == 10) {

							AngryNerds.this.runOnUpdateThread(new Runnable() {

								@Override
								public void run() {
									physicsWorld.destroyBody(bossBody);
									scene.getTopLayer().removeEntity(
											bosses.get(bossBody));
								}

							});

						}
					}
				}

			}

			@Override
			public void endContact(Contact contact) {

			}
		};
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent touchEvent) {
		if (shootCount < 0) {
			return false;
		}
		if (this.physicsWorld != null && !initiated) {
			initiated = true;
			if (touchEvent.getAction() == MotionEvent.ACTION_DOWN) {
				this.runOnUpdateThread(new Runnable() {
					@Override
					public void run() {

						AngryNerds.this.mEngine.getScene()
								.registerUpdateHandler(
										AngryNerds.this.physicsWorld);
					}
				});
				return true;
			}
		}

		else if (touchEvent.getX() < 170 && touchEvent.getY() > 300
				&& shootCount > 0) {
			int action = touchEvent.getAction();
			if (action == TouchEvent.ACTION_DOWN) {
				startX = (int) touchEvent.getMotionEvent().getX();
				startY = (int) touchEvent.getMotionEvent().getY();
				return true;
			} else if (action == TouchEvent.ACTION_UP) {
				float xDiff = (int) (startX - touchEvent.getMotionEvent()
						.getX());
				float yDiff = (int) (startY - touchEvent.getMotionEvent()
						.getY());
				can.applyLinearImpulse(new Vector2(2 * xDiff, yDiff),
						new Vector2(can.getPosition().x, can.getPosition().y));
				if (shootCount > 1) {
					this.runOnUpdateThread(new Runnable() {

						@Override
						public void run() {
							can = add(pScene, joltRegion, 50, 300, 0);
							shootCount--;
						}
					});
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onLoadComplete() {
	}

	private Body add(Scene scene, TextureRegion region, float xTranslate,
			float yTranslate, float rotation) {
		return add(scene, region, xTranslate, yTranslate, rotation,
				BodyType.DynamicBody);
	}

	private Body add(Scene scene, TextureRegion region, float xTranslate,
			float yTranslate, float rotation, BodyType bodyType) {
		Sprite sprite = new Sprite(xTranslate, yTranslate, region.clone());
		sprite.setRotation(rotation);
		sprite.setUpdatePhysics(false);
		scene.getTopLayer().addEntity(sprite);
		Body body = PhysicsFactory.createBoxBody(physicsWorld, sprite,
				bodyType, fixtureDefinition);

		if (region == bossRegion) {
			bosses.put(body, sprite);
		}

		if (region == joltRegion) {
			scene.registerTouchArea(sprite);
			scene.setTouchAreaBindingEnabled(true);
		}

		this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite,
				body, true, true, false, false));
		return body;
	}

}
