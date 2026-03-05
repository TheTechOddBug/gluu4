.DEFAULT_GOAL := develop

develop:
	/usr/bin/env pip install --build-constraint build-constraint.txt -e .

install:
	/usr/bin/env pip install --build-constraint build-constraint.txt .

zipapp:
	shiv --compressed -o pygluu-compose.pyz -p '/usr/bin/env python3' -e pygluu.compose.cli:cli . --no-cache --no-build-isolation
