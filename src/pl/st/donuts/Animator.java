package pl.st.donuts;

public class Animator implements Runnable {
	private Thread thread;
	private boolean running = false;
	
	private int ups;
	
	private Runnable update;
	private Runnable render;
	private Runnable setUp;
	private Runnable tearDown;
	
	private Animator() {}
	
	public synchronized void start() {
		if (running) return;

		running = true;
		thread = new Thread(this, "Animator");
		thread.start();
	}

	public synchronized void stop() {
		if (!running) return;

		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		long lastTimeUpdate = System.nanoTime();
		final long updateInterval = 1_000_000_000 / ups;
		long updateDelta = 0;
		long updateNow = 0;
		
		setUp.run();
		try {
			while (running) {
				if (update!= null) {
					updateNow = System.nanoTime();
					updateDelta += (updateNow - lastTimeUpdate);
					lastTimeUpdate = updateNow;
					while (updateDelta >= updateInterval) {
						update.run();
						updateDelta -= updateInterval;
					}
				}
				
				render.run();
				
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tearDown.run();
	}
	
	public static class Builder {
		private Runnable update;
		private Runnable render;
		private Runnable setUp = ()->{};
		private Runnable tearDown = ()->{};
		private int ups = 30;
		
		public Builder(Runnable render) {
			this.render = render;
		}
		
		public Animator build() {
			Animator ugr = new Animator();
			ugr.render = this.render;
			ugr.update = this.update;
			ugr.setUp = this.setUp;
			ugr.tearDown = this.tearDown;
			ugr.ups = ups;
			return ugr;
		}
		
		public Builder update(Runnable update) {
			this.update = update;
			return this;
		}
		public Builder setUp(Runnable setUp) {
			this.setUp = setUp;
			return this;
		}
		public Builder tearDown(Runnable tearDown) {
			this.tearDown = tearDown;
			return this;
		}

		public Builder updatesPerSecond(int ups) {
			if (ups > 0) {
				this.ups = ups;
			}
			return this;
		}
	}
}
