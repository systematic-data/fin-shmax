# Fix Acceptor for testing purposes.

## How to create distributable

Invoking the `package` goal in Maven:

        mvn package

It will create under `target` the file `fix-acceptor-RELEASE-tarball.tar.gz`.


## How to execute

1. Expand the file `fix-acceptor-RELEASE-tarball.tar.gz`.
1. In `bin` folder, execute the `fix-acceptor.sh` script:

        $ bin/fix-acceptor.sh

Then the acceptor is ready to accept FIX connections after the log line

    [QFJ Message Processor] INFO quickfix.SocketAcceptor -- Started QFJ Message Processor





