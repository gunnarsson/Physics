package nu.epsilon.physics;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Example2 extends Example1 implements IAccelerometerListener {

	protected PhysicsWorld physicsWorld;
	protected FixtureDef fixtureDefinition = PhysicsFactory.createFixtureDef(10, 0.5f, 0.5f);

	@Override
	public void onLoadResources() {
		super.onLoadResources();
		this.enableAccelerometerSensor(this);
	}

	@Override
	public Scene onLoadScene() {
		Scene scene = super.onLoadScene();
		this.physicsWorld = new PhysicsWorld(new Vector2(0, 2 * SensorManager.GRAVITY_EARTH), false);

		for (TiledSprite sprite : sprites) {
			Body body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.DynamicBody, fixtureDefinition);
			sprite.setUpdatePhysics(false);
			this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true, false, false));
		}

		scene.registerUpdateHandler(this.physicsWorld);
		return scene;
	}

	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		this.physicsWorld.setGravity(new Vector2(4 * pAccelerometerData.getY(), 4 * pAccelerometerData.getX()));
	}

}
