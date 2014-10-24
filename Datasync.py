__author__ = 'Jakob Klepp'

import sys

import datasync

def run_web_service(host, port):
    datasync.run_web_service(__name__, host, port)

if __name__ == '__main__':
    args = datasync.parse(sys.argv)
    run_web_service(args.host, args.port)