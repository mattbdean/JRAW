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
    _underline_exceptions = [
        'needs/captcha',
        'new/captcha',
        'delete/update',
        'strike/update',
        'accept/contributor/invite',
        'close/thread',
        'invite/contributor',
        'leave/contributor',
        'rm/contributor/invite',
        'rm/contributor',
        'set/contributor/permissions',
        '/sr/banner',
        '/sr/icon',
        '/sr/img',
        '/sr/header',
        'site/admin',
        'subreddit/stylesheet',
        '/mute/message/author',
        'ignore/reports',
        'set/contest/mode',
        'set/subreddit/sticky',
        'set/suggested/sort',
        'unignore/reports',
        'accept/contributor/invite',
        'accept/moderator/invite',
        'read/all/messages',
        'unblock/subreddit',
        'read/message',
        'search/reddit/names',
        'subreddits/by/topic',
        'by/id',
        'saved/categories',
        'store/visits',
        'submit/text',
        'unblock/subreddit'
    ]

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
                descriptor = self._format_href(endpoint.get('href'))
                endpointlist.append(descriptor)
            
            sections[scope] = endpointlist
        return sections

    def _format_href(self, href):
        descriptor = href[1:].replace('_', ' /', 1).replace('_', '/')
        for ex in self._underline_exceptions:
            if ex in descriptor:
                descriptor = descriptor.replace(ex, ex.replace('/', '_'))
        return descriptor

if __name__ == "__main__":
    print(json.dumps(EndpointIdentifier().find(), indent=4, sort_keys=True))

