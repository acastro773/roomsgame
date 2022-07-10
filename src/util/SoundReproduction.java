package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.ReverbType;
import javax.sound.sampled.FloatControl;

import characters.active.ActiveCharacter;
import map.Map;
import net.slashie.util.Util;

public class SoundReproduction implements Runnable {
	
	private File audiofile;
	private Thread t;
	static SoundReproduction sound;
	private String typeSound;
	static ActiveCharacter user = null;
	static ActiveCharacter origin = null;
	static ActiveCharacter player = null;
	double distanceToPlayer = 0.0;
	static Map map;
	int BUFFER_SIZE = 4096;
	
	public SoundReproduction(String pathFile) {
		pathFile = pathFile.replace("\\", "/");
		this.audiofile = new File(pathFile);
		String fileName = this.audiofile.getName();
		this.typeSound = fileName.substring(0, fileName.lastIndexOf("."));
	}
	public SoundReproduction(String pathFile, ActiveCharacter origin, ActiveCharacter player) {
		pathFile = pathFile.replace("\\", "/");
		this.audiofile = new File(pathFile);
		String fileName = this.audiofile.getName();
		this.typeSound = fileName.substring(0, fileName.lastIndexOf("."));
		this.origin = origin;
		this.player = player;
	}
	public void reproduce() {
		t = new Thread(this, "thread");
		t.start();
	}
	
	static void RelativeDistance(SourceDataLine audioLine, FloatControl pan, FloatControl volume) {
		Tuple<Integer, Integer> pos_or = origin.getPosition();
		Tuple<Integer, Integer> pos_pl = player.getPosition();
		int lim_room = origin.getRoom().getCorners().get(1).x - origin.getRoom().getCorners().get(0).x;
		//for (Tuple<?, ?> corner : origin.getRoom().getCorners())
		//	System.out.println(corner.x);
		float range = volume.getMaximum() - volume.getMinimum();
		double dist = pos_or.x - pos_pl.x;
		float vol = (float)(dist/lim_room);
		if (vol > 1)
			vol = 1;
		else if (vol < -1)
			vol = -1;
		if (dist == 0)
			dist = pos_or.y - pos_pl.y;
		float vol_c = (range - (float)Math.abs(dist) * volume.getMaximum());
		float gain = vol_c + volume.getMinimum();
		if (gain > volume.getMaximum())
			gain = volume.getMaximum();
		else if (gain < volume.getMinimum())
			gain = volume.getMinimum();
		pan.setValue(vol);
		volume.setValue(gain);
		//System.out.println("Panning: " + pan.getValue());
		//System.out.println("Volume (dB): " + volume.getValue());
	}
	
	static void WaterdropRand(SourceDataLine audioLine, FloatControl pan, FloatControl volume) {
		float range = volume.getMaximum() - volume.getMinimum();
		double dist = Util.rand(-3, 3);
		System.out.println("VolMax: " + volume.getMaximum());
		System.out.println("VolMin: " + volume.getMinimum());
		System.out.println("Dist: " + dist);
		float vol = (float)(dist/3);
		if (vol > 1)
			vol = 1;
		else if (vol < -1)
			vol = -1;
		float vol_c = (range - (float)Math.abs(dist) * volume.getMaximum());
		System.out.println("VolC: " + vol_c);
		float gain = vol_c + volume.getMinimum();
		if (gain > volume.getMaximum())
			gain = volume.getMaximum();
		else if (gain < volume.getMinimum())
			gain = volume.getMinimum();
		pan.setValue(vol);
		volume.setValue(gain);
		System.out.println("Panning: " + pan.getValue());
		System.out.println("Volume (dB): " + volume.getValue());
	}
	@Override
	public void run() {
		AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(this.audiofile);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		AudioFormat format = audioStream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine audioLine = null;
		try {
			audioLine = (SourceDataLine) AudioSystem.getLine(info);
			audioLine.open(format);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		FloatControl pan = null;
		FloatControl volume = null;
		if (audioLine.isControlSupported(FloatControl.Type.PAN))
			pan = (FloatControl) audioLine.getControl(FloatControl.Type.PAN);
		if (audioLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
			volume = (FloatControl) audioLine.getControl(FloatControl.Type.MASTER_GAIN);
		if ((typeSound.equals("ratappear")) || (typeSound.equals("goblinappear")) || (typeSound.equals("dragonappear"))) {
			RelativeDistance(audioLine, pan, volume);
		} else if (typeSound.equals("waterdrop")) {
			WaterdropRand(audioLine, pan, volume);
		}
		audioLine.start();
		 
		byte[] bytesBuffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		 
		try {
			while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
			    audioLine.write(bytesBuffer, 0, bytesRead);
			}
			audioLine.drain();
		    audioLine.close();
			audioStream.close();
			Thread.sleep(50);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
