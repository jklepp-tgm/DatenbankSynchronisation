__author__ = 'Jakob Klepp'

import argparse

def parse(argv):
    parser = argparse.ArgumentParser()  # TODO maybe a description
    parser.add_argument('--port', type=int, default=9876)
    parser.add_argument('--host', type=str, default='0.0.0.0')
    parser.add_argument('--debug', type=bool, default=False)

    args, unknown = parser.parse_known_args()

    return args
