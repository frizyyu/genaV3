# -*- coding: utf-8-*-

from choose_command_getter import get_args


def choose(vectorizer, knn, nlp, orig_command):
    future_text = vectorizer.transform([orig_command])
    command = knn.predict(future_text)[0]
    get_args_class = get_args.GetArgs(orig_command, nlp)
    args = get_args_class.choose_current_getter(command)
    return f"{command}|{args}"