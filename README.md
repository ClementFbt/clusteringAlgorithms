# Algorithmes de clustering

<b>Girvan-Newman :</b>

* python girvan-newman.py <fichier .txt du graph>
* python jsonconvert.py <ouput .txt de girvan-newman.py>

<b>NodeTrix :</b>
* python jsonconvertToNodetrix.py <un des output .json de jsonconvert.py> <graph initial>
* cloner [NodeTrix](https://github.com/IRT-SystemX/nodetrix)
* <b>python -m SimpleHTTPServer 8888</b> depuis le dossier cloné
* dans le dossier cloné, dans /data/, remplacer "miserables.json" par l'output de jsonconvertToNodeTrix.py et le renommer "miserables.json" 
* accéder à (http://localhost:8888/)
