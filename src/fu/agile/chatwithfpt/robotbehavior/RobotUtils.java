package fu.agile.chatwithfpt.robotbehavior;

import java.util.Random;

import com.fpt.robot.Robot;
import com.fpt.robot.RobotException;
import com.fpt.robot.motion.RobotGesture;
import com.fpt.robot.tts.RobotTextToSpeech;

public class RobotUtils {
	public static enum ROBOT_ACTION_STATUS {
		SUCCESS, ERROR, ROBOT_NOT_FOUND
	}

	public static synchronized void speakWithGesture(final Robot mRobot,
			final String statement) {
		if (mRobot != null)
			new Thread(new Runnable() {

				@Override
				public void run() {
					SpeakThread speakThread = new SpeakThread(mRobot, statement);
					GestureThread gestureThread = new GestureThread(mRobot);

					speakThread.start();
					gestureThread.start();

					try {
						speakThread.join();
					} catch (InterruptedException e) {
						// Do nothing
					}
					gestureThread.stopDoGesture();

				}
			}).start();
	}

	private static class SpeakThread extends Thread {
		private String statement;
		private ROBOT_ACTION_STATUS status;
		private Robot mRobot;

		public SpeakThread(Robot mRobot, String statement) {
			this.statement = statement;
			this.mRobot = mRobot;
		}

		@Override
		public void run() {
			try {
				// say text with VietNamese language
				RobotTextToSpeech.say(mRobot, statement,
						RobotTextToSpeech.ROBOT_TTS_LANG_VI);
				status = ROBOT_ACTION_STATUS.SUCCESS;
			} catch (RobotException e) {
				status = ROBOT_ACTION_STATUS.ERROR;
			}
		}

		public ROBOT_ACTION_STATUS getActionStatus() {
			return status;
		}
	}

	private static class GestureThread extends Thread {
		private volatile boolean isKeepDoGesture;
		private ROBOT_ACTION_STATUS status;
		private Robot mRobot;

		public GestureThread(Robot mRobot) {
			isKeepDoGesture = true;
			this.mRobot = mRobot;
		}

		public void stopDoGesture() {
			isKeepDoGesture = false;
		}

		@Override
		public void run() {
			while (isKeepDoGesture) {
				Random rand = new Random();
				int randomGesNum = rand.nextInt(10) + 1;
				String gesName = "HandMotionBehavior" + randomGesNum;
				try {
					RobotGesture.runGesture(mRobot, gesName);
					status = ROBOT_ACTION_STATUS.SUCCESS;
				} catch (RobotException e) {
					status = ROBOT_ACTION_STATUS.ERROR;
				}
			}

			try {
				// Return to default position
				RobotGesture.runGesture(mRobot, "HandTalkBehavior11");
			} catch (RobotException e) {
				status = ROBOT_ACTION_STATUS.ERROR;
			}
		}

		public ROBOT_ACTION_STATUS getActionStatus() {
			return status;
		}
	}
}
