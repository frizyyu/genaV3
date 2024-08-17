# -*- coding: utf-8-*-
import json
import os
import sys
import pickle
import wave
from langdetect import detect_langs
from vosk import Model, KaldiRecognizer, SetLogLevel

sys.path.append(os.path.abspath("F:/pycharm projects/AIforGena"))


# из возможных минусов - англо-русский текст будет распознан кусками, сначала в вывод поступит русский, затем английский

# посмотреть время выполнения. если быстро, то можно и так оставить, если медленно, то детектить только русский текст,
# затем определять тип команды, затем детектить анл текст (в джаве разбить на несколько вызовов: ру текст, детект типа,
# англ текст), либо многопоточка(затратно по ресурсам)
def decode(model, file_path):
    # отключение логов, чтобы джава правильно восприняла
    SetLogLevel(-1)
    model_ru = model
    #model_en = Model(sys.argv[2])

    wf = wave.open(file_path, "rb")

    recognizer_ru = KaldiRecognizer(model_ru, wf.getframerate())
    #recognizer_en = KaldiRecognizer(model_en, wf.getframerate())

    results_ru = []
    #results_en = []

    # обработка русской части
    while True:
        data = wf.readframes(4000)
        if len(data) == 0:
            break
        if recognizer_ru.AcceptWaveform(data):
            result = json.loads(recognizer_ru.Result())
            results_ru.append(result)
    results_ru.append(json.loads(recognizer_ru.FinalResult()))

    # Сброс положения в файле
    '''wf.rewind()

    # Обработка англ части
    while True:
        data = wf.readframes(4000)
        if len(data) == 0:
            break
        if recognizer_en.AcceptWaveform(data):
            result = json.loads(recognizer_en.Result())
            results_en.append(result)
    results_en.append(json.loads(recognizer_en.FinalResult()))'''

    #final_result = results_ru[0].get("text") + " | " + results_en[0].get("text")
    final_result = results_ru[0].get("text")
    wf.close()
    return final_result
