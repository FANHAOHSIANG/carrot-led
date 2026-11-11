"""Fail if either client's shared behavior fixtures or shipped artwork drift."""
from pathlib import Path
import hashlib
p=Path(__file__).resolve().parents[1]
for name in ('vehicle_scenarios.csv','startup_scenarios.csv','gif_vectors.csv','startup_gif.csv'):
    a=p/'android/tests'/name;b=p/'ios/tests'/name
    assert a.read_bytes()==b.read_bytes(),f'{name}: cross-platform fixture drift'
for name in ('crystal','gaze','devil','spiral','flame','mecha','sasuke_sharingan','sasuke_rinnegan'):
    a=p/'android/app/src/main/assets/eyes'/f'{name}.gif'
    b=p/'ios/Resources/Eyes'/f'{name}.gif'
    assert a.read_bytes()==b.read_bytes(),f'{name}: animation drift'
for name in ('startup64.bin','startup96.bin','startup_car.gif'):
    a=p/'android/app/src/main/assets/startup'/name;b=p/'ios/Resources/Startup'/name
    assert a.read_bytes()==b.read_bytes(),f'{name}: opening drift'
print('Shared car-state/startup/protocol fixtures and 11 animation assets match exactly')
