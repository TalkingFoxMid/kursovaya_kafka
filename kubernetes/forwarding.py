from concurrent.futures import ThreadPoolExecutor
import subprocess
d = 4

with ThreadPoolExecutor(max_workers=64) as tpe:
    tpe.submit(lambda: subprocess.run("kubectl port-forward deployment/zk1 2181:2181", shell=True))
    tpe.submit(lambda: subprocess.run("kubectl port-forward deployment/zk2 2182:2181", shell=True))
    tpe.submit(lambda: subprocess.run("kubectl port-forward deployment/zk3 2183:2181", shell=True))
    tpe.submit(lambda: subprocess.run("kubectl port-forward deployment/kfc1 9093:9093", shell=True))
    tpe.submit(lambda: subprocess.run("kubectl port-forward deployment/kfc2 9094:9094", shell=True))
    tpe.submit(lambda: subprocess.run("kubectl port-forward deployment/kfc3 9095:9095", shell=True))
