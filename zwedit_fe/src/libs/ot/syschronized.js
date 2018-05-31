import AwaitingConfirm from '@/libs/ot/awaitingConfirm';

class Synchronized {

    applyClient(client, operation) {
        client.sendOperation(client.revision, operation);
        return new AwaitingConfirm(operation);
    }
    
    applyServer(client, operation) {
        client.applyOperation(operation);
        return this;
    }

    serverAck(client) {
        throw new Error("There is no pending operation.");
    }
}

export default Synchronized;
