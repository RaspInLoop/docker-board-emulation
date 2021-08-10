# ssh-bastion-updater

A bastion host is a special purpose computer on a network specifically designed and configured to withstand attacks. The computer generallyhosts a single application, for example a proxy server, and all otherservices are removed or limited to reduce the threat to the computer. It is hardened in this manner primarily due to its location and purpose,which is either on the outside of a firewall or in a demilitarized zone (DMZ) and usually involves access from untrusted networks orcomputers.

In this project, ssh-bastion accepts customer ssh connection and redirect forward them based on the client public key (using forced command within the authorized_keys )

This containerized application is intended runs with  openssh server (binlab/bastion). their shared a empty_dir volume on pod to store  authorized_keys key.
authorized_keys is updated by this app and read by openssh server

# Usage

With redis-cli, publish a message telling to ssh-bastion to allow key to reach pod. 
ex.: `PUBLISH ssh-access.add 'ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIJI2ZFl+if5ScoWmvhhZnRE30tsa+M4wJ/fr+pqXzzKr emulator-0.ssh-access.default.svc.cluster.local'`

With ssh use bastion as proxy jump

ssh -J bastion:22 emulator-0

or 