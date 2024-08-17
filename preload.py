# -*- coding: utf-8-*-
import joblib
import spacy
import torch
from TTS.api import TTS
from vosk import Model


class Preload:
    def __init__(self):
        self.models = {}

    def load_models(self, tts_model_path, tts_model_name, cmd_decoder_model_path, vectorizer_path, knn_path, nlp_model_name):
        device = "cuda" if torch.cuda.is_available() else "cpu"
        self.models['tts_model'] = TTS(tts_model_name).to(device)
        self.models['cmd_decoder_model_ru'] = Model(cmd_decoder_model_path)
        self.models['vectorizer'] = joblib.load(vectorizer_path)
        self.models['knn'] = joblib.load(knn_path)
        self.models['nlp'] = spacy.load(nlp_model_name)

    def save_models(self):
        joblib.dump(self.models['tts_model'], 'tts_model.joblib')
        joblib.dump(self.models['cmd_decoder_model_ru'], 'cmd_decoder_model_ru.joblib')
        joblib.dump(self.models['vectorizer'], 'vectorizer.joblib')
        joblib.dump(self.models['knn'], 'knn.joblib')
        joblib.dump(self.models['nlp'], 'nlp.joblib')

    def load_saved_models(self):
        self.models['tts_model'] = joblib.load('tts_model.joblib')
        self.models['cmd_decoder_model_ru'] = joblib.load('cmd_decoder_model_ru.joblib')
        self.models['vectorizer'] = joblib.load('vectorizer.joblib')
        self.models['knn'] = joblib.load('knn.joblib')
        self.models['nlp'] = joblib.load('nlp.joblib')

    def get_model(self, model_name):
        return self.models.get(model_name)


preload_instance = Preload()  # Singleton pattern
