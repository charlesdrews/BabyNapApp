package com.charlesdrews.babynapapp;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import java.util.Random;

/**
 * Created by charlie on 8/6/17.
 */

public class WhiteNoiseGenerator {
    private static final int SAMPLE_RATE = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int ENCODING_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioTrack.getMinBufferSize(
            SAMPLE_RATE, CHANNEL_CONFIG, ENCODING_FORMAT);
    private static final float VOLUME_INCREMENT_UP = 0.02f;
    private static final float VOLUME_INCREMENT_DOWN = 0.05f;

    private volatile boolean playing = false;
    private AudioTrack audioTrack;

    public void play() {
        playing = true;

        new Thread(() -> {
            float volume = 0;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioTrack = new AudioTrack.Builder()
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build())
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(ENCODING_FORMAT)
                                .setSampleRate(SAMPLE_RATE)
                                .setChannelMask(CHANNEL_CONFIG)
                                .build())
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .setBufferSizeInBytes(BUFFER_SIZE)
                        .build();

                audioTrack.setVolume(volume);
            } else {
                audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE,
                        CHANNEL_CONFIG,
                        ENCODING_FORMAT,
                        BUFFER_SIZE,
                        AudioTrack.MODE_STREAM);

                audioTrack.setStereoVolume(volume, volume);
            }

            short[] sample = new short[BUFFER_SIZE];
            Random random = new Random();

            audioTrack.play();

            while (playing || volume > 0) {
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    sample[i] = (short) (random.nextGaussian() * Short.MAX_VALUE);
                }
                audioTrack.write(sample, 0, sample.length);

                boolean volumeChanged = false;

                // fade in
                if (playing && volume < 1) {
                    volume += VOLUME_INCREMENT_UP;
                    volumeChanged = true;
                }

                // fade out
                if (!playing) {
                    volume -= VOLUME_INCREMENT_DOWN;
                    volumeChanged = true;
                }

                if (volumeChanged) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        audioTrack.setVolume(volume);
                    } else {
                        audioTrack.setStereoVolume(volume, volume);
                    }
                }
            }

            audioTrack.stop();
            audioTrack.release();
        }).start();
    }

    public void stop() {
        playing = false;
    }
}
