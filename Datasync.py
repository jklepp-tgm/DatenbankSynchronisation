__author__ = 'Jakob Klepp'

import sys

import datasync

def run_web_service(host, port, debug):
    web = datasync.Webservice(__name__, host, port)
    web.run_web_service(debug)

if __name__ == '__main__':
    args = datasync.parse(sys.argv)
    run_web_service(args.host, args.port, args.debug)
