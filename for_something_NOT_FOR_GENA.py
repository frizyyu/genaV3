import wave


with wave.open('F:/out.wav', 'rb') as wav_file:
    n_channels = wav_file.getnchannels()
    sample_width = wav_file.getsampwidth()
    frame_rate = wav_file.getframerate()

print(n_channels, sample_width, frame_rate)
