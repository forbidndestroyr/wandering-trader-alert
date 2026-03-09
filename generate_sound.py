import wave
import struct
import math

# Parameters for the sound
sample_rate = 44100  # Hz
duration = 0.5  # seconds
frequency = 800  # Hz (bell-like frequency)
frequency2 = 1200  # Hz (second harmonic)
volume = 0.3

# Generate samples
num_samples = int(sample_rate * duration)
samples = []

for i in range(num_samples):
    t = float(i) / sample_rate
    
    # Create a bell-like sound with two harmonics and exponential decay
    decay = math.exp(-3.0 * t)  # Exponential decay
    
    # Mix two frequencies for a more bell-like sound
    sample = decay * (
        0.7 * math.sin(2 * math.pi * frequency * t) +
        0.3 * math.sin(2 * math.pi * frequency2 * t)
    )
    
    # Scale to 16-bit integer range
    sample = int(sample * volume * 32767)
    samples.append(struct.pack('<h', sample))

# Write WAV file
output_path = "src/main/resources/assets/traderalert/sounds/ding.wav"
with wave.open(output_path, 'wb') as wav_file:
    wav_file.setnchannels(1)  # Mono
    wav_file.setsampwidth(2)  # 2 bytes per sample (16-bit)
    wav_file.setframerate(sample_rate)
    wav_file.writeframes(b''.join(samples))

print(f"Generated ding.wav at {output_path}")
print(f"Duration: {duration}s, Frequency: {frequency}Hz/{frequency2}Hz")