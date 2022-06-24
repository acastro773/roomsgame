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

public class SoundReproduction {
	
	private File audiofile;
	static SoundReproduction walkSound;
	private String typeSound;
	static ActiveCharacter user = null;
	static ActiveCharacter origin = null;
	static ActiveCharacter player = null;
	double distanceToPlayer = 0.0;
	static Map map;
	int BUFFER_SIZE = 4096;
	
	public SoundReproduction(String pathFile) {
		this.audiofile = new File(pathFile);
		String fileName = this.audiofile.getName();
		this.typeSound = fileName.substring(0, fileName.lastIndexOf("."));
	}
	public SoundReproduction(String pathFile, ActiveCharacter origin, ActiveCharacter player) {
		this.audiofile = new File(pathFile);
		String fileName = this.audiofile.getName();
		this.typeSound = fileName.substring(0, fileName.lastIndexOf("."));
		this.origin = origin;
		this.player = player;
	}
	public void reproduce() {
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
		if (typeSound.equals("steps")) {
			RelativeDistance(audioLine);
		}
		audioLine.start();
		 
		byte[] bytesBuffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		 
		try {
			while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
			    audioLine.write(bytesBuffer, 0, bytesRead);
			    audioLine.drain();
			    audioLine.close();
			    //audioStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void RelativeDistance(SourceDataLine audioLine) {
		FloatControl pan = null;
		if (audioLine.isControlSupported(FloatControl.Type.PAN))
			pan = (FloatControl) audioLine.getControl(FloatControl.Type.PAN);
		Tuple<Integer, Integer> pos_or = origin.getPosition();
		Tuple<Integer, Integer> pos_pl = player.getPosition();
		int lim_room = origin.getRoom().getCorners().get(1).x - origin.getRoom().getCorners().get(0).x;
		//for (Tuple<?, ?> corner : origin.getRoom().getCorners())
		//	System.out.println(corner.x);
		double dist = pos_or.x + 4 - pos_pl.x;
		System.out.println(dist);
		System.out.println(lim_room);
		float vol = (float)(dist/lim_room);
		if (vol > 1)
			vol = 1;
		else if (vol < -1)
			vol = -1;
		pan.setValue(vol);
		System.out.println(pan.getValue());
	}
}
