# -*- coding: utf-8-*-
import os
import sys
sys.path.append(os.path.abspath("F:/pycharm projects/AIforGena/command decoder"))
sys.path.append(os.path.abspath("F:/pycharm projects/AIforGena/command chooser"))
sys.path.append(os.path.abspath("F:/pycharm projects/AIforGena/text to speech"))
from preload import preload_instance
from command_decoder import decode
from command_chooser import choose
from text_to_speech import tts


def main():
    os.environ['PYTHONIOENCODING'] = 'utf-8'
    print("READY")
    sys.stdout.flush()

    for line in sys.stdin:
        request = line.strip().split("|")
        command = request[0]
        args = request[1::]
        if command == "exit":
            break
        elif command == "load_models":
            preload_instance.load_models(args[0], args[1], args[2], args[3], args[4], args[5])
            response = f"Processed command for {command}"
        elif command == "command_decoder":
            response = decode(preload_instance.get_model("cmd_decoder_model_ru"), args[0])
        elif command == "get_command":
            response = choose(preload_instance.get_model("vectorizer"),
                              preload_instance.get_model("knn"),
                              preload_instance.get_model("nlp"),
                              args[0])
        elif command == "tts":
            response = tts(preload_instance.get_model("tts_model"), args[0], args[1], args[2], args[3])
        else:
            response = "ответ от сервера: неизвестная команда"
        print(response)
        print("success")
        sys.stdout.flush()


if __name__ == "__main__":
    main()
