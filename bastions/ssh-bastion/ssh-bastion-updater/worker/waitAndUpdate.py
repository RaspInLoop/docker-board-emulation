import redis
import fileinput
import re
import time
from os import environ

def process(message):
    print(f"received {message}")
    channel = message["channel"].decode("utf-8") 
    data = message["data"]
    print(f"data {data} on channel {channel}")
    if channel == "ssh-access.add":
        add_entry(data)
    elif channel == "ssh-access.del":
        del_entry(data)

def add_entry(data):
    key_type, pub_key, destination = data.decode("utf-8").split()
    line= f"no-pty,command=\"\",permitopen=\"{destination}:{ssh_emulator_port}\" {key_type} {pub_key}\n"
    with open(authorized_keys, "a") as file_object:
        # Append 'entry' at the end of file
        file_object.write(line)
        print("entry added.")

def del_entry(data):
    key_type, pub_key, destination = data.decode("utf-8").split()
    matched = re.compile(f"no-pty,command=.*{pub_key}$").search
    with fileinput.FileInput(authorized_keys, inplace=True) as file:
        for line in file:
            if pub_key not in line: # save lines that do not match               
                print(line, end='') # this goes to filename due to inplace=1
    print("entry removed (if any).")

print("ssh-bastion-updater started.")
ssh_emulator_port =environ.get("SSH_EMULATOR_PORT", "22")
redis_host =environ.get("REDIS_MASTER_SERVICE_HOST", "localhost")
redis_port = int(environ.get("REDIS_PORT", "6379"))
authorized_keys =environ.get("AUTHORIZED_KEYS", "/var/lib/bastion/authorized_keys")
# connect with redis server as Bob
r = redis.Redis(host=redis_host, port=redis_port, db=0)
p = r.pubsub()

p.psubscribe("ssh-access.*")
print("ssh-bastion-updater subscribed to ssh-access.*")
while True:
    message = p.get_message()
    if message:
        process(message)
    time.sleep(0.001)