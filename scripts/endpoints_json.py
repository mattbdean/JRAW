#!/usr/bin/env python3

import lxml.html
from lxml.cssselect import CSSSelector
import requests
import json

class EndpointIdentifier:
    _page = 'https://www.reddit.com/dev/api/oauth'
    _no_scope = '(any scope)'
    _headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
        }

    def __init__(self):
        pass

    def find(self):
        page = requests.get(self._page, headers=self._headers)
        if page.status_code != 200:
            print("Bad status code:", page.status_code)
            from sys import exit
            exit(1)

        tree = lxml.html.fromstring(page.text)

        sel = CSSSelector('div[class="toc"] > ul > li > ul > li')
        results = sel(tree)
        
        sections = {}
        for result in results:
            scope = result.find('a').text_content()
            if not scope:
                scope = self._no_scope
            
            endpointlist = []
            endpoints = result.cssselect('li > a')
            for endpoint in endpoints[1:]:
                descriptor = endpoint.get('href')[1:].replace('_', ' /', 1).replace('_', '/')
                endpointlist.append(descriptor)
            
            sections[scope] = endpointlist
        return sections

if __name__ == "__main__":
    print(json.dumps(EndpointIdentifier().find(), indent=4, sort_keys=True))

