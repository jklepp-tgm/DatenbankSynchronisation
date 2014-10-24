__author__ = 'Jakob Klepp'

from flask import Flask

def run_web_service(import_name=__name__, host='0.0.0.0', port=9876):
    app = Flask(import_name)
    app.run(host=host, port=port)
