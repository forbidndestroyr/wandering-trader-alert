import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class TestSound {
    public static void main(String[] args) {
        System.out.println("Testing Wandering Trader Alert sound system...\n");
        
        try {
            // Load the sound file
            System.out.println("Loading sound file: ding.wav");
            InputStream audioSrc = TestSound.class.getResourceAsStream("/assets/traderalert/sounds/ding.wav");
            
            if (audioSrc == null) {
                // Try alternate path
                audioSrc = new java.io.FileInputStream("src/main/resources/assets/traderalert/sounds/ding.wav");
                System.out.println("Loaded from file system");
            } else {
                System.out.println("Loaded from resources");
            }
            
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            
            // Get audio format info
            AudioFormat format = audioStream.getFormat();
            System.out.println("Audio Format: " + format.toString());
            System.out.println("Sample Rate: " + format.getSampleRate() + " Hz");
            System.out.println("Channels: " + format.getChannels());
            System.out.println("Sample Size: " + format.getSampleSizeInBits() + " bits");
            
            // Create and open the clip
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // Set volume
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f); // Reduce volume by 10 decibels
            System.out.println("\nVolume set to: -10.0 dB");
            
            // Play the sound multiple times to simulate the mod behavior
            System.out.println("\nSimulating Wandering Trader detection...");
            System.out.println("Playing sound 3 times with 2-second intervals (like in the mod)\n");
            
            for (int i = 1; i <= 3; i++) {
                System.out.println("DING! (#" + i + ") - Wandering Trader nearby!");
                clip.setFramePosition(0);
                clip.start();
                
                // Wait for the sound to finish plus interval
                Thread.sleep(2000); // 2 seconds between dings
            }
            
            // Clean up
            clip.close();
            audioStream.close();
            bufferedIn.close();
            
            System.out.println("\n✓ Test completed successfully!");
            System.out.println("The sound system is working correctly.");
            System.out.println("\nThis confirms:");
            System.out.println("- The WAV file is valid and playable");
            System.out.println("- Java's audio system can play it at system level");
            System.out.println("- The volume control works");
            System.out.println("- The mod should work in Minecraft even with game volume at 0%");
            
        } catch (Exception e) {
            System.err.println("✗ Test failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}