package audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;

public class Audio {
    private long audioContext, audioDevice;

    public void init() {
        // Initialize audio device
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        // Set up audio context
        int[] attributes = {0};
        alcCreateContext(audioDevice, attributes);
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        // If audio library isn't supported
        assert !alCapabilities.OpenAL10 : "Audio library not supported.";
    }

    // Destroy audio context
    public void freeMemory() {
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);
    }
}
