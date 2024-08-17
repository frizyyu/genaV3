# -*- coding: utf-8-*-
import re

from words2numsrus import NumberExtractor


class GetArgs:
    def __init__(self, orig_command, nlp):
        self.nlp = nlp
        self.orig_command = orig_command
        self.special_time = {
            "половина": 0.5,
            "полтора": 1.5,
            "пол": 0.5,
            "день": 1
        }
        self.time_units = {
            "секунда": 1,
            "минута": 60,
            "час": 3600,
            "день": 86400,
            "неделя": 604800,
            "месяц": 2592000,
            "год": 31536000,
        }
        self.commands_getters_dict = {
            "say time": self.say_time_getter
        }
        self.presence_pattern = re.compile(r'\b(было|назад|до этого|раньше|минус)\b', re.IGNORECASE)
        self.absence_pattern = re.compile(r'\b(плюс|будет|через|спустя)\b', re.IGNORECASE)

    def choose_current_getter(self, command):
        getter = self.commands_getters_dict.get(command)
        return getter() if getter else None

    def say_time_getter(self):
        self.orig_command = self.orig_command.replace("полчаса", "пол часа").replace(" и ", "|").replace(" а ", "|")
        res = []
        for orig_comand in self.orig_command.split("|"):
            simple_phrase = self.word_to_num(self.simplify_text(orig_comand))
            time, c = 0, 0
            time_int, time_part, time_unit = 0, 0, 0
            for word in simple_phrase.split():
                if word.isdigit():
                    time_int = int(word)
                    if "день" in simple_phrase:
                        time_int -= 1
                    c += 1
                elif word in self.special_time:
                    time_part = self.special_time[word]
                    c += 1
                elif word in self.time_units:
                    time_unit = self.time_units[word]
                    c += 1
                if c == 3:
                    break
            time = time_int + time_part
            if self.check_is_last_time(orig_comand):
                time *= -1
            res.append(int(time * time_unit))
        return res

    def check_is_last_time(self, command):
        has_presence_words = self.presence_pattern.search(command) is not None
        has_absence_words = self.absence_pattern.search(command) is not None
        return has_presence_words and not has_absence_words

    def word_to_num(self, phrase):
        extractor = NumberExtractor()
        try:
            return extractor.replace_groups(phrase.lower())
        except ValueError:
            return None

    def simplify_text(self, command):
        doc = self.nlp(command)
        simplified = []
        for sent in doc.sents:
            words = [token.lemma_ for token in sent if not token.is_stop and not token.is_punct]
            simplified.append(" ".join(words))
        return " ".join(simplified)
