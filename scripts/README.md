# JRAW Scripts

This is a collection of scripts related to the Java Reddit API Wrapper.

#### `endpoints_json.py`

Parses [reddit.com/dev/api/oauth](https://www.reddit.com/dev/api/oauth) and outputs a JSON object  to the standard out where the keys are the OAuth scopes and the values are a list of endpoints under that scope.

Requires Python 3+ to be installed in addition to these modules: [`lxml`](https://pypi.python.org/pypi/lxml), [`requests`](https://pypi.python.org/pypi/requests), and [`cssselect`](https://pypi.python.org/pypi/cssselect).

Usage: `endpoints_json.py`

