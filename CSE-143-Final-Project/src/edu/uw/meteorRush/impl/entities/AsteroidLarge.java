package edu.uw.meteorRush.impl.entities;

import java.awt.Graphics;
import java.awt.Image;

import edu.uw.meteorRush.common.Entity;
import edu.uw.meteorRush.common.Game;
import edu.uw.meteorRush.common.ResourceLoader;
import edu.uw.meteorRush.common.Vector2;
import edu.uw.meteorRush.impl.Main;
import edu.uw.meteorRush.impl.scenes.GameScene;

public class AsteroidLarge extends Entity implements DamagableEntity {

	private static final double BASE_DAMAGE_AMOUNT = 3;
	private static final double MAX_HEALTH = 5;
	private static final double BASE_SPEED = 300;
	private static final int BASE_SCORE_VALUE = 50;
	private static final int WIDTH = 200;
	private static final int HEIGHT = 200;
	public static final Image SPRITE_1 = ResourceLoader.loadImage("res/images/entities/asteroids/AsteroidLarge1.png")
			.getScaledInstance(WIDTH, HEIGHT, 0);
	public static final Image SPRITE_2 = ResourceLoader.loadImage("res/images/entities/asteroids/AsteroidLarge2.png")
			.getScaledInstance(WIDTH, HEIGHT, 0);
	public static final Image SPRITE_3 = ResourceLoader.loadImage("res/images/entities/asteroids/AsteroidLarge3.png")
			.getScaledInstance(WIDTH, HEIGHT, 0);

	private Vector2 velocity;
	private double currentHealth;

	public AsteroidLarge(Vector2 position, Vector2 direction) {
		super(position, new Vector2(WIDTH, HEIGHT));
		double difficultyModifier = Main.difficulty.getModifier();
		this.velocity = direction.clone().normalize().multiply(BASE_SPEED * difficultyModifier);
		currentHealth = MAX_HEALTH;
	}

	@Override
	public void tick() {
		Vector2 position = getPosition();
		position.add(velocity.clone().multiply(Game.getInstance().getDeltaTime()));
		setPosition(position);
	}

	@Override
	public void render(Graphics g) {
		Vector2 position = getPosition();
		Image sprite;
		if (currentHealth < 3) {
			sprite = SPRITE_3;
		} else if (currentHealth < 5) {
			sprite = SPRITE_2;
		} else {
			sprite = SPRITE_1;
		}
		g.drawImage(sprite, (int) position.getX() - WIDTH / 2, (int) position.getY() - HEIGHT / 2, null);
	}

	@Override
	public void onCollisionEnter(Entity other) {
		if (other instanceof PlayerShip) {
			((PlayerShip) other).damage(BASE_DAMAGE_AMOUNT * Main.difficulty.getModifier());
			ResourceLoader.loadAudioClip("res/audio/Explosion.wav").start();
			Game.getInstance().getOpenScene().removeObject(this);
		}
	}

	@Override
	public void onCollisionExit(Entity other) {
	}

	@Override
	public void damage(double amount) {
		currentHealth -= amount;
		if (currentHealth <= 0) {
			destroy();
		} else {
			Explosion explosion = new Explosion(getPosition(), new Vector2(100, 100), 0.1);
			Game.getInstance().getOpenScene().addObject(explosion);
		}
	}

	private void destroy() {
		ResourceLoader.loadAudioClip("res/audio/AsteroidHit.wav").start();
		GameScene scene = (GameScene) Game.getInstance().getOpenScene();
		scene.addScore((int) (BASE_SCORE_VALUE * Main.difficulty.getModifier()));
		Explosion explosion = new Explosion(getPosition(), new Vector2(200, 200), 0.1);
		scene.addObject(explosion);
		scene.removeObject(this);
		Vector2 position = getPosition();
		scene.addObject(new AsteroidSmall(position, velocity));
		velocity.rotate(-Math.toRadians(30));
		scene.addObject(new AsteroidSmall(position, velocity));
		velocity.rotate(Math.toRadians(60));
		scene.addObject(new AsteroidSmall(position, velocity));
	}

}
