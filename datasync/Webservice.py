__author__ = 'Jakob Klepp'

from flask import Flask

class Webservice:

    def __init__(self, import_name=__name__, host='0.0.0.0', port=9876):
        app = Flask(import_name)
        self.host = host
        self.port = port

        @app.route("/")
        def hello():
            return "Hello World!"

        self.app = app

    def run_web_service(self, debug=False):
        self.app.run(host=self.host, port=self.port, debug=debug)
