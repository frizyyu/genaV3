#активациа по алиасу сделана на джаве

'''import json
import sys
import wave
from langdetect import detect_langs
from vosk import Model, KaldiRecognizer, SetLogLevel

# из возможных минусов - англо-русский текст будет распознан кусками, сначала в вывод поступит русский, затем английский

# посмотреть время выполнения. если быстро, то можно и так оставить, если медленно, то детектить только русский текст,
# затем определять тип команды, затем детектить анл текст (в джаве разбить на несколько вызовов: ру текст, детект типа,
# англ текст), либо многопоточка(затратно по ресурсам)
def main():
    # отключение логов, чтобы джава правильно восприняла
    SetLogLevel(-1)

    model = Model(sys.argv[1])

    # Открытие аудиофайла
    wf = wave.open(sys.argv[2], "rb")

    # Создание распознавателя
    recognizer_ru = KaldiRecognizer(model, wf.getframerate())

    results = []

    # обработка русской части
    while True:
        data = wf.readframes(4000)
        if len(data) == 0:
            break
        if recognizer_ru.AcceptWaveform(data):
            result = json.loads(recognizer_ru.Result())
            results.append(result)
    results.append(json.loads(recognizer_ru.FinalResult()))
    wf.close()
    print(results[0].get("text"))


if __name__ == "__main__":
    main()'''





'''from pvrecorder import PvRecorder
import pvporcupine as pvporcupine
import sys


def main():
    microId = int(sys.argv[1])
    aliasAIToken = sys.argv[2]
    porcupine = pvporcupine.create(
        access_key=aliasAIToken,
        keywords=['alexa'],
        sensitivities=[1]
    )
    recorder = PvRecorder(device_index=microId, frame_length=porcupine.frame_length)
    recorder.start()
    while True:
        if listen(recorder, porcupine):
            break


def listen(recorder, porcupine):
    pcm = recorder.read()
    keyword_index = porcupine.process(pcm)
    if keyword_index >= 0:
        print("Hear")
        return True


if __name__ == "__main__":
    main()'''
