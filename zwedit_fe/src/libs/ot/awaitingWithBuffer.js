import AwaitingConfirm from '@/libs/ot/awaitingConfirm';
import TextOperation from '@/libs/ot/textOperation';

class AwaitingWithBuffer {
    
    outstanding;

    buffer;
    
    constructor(outstanding, buffer) {
        this.outstanding = outstanding;
        this.buffer = buffer;
    }
    
    applyClient(client, operation) {
        console.log("AwaitingWithBuffer");
        var newBuffer = this.buffer.compose(operation);
        return new AwaitingWithBuffer(this.outstanding, newBuffer);
    }

    applyServer(client, operation) {
        var pair1 = TextOperation.transform(this.outstanding, operation);
        var pair2 = TextOperation.transform(this.buffer, pair1[1]);
        client.applyOperation(pair2[1]);
        return new AwaitingWithBuffer(pair1[0], pair2[0]);
    }
    
    serverAck(client) {
        client.sendOperation(client.revision, this.buffer);
        return new AwaitingConfirm(this.buffer);
    }
}

export default AwaitingWithBuffer;
