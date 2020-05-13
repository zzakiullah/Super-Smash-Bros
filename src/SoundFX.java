import javax.sound.sampled.*;
import java.io.File;

class SoundFX {
	public static final int LOOP_CONTINUOUSLY = Clip.LOOP_CONTINUOUSLY, SOUND = 50, MUSIC = 60;
	private double volume;
	private Clip clip;				// clip that opens sound file
	private FloatControl volCtrl;	// controls volume of clip
	private int type;

	public SoundFX(String dir) {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(dir)));
			volCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			volume = convertToDB(0.5);
		}
		catch(Exception ex) {
			System.err.println(ex+": "+dir);
		}
	}

	public SoundFX(String dir, double v) {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(dir)));
			volCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			volume = convertToDB(v);
		}
		catch(Exception ex) {
			System.err.println(ex+": "+dir);
		}
	}

	public void setType(int t) {type = t;}

	public void play(int n) {
		try {
			volCtrl.setValue((float) volume);
			clip.setMicrosecondPosition(0);
			clip.start();
			clip.loop(n);
		}
		catch(Exception ex) {
			System.err.println(ex);
		}
	}
	public void resume(int n) {
		try {
			volCtrl.setValue((float) volume);
			clip.start();
			clip.loop(n);
		}
		catch(Exception ex) {
			System.err.println(ex);
		}
	}
	public void setVolume(double v) {
		boolean wasRunning = clip.isRunning();
		stop();
		volume = convertToDB((v <= volCtrl.getMaximum()) ? ((v >= volCtrl.getMinimum()) ? v : volume) : volume);
		if (wasRunning) {resume(SoundFX.LOOP_CONTINUOUSLY);}
	}
	public void stop() {if (clip.isRunning()) {clip.stop();}}
	public void close() {clip.close();}

	// convertToDB - takes volume on a linear scale (%) and converts it to decibels on a logarithmic scale
	public double convertToDB(double v) {
		// formula for % to dB retrieved from http://www.java2s.com/Code/Java/Development-Class/SettingtheVolumeofaSampledAudioPlayer.htm
		if (v <= volCtrl.getMaximum() && v >= volCtrl.getMinimum()) {
			return (float) (Math.log(v) / Math.log(10.0) * 20.0);
		}
		else {
			return 0.0;
		}

	}

	// fadeout - fades music out as screen leaves
	public void fadeout() {

	}
	// fadein - fades music in as screen enters
	public void fadein() {

	}
}
