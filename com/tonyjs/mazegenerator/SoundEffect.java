package com.tonyjs.mazegenerator;

import java.io.*;
import javax.sound.sampled.*;

public class SoundEffect {
	private static File wallFile;
	private static Clip songClip;
	final static String sep = java.io.File.separator;

	public static void playWallEffect() {
		wallFile = new File("sounds" + sep + "wall.wav");
		playSongFile(wallFile);
	}

	public static Clip playSongFile(File song) {
		try {
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;

			stream = AudioSystem.getAudioInputStream(song);
			format = stream.getFormat();
			info = new DataLine.Info(Clip.class, format);
			songClip = (Clip) AudioSystem.getLine(info);
			songClip.open(stream);
			if (songClip.isRunning()) {
				songClip.stop();
			} else {
				songClip.start();
			}
		}
		catch (Exception e) {
			System.out.println("Could not load song file");
		}
		return songClip;
	}
}
