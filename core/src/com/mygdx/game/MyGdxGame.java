package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.*;
import java.security.Key;
import java.util.Iterator;


public class MyGdxGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	BitmapFont font;
	int score = 0;
	Boolean isRunning;
	double scale = 1.0;

	int gravity = -100;
	
	@Override
	public void create () {
		dropImage = new Texture(Gdx.files.internal("watersprite.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false,800,480);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 64+20;
		bucket.y = 480/2-64/2;
		bucket.width = 64;
		bucket.height = 64;
		raindrops = new Array<Rectangle>();
		font = new BitmapFont();
		isRunning = true;
		spawnRaindrop();
	}

	@Override
	public void render () {
		ScreenUtils.clear(117.0F/256.0F, 172.0F/256.0F, 245.0F/256.0F, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		if(isRunning) {
			batch.begin();
			batch.draw(bucketImage, bucket.x, bucket.y);
			for (Rectangle raindrop : raindrops) {
				batch.draw(dropImage, raindrop.x, raindrop.y);
			}
			font.draw(batch, "Score: " + score, 5, 470);
			batch.end();
			if (Gdx.input.isTouched()) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos);
				//bucket.y = touchPos.y - 64 / 2;
				//bucket.y += 200 * Gdx.graphics.getDeltaTime();
				//camera.unproject(touch)
			}

			if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			{
				gravity = (int)(20000 * Gdx.graphics.getDeltaTime());

			}

			if (bucket.y < 0) bucket.y = 0;
			if (bucket.y > 480 - 64) bucket.y = 480 - 64;
			if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
				spawnRaindrop();
			}
			for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
				Rectangle raindrop = iter.next();
				raindrop.x -= (200 * scale) * Gdx.graphics.getDeltaTime();
				if (raindrop.x + 64 < 0) {
					iter.remove();
					score++;
				}
				if (raindrop.overlaps(bucket)) {
					isRunning = false;
				}
			}

			bucket.y += (gravity) * Gdx.graphics.getDeltaTime();

			gravity -= 800 * Gdx.graphics.getDeltaTime();

			if(score != 0 && score % 5 == 0)
			{
				scale += 0.01;
			}
		}
		else
		{
			String winText = "Your Score is: " + score;
			String retryText = "Would you like to try again ? Press Y to retry";
			batch.begin();
			font.draw(batch, winText, 800 / 2 - (winText.length() * 8) / 2, 480 / 2);
			font.draw(batch, retryText, 800 / 2 - (retryText.length() * 7) / 2, 480 / 2 - 20);
			batch.end();

			if (Gdx.input.isKeyPressed(Input.Keys.Y))
			{
				raindrops.clear();
				score = 0;
				scale = 1.0;
				isRunning = true;
				bucket.x = 64+20;
				bucket.y = 480/2-64/2;
				gravity = -100;
				spawnRaindrop();
			}
		}
	}
	
	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		batch.dispose();
	}

	private void spawnRaindrop()
	{
		Rectangle raindrop = new Rectangle();
		raindrop.y = MathUtils.random(240,480-64);
		raindrop.x = 800;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		int bound = (int) raindrop.y;
		for(int y = bound; y < 480; y+=64)
		{
			Rectangle raindropTemp = new Rectangle();
			raindropTemp.y = y;
			raindropTemp.x = 800;
			raindropTemp.width = 64;
			raindropTemp.height = 64;
			raindrops.add(raindropTemp);
		}
		for(int y = bound - (64*4); y > -64; y-=64)
		{
			Rectangle raindropTemp = new Rectangle();
			raindropTemp.y = y;
			raindropTemp.x = 800;
			raindropTemp.width = 64;
			raindropTemp.height = 64;
			raindrops.add(raindropTemp);
		}

		lastDropTime = TimeUtils.nanoTime();
	}
}
