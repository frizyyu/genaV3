# -*- coding: utf-8-*-
import torch


def tts(tts, lang, path, speaker_wav_path, text):
    with torch.no_grad():
        tts.tts_to_file(text=text, language=lang, speaker_wav=speaker_wav_path, file_path=path)
    return path

