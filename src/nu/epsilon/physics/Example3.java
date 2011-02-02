package nu.epsilon.physics;

import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Example3 extends Example2 {

	@Override
	public Scene onLoadScene() {
		Scene scene = super.onLoadScene();

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		PhysicsFactory.createBoxBody(this.physicsWorld, ground, BodyType.StaticBody, this.fixtureDefinition);
		PhysicsFactory.createBoxBody(this.physicsWorld, roof, BodyType.StaticBody, this.fixtureDefinition);
		PhysicsFactory.createBoxBody(this.physicsWorld, left, BodyType.StaticBody, this.fixtureDefinition);
		PhysicsFactory.createBoxBody(this.physicsWorld, right, BodyType.StaticBody, this.fixtureDefinition);

		scene.getBottomLayer().addEntity(ground);
		scene.getBottomLayer().addEntity(roof);
		scene.getBottomLayer().addEntity(left);
		scene.getBottomLayer().addEntity(right);

		return scene;
	}

}
