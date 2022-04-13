package sirius;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {
    private int bufferId;
    private int sourceId;
    private final String FILE_PATH;
    private boolean playing;

    public Sound(String filePath, boolean doesLoop) {
        this.FILE_PATH = filePath;
        loadAudioFile(doesLoop);
    }

    private void loadAudioFile(boolean doesLoop) {
        // Allocate space to store the return information from stb
        stackPush(); // Ensures that we don't have any memory leaks -- When we do stackPop(); it
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        // Load our stuff
        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(FILE_PATH, channelsBuffer, sampleRateBuffer);

        boolean succeed = rawAudioBuffer != null;
        if (!succeed) {
            System.err.println("Couldn't load sound '" + FILE_PATH + "'.");
            stackPop();
            stackPop();
            return;
        }

        // Retrieve the extra information that was stores in the buffers by stb
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        // Free
        stackPop();
        stackPop();

        // Find the correct OpenAL format
        int format = -1;
        if (channels == 1) format = AL_FORMAT_MONO16;
        else if (channels == 2) format = AL_FORMAT_STEREO16;

        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        // Generate the source
        sourceId = alGenSources();

        // Set source parameters
        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_LOOPING, doesLoop ? 1 : 0);
        alSourcei(sourceId, AL_POSITION, 0);
        alSourcef(sourceId, AL_GAIN, 0.3f);

        // Free stb raw audio buffer
        free(rawAudioBuffer);
    }

    public void delete() {
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }

    /**
     * Plays a sound if the sound isn't already playing.
     */
    public void play() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            playing = false;
            // Reset into the beginning if the sound is stopped
            alSourcei(sourceId, AL_POSITION, 0);
        }

        if (!playing) {
            alSourcePlay(sourceId);
            playing = true;
        }
    }

    /**
     * Stops a sound.
     */
    public void stop() {
        if (playing) {
            alSourceStop(sourceId);
            // alSourcei(sourceId, AL_POSITION, 0);
            playing = false;
        }
    }

    public String getFilePath() {
        return FILE_PATH;
    }

    /**
     * Checks if a sound is playing. If it is a sound that was supposed to alredy stopped and
     * it is still playing, we will stop it too.
     * @return if the sound is playing
     */
    public boolean isPlaying() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) playing = false;

        return playing;
    }
}
